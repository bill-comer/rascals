package uk.co.utilisoft.parms.web.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.dao.HalfHourlyQualifyingMpansReportDao;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.HalfHourlyQualifyingMpansReport;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.web.service.report.ReportServiceImpl;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class ReportServiceUnitTest
{
  private ReportServiceImpl mReportService;
  private Sp04FromAFMSMpanDao mSp04FromAFMSMpanDao;
  private SupplierDao mSupplierDao;
  private P0028ActiveDao mP0028ActiveDao;
  private HalfHourlyQualifyingMpansReportDao mHalfHourlyQualifyingMpanReportDao;

  /**
   * Download a csv containing data showing non half hourly mpans qualifying for half hourly meter installation.
   *
   * @throws IOException
   */
  @Test
  public void downloadHalfHourlyQualifyingMpansReport() throws IOException
  {
    Long supplierPk = 1L;
    MockHttpServletResponse resp = new MockHttpServletResponse();
    List<Sp04FromAFMSMpan> sp04FromAFMSMpans = new ArrayList<Sp04FromAFMSMpan>();

    Sp04FromAFMSMpan sp04FromAFMSMpan1 = new Sp04FromAFMSMpan();
    DateTime now = new DateTime();
    sp04FromAFMSMpan1.setCalculatedMeterInstallationDeadline(now.plusDays(15));
    sp04FromAFMSMpan1.setCalculatedStandard1(100L);
    sp04FromAFMSMpan1.setCalculatedStandard2(100L);
    sp04FromAFMSMpan1.setCalculatedStandard3(100F);
    sp04FromAFMSMpan1.setDataCollector("SEEB");
    sp04FromAFMSMpan1.setLastUpdated(now);
    sp04FromAFMSMpan1.setMaxDemand(100F);
    sp04FromAFMSMpan1.setMeterId("M200033098");
    sp04FromAFMSMpan1.setMeterReadingDate1(now.minusDays(25));
    sp04FromAFMSMpan1.setMeterRegisterReading1(100F);
    sp04FromAFMSMpan1.setMeterReadingDate2(now.minusDays(15));
    sp04FromAFMSMpan1.setMeterRegisterReading2(100F);
    sp04FromAFMSMpan1.setMeterReadingDate3(now.minusDays(5));
    sp04FromAFMSMpan1.setMeterRegisterReading3(100F);
    sp04FromAFMSMpan1.setSupplierFk(supplierPk);
    sp04FromAFMSMpan1.setPk(1L);
    sp04FromAFMSMpan1.setMpan(new MPANCore("0000000000001"));

    Sp04FromAFMSMpan sp04FromAFMSMpan2 = new Sp04FromAFMSMpan();
    sp04FromAFMSMpan2.setCalculatedMeterInstallationDeadline(now.plusDays(30));
    sp04FromAFMSMpan2.setCalculatedStandard1(100L);
    sp04FromAFMSMpan2.setCalculatedStandard2(100L);
    sp04FromAFMSMpan2.setCalculatedStandard3(100F);
    sp04FromAFMSMpan2.setDataCollector("NAAB");
    sp04FromAFMSMpan2.setLastUpdated(now);
    sp04FromAFMSMpan2.setMaxDemand(200F);
    sp04FromAFMSMpan2.setMeterId("M200034444");
    sp04FromAFMSMpan2.setMeterReadingDate1(now.minusDays(50));
    sp04FromAFMSMpan2.setMeterRegisterReading1(200F);
    sp04FromAFMSMpan2.setMeterReadingDate2(now.minusDays(30));
    sp04FromAFMSMpan2.setMeterRegisterReading2(200F);
    sp04FromAFMSMpan2.setMeterReadingDate3(now.minusDays(10));
    sp04FromAFMSMpan2.setMeterRegisterReading3(200F);
    sp04FromAFMSMpan2.setSupplierFk(supplierPk);
    sp04FromAFMSMpan2.setPk(2L);
    sp04FromAFMSMpan2.setMpan(new MPANCore("0000000000002"));

    sp04FromAFMSMpans.add(sp04FromAFMSMpan1);
    sp04FromAFMSMpans.add(sp04FromAFMSMpan2);

    expect(mSp04FromAFMSMpanDao.getAll(supplierPk)).andReturn(sp04FromAFMSMpans).once();

    Supplier supplier = new Supplier();
    supplier.setPk(supplierPk);
    supplier.setSupplierId("SEEB");
    expect(mSupplierDao.getById(supplierPk)).andReturn(supplier).once();

    IterableMap<String, P0028Active> p28Actives = new HashedMap<String, P0028Active>();

    String p28Mpan1 = "0000000000003";
    P0028Active p28Active1 = new P0028Active();
    p28Active1.setPk(1L);
    p28Active1.setDataCollectorName("BEEB");
    p28Active1.setMaxDemand(177L);
    p28Active1.setMeterReadingDate(now.minusDays(22));
    p28Active1.setMeterSerialId("M50000005");
    p28Active1.setMpanCore(new MPANCore(p28Mpan1));
    p28Active1.setSupplier(supplier);

    P0028Data latestP0028Data1 = new P0028Data();
    P0028File p0028File1 = new P0028File();
    p0028File1.setReceiptDate(now.minusDays(25));
    latestP0028Data1.setPk(1L);
    latestP0028Data1.setP0028File(p0028File1);
    p28Active1.setLatestP0028Data(latestP0028Data1);

    String p28Mpan2 = "0000000000004";
    P0028Active p28Active2 = new P0028Active();
    p28Active2.setPk(2L);
    p28Active2.setDataCollectorName("BOOM");
    p28Active2.setMaxDemand(190L);
    p28Active2.setMeterReadingDate(now.minusDays(25));
    p28Active2.setMeterSerialId("M60000006");
    p28Active2.setMpanCore(new MPANCore(p28Mpan2));
    p28Active2.setSupplier(supplier);

    P0028Data latestP0028Data2 = new P0028Data();
    P0028File p0028File2 = new P0028File();
    p0028File2.setReceiptDate(now.minusDays(27));
    latestP0028Data2.setPk(2L);
    latestP0028Data2.setP0028File(p0028File2);
    p28Active2.setLatestP0028Data(latestP0028Data2);

    p28Actives.put(p28Mpan1, p28Active1);
    p28Actives.put(p28Mpan2, p28Active2);

    expect(mP0028ActiveDao.getAllForSupplier(supplier)).andReturn(p28Actives).once();

    expect(mHalfHourlyQualifyingMpanReportDao.makePersistent((HalfHourlyQualifyingMpansReport) anyObject()))
      .andReturn(new HalfHourlyQualifyingMpansReport()).once();

    replay(mSp04FromAFMSMpanDao, mSupplierDao, mP0028ActiveDao, mHalfHourlyQualifyingMpanReportDao);

    String fileName = mReportService.downloadHalfHourlyQualifyingMpansReport(supplierPk, resp);

    verify(mSp04FromAFMSMpanDao, mSupplierDao, mP0028ActiveDao, mHalfHourlyQualifyingMpanReportDao);

    String expFileNamePrefix = "halfHourlyQualifyingMpans.";
    String expFileNameSuffix =  ".csv";
    assertEquals(true, StringUtils.isNotBlank(fileName) && fileName.startsWith(expFileNamePrefix)
                 && fileName.endsWith(expFileNameSuffix));
  }

  @Before
  public void setup()
  {
    if (mReportService == null)
    {
      mReportService = new ReportServiceImpl();
      mSp04FromAFMSMpanDao = createMock(Sp04FromAFMSMpanDao.class);
      mSupplierDao = createMock(SupplierDao.class);
      mP0028ActiveDao = createMock(P0028ActiveDao.class);
      mHalfHourlyQualifyingMpanReportDao = createMock(HalfHourlyQualifyingMpansReportDao.class);

      mReportService.setSp04FromAfmsMpanDao(mSp04FromAFMSMpanDao);
      mReportService.setSupplierDao(mSupplierDao);
      mReportService.setP0028ActiveDao(mP0028ActiveDao);
      mReportService.setHalfHourlyQualifyingMpanDao(mHalfHourlyQualifyingMpanReportDao);
    }
  }
}
