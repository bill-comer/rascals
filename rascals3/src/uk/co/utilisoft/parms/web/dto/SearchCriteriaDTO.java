package uk.co.utilisoft.parms.web.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import uk.co.utilisoft.table.model.JodaDateTimeDTO;


/**
 * @author Kirk Hawksworth, Daniel Winstanley
 *
 * @version 1.0
 * @param <T> the Serializable object
 */
public class SearchCriteriaDTO<T> implements Serializable
{
  public static final String TYPE_TEXT = "text";
  public static final String TYPE_DATE = "date";
  public static final String TYPE_LIST = "list";
  public static final String TYPE_BOOLEAN = "boolean";
  public static final String TYPE_TEXT_LIKE = "text_like";
  public static final String TYPE_CUSTOM_MPANCORE = "mpancore";
  public static final String TYPE_CUSTOM_PARMSREPORTINGPERIOD = "parmsreportingperiod";
  public static final String TYPE_CUSTOM_SELECT = "customselect";

  public static final String COMPARATOR_EQUALS = "=";
  public static final String COMPARATOR_NOT_EQUALS = "!=";
  public static final String COMPARATOR_IN = "in";
  public static final String COMPARATOR_GREATER_THAN = ">";
  public static final String COMPARATOR_LESS_THAN = "<";
  public static final String COMPARATOR_GREATER_THAN_EQUALS = ">=";
  public static final String COMPARATOR_LESS_THAN_EQUALS = "<=";

  public static final String COMPARATOR_LIKE = "like";
  public static final String COMPARATOR_NOT_LIKE = "not like";

  private static final long serialVersionUID = -2066830451726505832L;
  private String mDisplayName;
  private String mComparator;
  private T mSearchValue;
  private boolean mShowInFilter;

  private String mModelName;

  //For dropdowns
  private List<OptionDTO> mFilterValues;

  // For the filter
  private String mType;

  private Class<T> mClass;

  /**
   * create.
   */
  public SearchCriteriaDTO()
  {
  }

  /**
   * Create new.
   * @param aClass the class
   */
  public SearchCriteriaDTO(Class<T> aClass)
  {
    mClass = aClass;
  }
  /**
   * @param aClass the class
   * @param aDisplayName display name
   * @param aModelName model name for hql
   * @param aType type
   */
  public SearchCriteriaDTO(Class<T> aClass, String aDisplayName,
                           String aModelName, String aType)
  {
    mDisplayName = aDisplayName;
    mModelName = aModelName;
    mType = aType;
    mClass = aClass;
  }

  /**
   *
   * @param aClass the class
   * @param aDisplayName display name
   * @param aModelName model name for hql
   * @param aType type
   * @param aEnabled show this in the filter, true by default
   */
  public SearchCriteriaDTO(Class<T> aClass, String aDisplayName,
                           String aModelName, String aType, boolean aEnabled)
  {
    mDisplayName = aDisplayName;
    mModelName = aModelName;
    mType = aType;
    mShowInFilter = aEnabled;
    mClass = aClass;
  }

  /**
   *
   * @param aClass the class
   * @param aDisplayName display name
   * @param aModelName model name for hql
   * @param aType type
   * @param aEnabled show this in the filter, true by default
   * @param aComparator comparator
   */
  public SearchCriteriaDTO(Class<T> aClass, String aDisplayName,
                           String aModelName, String aType, boolean aEnabled,
                           String aComparator)
  {
    mDisplayName = aDisplayName;
    mModelName = aModelName;
    mType = aType;
    mShowInFilter = aEnabled;
    mComparator = aComparator;
    mClass = aClass;
  }

  /**
   *
   * @param aClass the class
   * @param aDisplayName display name
   * @param aModelName model name for hql
   * @param aType type
   * @param aShownInFilter show this in the filter, true by default
   * @param aFilterValues the filter drop down values
   */
  public SearchCriteriaDTO(Class<T> aClass,
                           String aDisplayName,
                           String aModelName,
                           String aType,
                           boolean aShownInFilter,
                           List<OptionDTO> aFilterValues)
  {
    mDisplayName = aDisplayName;
    mModelName = aModelName;
    mType = aType;
    mShowInFilter = aShownInFilter;
    mClass = aClass;
    mFilterValues = aFilterValues;
  }

  /**
   * @return the comparator.
   */
  public String getComparator()
  {
    return mComparator != null ? mComparator : "";
  }

  /**
   * @param aComparator The comparator to set.
   */
  public void setComparator(String aComparator)
  {
    mComparator = aComparator;
  }

  /**
   * @return the searchValue.
   */
  public T getSearchValue()
  {
    return mSearchValue;
  }

  /**
   * @param aSearchValue The searchValue to set.
   */
  public void setSearchValue(T aSearchValue)
  {
    mSearchValue = aSearchValue;
  }

  /**
   * @return the modelName.
   */
  public String getModelName()
  {
    return mModelName;
  }

  /**
   * @param aModelName The modelName to set.
   */
  public void setModelName(String aModelName)
  {
    mModelName = aModelName;
  }

  /**
   * @return the displayName.
   */
  public String getDisplayName()
  {
    return mDisplayName;
  }

  /**
   * @param aDisplayName The displayName to set.
   */
  public void setDisplayName(String aDisplayName)
  {
    mDisplayName = aDisplayName;
  }

  /**
   * @return the type.
   */
  public String getType()
  {
    return mType;
  }

  /**
   * @param aType The type to set.
   */
  public void setType(String aType)
  {
    mType = aType;
  }

  /**
   * @return the enabled.
   */
  public boolean getEnabled()
  {
    return mShowInFilter;
  }

  /**
   * @param aEnabled The enabled to set.
   */
  public void setEnabled(boolean aEnabled)
  {
    mShowInFilter = aEnabled;
  }

  /**
   * @return the class.
   */
  public Class<T> getClassType()
  {
    return mClass;
  }

  /**
   * @param aClass the class to set
   */
  public void setClassType(Class<T> aClass)
  {
    mClass = aClass;
  }

  /**
   *
   * @return filter values
   */
  public List<OptionDTO> getFilterValues()
  {
    return mFilterValues;
  }

  /**
   * @param aFilterValues The filterValues to set.
   */
  public void setFilterValues(List<OptionDTO> aFilterValues)
  {
    mFilterValues = aFilterValues;
  }

  /**
   * Make generic to stop duplications of initial filters.
   * @param aCriteria search criteria
   * @return true if equal, false otherwise
   */
  public boolean equalsIncludingSameDay(SearchCriteriaDTO< ? > aCriteria)
  {
    if (aCriteria.getClassType().equals(getClassType())
        && aCriteria.getComparator().compareTo(getComparator()) == 0
        && aCriteria.getDisplayName().compareTo(getDisplayName()) == 0
        && aCriteria.getModelName().compareTo(getModelName()) == 0)
    {
      if (getClassType().equals(JodaDateTimeDTO.class))
      {
        DateTime thisDateTime = ((JodaDateTimeDTO) getSearchValue()).getDateTime();
        DateTime otherDateTime = ((JodaDateTimeDTO) aCriteria.getSearchValue()).getDateTime();

        if (thisDateTime.get(DateTimeFieldType.year()) == otherDateTime.get(DateTimeFieldType.year())
            && thisDateTime.get(DateTimeFieldType.monthOfYear()) == otherDateTime.get(DateTimeFieldType.monthOfYear())
            && thisDateTime.get(DateTimeFieldType.dayOfMonth()) == otherDateTime.get(DateTimeFieldType.dayOfMonth()))
        {
          return true;
        }
        return false;
      }
      return true;
    }
    return false;
  }
}
