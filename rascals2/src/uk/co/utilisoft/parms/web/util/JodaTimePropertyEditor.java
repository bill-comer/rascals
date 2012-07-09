package uk.co.utilisoft.parms.web.util;

import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Kirk Hawksworth
 * @version 1.0
 */
public class JodaTimePropertyEditor extends PropertyEditorSupport
{
  private DateTimeFormatter mDateTimeFormatter;

  public JodaTimePropertyEditor()
  {
    super();
  }

  public JodaTimePropertyEditor(Object source)
  {
    super(source);
  }

  public JodaTimePropertyEditor(DateTimeFormatter aDateTimeFormatter)
  {
    mDateTimeFormatter = aDateTimeFormatter;
  }

  /**
   * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
   */
  @Override
  public void setAsText(String aText) throws IllegalArgumentException
  {
    if (aText == null || aText.equals(""))
    {
      setValue(null);
    }
    else
    {
      setValue(mDateTimeFormatter.parseDateTime(aText));
    }
  }

  /**
   * @see java.beans.PropertyEditorSupport#getAsText()
   */
  @Override
  public String getAsText()
  {
    if (getValue() == null) return null;
    DateTime dateTime = (DateTime) getValue();
    return mDateTimeFormatter.print(dateTime.getMillis());
  }
}
