package uk.co.utilisoft.parmsmop.file.sp11;

import org.springframework.stereotype.Service;

import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;
import uk.co.utilisoft.parmsmop.file.MopPoolHeader;
import uk.co.utilisoft.parmsmop.file.MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.MopSubRecord;


@Service("parmsmop.sp14RowBuilder")
public class Sp14MopRowBuilder extends MopRowBuilder
{

  @Override
  public String getLinePrefix()
  {
    return "X14";
  }

  public Sp14MopRowBuilder()
  {
  }



  

  @Override
  public String createLineForSerial(ParmsReportSummary row)
  {
    return createLineForValues(getLinePrefix(), row.getReportString2(), row.getReportString3(), row.getReportString4(), row.getReportString5(), row.getReportString6(),
                row.getReportString7(), row.getReportString8(), row.getReportString9(), row.getReportString10()
                );
  }

  String createLineForValues(String aSp04Prefix, String aGsp, String aS3, String aS4, String aS5, String aS6, String aS7, String aS8, String aS9, String aS10)
  {
    return aSp04Prefix  + getSeperator()
      + aGsp + getSeperator()
      + aS3 + getSeperator()
      + aS4 + getSeperator()
      + aS5 + getSeperator()
      + aS6 + getSeperator()
      + aS7 + getSeperator()
      + aS8 + getSeperator()
      + aS9 + getSeperator()
      + aS10 ;
  }

  @Override
  public String getSubRecord(ParmsReportSummary summary)
  {
    MopSubRecord subRecord = new MopSubRecord();
    return subRecord.create(summary.getParticipantId(), summary.isHalfHourlyIndicatorAsBoolean());
  }

  @Override
  public String getHeader()
  {
    Sp14PoolHeader header = new Sp14PoolHeader();
    return header.createHeader(getParticpantId());
  }

  @Override
  public String getReportNumber()
  {
    return "227";
  }
}


class Sp14PoolHeader extends MopPoolHeader
{
  @Override
  public String getFileType()
  {
    String reportNumber = new Sp14MopRowBuilder().getReportNumber();
    return "P0" + reportNumber + "001";
  }
  
}
