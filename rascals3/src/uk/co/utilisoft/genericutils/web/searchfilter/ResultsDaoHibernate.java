package uk.co.utilisoft.genericutils.web.searchfilter;


import static config.GenericWebConstants.SCREEN_DATE_FORMAT;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.orm.hibernate3.HibernateCallback;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.formfill.hibernateutils.domain.DomainObject;

/**
 * @author Kirk Hawksworth, Daniel Winstanley
 *
 * @param <T> the domain object
 * @param <S> the Serializable domain object
 * @version 1.0
 */
public class ResultsDaoHibernate<T extends DomainObject<?, ?>, S extends Serializable>
                                   extends UtilisoftGenericDaoHibernate<T, S>
{
  private Class<T> mBaseClass;
  protected DateTimeFormatter mDtf = DateTimeFormat.forPattern(SCREEN_DATE_FORMAT);

  /**
   * Allow options for join and special select sql queries.
   *
   * @param aSearchCriteria the search criteria
   * @param aInitialFilters the initial filters
   * @param aSortOrder the sort order
   * @param firstResults the first result
   * @param maxResults the max results
   * @param aIncludeFixedCriteria include fixed criteria
   * @param aIncludeDynamicCriteria include dynamic criteria
   * @param aIncludeGroupBySql include group by sql
   * @param aJoinSql join sql
   * @return a collection of objects
   */
  @SuppressWarnings("unchecked")
  public Collection<T> getData(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                               final List<SearchCriteriaDTO< ? >> aInitialFilters,
                               final String aSortOrder,
                               final int firstResults,
                               final int maxResults,
                               final boolean aIncludeFixedCriteria,
                               final boolean aIncludeDynamicCriteria,
                               final boolean aIncludeGroupBySql,
                               final String aJoinSql)
  {
    return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        Query q = getSearchCriteria(getBaseClassName(),
                                    aSearchCriteria,
                                    aInitialFilters,
                                    aSession,
                                    false,
                                    aSortOrder,
                                    aIncludeFixedCriteria,
                                    aIncludeDynamicCriteria,
                                    aIncludeGroupBySql,
                                    aJoinSql,
                                    getSpecialSelectSql());

        if (q == null)
        {
          return new ArrayList<T>();
        }

        q.setFirstResult(firstResults);
        q.setMaxResults(maxResults);

        List<T> r = q.list();
        doWhileInSession(aSession, r);
        return convertObjectsToReturnTypes(r);
      }
    });
  }

  /**
   * Allow options for join and special select sql queries.
   *
   * @param aSearchCriteria the search criteria
   * @param aInitialFilters the initial filters
   * @param aIncludeFixedCriteria true if including a fixed criteria; otherwise, false
   * @param aIncludeDynamicCriteria true if including a dynamic criteria; otherwise, false
   * @param aIncludeGroupBySql a group by statement
   * @return a count of the number of results found
   */
  @SuppressWarnings("unchecked")
  public Long getCount(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                       final List<SearchCriteriaDTO< ? >> aInitialFilters,
                       final boolean aIncludeFixedCriteria,
                       final boolean aIncludeDynamicCriteria,
                       final boolean aIncludeGroupBySql,
                       final String aJoinSql,
                       final boolean addSpecialSelectSql)
  {
    return (Long) getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        String specialSelectSql = addSpecialSelectSql ? getSpecialSelectSql() : null;
        Query q = getSearchCriteria(getBaseClassName(),
                                    aSearchCriteria,
                                    aInitialFilters,
                                    aSession,
                                    true,
                                    null,
                                    aIncludeFixedCriteria,
                                    aIncludeDynamicCriteria,
                                    aIncludeGroupBySql,
                                    aJoinSql,
                                    specialSelectSql);

        if (q == null)
        {
          return new Long(0);
        }
        else if (aIncludeGroupBySql)
        {
          List<Long> counts = q.list();
          return new Long(counts.size());
        }
        return (Long) q.uniqueResult();
      }
    });
  }

  /**
   * @param aSearchCriteria the search criteria
   * @param aInitialFilters the initial filters
   * @param aIncludeFixedCriteria true if including a fixed criteria; otherwise, false
   * @param aIncludeGroupBySql a group by statement
   * @return a count of the number of results found
   */
  public Long getCount(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                       final List<SearchCriteriaDTO< ? >> aInitialFilters,
                       final boolean aIncludeFixedCriteria,
                       final boolean aIncludeGroupBySql)
  {
    return getCount(aSearchCriteria, aInitialFilters, aIncludeFixedCriteria, false, aIncludeGroupBySql);
  }

  /**
   * @param aSearchCriteria the search criteria
   * @param aInitialFilters the initial filters
   * @param aIncludeFixedCriteria true if including a fixed criteria; otherwise, false
   * @param aIncludeDynamicCriteria true if including a dynamic criteria; otherwise, false
   * @param aIncludeGroupBySql a group by statement
   * @return a count of the number of results found
   */
  @SuppressWarnings("unchecked")
  public Long getCount(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                       final List<SearchCriteriaDTO< ? >> aInitialFilters,
                       final boolean aIncludeFixedCriteria,
                       final boolean aIncludeDynamicCriteria,
                       final boolean aIncludeGroupBySql)
  {
    return (Long) getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        Query q = getSearchCriteria(getBaseClassName(),
                                    aSearchCriteria,
                                    aInitialFilters,
                                    aSession,
                                    true,
                                    null,
                                    aIncludeFixedCriteria,
                                    aIncludeDynamicCriteria,
                                    aIncludeGroupBySql,
                                    null,
                                    null);

        if (q == null)
        {
          return new Long(0);
        }
        else if (aIncludeGroupBySql)
        {
          List<Long> counts = q.list();
          return new Long(counts.size());
        }
        return (Long) q.uniqueResult();
      }
    });
  }

  public List<T> getData(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                               final List<SearchCriteriaDTO< ? >> aInitialFilters,
                               final String aSortOrder,
                               final int firstResults,
                               final int maxResults,
                               final boolean aIncludeFixedCriteria,
                               final boolean aIncludeGroupBySql)
  {
    return getData(aSearchCriteria, aInitialFilters, aSortOrder, firstResults, maxResults, aIncludeFixedCriteria, false, aIncludeGroupBySql);
  }

  @SuppressWarnings("unchecked")
  public List<T> getData(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                               final List<SearchCriteriaDTO< ? >> aInitialFilters,
                               final String aSortOrder,
                               final int firstResults,
                               final int maxResults,
                               final boolean aIncludeFixedCriteria,
                               final boolean aIncludeDynamicCriteria,
                               final boolean aIncludeGroupBySql)
  {
    return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        Query q = getSearchCriteria(getBaseClassName(),
                                    aSearchCriteria,
                                    aInitialFilters,
                                    aSession,
                                    false,
                                    aSortOrder,
                                    aIncludeFixedCriteria,
                                    aIncludeDynamicCriteria,
                                    aIncludeGroupBySql,
                                    null,
                                    getSpecialSelectSql());

        if (q == null)
        {
          return new ArrayList<T>();
        }

        q.setFirstResult(firstResults);
        q.setMaxResults(maxResults);

        List<T> r = q.list();
        doWhileInSession(aSession, r);
        return convertObjectsToReturnTypes(r);
      }
    });
  }

  public Long getCount(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                       final List<SearchCriteriaDTO< ? >> aInitialFilters,
                       final boolean aIncludeGroupBySql)
  {
    return getCount(aSearchCriteria, aInitialFilters, false, aIncludeGroupBySql);
  }

  public List<T> getData(final List<SearchCriteriaDTO< ? >> aSearchCriteria,
                               final List<SearchCriteriaDTO< ? >> aInitialFilters,
                               final String aSortOrder,
                               final int firstResults,
                               final int maxResults,
                               final boolean aIncludeGroupBySql)
  {
    return getData(aSearchCriteria, aInitialFilters, aSortOrder, firstResults, maxResults, true, aIncludeGroupBySql);
  }

  protected void doWhileInSession(Session aSession, List< T > aList)
  { }

  /**
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getSpecialSelectSql()
   */
  protected String getSpecialSelectSql()
  {
    return "select distinct " + getAlias() + " ";
  }

  protected String getGroupBy()
  {
    return null;
  }

  /**
   * Get Search Criteria.
   *
   * //TODO check the setParamter methods on the binding of values to query
   *
   * @param aBaseClassName
   * @param aSearchCriteria
   * @param aInitialFilters
   * @param aSession
   * @param isCount
   * @param aSortOrder
   * @return Query
   */
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
    if (isCount)
    {
      hql.append("select count(*) ");
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

    if (aIncludeGroupBySql)
    {
      hql.append(" " + getGroupBy());
    }

    if (aSortOrder != null && !isCount)
    {
      hql.append(" order by " + aSortOrder);
    }

    Query q  = aSession.createQuery(hql.toString());
    bindFixedCriteriaParms(q);
    return bindValuesToQuery(q, queryStringBindValueCombos);
  }

  protected Query bindValuesToQuery(Query aQueryToBindTo,
                                    List<QueryStringBindValueCombo> aCombos)
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
              aQueryToBindTo.setParameter("id" + paramCount, (String) currentValue);
            }
            else if (currentClassType.equals(Boolean.class))
            {
              aQueryToBindTo.setBoolean("id" + paramCount, (Boolean) currentValue);
            }
            paramCount++;

        }
      }
    }
    return aQueryToBindTo;
  }

  protected List<QueryStringBindValueCombo> getQueryStringBindValueCombos(List<SearchCriteriaDTO< ? >> aSearchCriteria,
                                                                          List<SearchCriteriaDTO< ? >> aInitialCriteria)
  {
    List<QueryStringBindValueCombo> combos = new ArrayList<QueryStringBindValueCombo>();

    if (!aSearchCriteria.isEmpty())
    {
      combos.addAll(getCombos(aSearchCriteria, 0));
    }

    if (!aInitialCriteria.isEmpty())
    {
      combos.addAll(getCombos(aInitialCriteria, getCurrentParamIndex(combos)));
    }

    return combos;
  }

  private int getCurrentParamIndex(List<QueryStringBindValueCombo> aCombos)
  {
    int paramIndex = 0;
    for (QueryStringBindValueCombo currentCombo : aCombos)
    {
      if (currentCombo.getValues() != null)
      {
        paramIndex += currentCombo.getValues().size();
      }
    }
    return paramIndex;
  }

  @SuppressWarnings("unchecked")
  private List<QueryStringBindValueCombo> getCombos(List<SearchCriteriaDTO< ? >> aCriteria,
                                                    int aStartIndex)
  {
    List<QueryStringBindValueCombo> combos = new ArrayList<QueryStringBindValueCombo>();

    Object currentValue;
    String currentType, currentComparator, currentModelName, currentListQueryString;
    JodaDateTimeDTO currentDateValue;
    List<Object> currentListValue;
    Class< ? > currentClassType;
    List<Class< ? >> currentListClassTypes;
    int paramCount = aStartIndex;

    for (SearchCriteriaDTO< ? > current : aCriteria)
    {
      currentValue = current.getSearchValue();
      currentType = current.getType();
      currentComparator = current.getComparator();
      currentModelName = current.getModelName();
      currentClassType = current.getClassType();

      if (currentValue != null)
      {
        currentValue = currentValue.toString().trim();
      }

      if (currentValue == null || currentValue.equals(""))
      {
        combos.add(new QueryStringBindValueCombo(currentModelName + " is null"));
      }
      else if (/*currentValue != null && !currentValue.equals("") && */current.getEnabled())
      {
        if (currentType.equals(SearchCriteriaDTO.TYPE_TEXT))
        {
          if (currentValue.toString().contains("*") || currentValue.toString().contains("?"))
          {
            if (currentComparator.equals(SearchCriteriaDTO.COMPARATOR_EQUALS))
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " like :id" + paramCount,
                                                       currentValue,
                                                       currentClassType));
            }
            else
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " not like :id" + paramCount,
                                                       currentValue,
                                                       currentClassType));
            }
          }
          else
          {
            combos.add(new QueryStringBindValueCombo(currentModelName + " " + currentComparator + " :id" + paramCount,
                                                     currentValue,
                                                     currentClassType));
          }
        }
        else if (currentType.equals(SearchCriteriaDTO.TYPE_DATE))
        {
          if (currentValue.getClass().equals(String.class))
          {
            JodaDateTimeDTO newDateTimeDTO = new JodaDateTimeDTO();
            newDateTimeDTO.setDateTime(mDtf.parseDateTime((String) currentValue));
            currentDateValue = newDateTimeDTO;
          }
          else
          {
            currentDateValue = (JodaDateTimeDTO) currentValue;
          }

          if (currentDateValue.getDateTime() != null)
          {
            if (currentComparator != null && currentComparator.equals(SearchCriteriaDTO.COMPARATOR_EQUALS))
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " >= :id" + paramCount,
                                                       midnightToday(currentDateValue.getDateTime()),
                                                       DateTime.class));
              paramCount++;
              combos.add(new QueryStringBindValueCombo(currentModelName + " <= :id" + paramCount,
                                                       midnightDayAfter(currentDateValue.getDateTime()),
                                                       DateTime.class));
            }
            else if (currentComparator != null
                && (currentComparator.equals(SearchCriteriaDTO.COMPARATOR_GREATER_THAN)
                    || currentComparator.equals(SearchCriteriaDTO.COMPARATOR_GREATER_THAN_EQUALS)))
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " " + currentComparator + " :id" + paramCount,
                                                       midnightToday(currentDateValue.getDateTime()),
                                                       DateTime.class));
            }
            else if (currentComparator != null
                && (currentComparator.equals(SearchCriteriaDTO.COMPARATOR_LESS_THAN)
                    || currentComparator.equals(SearchCriteriaDTO.COMPARATOR_LESS_THAN_EQUALS)))
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " " + currentComparator + " :id" + paramCount,
                                                       midnightDayAfter(currentDateValue.getDateTime()),
                                                       DateTime.class));
            }
            else
            {
              combos.add(new QueryStringBindValueCombo(currentModelName + " " + currentComparator + ":id" + paramCount,
                                                       currentDateValue.getDateTime(),
                                                       DateTime.class));
            }
          }
        }
        else if (currentType.equals(SearchCriteriaDTO.TYPE_LIST))
        {
          currentListValue = (List) currentValue;
          currentListClassTypes = new ArrayList<Class< ? >>();

          if (currentListValue.size() > 0)
          {
            currentListQueryString = currentModelName + " in (";
            for (int listItem = 0; listItem < currentListValue.size(); listItem++)
            {
              if (listItem != (currentListValue.size() - 1))
              {
                currentListQueryString += ":id" + paramCount + ", ";
                paramCount++;
              }
              else
              {
                currentListQueryString += ":id" + paramCount;
              }
              currentListClassTypes.add(currentListValue.get(listItem).getClass());
            }
            currentListQueryString += ")";

            combos.add(new QueryStringBindValueCombo(currentListQueryString, currentListValue, currentListClassTypes));
          }
          else
          {
            combos.add(new QueryStringBindValueCombo(null, currentListValue, currentListClassTypes));
            paramCount--;
          }
        }
        else
        {
          combos.add(new QueryStringBindValueCombo(currentModelName + " " + currentComparator + " :id" + paramCount,
                                                   currentValue,
                                                   currentClassType));
        }

        paramCount++;
      }
    }
    return combos;
  }

  protected DateTime midnightToday(DateTime aDate)
  {
    return aDate == null ? null : new DateMidnight(aDate).toDateTime();
  }

  protected DateTime midnightDayAfter(DateTime aDate)
  {
    return aDate == null ? null : new DateMidnight(aDate.plusDays(1)).toDateTime();
  }

  /**
   * Add fixed criteria to the middle of the query.
   * @return the fixed criteria
   */
  protected QueryStringBindValueCombo addFixedCriteria(int aCurrentParamIndex)
  {
    return null;
  }

  /**
   * Add fixed criteria to the middle of the query.
   * @return the fixed criteria
   */
  protected QueryStringBindValueCombo addDynamicCriteria()
  {
    return null;
  }

  protected void bindFixedCriteriaParms(Query query)
  {

  }

  public String getAlias()
  {
    return "stuff";
  }

  /**
  *
  * @param aQuery The query entered from the user
  * @return A new query with * and ? replaced by % and _
  */
  protected String convertWildcards(String aQuery)
  {
    return aQuery == null ? null : aQuery.replace("*", "%").replace("?", "_");
  }

  @SuppressWarnings("unchecked")
  protected List<T> convertObjectsToReturnTypes(List< ? > aResultsToConvert)
  {
    return (List<T>) aResultsToConvert;
  }

  /**
   * Default constructor. Resolve base class to a type.
   */
  @SuppressWarnings("unchecked")
  public ResultsDaoHibernate()
  {
    mBaseClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * Construct new with base c;ass
   */
  public ResultsDaoHibernate(Class<T> aClass)
  {
    mBaseClass = aClass;
  }

  public String getBaseClassName()
  {
    return mBaseClass.getSimpleName();
  }
}

