package uk.co.utilisoft.genericutils.web.searchfilter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.co.utilisoft.genericutils.dtc.MPANCore;

/**
 * @author Philip Lau
 * @version 1.0
 * @param <T> the domain wrapper object
 */
public abstract class GenericResultsDaoHibernate<T extends SearchWrapper> extends ResultsDaoHibernate<T, Long>
{
  /**
   * {@inheritDoc}
   * Overridden default functionality to allow for use of Boolean type values in the OptionDTO drop down menu.
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getSearchCriteria(java.lang.String, java.util.List,
   * java.util.List, org.hibernate.Session, boolean, java.lang.String, boolean, boolean, boolean, java.lang.String,
   * java.lang.String)
   */
  @Override
  protected Query getSearchCriteria(String aBaseClassName,
                                    List<SearchCriteriaDTO< ? >> aSearchCriteria,
                                    List<SearchCriteriaDTO< ? >> aInitialFilters,
                                    Session aSession,
                                    boolean isCount,
                                    String aSortOrder,
                                    boolean aIncludeFixedCriteria,
                                    boolean aIncludeDynamicCriteria,
                                    boolean aIncludeGroupBySql,
                                    String aJoinSql,
                                    String aSpecialSelectSql)
  {
    List<QueryStringBindValueCombo> queryStringBindValueCombos = getQueryStringBindValueCombos(aSearchCriteria,
                                                                                               aInitialFilters);

    if (aIncludeFixedCriteria && addFixedCriteria(queryStringBindValueCombos.size()) != null)
    {
      queryStringBindValueCombos.add(addFixedCriteria(queryStringBindValueCombos.size()));
    }

    QueryStringBindValueCombo dynamicCriteria = addDynamicCriteria();
    if (aIncludeDynamicCriteria && dynamicCriteria != null && !dynamicCriteria.equals(""))
    {
      queryStringBindValueCombos.add(dynamicCriteria);
    }

    StringBuffer hql = new StringBuffer();
    if (isCount && aJoinSql == null)
    {
      hql.append("select count(*) ");
    }
    else if (isCount && aSpecialSelectSql != null)
    {
      hql.append("select count(distinct " + getAlias() + ") ");
    }
    else if (aSpecialSelectSql != null && !StringUtils.isBlank(aSortOrder))
    {
      hql.append(aSpecialSelectSql + ", " + getOrderByItem(aSortOrder) + " ");
    }
    else if (aSpecialSelectSql != null)
    {
      hql.append(aSpecialSelectSql);
    }

    hql.append("from " + aBaseClassName + " " + getAlias());

    if (aJoinSql != null)
    {
      hql.append(aJoinSql);
    }

    boolean usedWhere = false;
    for (QueryStringBindValueCombo currentCombo : queryStringBindValueCombos)
    {
      if (currentCombo.getQueryString() != null)
      {
        if (!usedWhere)
        {
          hql.append(" where " + currentCombo.getQueryString());
          usedWhere = true;
        }
        else
        {
          hql.append(" and " + currentCombo.getQueryString());
        }
      }
    }

    if (aIncludeGroupBySql && getGroupBy() != null)
    {
      hql.append(" " + getGroupBy());
    }

    if (aSortOrder != null && !isCount)
    {
      hql.append(" order by " + getCustomSortOrder(aSortOrder));
    }

    Query q  = aSession.createQuery(hql.toString());
    bindFixedCriteriaParms(q);
    return bindValuesToQuery(q, queryStringBindValueCombos, isBooleanTypeOptionDTOUsed());
  }

  /**
   * Customise the sort order query.
   *
   * @param aOrderBySql the order by query
   * @return the order by query
   */
  protected String getCustomSortOrder(String aOrderBySql)
  {
    return aOrderBySql;
  }

  /**
   * Strip an order by sql query of desc, asc. Override to customise.
   *
   * @param aOrderBySql the order by sql
   * @return the order by item
   */
  protected String getOrderByItem(String aOrderBySql)
  {
    return aOrderBySql.replace("desc", "").replace("asc", "").trim();
  }

  /**
   * Defaults to OptionDTO drop down fields used to represent boolean values.
   *
   * @return true if OptionDTO drop down fields are used to represent boolean values;
   * false otherwise
   */
  protected boolean isBooleanTypeOptionDTOUsed()
  {
    return true;
  }

