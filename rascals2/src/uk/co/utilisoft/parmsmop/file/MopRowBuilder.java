package uk.co.utilisoft.parmsmop.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import uk.co.utilisoft.parms.file.BaseRowBuilder;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.parms.file.dpi.DpiFileExtensions;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public abstract class MopRowBuilder extends BaseRowBuilder
{

  private String mSeperator = "|";

  public String getSeperator()
  {
    return mSeperator;
  }
  
  private String mParticpantId;

  public String getParticpantId()
  {
    return mParticpantId;
  }
  public void setParticpantId(String aParticpantId)
  {
    this.mParticpantId = aParticpantId;
  }
  
  public List<String> buildAllRows(String aParticpantId, List<ParmsReportSummary> activeRows)
  {
    PoolChecksumCalculator checksumCalculator = getChecksumCalculator();
    setParticpantId(aParticpantId);


    List<String> rows = new ArrayList<String>();
    //add header
    rows.add(getHeader());

    
    
    buildBodyOfReport(activeRows, rows);

    //calc checksum for all rows in file
    for (String row : rows)
    {
      checksumCalculator.addLineToCheckSum(row);
    }

    //add footer
    rows.add(getFooter(checksumCalculator));
    return rows;
  }
  
  
  abstract public String getLinePrefix();
  abstract public String getSubRecord(ParmsReportSummary summary);
  abstract public String createLineForSerial(ParmsReportSummary row);
  abstract public String getHeader();
  abstract public String getReportNumber();
  
  public void buildBodyOfReport(List<ParmsReportSummary> activeRows, List<String> rows)
  {
    ParmsReportSummary lastSubRecord = null;
    
    Collections.sort(activeRows);
    
    for (ParmsReportSummary rowToAdd : activeRows)
    {
      
      if (lastSubRecord == null || !rowToAdd.equals(lastSubRecord))
      {
        //New Sub row reequired
        lastSubRecord = rowToAdd;
        rows.add(getSubRecord(lastSubRecord));
      }
      
      // add sub record
      rows.add(createLineForSerial(rowToAdd));
    }
  }
  
  public String getFilename(String aParticipant)
  {
    return aParticipant + getReportNumber() + getLastDigitOfYear() + "." + DpiFileExtensions.getExtension(getMonth());
  }
  
  private int getMonth()
  {
    return new DateTime().minusMonths(1).getMonthOfYear();
  }
  
  String getLastDigitOfYear()
  {
    int year = new DateTime().minusMonths(1).getYear();
    return Integer.toString(year).substring(3);
  }

  
}
