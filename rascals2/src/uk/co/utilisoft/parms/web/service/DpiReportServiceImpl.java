package uk.co.utilisoft.parms.web.service;

import static uk.co.utilisoft.parms.util.DateUtil.formatLongDate;
import static uk.co.utilisoft.parms.web.controller.WebConstants.DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT;
import static uk.co.utilisoft.parms.web.controller.WebConstants.MONTH_YEAR_DATE_FORMAT;
import static uk.co.utilisoft.parms.domain.Audit.TYPE;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.AgentDao;
import uk.co.utilisoft.parms.dao.ConfigurationParameterDao;
import uk.co.utilisoft.parms.dao.DpiFileDao;
import uk.co.utilisoft.parms.dao.DpiFileDataDao;
import uk.co.utilisoft.parms.dao.GridSupplyPointDao;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;


/**
 * @author Philip Lau
 * @version 1.0
 */
@Service("parms.dpiReportService")
public class DpiReportServiceImpl implements AdminService, DpiReportService
{
  @Autowired(required = true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDao mDpiFileDao;


  @Autowired(required = true)
  @Qualifier("parms.dpiFileDataDao")
  private DpiFileDataDao mDpiFileDataDao;


  @Autowired(required = true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDao mGridSupplyPointDao;


  @Autowired(required = true)
  @Qualifier("parms.agentDao")
  private AgentDao mAgentDao;

  @Autowired(required = true)
  @Qualifier("parms.configurationParameterDao")
  private ConfigurationParameterDao mConfigurationParameterDao;

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.AdminService#getAllSortedRecords()
   */
  @Override
  public List<AdminListDTO> getAllSortedRecords()
  {
    List<AdminListDTO> records = new ArrayList<AdminListDTO>();
    List<DpiFile> dpiFileRecs = mDpiFileDao.getAll();

    for (DpiFile dpiFileRec : dpiFileRecs)
    {
      List<Object> pk = new ArrayList<Object>();
      pk.add(dpiFileRec.getPk());

      List<Object> values = new ArrayList<Object>();
      values.add(formatLongDate(MONTH_YEAR_DATE_FORMAT, dpiFileRec.getReportingPeriod().getStartOfNextMonthInPeriod()));
      values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, dpiFileRec.getDateCreated()));
      values.add(dpiFileRec.getFileName());

      records.add(new AdminListDTO(pk, values));
    }

    return records;
  }

  /**
   * @param aDao the DpiFileDao
   */
  public void setDpiFileDao(DpiFileDao aDao)
  {
    mDpiFileDao = aDao;
  }

  @Override
  public List<DpiFileData> getByDpiFilePk(Long aDpiFilePk)
  {
    return mDpiFileDataDao.getByDpiFilePk(aDpiFilePk);
  }

  @Override
  public DpiFile getDpiFile(Long dpiFilePk)
  {
    return mDpiFileDao.getById(dpiFilePk);
  }

  @Override
  public List<GridSupplyPoint> getAllGSPsDpi(DpiFile aDpiFile)
  {
    return mGridSupplyPointDao.getAllGSPsDpi(aDpiFile);
  }

  @Override
  public List<GenericAgent> getAgents(DpiFile aDpiFile)
  {
    return new ArrayList<GenericAgent>(mAgentDao.getAllAgents(aDpiFile));
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.DpiReportService#replicateDpiFile(uk.co.utilisoft.parms.domain.DpiFile)
   */
  @Override
  @Transactional
  @ParmsAudit(auditType = TYPE.DPI_REPLICATE)
  public DpiFile replicateDpiFile(DpiFile aDpiFile)
  {
    DpiFile replicatedDpiFile = aDpiFile.replicate();
    mDpiFileDao.makePersistent(replicatedDpiFile);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();
    //replicate Agents
    List<GenericAgent> agents = new ArrayList<GenericAgent>(getAgents(aDpiFile));
    for (GenericAgent agent : agents)
    {
      GenericAgent replicatedAgent = agent.replicate(replicatedDpiFile, replicatedGSPs);
      mAgentDao.makePersistent(replicatedAgent);
    }

    return replicatedDpiFile;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.ParmsReportService#getAgentById(java.lang.Long)
   */
  @Override
  public GenericAgent getAgentById(Long aAgentId)
  {
    return mAgentDao.getAgentById(aAgentId);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.DpiReportService#downloadDpiFileReport(java.lang.Long)
   */
  @Override
  public String downloadDpiFileReport(Long aDpiFilePk) throws Exception
  {
    ConfigurationParameter fileLoc
      = mConfigurationParameterDao.getByName(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION);

    if (fileLoc == null || StringUtils.isBlank(fileLoc.getValue()))
    {
      throw new RuntimeException("The File Location must be configured for "
                                 + ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION);
    }

    List<DpiFileData> datas =  mDpiFileDataDao.getByDpiFilePk(aDpiFilePk);
    DpiFileData dataRec = datas.isEmpty() ? null : datas.get(0);

    // append prefix to original file name
    DateTime now = new DateTime();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");

    // dpi data file doesn't exist
    if (dataRec == null || StringUtils.isBlank(dataRec.getData()))
    {
      return null;
    }

    String filename =  fileLoc.getValue() + "\\DpiFileReport_" + now.toString(fmt) + "_" + dataRec.getDpiFile()
      .getFileName();

    FileWriter writer = new FileWriter(filename);
    writer.write(dataRec.getData());
    writer.flush();
    writer.close();
    return filename;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.DpiReportService#getFileToDownload(java.lang.String)
   */
  @ParmsAudit(auditType = TYPE.DPI_DOWNLOAD_FILE)
  public DpiFileData getFileToDownload(String aDpiFilePk)
  {
    List<DpiFileData> datas =  mDpiFileDataDao.getByDpiFilePk(Long.parseLong(aDpiFilePk));
    DpiFileData dataRec = datas.isEmpty() ? null : datas.get(0);
    return dataRec;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.DpiReportService#isIncludedInDpiReport(uk.co.utilisoft.parms.domain
   * .GridSupplyPoint, boolean)
   */
  @Override
  public Boolean isIncludedInDpiReport(GridSupplyPoint aGridSuppyPoint, boolean aUseMonthTData)
  {
    // display month T data
    if (aUseMonthTData)
    {
      if (aGridSuppyPoint.isHalfHourMpans2ndMonth() || aGridSuppyPoint.isNonHalfHourMpans2ndMonth())
      {
        return Boolean.TRUE;
      }
    }
    else
    {
      // display month T minus 1 data
      if (aGridSuppyPoint.isHalfHourMpansFirstMonth() || aGridSuppyPoint.isNonHalfHourMpansFirstMonth())
      {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }
}
