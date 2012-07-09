package uk.co.utilisoft.parms.file.p0028;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.P0028FileDao;
import uk.co.utilisoft.parms.dao.P0028FileDataDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.util.FileUtil;
import uk.co.utilisoft.parms.util.csv.ColumnMapping;
import uk.co.utilisoft.parms.util.csv.ColumnMappingName;
import uk.co.utilisoft.parms.util.csv.CsvImporter;

import static uk.co.utilisoft.parms.domain.Audit.TYPE;

/**
 *
 */
@Service("parms.p0028Importer")
public class P0028Importer
{
  private int mDayThresholfForParmsReportingPeriod = 7;

  @Autowired(required = true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDao mAfmsMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDao mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.sp04Validator")
  private Sp04Validator mSp04Validator;

  @Autowired(required = true)
  @Qualifier("parms.p0028ActiveDao")
  private P0028ActiveDao mP0028ActiveDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDao mAFMSMeterDao;

  /**
   * @param aP0028ActiveDao the active P0028 Dao
   */
  public void setP0028ActiveDao(P0028ActiveDao aP0028ActiveDao)
  {
    this.mP0028ActiveDao = aP0028ActiveDao;
  }

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDataDao")
  private P0028FileDataDao mP0028FileDataDao;



  /**
   * @param aSupplier the supplier
   * @param aFilePath the file path
   * @param aFileInputStream the file input stream
   * @param aP0028Received the P0028
   * @return the P0028 upload status and P0028Actives for the Data Collector in the file uploaded
   */
  @Transactional()
  @ParmsAudit(auditType = TYPE.P0028_UPLOAD)
  public Map<UploadStatus, Map<String, IterableMap<String, P0028Active>>> importer(Supplier aSupplier, String aFilePath,
                                                                                   InputStream aFileInputStream,
                                                                                   DateTime aP0028Received)
  {
    Map<UploadStatus, Map<String, IterableMap<String, P0028Active>>> statusInfo
      = new HashMap<UploadStatus, Map<String, IterableMap<String, P0028Active>>>();
    Map<String, IterableMap<String, P0028Active>> dcP0028ActiveMpans
      = new HashMap<String, IterableMap<String, P0028Active>>();

    P0028File p0028File =  buildP0028File(aSupplier, aFilePath, aFileInputStream, aP0028Received);

    if (getErrorData().size() == 0)
    {
      mP0028FileDao.makePersistent(p0028File);

      // only save p0028file as clob if data has been successfully read and a P0028File created
      P0028FileData p0028FileData = buildP0028FileData(aFileInputStream, p0028File);
      mP0028FileDataDao.makePersistent(p0028FileData);

      //list of all Active before upload
      IterableMap<String, P0028Active> allCurrentlyActives = mP0028ActiveDao.getAllForSupplier(aSupplier);
      UploadStatus status =  mSp04Validator.validate(p0028File, aSupplier);

      updateActiveP0028s(p0028File, allCurrentlyActives);
      //allCurrentlyActives now only contains one that were originally in the DB and not in new list

      if (status.equals(UploadStatus.UPLOADED_WITH_ROW_ERRORS))
      {
        p0028File.setErrored(true);
        mP0028FileDao.makePersistent(p0028File);
      }

      dcP0028ActiveMpans.put(p0028File.getDcAgentName(), allCurrentlyActives);
      statusInfo.put(status, dcP0028ActiveMpans);
    }

    // defaults status for a failed p0028 file uploads
    if (statusInfo.isEmpty())
    {
      statusInfo.put(UploadStatus.FAILED_STRUCTURAL_VALIDATION, dcP0028ActiveMpans);
    }

    return statusInfo;

  }

  /**
   * @param aInputStream the input stream
   * @param aP0028File the P0028 File
   * @return the Data for P0028 File
   */
  P0028FileData buildP0028FileData(InputStream aInputStream, P0028File aP0028File)
  {
    P0028FileData data = new P0028FileData();

    if (aP0028File == null)
    {
      throw new P0028ImporterException("Failed to create P0028FileData. A P0028File is required");
    }

    data.setP0028File(aP0028File);

    try
    {
      data.setData(new String(FileUtil.getBytes(aInputStream)));
    }
    catch (IOException ioe)
    {
      throw new P0028ImporterException("Failed to create P0028FileData for P0028File.pk " + aP0028File.getPk());
    }

    return data;
  }

  /**
   * bug#6152 - only associate P0028Active records for P0028File.p0028Data that contain no upload errors.
   *
   * persist new P0028s at Active table
   * and remove ones found from the allCurrentlyActives list
   * @param aP0028File the P0028 File
   * @param allCurrentlyActives the current P0028Active records
   */
  void updateActiveP0028s(P0028File aP0028File, IterableMap<String, P0028Active> allCurrentlyActives)
  {
    for (P0028Data p0028Data : aP0028File.getP0028Data())
    {
      DataCollector dc = null;   //TODO can not remember if we really need this so leave it null for the moment

      // bug#6152 - only create/update P0028Active records that contain no upload errors
      if (!p0028Data.hasErrors())
      {
        mP0028ActiveDao.storeNewP0028Active(p0028Data, dc, p0028Data.getDcAgentName());
      }

      //hashmap is not using equals method to remove
      //try String in hash map

      if (allCurrentlyActives.containsKey(p0028Data.getMpan().getValue()))
      {
        allCurrentlyActives.remove(p0028Data.getMpan().getValue());
      }

    }
  }

  /**
   * Builds the contents of the P0028 file
   * @param supplier the supplier
   * @param aFileName the file name
   * @param aFileInputStream the file input stream
   * @param aP0028Received the P0028 File
   * @return the P0028 File
   */
  P0028File buildP0028File(Supplier supplier, String aFileName, InputStream aFileInputStream, DateTime aP0028Received)
  {
    DateTime prpTime = aP0028Received;
    if (aP0028Received.getDayOfMonth() < mDayThresholfForParmsReportingPeriod)
    {
      // subtract a month as this period will be for the previous month
      prpTime = aP0028Received.minusMonths(1);
    }

    //get the rows of the File
    P0028File p0028File = new P0028File(aFileName, "NEED THE DC NAME HERE", supplier, null, aP0028Received,
                                        getReportingPeriod(prpTime));
    try
    {
      p0028File.setP0028Data(getRowsForFile(aFileInputStream, p0028File));
    }
    catch (IOException e)
    {
      throw new P0028ImporterException("Problem importing File:" + aFileName, e);
    }

    if (getErrorData().size() > 0)
    {
      return null;
//      throw new P0028ImporterException("Errors importing File:" + aFileName);
    }

    if (p0028File.getP0028Data().size() > 0)
    {
      p0028File.setDcAgentName(p0028File.getP0028Data().get(0).getDcAgentName());

      // set mpan uniq id for sp04 generation process
      setMpanUniqId(p0028File.getP0028Data());

      // set a reference to currently active mpan's J0082(Measurement Classification). This is a requirement for
      // sp04 elligible mpans and sp04 file generation actions which need a reference to the currently active mpan's
      // J0082(Measurement Classification) before a change was made
      setCurrentMeasurementClassification(p0028File.getP0028Data());
    }

    if (areThereAnyUploadsNewerThanThis(supplier, p0028File.getDcAgentName(), aP0028Received))
    {
      DateTimeFormatter ddMMyyyyFmt = DateTimeFormat.forPattern("dd/MM/yyyy");
      String problem = "There is already a P0028 Uploaded for DataCollector["
          + p0028File.getDcAgentName() + "] that is newer than " + ddMMyyyyFmt.print(aP0028Received) + ".";
      getErrorData().add(problem);
      return null;
    }

    if (p0028File.getP0028Data().size() == 0)
    {
      getErrorData().add("There were no rows found in the P0028 file.");
      return null;
    }

    p0028File.setDcAgentName(p0028File.getP0028Data().get(0).getDcAgentName());
    return p0028File;
  }

  private void setMpanUniqId(List<P0028Data> aP0028Datas)
  {
    for (P0028Data p28Data : aP0028Datas)
    {
      if (StringUtils.isNotBlank(p28Data.getMeterSerialId()))
      {
        AFMSMeter meter =  mAFMSMeterDao.getLatestMeterForMeterSerialId(p28Data.getMeterSerialId());
        if (meter != null)
        {
          p28Data.setMpanUniqId(meter.getMpanLinkId());
        }
      }
    }
  }

  private void setCurrentMeasurementClassification(List<P0028Data> aP0028Datas)
  {
    for (P0028Data p28Data : aP0028Datas)
    {
      if (StringUtils.isNotBlank(p28Data.getMeterSerialId()))
      {
        AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialId(p28Data.getMeterSerialId());

        if (meter != null)
        {
          p28Data.setCurrentMeasurementClassification(meter.getMpan().getMeasurementClassification());
        }
      }
    }
  }

  private ParmsReportingPeriod getReportingPeriod(DateTime p0028ReceiptTime)
  {
    return new ParmsReportingPeriod(p0028ReceiptTime.toDateMidnight());
  }

  /**
   * @param aSupplier the supplier
   * @param aDcAgentName the data collector agent name
   * @param aP0028Received the time the P0028 File was received
   * @return true if there are any newer P0028s
   */
  boolean areThereAnyUploadsNewerThanThis(Supplier aSupplier, String aDcAgentName,
                                          DateTime aP0028Received)
  {
    return mP0028FileDao.areThereAnyUploadsNewerThanThis(aSupplier, aDcAgentName, aP0028Received);
  }

  /**
   * gets rows from file
   *
   * @param aP0028FIS the P0028 File Input Stream
   * @param aP0028File the P0028 File
   * @return the Collection of P0028 Data records for the P0028 File
   * @throws IOException
   */
  List<P0028Data> getRowsForFile(InputStream aP0028FIS, P0028File aP0028File) throws IOException
  {
    List<P0028Data> list = importFile(aP0028FIS);

    for (P0028Data p0028Data : list)
    {
      p0028Data.setP0028File(aP0028File);
    }
    return list;

  }

  private List<String> mErrorData;

  /**
   * @return the List of errors
   */
  public List<String> getErrorData()
  {
    return mErrorData;
  }

  /**
   * @param aErrorData the errors
   */
  public void setErrorData(List<String> aErrorData)
  {
    this.mErrorData = aErrorData;
  }

  /**
   * @param aP0028FIS the P0028 File Input Stream
   * @return the P0028 Data errors
   * @throws IOException
   */
  List<P0028Data> importFile(InputStream aP0028FIS) throws IOException
  {
    boolean isColumnNullable = false;
    ColumnMapping[] columnMappings = new ColumnMapping[]
       {
        new ColumnMapping(ColumnMappingName.DC_AGENT_NAME, isColumnNullable),
        new ColumnMapping(ColumnMappingName.MAX_DEMAND, isColumnNullable),
        new ColumnMapping(ColumnMappingName.METER_SERIAL_ID, isColumnNullable),
        new ColumnMapping(ColumnMappingName.MPAN, isColumnNullable),
        new ColumnMapping(ColumnMappingName.READING_DATE, isColumnNullable)
       };

    CsvImporter<P0028Data> csvConverter = new CsvImporter<P0028Data>(P0028Data.class, columnMappings);

    List<P0028Data> list = null;
    try
    {
      list = csvConverter.importFile(aP0028FIS);
      setErrorData(csvConverter.getErrorData());
    }
    catch (Exception e)
    {
      throw new P0028ImporterException("Parse of file failed", e);
    }
    return list;
  }

  /**
   * @param aAFMSMeterDao the afms meter dao to set
   */
  public void setAFMSMeterDao(AFMSMeterDao aAFMSMeterDao)
  {
    mAFMSMeterDao = aAFMSMeterDao;
  }
}

/**
 *
 */
class P0028ParseReport
{
  List<P0028Data> mProcessedRows;
  List<String> aRowErrors;
}