package uk.co.utilisoft.parms.web.service.p0028;

import static uk.co.utilisoft.parms.util.DateUtil.formatLongDate;
import static uk.co.utilisoft.parms.web.controller.WebConstants.DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT;
import static uk.co.utilisoft.parms.web.controller.WebConstants.MONTH_YEAR_DATE_FORMAT;
import static uk.co.utilisoft.parms.domain.Audit.TYPE;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.P0028FileDao;
import uk.co.utilisoft.parms.dao.P0028FileDataDao;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.P0028UploadError;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;
import uk.co.utilisoft.parms.web.service.AdminService;
import uk.co.utilisoft.parms.web.util.ResponseOutputStreamWriter;


/**
 * @author Philip Lau
 * @version 1.0
 */
@Service("parms.p0028ReportService")
public class P0028ReportServiceImpl implements AdminService, P0028ReportService
{
  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDao mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDataDao")
  private P0028FileDataDao mP0028FileDataDao;

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.p0028.P0028ReportService#getP0028UploadWarnings(java.lang.String,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  @ParmsAudit(auditType = TYPE.P0028_UPLOAD_DOWNLOAD_WARNINGS)
  public String downloadP0028UploadWarnings(Long aP0028FileId,
                                            HttpServletResponse aResponse)
      throws Exception
  {
    P0028FileData p28FileData = mP0028FileDataDao.getByP0028FileId(aP0028FileId);

    if (p28FileData != null)
    {
      DateTime now = new DateTime();
      DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");
      String filename =  "p0028FileUploadWarnings." + now.toString(fmt) + ".csv";

      if (StringUtils.isNotBlank(p28FileData.getWarnings()))
      {
        ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
        writer.writeCsvFileToResponseOutputStream(aResponse, p28FileData.getWarnings(), filename);
        return filename;
      }
    }

    return null;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.AdminService#getAllSortedRecords()
   */
  @Override
  public List<AdminListDTO> getAllSortedRecords()
  {
    List<AdminListDTO> records = new ArrayList<AdminListDTO>();
    List<P0028File> fileRecs = mP0028FileDao.getAll();

    for (P0028File fileRec : fileRecs)
    {
      List<Object> pk = new ArrayList<Object>();
      pk.add(fileRec.getPk());

      List<Object> values = new ArrayList<Object>();
      values.add(fileRec.getSupplier().getSupplierId());
      values.add(fileRec.getDcAgentName());
      values.add(formatLongDate(MONTH_YEAR_DATE_FORMAT, fileRec.getReportingPeriod().getStartOfNextMonthInPeriod()));
      values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, fileRec.getDateImported()));
      values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, fileRec.getReceiptDate()));
      values.add(fileRec.getFilename());

      records.add(new AdminListDTO(pk, values));
    }

    return records;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.p0028.P0028ReportService#getP0028File(java.lang.Long)
   */
  @Override
  public P0028File getP0028File(Long aFilePk)
  {
    return mP0028FileDao.getById(aFilePk);
  }

  @Override
  @ParmsAudit(auditType = TYPE.P0028_DOWNLOAD_ERROR_REPORT)
  public void downloadErrorReport(String aP0028FileId, HttpServletResponse aResponse) throws Exception
  {
    DateTime now = new DateTime();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");
    String filename = "P0028UploadErrorReport." + now.toString(fmt) + ".csv";

    StringBuffer data = new StringBuffer();

    {
      P0028File p0028File =  mP0028FileDao.getById(new Long(aP0028FileId));

      //header
      data.append("P0028 Upload Error Report\n\n");
      data.append("Supplier, DC, Reporting Period, Filename, Import Date\n");
      data.append(p0028File.getSupplier().getSupplierId()
          + "," + p0028File.getDcAgentName()
          + "," + p0028File.getReportingPeriod()
          + "," + p0028File.getFilename()
          + "," + p0028File.getDateImported().toString(fmt) + "\n\n");
      data.append("MPAN, Meter ID, Failure Reason\n");

      for (P0028Data p0028Data : p0028File.getP0028Data())
      {
        if (p0028Data.hasErrors())
        {
          for (P0028UploadError p0028UploadError : p0028Data.getP0028UploadError())
          {
            data.append(p0028Data.getMpan()
                + "," + p0028Data.getMeterSerialId()
                + ","  + p0028UploadError.getFailureReason().getDescription() + "\n");
          }
        }
      }
    }


    ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
    writer.writeCsvFileToResponseOutputStream(aResponse, data.toString(), filename);

  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.p0028.P0028ReportService#getP0028FileData(java.lang.Long)
   */
  @Override
  public P0028FileData getP0028FileData(Long aP0028FileId)
  {
    return mP0028FileDataDao.getByP0028FileId(aP0028FileId);
  }
}
