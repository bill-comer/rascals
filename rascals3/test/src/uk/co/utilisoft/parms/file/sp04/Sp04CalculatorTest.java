package uk.co.utilisoft.parms.file.sp04;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSAregiProcessDao;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidatorImpl;
import uk.co.utilisoft.utils.Freeze;

/**
 *
 */
public class Sp04CalculatorTest
{
  private Supplier mSupplier;

  Long mpanUniqID = 161111L;

  /**
   *
   */
  @Before
  public void init()
  {
    mSupplier = new Supplier("TEST");
  }

  /**
   * Calculate sp04 data for a given P0028Active record.
   */
  @Test
  public void calculateSp04DataForAP0028Active()
  {
    Freeze.thaw();
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    ParmsReportingPeriod prpLastMonth = new ParmsReportingPeriod(2, 2011);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator();;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.setParmsReportingPeriod(prpLastMonth);
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(prpLastMonth);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);
    DateTime p0028RxDate = new DateTime(2011, 03, 15, 0, 0, 0, 0);
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(p0028RxDate.minusMonths(4));
    Long maxDemand = 101L;
    P0028Data p0028Data = new P0028Data();
    p0028Data.setMaxDemand(maxDemand);
    p0028Data.setP0028File(p0028File);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    String meterRegId = "11223344";
    MPANCore mpanCore = new MPANCore("1000000000003");
    DateTime meterReadingDate = new DateTime(2011, 02, 22, 0, 0, 0, 0);

