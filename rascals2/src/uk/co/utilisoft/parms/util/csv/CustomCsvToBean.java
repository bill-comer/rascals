package uk.co.utilisoft.parms.util.csv;

import java.beans.PropertyEditor;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.web.util.JodaTimePropertyEditor;
import uk.co.utilisoft.parms.web.util.MpanCorePropertyEditor;

/**
 * Based on CsvToBean
 * @author comerb
 *
 * @param <T>
 */
public class CustomCsvToBean<T> extends BaseCsvToBean<T>
{
  public CustomCsvToBean(String aDateTimeFormat)
  {
    resetEditorMap();
    addJodaDateTimePropertyEditorToMap(aDateTimeFormat);
    addMpanPropertyEditor();
  }

  
  private void addMpanPropertyEditor()
  {
    PropertyEditor mpanPropertyEditor = new MpanCorePropertyEditor();
    addEditorToMap(MPANCore.class, mpanPropertyEditor);
  }


  private void addJodaDateTimePropertyEditorToMap(String aDateTimeFormat)
  {
    DateTimeFormatter fmt = DateTimeFormat.forPattern(aDateTimeFormat);
    PropertyEditor dateTimeEditor = new JodaTimePropertyEditor(fmt);
    addEditorToMap(DateTime.class, dateTimeEditor);
  }


  public List<String> getErrorData()
  {
    return errorData;
  }


}
