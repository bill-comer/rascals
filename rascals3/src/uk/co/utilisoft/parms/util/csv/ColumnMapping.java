package uk.co.utilisoft.parms.util.csv;

public class ColumnMapping
{
  private ColumnMappingName mColumnMappingName;
  private boolean mNullable;



  public ColumnMapping(ColumnMappingName mColumnMappingName, boolean mNullable)
  {
    this.mColumnMappingName = mColumnMappingName;
    this.mNullable = mNullable;
  }

  public ColumnMappingName getColumnMappingName()
  {
    return mColumnMappingName;
  }
  public void setColumnMappingName(ColumnMappingName aColumnName)
  {
    this.mColumnMappingName = aColumnName;
  }
  public boolean isNullable()
  {
    return mNullable;
  }
  public void setNullable(boolean aNullable)
  {
    this.mNullable = aNullable;
  }


}