    P0028Active p28Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
                                            meterRegId, mpanCore, meterReadingDate);

    AFMSMpan afmsMpan = new AFMSMpan();
    afmsMpan.setPk(999999L);
    afmsMpan.setMpanCore(mpanCore.getValue());
    afmsMpan.setMeasurementClassification("A");
    afmsMpan.setSupplierId(supplierId);

    Collection<AFMSMeter> afmsMeters = new ArrayList<AFMSMeter>();
    AFMSMeter meter1 = new AFMSMeter();
    DateTime now = new DateTime();
    meter1.setSettlementDate(now.minusMonths(1));
    meter1.setMpan(afmsMpan);
    meter1.setMeterSerialId(meterRegId);
    afmsMeters.add(meter1);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, prpLastMonth)).andReturn(afmsMpan).once();
    expect(mockAFMSMeterDao.getByMpanUniqueId(afmsMpan.getPk())).andReturn(afmsMeters).once();
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(afmsMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    // test call
    Sp04Data data = calculator.calculate(p28Active, supplier);

    verify(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    assertNotNull(data);
  }

  /**
   * Check p0028 data mpan meter max demand > 100kw.
   */
  @Test
  public void calculateSp04DataWithExceededMaxDemand()
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);
    calculator.setParmsReportingPeriod(period);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(4));  //meter installation deadline is this + 3 months
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(new DateTime().minusDays(1));
    mockedMeter.setMeterSerialId(meterRegId);

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan).once();
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertNotNull(serial.getStandard1());
    assertNotNull(serial.getStandard2());
    assertNotNull(serial.getStandard3());
    assertNull(serial.getSp04FaultReason());

  }

  /**
   * Check p0028 data mpan meter max demand < 100kw
   */
  @Test
  public void calculateSp04DataWithMaxDemandLessThan100()
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(4));  //meter installation deadline is this + 3 months
    String meterRegId = "11223344";
    Long maxDemand = 99L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setMeterSerialId("meterserialid001");

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setSettlementDate(new DateTime().minusYears(1));

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);

    replay(mockAFMSMeterDao, mockAFMSMpanDao);


    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    assertNotNull(serial);
    assertNull(serial.getStandard1());
    assertNull(serial.getStandard2());
    assertNull(serial.getStandard3());
    assertNotNull(serial.getSp04FaultReason());
    assertEquals(Sp04FaultReasonType.MD_VALUE_LESS_100, serial.getSp04FaultReason());

    verify(mockAFMSMeterDao, mockAFMSMpanDao);

  }

  /**
   */
  @Test
  public void calculateSp04DataWithMIDBeforeStartOfMonthT()
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(4));  //meter installation deadline is this + 3 months
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    Supplier supplier = null;
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setSettlementDate(new DateTime().minusDays(1));
    mockedMeter.setMeterSerialId(meterRegId);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);


    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertNotNull(serial.getStandard1());
    assertNotNull(serial.getStandard2());
    assertNotNull(serial.getStandard3());
    assertNull(serial.getSp04FaultReason());
  }

  /**
   *
   */
  @Test
  public void calculateSp04Data_WithMIDAfterStartOfMonthT_noD0268()
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(3).plusDays(14));  //meter installation deadline day 15 of 28
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setSettlementDate(new DateTime().minusDays(1));
    mockedMeter.setMeterSerialId(meterRegId);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertNotNull(serial.getStandard1());
    assertNotNull(serial.getStandard2());
    assertNotNull(serial.getStandard3());

    assertEquals(new Long(28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth()), serial.getStandard1());
    assertEquals(new Long(28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth()), serial.getStandard2());
    assertEquals(new Float(100.0), serial.getStandard3());


  }

  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_lessThanMonthStart_noD068Rxd() throws Exception
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(4));  //meter installation deadline is this + 3 months
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setSettlementDate(new DateTime().minusDays(1));
    mockedMeter.setMeterSerialId(meterRegId);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertEquals(mpanCore, serial.getMpanCore());
    assertEquals("_GSP", serial.getGspGroupId());

    assertEquals("should be num days in month feb", new Long(28), serial.getStandard1());
    assertEquals("should be num days in month feb", new Long(28), serial.getStandard2());
    assertEquals("should be s2/s1 as %", new Float(100.0), serial.getStandard3());
    assertEquals("not coded yet - failed to get the GSP from the AFMSMpan", "_GSP", serial.getGspGroupId());


    Freeze.thaw();
  }


  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_inMonthStart_noD068Rxd() throws Exception
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    });
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(3).plusDays(14));  //meter installation deadline day 15 of 28
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime().minusMonths(3).plusDays(14);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setSettlementDate(new DateTime().minusDays(1));
    mockedMeter.setMeterSerialId(meterRegId);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertEquals(mpanCore, serial.getMpanCore());
    assertEquals("_GSP", serial.getGspGroupId());
    assertNotNull(serial.getStandard1());
    assertNotNull(serial.getStandard2());
    assertNotNull(serial.getStandard3());

    assertEquals(new Long(28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth()), serial.getStandard1());
    assertEquals(new Long(28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth()), serial.getStandard2());
    assertEquals(new Float(100.0), serial.getStandard3());


    Freeze.thaw();
  }


  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_d0268BeforeMID_edge1() throws Exception
  {
    Freeze.freeze(1, 8, 2011); //Date of report generation
    DateTime p0028RxDate = new DateTime(2011, 5, 15, 0, 0, 0, 0);  //15 Feb 2011
    DateTime d0268RxdDate = p0028RxDate.plusMonths(2);
    final ParmsReportingPeriod period = new ParmsReportingPeriod(8, 2011);


    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.mAFMSAregiProcessDao = mockAFMSAregiProcessDao;
    rowCalculator.setParmsReportingPeriod(period);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };
    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    sp04AfmsMpanValidator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(p0028RxDate);
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    //DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);


    AFMSMpan mockedMpan = new AFMSMpan();
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");

    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setLastUpdated(new DateTime());
    aregiProcess.setD0268ReceivedDate(d0268RxdDate);   // set D0268
    mockedMpan.getAregiProcesses().add(aregiProcess);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(d0268RxdDate);
    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setOutstationId("sdasd");
    mockedMeter.setLastUpdated(d0268RxdDate);
    mockedMeter.setMeterSerialId(meterRegId);

    mockedMpan.getMeters().add(mockedMeter);

    Collection<AFMSAregiProcess> mockedAregiProcs = new ArrayList<AFMSAregiProcess>();
    mockedAregiProcs.add(aregiProcess);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
   //expect(mockAFMSAregiProcessDao.getByMpanUniqueId(anyLong())).andReturn(mockedAregiProcs);

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    assertNull(serial);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    Freeze.thaw();
  }



  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_d0268BeforeMID_edge2() throws Exception
  {
    Freeze.freeze(1, 9, 2011); //Date of report generation
    DateTime p0028RxDate = new DateTime(2011, 5, 15, 0, 0, 0, 0);  //15 Feb 2011
    DateTime d0268RxdDate = p0028RxDate.plusMonths(2);
    final ParmsReportingPeriod period = new ParmsReportingPeriod(8, 2011);


    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.mAFMSAregiProcessDao = mockAFMSAregiProcessDao;
    rowCalculator.setParmsReportingPeriod(period);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };
    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    sp04AfmsMpanValidator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(p0028RxDate);
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    //DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMpan mockedMpan = new AFMSMpan();
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");

    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setLastUpdated(new DateTime());
    aregiProcess.setD0268ReceivedDate(d0268RxdDate);   // set D0268
    mockedMpan.getAregiProcesses().add(aregiProcess);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(d0268RxdDate);
    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setOutstationId("sdasd");
    mockedMeter.setLastUpdated(d0268RxdDate);
    mockedMeter.setMeterSerialId(meterRegId);

    mockedMpan.getMeters().add(mockedMeter);

    Collection<AFMSAregiProcess> mockedAregiProcs = new ArrayList<AFMSAregiProcess>();
    mockedAregiProcs.add(aregiProcess);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    //expect(mockAFMSAregiProcessDao.getByMpanUniqueId(anyLong())).andReturn(mockedAregiProcs);

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    assertNull(serial);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);
    Freeze.thaw();
  }

  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_lessThanMonthStart_D068RxdAfterMonthT() throws Exception
  {
    Freeze.freeze(3, 8, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime(2009, 10, 20, 0, 0, 0, 0));
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(period.getStartOfMonth(false).plusMonths(6));
    mockedMeter.setMeterSerialId(meterRegId);

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertEquals(mpanCore, serial.getMpanCore());
    assertEquals("_GSP", serial.getGspGroupId());

    assertEquals("should be num days in month feb", new Long(28), serial.getStandard1());
    assertEquals("should be num days in month feb", new Long(28), serial.getStandard2());
    assertEquals("should be s2/s1 as %", new Float(100.0), serial.getStandard3());
    assertEquals("not coded yet - failed to get the GSP from the AFMSMpan", "_GSP", serial.getGspGroupId());

    Freeze.thaw();
  }

  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_inMonthStart_D068RxdAfterMonthT() throws Exception
  {
    Freeze.freeze(3, 8, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);


    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
//    p0028File.setReceiptDate(new DateTime().minusMonths(3).plusDays(14));  //meter installation deadline day 15 of 28
    p0028File.setReceiptDate(new DateTime(2009, 12, 1, 0, 0, 0, 0));
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(period.getStartOfMonth(false).plusMonths(6));
    mockedMeter.setMeterSerialId(meterRegId);

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.getMeters().add(mockedMeter);

    mockedMeter.setMpan(mockedMpan);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).once();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertEquals(mpanCore, serial.getMpanCore());
    assertEquals("_GSP", serial.getGspGroupId());

    assertEquals("should be day of MID", new Long(28 - p0028File.getReceiptDate().getDayOfMonth()),
                 serial.getStandard1());
    assertEquals("should be num days in month feb", new Long(28 - p0028Active.getMeterInstallationDeadline()
                                                             .getDayOfMonth()), serial.getStandard2());
    assertEquals("should be s2/s1 as %", new Float(100.0), serial.getStandard3());

    Freeze.thaw();
  }


  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_lessThanMonthStart_D0268RxdBeforeMonthT() throws Exception
  {
    Freeze.freeze(1, 3, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);   //Feb
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };
    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(5));  //this makes MID 2 months ago - IE before start
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(new DateTime().minusMonths(1).plusDays(10));  //10th Jan
    mockedMeter.setOutstationId("OUTSTATION");

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMeter.setMeterSerialId(meterRegId);

    mockedMeter.setMpan(mockedMpan);

    mockedMpan.getMeters().add(mockedMeter);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).anyTimes();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull(serial);
    assertEquals(mpanCore, serial.getMpanCore());
    assertEquals("_GSP", serial.getGspGroupId());

    assertEquals("should be num days in month jan", new Long(28), serial.getStandard1());
    assertEquals("should be day of settlement date", new Long(mockedMeter.getSettlementDate().getDayOfMonth()),
                 serial.getStandard2());
    assertEquals("should be s2/s1 as %", new Float(39.3), serial.getStandard3());


    Freeze.thaw();
  }

  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_inMonthStart_D068RxdBeforeMonthT() throws Exception
  {
    Freeze.freeze(1, 2, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.mAFMSAregiProcessDao = mockAFMSAregiProcessDao;
    rowCalculator.setParmsReportingPeriod(period);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };
    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setHalfHourlySettlementDate(halfHourlySettlementDate);

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(3).plusDays(14));  //meter installation deadline day 15 of 28
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime().minusDays(20);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);


    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.setPk(99999L);

    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setLastUpdated(new DateTime());
    aregiProcess.setD0268ReceivedDate(new DateTime().minusDays(10));   // 21/2/2010
    mockedMpan.getAregiProcesses().add(aregiProcess);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(new DateTime().minusDays(10));
    mockedMeter.setMpan(mockedMpan);
    mockedMeter.setLastUpdated(new DateTime().minusDays(10));
    mockedMeter.setOutstationId("asdas");
    mockedMeter.setMeterSerialId(meterRegId);

    mockedMpan.getMeters().add(mockedMeter);

    Collection<AFMSAregiProcess> mockedAregiProcs = new ArrayList<AFMSAregiProcess>();
    mockedAregiProcs.add(aregiProcess);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    //expect(mockAFMSAregiProcessDao.getByMpanUniqueId(anyLong())).andReturn(mockedAregiProcs);

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    assertNull("should be null as D0268 rxd before Month T", serial);
    //assertEquals(mpanCore, serial.getMpanCore());
    //assertEquals("_GSP", serial.getGspGroupId());
    //assertNotNull(serial.getSp04FaultReason());
    //assertEquals(Sp04FaultReasonType.getMIDAfterMonthT(), serial.getSp04FaultReason());
    //assertNull(serial.getStandard1());
    //ssertNull(serial.getStandard2());
    //assertNull(serial.getStandard3());

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    Freeze.thaw();
  }


  /**
   * @throws Exception
   */
  @Test
  public void Calculator_mid_inMonthStart_D0268RxdInMonthT() throws Exception
  {
    Freeze.freeze(1, 3, 2010);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidatorImpl = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };
    sp04AfmsMpanValidatorImpl.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    halfHourlySettlementDate.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidatorImpl.setHalfHourlySettlementDate(halfHourlySettlementDate);

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidatorImpl);
    rowCalculator.setHalfHourlySettlementDate(halfHourlySettlementDate);
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusMonths(4).plusDays(14));  //meter installation deadline day 15 of 28
    String meterRegId = "11223344";
    Long maxDemand = 101L;
    DateTime meterReadingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(maxDemand, meterRegId, mpanCore, meterReadingDate, p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String dcName = "dc";
    DataCollector dc = new DataCollector(dcName, true, null, true);

    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    AFMSMeter mockedMeter = new AFMSMeter();
    mockedMeter.setSettlementDate(new DateTime().minusMonths(1).plusDays(16));   //meter install date
    mockedMeter.setOutstationId("OUSTATIONID");
    mockedMeter.setMeterSerialId(meterRegId);

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");
    mockedMpan.setPk(99999L);


    mockedMeter.setMpan(mockedMpan);
    mockedMpan.getMeters().add(mockedMeter);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();
    mockedMeters.add(mockedMeter);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(new DateTime());
    aregiProcs.add(aregiProc1);

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(mockedMeter);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mockedMpan.getPk())).andReturn(aregiProcs).anyTimes();

    replay(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    //test method
    Sp04Data serial = calculator.calculate(p0028Active, mSupplier);

    verify(mockAFMSMeterDao, mockAFMSMpanDao, mockAFMSAregiProcessDao);

    assertNotNull("should be not null as D0268 rxd in Month T", serial);
    assertNotNull(serial.getStandard1());
    assertNotNull(serial.getStandard2());
    assertNotNull(serial.getStandard3());

    assertEquals("day of MID", new Long(28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth()),
                 serial.getStandard1());

    halfHourlySettlementDate.getHalfHourlySettlementDate(mockedMpan);
    assertEquals("also day of MID", new Long(mockedMeter.getSettlementDate().getDayOfMonth()
                                             - p0028Active.getMeterInstallationDeadline().getDayOfMonth()),
                                             serial.getStandard2());

    Float expResult = new Float((mockedMeter.getSettlementDate().getDayOfMonth()
        - p0028Active.getMeterInstallationDeadline().getDayOfMonth()) * 100)
        / (28 - p0028Active.getMeterInstallationDeadline().getDayOfMonth());

    expResult = (Math.round(expResult * 10.0f) / 10.0f);

    assertEquals("should be s2/s1 as %", new Float(expResult), serial.getStandard3());



    Freeze.thaw();
  }

  /**
   * @throws Exception
   */
  @Test
  public void CalculatorWithNoMatchingMeterIds() throws Exception
  {
    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Sp04Calculator calculator = new Sp04Calculator();
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };
    rowCalculator.mAFMSMeterDao = mockAFMSMeterDao;
    rowCalculator.mAFMSMpanDao = mockAFMSMpanDao;
    rowCalculator.setParmsReportingPeriod(period);
    rowCalculator.setSp04AfmsMpanValidator(new Sp04AfmsMpanValidatorImpl());
    calculator.setSp04RowCalculator(rowCalculator);
    calculator.setParmsReportingPeriod(period);
    calculator.setAFMSMpanDao(mockAFMSMpanDao);

    MPANCore mpanCore = new MPANCore("1000000000003");
    String meterRegId = "11223344";
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime().minusYears(1));
    P0028Data p0028Data = new P0028Data(101L, meterRegId, mpanCore, new DateTime(), p0028File);
    p0028Data.setMpanUniqId(mpanUniqID);

    AFMSMpan mockedMpan = new AFMSMpan();  // no aregiProcesses set up
    mockedMpan.setMpanCore(mpanCore.getValue());
    mockedMpan.setGridSupplyPoint("_GSP");
    mockedMpan.setMeasurementClassification("A");
    mockedMpan.setSupplierId("TEST");

    Long maxDemand = 100L;
    DateTime meterReadingDate = new DateTime();

    DateTime p0028RxDate = new DateTime();
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    DataCollector dc = null;
    String dcName = "dc";
    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    Collection<AFMSMeter> mockedMeters = new ArrayList<AFMSMeter>();

    // mock afmsmeterdao call in p0028importer
    expect(mockAFMSMpanDao.getAfmsMpan(mpanCore, supplier, period)).andReturn(mockedMpan);
    expect(mockAFMSMeterDao.getLatestMeterForMpanUniqId(anyLong())).andStubReturn(null);
    expect(mockAFMSMeterDao.getByMpanUniqueId(anyLong())).andReturn(mockedMeters);

    replay(mockAFMSMeterDao, mockAFMSMpanDao);

    //test method
    Sp04Data data = calculator.calculate(p0028Active, mSupplier);
    assertNotNull(data);
    assertNotNull(data.getSp04FaultReason());
    assertTrue(data.getSp04FaultReason().isNoAfmsMeterForMeterRegister());

    verify(mockAFMSMeterDao, mockAFMSMpanDao);

  }

  /**
   * @throws Exception
   */
  @Test
  public void isMIDAfterEndOfMonthT_MIDBefore() throws Exception
  {
    //before PRP
    Freeze.freeze(1, 3, 2010);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime());
    MPANCore mpanCore = new MPANCore("1000000000003");
    String meterRegId = "11223344";

    P0028Data p0028Data = new P0028Data(101L, meterRegId, mpanCore, new DateTime(), p0028File);

    Long maxDemand = 100L;
    DateTime meterReadingDate = new DateTime();

    DateTime p0028RxDate = new DateTime();
    Supplier supplier = null;
    DataCollector dc = null;
    String dcName = "dc";
    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    //test method
    assertFalse(calculator.isMIDAfterEndOfMonthT(p0028Active));
  }

  /**
   * @throws Exception
   */
  @Test
  public void isMIDAfterEndOfMonthT_MIDAfter() throws Exception
  {
    //after PRP
    Freeze.freeze(2, 10, 2010);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };
    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime());
    MPANCore mpanCore = new MPANCore("1000000000003");
    String meterRegId = "11223344";
    P0028Data p0028Data = new P0028Data(101L, meterRegId, mpanCore, new DateTime(), p0028File);

    Long maxDemand = 100L;
    DateTime meterReadingDate = new DateTime();

    DateTime p0028RxDate = new DateTime();
    Supplier supplier = null;
    DataCollector dc = null;
    String dcName = "dc";
    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId.toString(), mpanCore, meterReadingDate);

    //test method
    assertTrue(calculator.isMIDAfterEndOfMonthT(p0028Active));
  }

  /**
   * @throws Exception
   */
  @Test
  public void isMIDBeforeStartOfMonthT_MIDBefore() throws Exception
  {
    //before PRP
    Freeze.freeze(1, 3, 2010);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime());
    MPANCore mpanCore = new MPANCore("1000000000003");
    String meterRegId = "11223344";
    P0028Data p0028Data = new P0028Data(101L, meterRegId, mpanCore, new DateTime(), p0028File);

    Long maxDemand = 100L;
    DateTime meterReadingDate = new DateTime();

    DateTime p0028RxDate = new DateTime();
    Supplier supplier = null;
    DataCollector dc = null;
    String dcName = "dc";
    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId.toString(), mpanCore, meterReadingDate);

    //test method
    assertTrue(calculator.isMIDBeforeStartOfMonthT(p0028Active));
  }

  /**
   * @throws Exception
   */
  @Test
  public void isMIDBeforeStartOfMonthT_MIDAfter() throws Exception
  {
    //before PRP
    Freeze.freeze(1, 11, 2010);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    P0028File p0028File = new P0028File();
    p0028File.setReceiptDate(new DateTime());
    MPANCore mpanCore = new MPANCore("1000000000003");
    String meterRegId = "11223344";
    P0028Data p0028Data = new P0028Data(101L, meterRegId, mpanCore, new DateTime(), p0028File);

    Long maxDemand = 100L;
    DateTime meterReadingDate = new DateTime();

    DateTime p0028RxDate = new DateTime();
    Supplier supplier = null;
    DataCollector dc = null;
    String dcName = "dc";
    P0028Active p0028Active = new P0028Active(supplier, dc, dcName, p0028Data, maxDemand, p0028RxDate,
        meterRegId, mpanCore, meterReadingDate);

    //test method
    assertFalse(calculator.isMIDBeforeStartOfMonthT(p0028Active));
  }

  /**
   * @throws Exception
   */
  @Test
  public void getNumDaysInMonthT_July() throws Exception
  {
    final ParmsReportingPeriod period = new ParmsReportingPeriod(7, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    assertEquals(new Long(31), calculator.getNumDaysInMonthT(period));
  }

  /**
   * @throws Exception
   */
  @Test
  public void getNumDaysInMonthT_Feb_leapYear() throws Exception
  {
    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2008);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    //test method
    assertEquals(new Long(29), calculator.getNumDaysInMonthT(period));
  }

  /**
   * @throws Exception
   */
  @Test
  public void getNumDaysInMonthT_Feb_nonleapYear() throws Exception
  {
    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2009);
    Sp04RowCalculator calculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    //test method
    assertEquals(new Long(28), calculator.getNumDaysInMonthT(period));
  }


  /**
   * @throws Exception
   */
  @Test
  public void isD0268AfterEndOfMonthOrNotHappened_D0268Before() throws Exception
  {
    Freeze.freeze(1, 7, 2010);
    final ParmsReportingPeriod period = new ParmsReportingPeriod(7, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator();
    calculator.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime d0268Date = new DateTime().minusMonths(2);
    AFMSMeter meter = createHalfHourlyMeter(d0268Date);
    meter.setSettlementDate(new DateTime().minusYears(1));

    //test method
    assertTrue(calculator.isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(meter.getMpan(), period));
  }



  /**
   * @throws Exception
   */
  @Test
  public void isD0268AfterEndOfMonthOrNotHappened_D0268After() throws Exception
  {
    Freeze.freeze(1, 7, 2010);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(7, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator();
    calculator.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime d0268Date = new DateTime().minusDays(1);
    AFMSMeter meter = createHalfHourlyMeter(d0268Date);
    meter.setSettlementDate(new DateTime().minusYears(1));

    //test method
    assertTrue(calculator.isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(meter.getMpan(), period));
  }


  /**
   * @throws Exception
   */
  @Test
  public void isD0268AfterEndOfMonthOrNotHappened_D0268AfterEdgeCaseSettlemtDateFirstOfMonth() throws Exception
  {
    Freeze.freeze(15, 5, 2010);
    final ParmsReportingPeriod period = new ParmsReportingPeriod(4, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator();
    calculator.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime d0268Date = new DateTime().minusDays(1);
    AFMSMeter meter = createHalfHourlyMeter(d0268Date);

    DateTime settlementDate = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    meter.setSettlementDate(settlementDate);

    //test method
    assertTrue(calculator.isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(meter.getMpan(), period));
  }



  /**
   * @throws Exception
   */
  @Test
  public void isD0268AfterEndOfMonthOrNotHappened_D0268AfterEdgeCaseSettlemtDateInParmsMonth() throws Exception
  {
    Freeze.freeze(15, 5, 2010);
    final ParmsReportingPeriod period = new ParmsReportingPeriod(4, 2010);
    Sp04RowCalculator calculator = new Sp04RowCalculator();
    calculator.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime d0268Date = new DateTime().minusDays(1);
    AFMSMeter meter = createHalfHourlyMeter(d0268Date);

    DateTime settlementDate = new DateTime(2010, 4, 15, 0, 0, 0, 0);
    meter.setSettlementDate(settlementDate);

    //test method
    assertFalse(calculator.isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(meter.getMpan(), period));
  }



  /**
   * @throws Exception
   */
  @Test
  public void calculate_iterableMap() throws Exception
  {
    Freeze.freeze(1, 7, 2010);
    ParmsReportingPeriod period = new ParmsReportingPeriod(7, 2010);
    Sp04Calculator calculator = new Sp04Calculator()
    {
      public Sp04Data calculate(P0028Active aP0028Active, Supplier aSupplier)
      {
        Sp04Data data = new Sp04Data();
        return data;
      }

    };

    Supplier supplier = new Supplier("SUPP");

    IterableMap<String, P0028Active> activeList = new HashedMap<String, P0028Active>();
    P0028Active active1 = new P0028Active();
    P0028Active active2 = new P0028Active();
    activeList.put("a1", active1);
    activeList.put("a2", active2);

    Sp04File sp04File = new Sp04File("file", null, period);

    List<Sp04Data> allSp04data = calculator.calculate(sp04File, activeList, supplier);
    assertNotNull(allSp04data);
    assertEquals(2, allSp04data.size());
  }

  private AFMSMeter createHalfHourlyMeter(DateTime d0268Date)
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setOutstationId("OUT");
    meter.setMeterType("H");

    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setD0268ReceivedDate(d0268Date);

    AFMSMpan mpan = new AFMSMpan();
    mpan.setLastUpdated(new DateTime().minusMonths(1));
    mpan.getAregiProcesses().add(aregiProcess);
    mpan.getMeters().add(meter);

    meter.setMpan(mpan);

    return meter;
  }
}

