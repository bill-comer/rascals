package uk.co.utilisoft.parms.web.dto;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.co.utilisoft.table.TableConstants;

/**
 * (Copied) from uk.co.utilisoft.table.model.CellDTO
 *
 * @author Philip Lau
 * @version 1.0
 */
public class CellDTO
{
private String mValue;

  private Map<String, String> mCellMap = new HashMap<String, String>();

  private static DateTimeFormatter mScreenDateFormatter = DateTimeFormat.forPattern(TableConstants.SCREEN_DATE_FORMAT);

  private static DecimalFormat mLongDecimalFormat = new DecimalFormat(TableConstants.LONG_FORMAT);

  private static DecimalFormat mIntegerDecimalFormat = new DecimalFormat(TableConstants.INTEGER_FORMAT);

  private static DecimalFormat mDoubleDecimalFormat = new DecimalFormat(TableConstants.DOUBLE_FORMAT);

  //Optional field, can be set if dealing with a non-string display for the table (eg, boolean as a checkbox, image etc)
  private String mType;

  /**
   *
   * @param aValue value to display
   */
  public CellDTO(String aValue)
  {
    mValue = aValue;
  }
  /**
   * @param aInteger the date time
   */
  public CellDTO(Integer aInteger)
  {
    setValue(mIntegerDecimalFormat.format(aInteger));
  }

  /**
   * @param aLong the date time
   */
  public CellDTO(Long aLong)
  {
    setValue(mLongDecimalFormat.format(aLong));
  }

  /**
   * @param aDouble the double
   */
  public CellDTO(Double aDouble)
  {
    setValue(mDoubleDecimalFormat.format(aDouble));
  }


  /**
   *
   * @param aBooleanValue primitive boolean value, sets type so checbox can be displayed ons creen
   */
  public CellDTO(Boolean aBooleanValue)
  {
    if (aBooleanValue == null)
    {
      setValue("false");
    }
    else
    {
      setValue(aBooleanValue + "");
    }
    setType("boolean");
  }

  /**
   * Sets value of date time with defualt format given in the constants file
   * @param aDateTime the date time
   */
  public CellDTO(DateTime aDateTime)
  {
    setValue(mScreenDateFormatter.print(aDateTime));
  }

  /**
   * @param aDateMidnight the DateMidnight
   */
  public CellDTO(DateMidnight aDateMidnight)
  {
    setValue(mScreenDateFormatter.print(aDateMidnight.toDateTime()));
  }

  /**
   *
   * @param aValue value to display
   * @param aType eg boolean for checkbox
   */
  public CellDTO(String aValue, String aType)
  {
    mValue = aValue;
    mType = aType;
  }

  /**
   * @return the value.
   */
  public String getValue()
  {
    return mValue;
  }

  /**
   * @param aValue The value to set.
   */
  public void setValue(String aValue)
  {
    mValue = aValue;
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
   * @return the cellMap.
   */
  public Map<String, String> getCellMap()
  {
    return mCellMap;
  }


  /**
   * @param aCellMap The cellMap to set.
   */
  public void setCellMap(Map<String, String> aCellMap)
  {
    mCellMap = aCellMap;
  }
}
