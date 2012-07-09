package uk.co.utilisoft.genericutils.web.searchfilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author winstanleyd
 */
public class QueryStringBindValueCombo
{
  private String mQueryString;
  private List<Object> mValues;
  private List<Class< ? >> mClassTypes;

  /**
   * Constructor.
   *
   * @param aQueryString Query String.
   */
  public QueryStringBindValueCombo(String aQueryString)
  {
    mQueryString = aQueryString;
  }

  /**
   * Constructor.
   *
   * @param aQueryString Query String.
   * @param aValues Query Value.
   * @param aClassTypes List of the Class Types.
   */
  public QueryStringBindValueCombo(String aQueryString,
                                   List<Object> aValues,
                                   List<Class< ? >> aClassTypes)
  {
    mQueryString = aQueryString;
    mValues = aValues;
    mClassTypes = aClassTypes;
  }

  /**
   * Constructor.
   *
   * @param aQueryString Query String.
   * @param aValue Query Value.
   * @param aClassType Class Type.
   */
  public QueryStringBindValueCombo(String aQueryString,
                                   Object aValue,
                                   Class< ? > aClassType)
  {
    mQueryString = aQueryString;
    mValues = new ArrayList<Object>();
    mValues.add(aValue);
    mClassTypes = new ArrayList<Class< ? >>();
    mClassTypes.add(aClassType);
  }

  /**
   * @return the queryString
   */
  public String getQueryString()
  {
    return mQueryString;
  }

  /**
   * @param aQueryString the queryString to set
   */
  public void setQueryString(String aQueryString)
  {
    mQueryString = aQueryString;
  }

  /**
   * @return the values
   */
  public List<Object> getValues()
  {
    return mValues;
  }

  /**
   * @param aValues the values to set
   */
  public void setValues(List<Object> aValues)
  {
    mValues = aValues;
  }

  /**
   * @return the classTypes
   */
  public List<Class< ? >> getClassTypes()
  {
    return mClassTypes;
  }

  /**
   * @param aClassTypes the classTypes to set
   */
  public void setClassTypes(List<Class< ? >> aClassTypes)
  {
    mClassTypes = aClassTypes;
  }
}
