package uk.co.utilisoft.parms.util.csv;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.MappingStrategy;

/**
 * Based on CsvToBean
 * @author comerb
 *
 * @param <T>
 */
public class BaseCsvToBean<T> extends CsvToBean<T>
{
  List<String> errorData;;
  long rowNumber;

Map <Class<?>, PropertyEditor> editorMap = null;

public Map<Class<?>, PropertyEditor> getEditorMap()
{
  return editorMap;
}

void resetEditorMap()
{
  if (editorMap == null)
  {
     editorMap = new HashMap<Class<?>, PropertyEditor>();
  }
}

public BaseCsvToBean() {
  rowNumber = 1;
  errorData = new ArrayList<String>();
}

@Override
public List<T> parse(MappingStrategy<T> mapper, Reader reader) {

  return parse(mapper, new CSVReader(reader));
}

public List<T> parse(ColumnMappingStrategy<T> mapper, CSVReader csv) {
    try {
        mapper.captureHeader(csv);
        String[] line;

        List<T> list = new ArrayList<T>();
        while(null != (line = csv.readNext()) ) {
          String actualLine = arrayToString(line);
          if (actualLine.trim().length() > 0)
          {
            T obj = processLine(mapper, line);
            if (obj != null) {
              list.add(obj);
            }
          }
          rowNumber++;
        }
        return list;
    } catch (Exception e) {
        throw new RuntimeException("Error parsing CSV!", e);
    }
}

  public String arrayToString(String[] stringarray)
  {
    String str = " ";
    for (int i = 0; i < stringarray.length; i++)
    {
      str = str + stringarray[i];
    }
    return str;
  }

protected T processLine(ColumnMappingStrategy<T> mapper, String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
    T bean = mapper.createBean();

    String errorText = new String();

    if (mapper.getColumnMappings().length > line.length) {
      String numCols = line.length > 1 ? "columns" : "column";
      errorText = "ROW[" + rowNumber + "], Only " + line.length + " " + numCols + ", expected "
        + mapper.getColumnMappings().length;
      errorData.add(errorText );
      return null;
    }

    for(int col = 0; col < line.length; col++) {
        String value = line[col];

        PropertyDescriptor prop = mapper.findDescriptor(col);
        if (null != prop) {
          try {
            if (StringUtils.isBlank(value) && !mapper.isColumnNullable(col)) {
              throw new Exception("");
            }
            if (StringUtils.isBlank(value) && mapper.isColumnNullable(col)) {
              continue;
            }

            Object obj = convertValue(value, prop);
            prop.getWriteMethod().invoke(bean, new Object[] {obj});
          } catch (Exception e) {
            String valTxt = value.trim().length() > 0 ? ("[" + value.trim() + "] is Invalid") : "is Missing";
            errorText += ", COL[" + (col + 1) + "]:" + mapper.getColumnMappings()[col].getColumnMappingName().getLabel()
              + " value " + valTxt;
          }
        }
    }

    if (errorText.length() > 0) {
      errorData.add("ROW[" + rowNumber + "]" + errorText);

      // bug#5678 send back useful message in bean instead of null
      return null;
    }

    return bean;
}



@Override
protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
    PropertyEditor editor = getPropertyEditor(prop);
    Object obj = value;
    if (null != editor) {
        editor.setAsText(value.trim());
        obj = editor.getValue();
    }
    return obj;
}

private PropertyEditor getPropertyEditorValue(Class<?> cls)
{
  resetEditorMap();

   PropertyEditor editor = editorMap.get(cls);

   if (editor == null)
   {
      editor = PropertyEditorManager.findEditor(cls);
      addEditorToMap(cls, editor);
   }

   return editor;
}

void addEditorToMap(Class<?> cls, PropertyEditor editor)
{
  if (editor != null)
   {
      editorMap.put(cls, editor);
   }
}


/*
 * Attempt to find custom property editor on descriptor first, else try the propery editor manager.
 */
protected PropertyEditor getPropertyEditor(PropertyDescriptor desc) throws InstantiationException, IllegalAccessException {
    Class<?> cls = desc.getPropertyEditorClass();
    if (null != cls) return (PropertyEditor) cls.newInstance();
    return getPropertyEditorValue(desc.getPropertyType());
}

}