  /**
   * Allows for used of boolean values passed from screen drop down field menus.
   *
   * @param aQueryToBindTo the query to bind to
   * @param aCombos the query string bind value combos
   * @param aIsBooleanTypeOptionDTOUsed true if OptionDTO drop down fields are used to represent boolean values;
   * false otherwise
   * @return the Query
   */
  protected Query bindValuesToQuery(Query aQueryToBindTo,
                                    List<QueryStringBindValueCombo> aCombos, Boolean aIsBooleanTypeOptionDTOUsed)
  {
    Class< ? > currentClassType;
    Object currentValue;
    int innerSize, paramCount = 0;
    for (QueryStringBindValueCombo currentCombo : aCombos)
    {
      if (currentCombo.getQueryString() != null
          && currentCombo.getClassTypes() != null)
      {
        innerSize = currentCombo.getValues().size();
        for (int innerIndex = 0; innerIndex < innerSize; innerIndex++)
        {
          currentClassType = currentCombo.getClassTypes().get(innerIndex);
          currentValue = currentCombo.getValues().get(innerIndex);

            if (currentClassType.equals(String.class))
            {
              aQueryToBindTo.setParameter("id" + paramCount, convertWildcards(currentValue.toString()));
            }
            else if (currentClassType.equals(Integer.class))
            {
              aQueryToBindTo.setParameter("id" + paramCount, new Integer(currentValue.toString()));
            }
            else if (currentClassType.equals(Long.class))
            {
              aQueryToBindTo.setParameter("id" + paramCount, new Long(currentValue.toString()));
            }
            else if (currentClassType.equals(Double.class))
            {
              aQueryToBindTo.setParameter("id" + paramCount, new Double(currentValue.toString()));
            }
            else if (currentClassType.equals(BigDecimal.class))
            {
              aQueryToBindTo.setBigDecimal("id" + paramCount, new BigDecimal(currentValue.toString()));
            }
            else if (currentClassType.equals(DateTime.class))
            {
              aQueryToBindTo.setDate("id" + paramCount, ((DateTime) currentValue).toDate());
            }
            else if (currentClassType.equals(JodaDateTimeDTO.class))
            {
              aQueryToBindTo.setDate("id" + paramCount, ((JodaDateTimeDTO) currentValue).getDateTime().toDate());
            }
            else if (currentClassType.equals(OptionDTO.class))
            {
              if (convertibleToBoolean(currentValue.toString()) && aIsBooleanTypeOptionDTOUsed)
              {
                // set query parameter with boolean value for an OptionDTO selected value, otherwise set as string parameter
                aQueryToBindTo.setBoolean("id" + paramCount, Boolean.valueOf(currentValue.toString()));
              }
              else if (convertibleToLong(currentValue.toString()))
              {
                aQueryToBindTo.setLong("id" + paramCount, Long.parseLong(currentValue.toString()));
              }
              else
              {
                Object enumValue = convertToEnum(currentValue.toString());
                aQueryToBindTo.setParameter("id" + paramCount, (enumValue != null ? enumValue : currentValue.toString()));
              }
            }
            else if (currentClassType.equals(Boolean.class))
            {
              // set query parameter with boolean value for an OptionDTO selected value, otherwise set as string parameter
              if (convertibleToBoolean(currentValue.toString()))
              {
                aQueryToBindTo.setBoolean("id" + paramCount, Boolean.valueOf(currentValue.toString()));
              }
              else
              {
                aQueryToBindTo.setParameter("id" + paramCount, currentValue.toString());
              }
            }
            else if (currentClassType.equals(MPANCore.class))
            {
              aQueryToBindTo.setParameter("id" + paramCount, new MPANCore(currentValue.toString()));
            }
           

            paramCount++;

        }
      }
    }
    return aQueryToBindTo;
  }

  /**
   * Override to convert user selected value to a known enum type.
   *
   * @param <EnumType> the enum
   * @param aEnumTypeName the name
   * @return the enum
   */
  protected <EnumType> EnumType convertToEnum(String aEnumTypeName)
  {
    return null;
  }

  /**
   * Parse a date text as a JodaDateTime object.
   *
   * @param aDateAsText the date as text
   * @return DateTime
   */
  protected DateTime parseTextAsJodaDateTime(String aDateAsText)
  {
    if (aDateAsText != null && !aDateAsText.equals(""))
    {
      // DateTimeFormatter has a problem with 'jUn 1888', but 'JUN 1888' is ok
      String dateText = aDateAsText.toUpperCase();

      dateTimeFormatsLoop:
      for (DateTimeFormatter dtfSupported : addSupportedJodaDateTimeFormatters())
      {
        try
        {
          return dtfSupported.parseDateTime(dateText);
        }
        catch (IllegalArgumentException iae)
        {
          continue dateTimeFormatsLoop;
        }
      }
    }

    return null;
  }

  /**
   * Additional date time formats that need to be recognised when binding date text from a search filter
   * into JodaTime object.
   * @return the supported DateTimeFormatter(s)
   */
  protected List<DateTimeFormatter> addSupportedJodaDateTimeFormatters()
  {
    List<DateTimeFormatter> dtfFormatters = new ArrayList<DateTimeFormatter>();
    dtfFormatters.add(DateTimeFormat.forPattern("dd/MM/yyyy"));
    dtfFormatters.add(DateTimeFormat.forPattern("MMM yyyy"));
    return dtfFormatters;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#convertObjectsToReturnTypes(java.util.List)
   */
  @Override
  protected List<T> convertObjectsToReturnTypes(List<?> aResultsToConvert)
  {
    List<T> results = new ArrayList<T>();

    for (Object resultToConvert : aResultsToConvert)
    {
      results.add(createConvertedObject(resultToConvert));
    }

    return results;
  }

  /**
   * @param aResultToConvert the result of the dao query
   * @return the result as a domain object wrapper
   */
  protected abstract T createConvertedObject(Object aResultToConvert);

  /*
   * @param aValue the string value
   * @return true if string is convertable to boolean; false otherwise
   */
  private boolean convertibleToBoolean(String aValue)
  {
    if (StringUtils.isBlank(aValue))
    {
      return false;
    }

    if (!(aValue.trim().equalsIgnoreCase("false") || aValue.trim().equalsIgnoreCase("true")))
    {
      return false;
    }

    return true;
  }

  private boolean convertibleToLong(String aValue)
  {
    try
    {
      Long.parseLong(aValue);
      return true;
    }
    catch (NumberFormatException nfe)
    {
    }
    return false;
  }
}
