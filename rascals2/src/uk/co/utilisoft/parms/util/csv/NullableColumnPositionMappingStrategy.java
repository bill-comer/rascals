package uk.co.utilisoft.parms.util.csv;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

public class NullableColumnPositionMappingStrategy<T> extends ColumnPositionMappingStrategy<T> implements ColumnMappingStrategy<T>
{
  protected ColumnMapping[] columnMapping = new ColumnMapping[] {};
  public void captureHeader(CSVReader reader) throws IOException {
    //do nothing, first line is not header
    reader.readNext();
  }

  protected String getColumnName(int col) {
    return (null != columnMapping && col < columnMapping.length) ? columnMapping[col].getColumnMappingName().getName() : null ;
  }

  /*
   * defaults to false
   */
  public boolean isColumnNullable(int col) {
    return (null != columnMapping && col < columnMapping.length) ? columnMapping[col].isNullable() : false ;
  }

  public ColumnMapping[] getColumnMappings() {
      return columnMapping;
  }

  public void setColumnMapping(ColumnMapping[] columnMapping) {
      this.columnMapping = columnMapping;
  }
}
