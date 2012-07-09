package uk.co.utilisoft.parms.web.dto;

import java.io.Serializable;

/**
 * Data Transfer Object to be used to transfer data for options
 * in lists for combo boxes etc.
 * @author Bev Ridyard
 * @version 1.0
 */
public class OptionDTO implements Serializable
{
  public static final long serialVersionUID = -4788396768379169780L;

  private String mName;
  private String mValue;

  /**
   * Default constructor.
   */
  public OptionDTO()
  { }

  /**
   * Complete constructor.
   * @param aName Display name of option item
   * @param aValue Value of option item
   */
  public OptionDTO(String aValue, String aName)
  {
    mName = aName;
    mValue = aValue;
  }

  /**
   * @return Display name of option
   */
  public String getName()
  {
    return mName;
  }

  /**
   * @param aName Display name of option
   */
  public void setName(String aName)
  {
    mName = aName;
  }

  /**
   * @return Value of option
   */
  public String getValue()
  {
    return mValue;
  }

  /**
   * @param aValue Value of option
   */
  public void setValue(String aValue)
  {
    mValue = aValue;
  }

}