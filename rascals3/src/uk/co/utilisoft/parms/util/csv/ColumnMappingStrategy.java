package uk.co.utilisoft.parms.util.csv;

import au.com.bytecode.opencsv.bean.MappingStrategy;

public interface ColumnMappingStrategy<T> extends MappingStrategy<T>
{
  public ColumnMapping[] getColumnMappings();
  
  public boolean isColumnNullable(int col);
}
