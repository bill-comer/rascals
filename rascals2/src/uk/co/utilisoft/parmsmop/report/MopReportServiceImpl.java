package uk.co.utilisoft.parmsmop.report;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import uk.co.utilisoft.parms.web.util.ResponseOutputStreamWriter;
import uk.co.utilisoft.parmsmop.dao.ParmsMopReportDao;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.file.MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Hm12MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Nm12MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.ReportBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Sp11MopReportBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Sp11MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Sp14MopRowBuilder;
import uk.co.utilisoft.parmsmop.file.sp11.Sp15MopRowBuilder;

@Service("parmsmop.mopReportService")
public class MopReportServiceImpl implements MopReportService<ParmsMopReport>
{

  @Autowired(required = true)
  @Qualifier("parms.mopReportDao")
  private ParmsMopReportDao mParmsMopReportDao;

  //Report Builders
  @Autowired(required = true)
  @Qualifier("parmsmop.sp11ReportBuilder")
  private Sp11MopReportBuilder mSp11MopReportBuilder;
  
  //Row builders
  @Autowired(required = true)
  @Qualifier("parmsmop.sp11RowBuilder")
  private Sp11MopRowBuilder mSp11MopRowBuilder;
  
  @Autowired(required = true)
  @Qualifier("parmsmop.sp14RowBuilder")
  private Sp14MopRowBuilder mSp14MopRowBuilder;
  
  @Autowired(required = true)
  @Qualifier("parmsmop.sp15RowBuilder")
  private Sp15MopRowBuilder mSp15MopRowBuilder;
  
  @Autowired(required = true)
  @Qualifier("parmsmop.hm12RowBuilder")
  private Hm12MopRowBuilder mHm12MopRowBuilder;
  
  @Autowired(required = true)
  @Qualifier("parmsmop.nm12RowBuilder")
  private Nm12MopRowBuilder mNm12MopRowBuilder;

  
  /**
   * Creates new report.
   * Copying data From Parms_Data in to ParmsMopReport
   * @param aSerial
   */
  @Transactional
  @Override
  public ParmsMopReport createNewReport(String aSerial)
  {
    return getReportBuilder(aSerial).buildReport();
  }
  
  
  
  public void getReport(Long aId, HttpServletResponse aResponse) throws Exception
  {
    ParmsMopReport report = mParmsMopReportDao.getById(aId);
    
    List<String> rows = getRowBuilder(report).buildAllRows(getParticipant(), report.getParmsReportSummary());
    
    StringUtils.collectionToDelimitedString(rows, System.getProperty("line.separator"));
    String data = StringUtils.collectionToDelimitedString(rows, System.getProperty("line.separator"));
    
    ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
    
    writer.writePlainFileToResponseOutputStream(aResponse, data, getRowBuilder(report).getFilename(getParticipant()));
  }
  
  private static String mParticipant;
  private String getParticipant()
  {
    if (!StringUtils.hasLength(mParticipant))
    {
      mParticipant = mParmsMopReportDao.getParticipant();
      
    }
    return mParticipant;
  }
  
  MopRowBuilder getRowBuilder(ParmsMopReport report)
  {
    if (report.isSerialSP11()) {
      return mSp11MopRowBuilder;
    }
    else if (report.isSerialSP14()) {
      return mSp14MopRowBuilder;
    }
    else if (report.isSerialSP15()) {
      return mSp15MopRowBuilder;
    }
    else if (report.isSerialHM12()) {
      return mHm12MopRowBuilder;
    }
    else if (report.isSerialNM12()) {
      return mNm12MopRowBuilder;
    }
    else
    {
      throw new UnsupportedOperationException("Failed to get MopRowBuilder - Unexpected Serial Type[" + report.getSerial() + "]");
    }
  }

  ReportBuilder getReportBuilder(String aSerial)
  {
    if (aSerial.toUpperCase().equals(MopSerialTypes.SP11.name())) {
      return mSp11MopReportBuilder;
    }
    /*else if (report.isSerialSP14()) {
      return mSp14MopRowBuilder;
    }
    else if (report.isSerialSP15()) {
      return mSp15MopRowBuilder;
    }
    else if (report.isSerialHM12()) {
      return mHm12MopRowBuilder;
    }
    else if (report.isSerialNM12()) {
      return mNm12MopRowBuilder;
    }*/
    else
    {
      throw new UnsupportedOperationException("Failed to get ReportBuilder - Unexpected Serial Type[" + aSerial + "]");
    }
  }
  
  
  
 
}
