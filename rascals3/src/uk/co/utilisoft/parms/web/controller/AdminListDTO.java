/**
 *
 */
package uk.co.utilisoft.parms.web.controller;

import java.util.List;

/**
 * Class to represent a row on the Administration List screen.
 *
 * @author winstanleyd
 */
public class AdminListDTO
{
  private List<Object> mIdentifier;
  private List<Object> mValues;

  /**
   * Constructor.
   *
   * @param aIdentifier Identifier of the object.
   * @param aValues The value list that make up the object row.
   */
  public AdminListDTO(List<Object> aIdentifier, List<Object> aValues)
  {
    mIdentifier = aIdentifier;
    mValues = aValues;
  }

  /**
   * @return the identifier
   */
  public List<Object> getIdentifier()
  {
    return mIdentifier;
  }

  /**
   * @param aIdentifier the identifier to set
   */
  public void setIdentifier(List<Object> aIdentifier)
  {
    mIdentifier = aIdentifier;
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
}
