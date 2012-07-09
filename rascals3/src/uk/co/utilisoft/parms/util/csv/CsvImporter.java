package uk.co.utilisoft.parms.util.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import uk.co.utilisoft.parms.file.p0028.P0028ImporterException;
import au.com.bytecode.opencsv.CSVReader;

public class CsvImporter<T>
{
  private List<String> mErrorData;

  private Class mClazz;
  ColumnMapping[] mColumnMappings;
  String mDateFormat = "dd/MM/yyyy";

  public CsvImporter(Class aClazz, ColumnMapping[] aColumnMappings, String aDateFormat)
  {
    mClazz = aClazz;
    mColumnMappings = aColumnMappings;
    mDateFormat = aDateFormat;
  }


  public CsvImporter(Class aClazz, ColumnMapping[] aColumnMappings)
  {
    mClazz = aClazz;
    mColumnMappings = aColumnMappings;
  }

  public List<T> importFile(InputStream aFIS) throws IOException
  {
    CSVReader reader = new CSVReader(new InputStreamReader(aFIS));

    NullableColumnPositionMappingStrategy<T> strat = new NullableColumnPositionMappingStrategy<T>();

    strat.setType(mClazz);

    strat.setColumnMapping(mColumnMappings);

    CustomCsvToBean<T> csvConverter = new CustomCsvToBean<T>(mDateFormat);

    List<T> list = null;
    try {
      list = csvConverter.parse(strat, reader);
      setErrorData(csvConverter.getErrorData());
    } catch (Exception e) {
      throw new P0028ImporterException("Parse of file failed", e);
    }
    return list;
  }

  public List<String> getErrorData()
  {
    return mErrorData;
  }

  public void setErrorData(List<String> aErrorData)
  {
    this.mErrorData = aErrorData;
  }
}

