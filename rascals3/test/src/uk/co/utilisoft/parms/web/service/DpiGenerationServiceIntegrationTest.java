package uk.co.utilisoft.parms.web.service;

import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.afms.dao.AFMSAgentDaoHibernate;
import uk.co.utilisoft.afms.dao.AFMSAgentHistoryDaoHibernate;
import uk.co.utilisoft.afms.dao.AFMSMpanDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDaoHibernate;
import uk.co.utilisoft.parms.dao.DpiFileDaoHibernate;
import uk.co.utilisoft.parms.dao.DpiFileDataDaoHibernate;
import uk.co.utilisoft.parms.dao.GridSupplyPointDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDaoHibernate;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.dpi.AgentBuilder;
import uk.co.utilisoft.parms.file.dpi.DpiDataCreator;
import uk.co.utilisoft.parms.file.dpi.DpiFileBuilder;
import uk.co.utilisoft.parms.file.dpi.DpiFileDataWriterImpl;
import uk.co.utilisoft.parms.file.dpi.DpiFileProcess;

/**
 * @author Philip Lau
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/test-parms.xml"})
public class DpiGenerationServiceIntegrationTest
{
  private AFMSMpanDaoHibernate mAFMSMPanDao;
  private SupplierDaoHibernate mSupplierDao;
  private DpiFileDaoHibernate mDpiFileDao;
  private GridSupplyPointDaoHibernate mGridSupplyPointDao;
  private AFMSAgentHistoryDaoHibernate mAFMSAgentHistoryDao;
  private AFMSAgentDaoHibernate mAfmsAgentDao;
  private AgentDaoHibernate mAgentDao;
  private DpiFileDataWriterImpl mDpiFileDataWriter;
  private DpiFileDataDaoHibernate mDpiFileDataDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileProcess")
  private DpiFileProcess mDpiFileProcess;

  @Autowired(required=true)
  @Qualifier("parms.dpiDataCreator")
  private DpiDataCreator mDpiDataCreator;


  @Autowired(required=true)
  @Qualifier("parms.agentBuilder")
  private AgentBuilder mAgentBuilder;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileBuilder")
  private DpiFileBuilder mDpiFileBuilder;

  private DpiGenerationService mDpiGenerationService;

  @Test
  @SuppressWarnings("unchecked")
  public void generateParmsReport()
  {
    List<String> suppliers = new ArrayList<String>();
    suppliers.add("OVOE");
    Supplier supplier = new Supplier(suppliers.get(0));
    supplier.setPk(1L);
    supplier.setVersion(0L);
    supplier.setLastUpdated(new DateTime());
    DateMidnight dec2010start = new DateMidnight(2010, 12, 1);
    DateTime dec2010end = new DateTime(2010, 12, 1, 23, 59, 59, 001);
    ParmsReportingPeriod reportPeriod = new ParmsReportingPeriod(dec2010start);
    ParmsReportingPeriod reportPeriodDec2010 = new ParmsReportingPeriod(new DateMidnight(2010, 12, 1));
    ParmsReportingPeriod reportPeriodJan2011 = new ParmsReportingPeriod(new DateMidnight(2011, 01, 1));
    DpiFile dpiFile = new DpiFile(reportPeriod, supplier);
    dpiFile.setFileName("OVOE1350.JAN");
    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", dpiFile);
    gsp.setLastUpdated(new DateTime());
    gsp.setHalfHourMpansFirstMonth(true);
    gsp.setHalfHourMpans2ndMonth(false);
    gsp.setPk(1L);
    gsp.setVersion(0L);
    gsps.add(gsp);

    Map<String, GridSupplyPoint> builderGsps = new HashMap<String, GridSupplyPoint>();
    builderGsps.put(gsp.getName(), gsp);

    List<AFMSMpan> mpans = new ArrayList<AFMSMpan>();
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(dec2010start.toDateTime());
    mpan.setEffectiveToDate(dec2010end);
    mpan.setLastUpdated(new DateTime());
    mpan.setMeasurementClassification("C");
    mpan.setMpanCore("2000011644634");
    mpan.setGridSupplyPoint("_A");
    mpans.add(mpan);

    DpiFile expDpiFile = new DpiFile(reportPeriodDec2010, supplier);
    DpiFileData expData = new DpiFileData("ZHD|P0135001|X|OVOE|Z|POOL|20110125170748", expDpiFile);

    expect(mAFMSMPanDao.getSupplierIdsForTwoMonths(reportPeriod)).andReturn(suppliers).anyTimes();
    expect(mSupplierDao.getSupplier(suppliers.get(0))).andReturn(supplier).anyTimes();
    expect(((UtilisoftGenericDao) mDpiFileDao).makePersistent(dpiFile)).andReturn(dpiFile).anyTimes();
    expect(mAFMSMPanDao.getActiveMpans(reportPeriodDec2010, supplier, true)).andReturn(mpans).anyTimes();
    expect(mAFMSMPanDao.getActiveMpans(reportPeriodDec2010, supplier, false)).andReturn(mpans).anyTimes();
    expect(mGridSupplyPointDao.batchMakePersistent(gsps)).andReturn(gsps).anyTimes();
    expect(mAfmsAgentDao.getDataCollectorAgents(reportPeriodDec2010, true)).andReturn(new MultiHashMap<String, AFMSAgent>()).anyTimes();
    expect(mAfmsAgentDao.getDataCollectorAgents(reportPeriodDec2010, false)).andReturn(new MultiHashMap<String, AFMSAgent>()).anyTimes();
    expect(mAfmsAgentDao.getMOPAgents(reportPeriodDec2010, true)).andReturn(new MultiHashMap<String, AFMSAgent>()).anyTimes();
    expect(mAfmsAgentDao.getMOPAgents(reportPeriodDec2010, false)).andReturn(new MultiHashMap<String, AFMSAgent>()).anyTimes();
    expect(mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(reportPeriodDec2010, supplier, true))
      .andReturn(new MultiHashMap<String, AFMSAgentHistory>()).anyTimes();
    expect(mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(reportPeriodDec2010, supplier, false))
      .andReturn(new MultiHashMap<String, AFMSAgentHistory>()).anyTimes();
    expect(((UtilisoftGenericDao) mAgentDao).batchMakePersistent(isA(List.class)))
      .andReturn(new ArrayList<GenericAgent>()).anyTimes();
    expect(mDpiFileDataWriter.save(isA(String.class), isA(String.class)))
      .andReturn(new String[] {"", null}).once();
    expect(((UtilisoftGenericDao) mDpiFileDataDao).makePersistent(isA(DpiFileData.class))).andReturn(expData).once();

    replay(mAFMSMPanDao, mSupplierDao, mDpiFileDao, mGridSupplyPointDao, mAfmsAgentDao, mAFMSAgentHistoryDao,
           mAgentDao, mDpiFileDataWriter, mDpiFileDataDao);

    mDpiGenerationService.generateParmsReport(dec2010start.toDateTime());

    verify(mAFMSMPanDao, mSupplierDao, mDpiFileDao, mGridSupplyPointDao, mAfmsAgentDao, mAFMSAgentHistoryDao,
           mAgentDao, mDpiFileDataWriter, mDpiFileDataDao);
  }

  @Before
  public void init()
  {
      try
      {
        mAFMSMPanDao = createMock(AFMSMpanDaoHibernate.class, AFMSMpanDaoHibernate.class
                                  .getMethod("getSupplierIdsForTwoMonths", ParmsReportingPeriod.class),
                                  AFMSMpanDaoHibernate.class.getMethod("getActiveMpans", ParmsReportingPeriod.class,
                                                                       Supplier.class, boolean.class));
        mSupplierDao = createMock(SupplierDaoHibernate.class, SupplierDaoHibernate.class
                                  .getMethod("getSupplier", String.class));
        mDpiFileDao = createMock(DpiFileDaoHibernate.class);
        mGridSupplyPointDao = createMock(GridSupplyPointDaoHibernate.class);
        mAFMSAgentHistoryDao = createMock(AFMSAgentHistoryDaoHibernate.class);
        mAfmsAgentDao = createMock(AFMSAgentDaoHibernate.class);
        mAgentDao = createMock(AgentDaoHibernate.class);
        mDpiFileDataWriter = createMock(DpiFileDataWriterImpl.class);
        mDpiFileDataDao = createMock(DpiFileDataDaoHibernate.class);
      }
      catch (NoSuchMethodException nsme)
      {
        nsme.printStackTrace();
        fail("Unexpected NoSuchMethodException");
      }


      mDpiFileProcess.setAfmsMpanDao(mAFMSMPanDao);
      mDpiFileProcess.setSupplierDao(mSupplierDao);

      mDpiDataCreator.setAgentBuilder(mAgentBuilder);
      mDpiDataCreator.setDpiFileDao(mDpiFileDao);

      mAgentBuilder.setAfmsAgentDao(mAfmsAgentDao);
      mAgentBuilder.setAFMSAgentHistoryDao(mAFMSAgentHistoryDao);
      mAgentBuilder.setAfmsMpanDao(mAFMSMPanDao);
      mAgentBuilder.setAgentDao(mAgentDao);

      mDpiFileBuilder.setDpiFileDataDao(mDpiFileDataDao);
      mDpiFileBuilder.setDpiFileDataWriter(mDpiFileDataWriter);

      mDpiFileProcess.setDpiFileBuilder(mDpiFileBuilder);

      mDpiGenerationService = new DpiGenerationService();
      mDpiGenerationService.setDpiFileProcess(mDpiFileProcess);
  }
}
