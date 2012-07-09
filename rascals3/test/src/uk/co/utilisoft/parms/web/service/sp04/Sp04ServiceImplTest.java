package uk.co.utilisoft.parms.web.service.sp04;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.*;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSAregiProcessDao;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading.BSC_VALIDATION_STATUS;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister.MEASUREMENT_QUANTITY_ID;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister.METER_REGISTER_ID;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.MeterRegisterType;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.HalfHourlySettlementDate;
import uk.co.utilisoft.parms.file.sp04.Sp04Calculator;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;
import uk.co.utilisoft.parms.file.sp04.Sp04RowCalculator;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidatorImpl;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;
import uk.co.utilisoft.utils.Freeze;


/**
 *
 */
public class Sp04ServiceImplTest
{
  /**
   * Get a combination of P0028Active and Sp04FromAFMSMpan records, and remove any duplicate
   * Sp04FromAFMSMpan records that have matching mpans in the P0028Active records.
   */
  @Test
  public void combineP0028AndAfmsMpans()
  {
    Sp04ServiceImpl sp04Service = new Sp04ServiceImpl();
    MultiHashMap<String, Sp04FromAFMSMpan> afmsMpans = new MultiHashMap<String, Sp04FromAFMSMpan>();
    MultiHashMap<String, P0028Active> p28Mpans = new MultiHashMap<String, P0028Active>();
    String mpan1111111111111 = "1111111111111";
    String mpan2222222222222 = "2222222222222";
    String mpan3333333333333 = "3333333333333";
    Sp04FromAFMSMpan sp04FromAfmsMpan1 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan1.setMpan(new MPANCore(mpan1111111111111));
    Sp04FromAFMSMpan sp04FromAfmsMpan2 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan2.setMpan(new MPANCore(mpan2222222222222));
    Sp04FromAFMSMpan sp04FromAfmsMpan3 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan3.setMpan(new MPANCore(mpan3333333333333));
    afmsMpans.put(mpan1111111111111, sp04FromAfmsMpan1);
    afmsMpans.put(mpan2222222222222, sp04FromAfmsMpan2);
    afmsMpans.put(mpan3333333333333, sp04FromAfmsMpan3);
    p28Mpans.put(mpan1111111111111, new P0028Active(null, null, null, null, null, null, null,
                                                    new MPANCore(mpan1111111111111), null));
    p28Mpans.put(mpan2222222222222, new P0028Active(null, null, null, null, null, null, null,
                                                    new MPANCore(mpan2222222222222), null));
    IterableMap<String, Object> p28AndAfmsMpans = sp04Service.combineP0028AndAfmsMpans(afmsMpans, p28Mpans);
    assertNotNull(p28AndAfmsMpans);
    assertEquals(3, p28AndAfmsMpans.size());
  }

  /**
   * Check mpans are removed from p0028active and sp04FromAfmsMpan records which are to be excluded from sp04 report.
   */
  @Test
  public void getAllSortedRecordsExcludeMpansFromSp04Report()
  {
    Freeze.freeze(new DateTime(2010, 11, 10, 0, 0, 0, 0));

    SupplierDao mockSupplierDao = createMock(SupplierDao.class);
    P0028ActiveDao mockP0028ActiveDao = createMock(P0028ActiveDao.class);
    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    Sp04FromAFMSMpanDao mockSp04FromAFMSMpanDao = createMock(Sp04FromAFMSMpanDao.class);

    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);

    final ParmsReportingPeriod period = new ParmsReportingPeriod(2, 2010);
    Sp04RowCalculator rowCalculator = new Sp04RowCalculator()
    {
      @Override
      public ParmsReportingPeriod getParmsReportingPeriod()
      {
        return period;
      }
    };

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);

    rowCalculator.setAFMSMeterDao(mockAFMSMeterDao);
    rowCalculator.setAFMSMpanDao(mockAFMSMpanDao);
    rowCalculator.setParmsReportingPeriod(period);
    rowCalculator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    rowCalculator.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    sp04AfmsMpanValidator.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    rowCalculator.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);

    Sp04Calculator calculator = new Sp04Calculator();
    calculator.setAFMSMpanDao(mockAFMSMpanDao);
    calculator.setSp04RowCalculator(rowCalculator);

    Sp04ServiceImpl sp04Service = new Sp04ServiceImpl()
    {
      @Override
      boolean isSp04Valid(Sp04Data sp04Data)
      {
        return true;
      }
    };
    sp04Service.setSupplierDao(mockSupplierDao);
    sp04Service.setP0028ActiveDao(mockP0028ActiveDao);
    sp04Service.setSp04Calculator(calculator);
    sp04Service.setSp04FromAFMSMpanDao(mockSp04FromAFMSMpanDao);
    sp04Service.setAFMSMpanDao(mockAFMSMpanDao);
    sp04Service.setAFMSMeterDao(mockAFMSMeterDao);

    IterableMap<String, P0028Active> currentP0028ActivesToReport = new HashedMap<String, P0028Active>();
    String mpan1111111111111 = "1111111111111";
    String mpan2222222222222 = "2222222222222";
    String mpan3333333333333 = "3333333333333";
    String mpan4444444444444 = "4444444444444";
    String mpan5555555555555 = "5555555555555"; // included in report
    String mpan6666666666666 = "6666666666666";
    String mpan7777777777777 = "7777777777777"; // included in report
    String exclMpan1111111111111 = "1111111111111";
    String exclMpan2222222222222 = "2222222222222";
    String exclMpan3333333333333 = "3333333333333";
    String exclMpan4444444444444 = "4444444444444";
    String exclMpan6666666666666 = "6666666666666";

    Set<String> afmsMpansIncludedInReport = new HashSet<String>();
    afmsMpansIncludedInReport.add(mpan5555555555555);
    afmsMpansIncludedInReport.add(mpan7777777777777);

    Long supplierPk = 234L;
    String supplierId = "FROG";
    Supplier expectedSupplier = new Supplier(supplierId);
    expectedSupplier.setPk(supplierPk);

    DateTime receiptDate = new DateTime(2011, 5, 15, 0, 0, 0, 0);
    P0028Active p28Active1 = new P0028Active();
    p28Active1.setMpanCore(new MPANCore(mpan1111111111111));
    P0028Data p28Data1 = new P0028Data();
    p28Data1.setMpanUniqId(111111L);
    P0028File p28File1 = new P0028File();
    p28File1.setReceiptDate(receiptDate);
    p28Data1.setP0028File(p28File1);
    p28Active1.setLatestP0028Data(p28Data1);
    p28Active1.setSupplier(expectedSupplier);

    P0028Active p28Active2 = new P0028Active();
    p28Active2.setMpanCore(new MPANCore("2222222222222"));
    P0028Data p28Data2 = new P0028Data();
    p28Data2.setMpanUniqId(222222L);
    P0028File p28File2 = new P0028File();
    p28File2.setReceiptDate(receiptDate);
    p28Data2.setP0028File(p28File2);
    p28Active2.setLatestP0028Data(p28Data2);
    p28Active2.setSupplier(expectedSupplier);

    P0028Active p28Active3 = new P0028Active();
    p28Active3.setMpanCore(new MPANCore("3333333333333"));
    P0028Data p28Data3 = new P0028Data();
    p28Data3.setMpanUniqId(333333L);
    P0028File p28File3 = new P0028File();
    p28File3.setReceiptDate(receiptDate);
    p28Data3.setP0028File(p28File3);
    p28Active3.setLatestP0028Data(p28Data3);
    p28Active3.setSupplier(expectedSupplier);

    P0028Active p28Active4 = new P0028Active();
    p28Active4.setMpanCore(new MPANCore("4444444444444"));
    P0028Data p28Data4 = new P0028Data();
    p28Data4.setMpanUniqId(444444L);
    P0028File p28File4 = new P0028File();
    p28File4.setReceiptDate(receiptDate);
    p28Data4.setP0028File(p28File4);
    p28Active4.setLatestP0028Data(p28Data4);
    p28Active4.setSupplier(expectedSupplier);

    P0028Active p28Active5 = new P0028Active();
    p28Active5.setMpanCore(new MPANCore("5555555555555"));
    P0028Data p28Data5 = new P0028Data();
    p28Data5.setMpanUniqId(555555L);
    P0028File p28File5 = new P0028File();
    p28File5.setReceiptDate(new DateTime(2009, 8, 1, 0, 0, 0, 0));
    p28Data5.setP0028File(p28File5);
    p28Active5.setLatestP0028Data(p28Data5);
    p28Active5.setSupplier(expectedSupplier);
    p28Active5.setMaxDemand(155L);
    p28Active5.setLatestP0028Data(p28Data5);
    String msid5 = "55555";
    p28Active5.setMeterSerialId(msid5);

    P0028Active p28Active6 = new P0028Active();
    p28Active6.setMpanCore(new MPANCore("6666666666666"));
    P0028Data p28Data6 = new P0028Data();
    p28Data6.setMpanUniqId(666666L);
    P0028File p28File6 = new P0028File();
    p28File6.setReceiptDate(receiptDate);
    p28Data6.setP0028File(p28File6);
    p28Active6.setLatestP0028Data(p28Data6);
    p28Active6.setSupplier(expectedSupplier);

    currentP0028ActivesToReport.put(mpan1111111111111, p28Active1);
    currentP0028ActivesToReport.put(mpan2222222222222, p28Active2);
    currentP0028ActivesToReport.put(mpan3333333333333, p28Active3);
    currentP0028ActivesToReport.put(mpan4444444444444, p28Active4);
    currentP0028ActivesToReport.put(mpan5555555555555, p28Active5);
    currentP0028ActivesToReport.put(mpan6666666666666, p28Active6);

    List<String> mpansToExclude = new ArrayList<String>();
    mpansToExclude.add(exclMpan1111111111111);
    mpansToExclude.add(exclMpan2222222222222);
    mpansToExclude.add(exclMpan3333333333333);
    mpansToExclude.add(exclMpan4444444444444);
    mpansToExclude.add(exclMpan6666666666666);

    // simulate valid afms mpan and associated meter, registers, readings
    AFMSMpan afmsMpan7777777777777 = new AFMSMpan();
    long afmsMpan7777777777777Pk = 77777L;
    afmsMpan7777777777777.setPk(afmsMpan7777777777777Pk);
    afmsMpan7777777777777.setMpanCore(mpan7777777777777);
    afmsMpan7777777777777.setSupplierId(supplierId);

    List<AFMSMeter> afmsMeters7777777777777s = new ArrayList<AFMSMeter>();
    AFMSMeter afmsMeter7777777777777 = new AFMSMeter();
    afmsMeter7777777777777.setPk(77777L);
    afmsMeter7777777777777.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter7777777777777.setMpan(afmsMpan7777777777777);
    afmsMeters7777777777777s.add(afmsMeter7777777777777);

    Long afmsMpan5555555555555Pk = 55555L;
    AFMSMpan mpan555555L = new AFMSMpan();
    mpan555555L.setPk(afmsMpan5555555555555Pk);
    mpan555555L.setMpanCore(mpan5555555555555);
    mpan555555L.setSupplierId(supplierId);

    Collection<AFMSMeter> afmsMeters = new ArrayList<AFMSMeter>();
    AFMSMeter afmsMeter = new AFMSMeter();
    afmsMeter.setPk(55555L);
    afmsMeter.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter.setMpan(mpan555555L);
    afmsMeter.setMeterSerialId(msid5);
    afmsMeters.add(afmsMeter);

    AFMSMeter afmsMeter2 = new AFMSMeter();
    afmsMeter2.setPk(77777L);
    afmsMeter2.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter2.setMpan(afmsMpan7777777777777);
    afmsMeter2.setLastUpdated(new DateTime());
    String msid2 = "77777";
    afmsMeter2.setMeterSerialId(msid2);
    afmsMeters.add(afmsMeter2);

    List<AFMSMeterRegister> meterRegisters = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister meterRegister1 = new AFMSMeterRegister();
    meterRegister1.setMeterRegType(MeterRegisterType.M.getValue());
    meterRegister1.setEffectiveFromDate(period.getStartOfMonth(true).toDateTime());
    meterRegister1.setMeasurementQuantityId(MEASUREMENT_QUANTITY_ID.KW.getValue());
    meterRegister1.setMeterRegisterId(METER_REGISTER_ID.MD.getValue());

    List<AFMSMeterRegReading> readings = new ArrayList<AFMSMeterRegReading>();
    AFMSMeterRegReading reading1 = new AFMSMeterRegReading();
    reading1.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading1.setRegisterReading(155F);
    DateTime received20110615 = new DateTime(2011, 6, 15, 0, 0 ,0 ,0);
    reading1.setMeterReadingDate(received20110615);
    reading1.setDateReceived(received20110615);
    readings.add(reading1);

    meterRegister1.setMeterRegReadings(readings);
    meterRegisters.add(meterRegister1);
    meterRegisters.add(meterRegister1);
    afmsMeter.setMeterRegisters(meterRegisters);

    List<Sp04FromAFMSMpan> sp04FromAfmsMpans = new ArrayList<Sp04FromAFMSMpan>();

    Sp04FromAFMSMpan sp04FromAfmsMpan1 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan1.setPk(1L);
    sp04FromAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime().minusMonths(9));
    sp04FromAfmsMpan1.setCalculatedStandard1(1L);
    sp04FromAfmsMpan1.setCalculatedStandard2(1L);
    sp04FromAfmsMpan1.setCalculatedStandard3(1F);
    sp04FromAfmsMpan1.setMpan(new MPANCore(mpan7777777777777));
    sp04FromAfmsMpan1.setMpanFk(afmsMpan7777777777777.getPk());
    sp04FromAfmsMpan1.setMaxDemand(101F);
    sp04FromAfmsMpan1.setMeterId(msid2);

    sp04FromAfmsMpans.add(sp04FromAfmsMpan1);

    expect(mockSp04FromAFMSMpanDao.get(supplierPk, new DateMidnight().toDateTime()))
      .andReturn(sp04FromAfmsMpans).anyTimes();
    expect(mockAFMSMpanDao.getById(afmsMpan7777777777777.getPk())).andReturn(afmsMpan7777777777777).anyTimes();
    expect(mockAFMSMeterDao.getByMpanUniqueId(afmsMpan7777777777777.getPk())).andReturn(afmsMeters).anyTimes();

    Collection<AFMSAregiProcess> afmsAregiProcesses = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0268ReceivedDate(new DateTime());
    aregiProc1.setD0150Received(new DateTime());
    afmsAregiProcesses.add(aregiProc1);

    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(afmsMpan7777777777777.getPk()))
      .andReturn(afmsAregiProcesses).anyTimes();

    expect(mockSupplierDao.getById(supplierPk)).andReturn(expectedSupplier).anyTimes();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan7777777777777), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(afmsMpan7777777777777).anyTimes();

    expect(mockP0028ActiveDao.get(expectedSupplier, new DateMidnight().toDateTime()))
      .andReturn(currentP0028ActivesToReport).anyTimes();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan5555555555555), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(mpan555555L).anyTimes();
    expect(mockAFMSMeterDao.getByMpanUniqueId(afmsMpan5555555555555Pk)).andReturn(afmsMeters).anyTimes();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan1111111111111), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(null).once();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan2222222222222), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(null).once();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan4444444444444), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(null).once();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan6666666666666), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(null).once();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan3333333333333), expectedSupplier,
      new ParmsReportingPeriod(10, 2010))).andReturn(null).once();
    expect(mockAFMSMpanDao.getAfmsMpan(new MPANCore(mpan5555555555555), expectedSupplier,
      new ParmsReportingPeriod(2, 2010))).andReturn(mpan555555L).anyTimes();

    expect(mockAFMSMeterDao.getLatestMeterForMeterSerialIdAndMpanUniqId(msid2, afmsMpan7777777777777Pk)).andReturn(afmsMeter2).once();
    expect(mockAFMSMeterDao.getLatestMeterForMeterSerialIdAndMpanUniqId(msid5, afmsMpan5555555555555Pk)).andReturn(afmsMeter).once();

    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(afmsMpan5555555555555Pk)).andReturn(afmsAregiProcesses).anyTimes();

    replay(mockSupplierDao, mockP0028ActiveDao, mockAFMSMeterDao, mockAFMSMpanDao, mockSp04FromAFMSMpanDao,
           mockAFMSAregiProcessDao);

    // call service method
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    List<AdminListDTO> changedP0028ActivesToReportWithoutExcludedMpans
      = sp04Service.getAllSortedRecords(supplierPk, mpansToExclude, lastMonthPrp);

    verify(mockSupplierDao, mockP0028ActiveDao, mockAFMSMeterDao, mockAFMSMpanDao, mockSp04FromAFMSMpanDao,
           mockAFMSAregiProcessDao);

    assertNotNull(changedP0028ActivesToReportWithoutExcludedMpans);

    assertEquals(2, changedP0028ActivesToReportWithoutExcludedMpans.size());

    for (AdminListDTO p28OrAfmsActiveToReport : changedP0028ActivesToReportWithoutExcludedMpans)
    {
      List<Object> identifierData = p28OrAfmsActiveToReport.getIdentifier();

      if (identifierData != null && identifierData.size() > 0)
      {
        String mpan = (String) identifierData.get(0);
        assertTrue(afmsMpansIncludedInReport.contains(mpan));
      }
    }

    Freeze.thaw();
  }

  /**
   * Check empty Sp04Data object return false for null.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_null() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();
    //test method
    assertFalse(sut.isSp04Valid(null));
  }

  /**
   * Check Sp04Data object with invalid values returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsNotSetup() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = new Sp04Data();
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with valid values returns true.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    //test method
    assertTrue(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with invalid mpan returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup_noMpan() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    sp04Data.setMpanCore(null);
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with invalid standard1 returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup_noStandard1() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    sp04Data.setStandard1(null);
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with invalid standard2 returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup_noStandard2() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    sp04Data.setStandard2(null);
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with invalid standard3 returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup_noStandard3() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    sp04Data.setStandard3(null);
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * Check Sp04Data object with Sp04FaultReasonType.MID_AFTER_T returns false.
   *
   * @throws Exception
   */
  @Test
  public void isSp04Valid_allValsSetup_ButThereisAFault() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();

    Sp04Data sp04Data = getSp04Data();
    sp04Data.setSp04FaultReason(Sp04FaultReasonType.MID_AFTER_T);
    //test method
    assertFalse(sut.isSp04Valid(sp04Data));
  }

  /**
   * @throws Exception
   */
  @Test
  public void combineTwoSourcesOfSp04Mpans_parmsEmpty_sp04ContainsOne() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();
    String mpan = "1232";

    MultiHashMap<String, AdminListDTO> allSp04AfmsData = new MultiHashMap<String, AdminListDTO>();
    MultiHashMap<String, AdminListDTO> emptyParms = new MultiHashMap<String, AdminListDTO>();

    List<Object> pv1 = new ArrayList<Object>();
    pv1.add(false);
    pv1.add(false);
    pv1.add(true);

    List<Object> pk = new ArrayList<Object>();
    String pk_1 = "pk_1";
    pk.add(pk_1);
    AdminListDTO p1 = new AdminListDTO(pk, pv1);

    allSp04AfmsData.put(mpan, p1);

    //test method
    List<AdminListDTO> results = sut.combineTwoSourcesOfSp04Mpans(allSp04AfmsData, emptyParms);

    assertNotNull(results);
    assertEquals(1, results.size());

    assertEquals(1, results.get(0).getIdentifier().size());
    assertEquals("pk_1", results.get(0).getIdentifier().get(0));

    assertFalse(((Boolean)results.get(0).getValues().get(0)) );
    assertFalse(((Boolean)results.get(0).getValues().get(1)) );
    assertTrue(((Boolean)results.get(0).getValues().get(2)) );
  }

  /**
   * @throws Exception
   */
  @Test
  public void combineTwoSourcesOfSp04Mpans_OneParms_sp04Empty() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();
    String mpan = "1232";

    MultiHashMap<String, AdminListDTO> emptySp04 = new MultiHashMap<String, AdminListDTO>();
    MultiHashMap<String, AdminListDTO> allParms = new MultiHashMap<String, AdminListDTO>();

    List<Object> pv1 = new ArrayList<Object>();
    pv1.add(false);
    pv1.add(true);
    pv1.add(false);

    List<Object> pk = new ArrayList<Object>();
    String pk_1 = "pk_1";
    pk.add(pk_1);
    AdminListDTO p1 = new AdminListDTO(pk, pv1);

    allParms.put(mpan, p1);

    //test method
    List<AdminListDTO> results = sut.combineTwoSourcesOfSp04Mpans(emptySp04, allParms);

    assertNotNull(results);
    assertEquals(1, results.size());

    assertEquals(1, results.get(0).getIdentifier().size());
    assertEquals("pk_1", results.get(0).getIdentifier().get(0));

    assertFalse(((Boolean)results.get(0).getValues().get(0)) );
    assertTrue(((Boolean)results.get(0).getValues().get(1)) );
    assertFalse(((Boolean)results.get(0).getValues().get(2)) );
  }

  /**
   * @throws Exception
   */
  @Test
  public void combineTwoSourcesOfSp04Mpans_OneParms_OneSp04() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();
    String mpan = "1111";

    String mpan2 = "2222";

    MultiHashMap<String, AdminListDTO> oneSp04 = new MultiHashMap<String, AdminListDTO>();
    MultiHashMap<String, AdminListDTO> allParms = new MultiHashMap<String, AdminListDTO>();

    List<Object> pv1 = new ArrayList<Object>();
    pv1.add(false);
    pv1.add(true);
    pv1.add(false);
    List<Object> pk = new ArrayList<Object>();
    String pk_1 = "pk_1";
    pk.add(pk_1);
    AdminListDTO p1 = new AdminListDTO(pk, pv1);
    allParms.put(mpan, p1);

    List<Object> pv2 = new ArrayList<Object>();
    pv2.add(false);
    pv2.add(false);
    pv2.add(true);
    List<Object> pk2 = new ArrayList<Object>();
    String pk_2 = "pk_2";
    pk2.add(pk_2);
    AdminListDTO p2 = new AdminListDTO(pk2, pv2);
    oneSp04.put(mpan2, p2);

    //test method
    List<AdminListDTO> results = sut.combineTwoSourcesOfSp04Mpans(oneSp04, allParms);

    assertNotNull(results);
    assertEquals(2, results.size());

    boolean foundpk1 = false;
    boolean foundpk2 = false;

    for (AdminListDTO result : results)
    {
      if (result.getIdentifier().get(0).equals("pk_1"))
      {
        foundpk1 = true;
        assertFalse(((Boolean)result.getValues().get(0)) );
        assertTrue(((Boolean)result.getValues().get(1)) );
        assertFalse(((Boolean)result.getValues().get(2)) );
      }
      if (result.getIdentifier().get(0).equals("pk_2"))
      {
        foundpk2 = true;
        assertFalse(((Boolean)result.getValues().get(0)) );
        assertFalse(((Boolean)result.getValues().get(1)) );
        assertTrue(((Boolean)result.getValues().get(2)) );
      }
    }

    assertTrue(foundpk1);
    assertTrue(foundpk2);
  }

  /**
   * @throws Exception
   */
  @Test
  public void combineTwoSourcesOfSp04Mpans_OneParms_OneSp04bothSameMpan() throws Exception
  {
    Sp04ServiceImpl sut = new Sp04ServiceImpl();
    String mpan = "1111";

    String mpan2 = "1111";

    MultiHashMap<String, AdminListDTO> oneSp04 = new MultiHashMap<String, AdminListDTO>();
    MultiHashMap<String, AdminListDTO> allParms = new MultiHashMap<String, AdminListDTO>();

    List<Object> pv1 = new ArrayList<Object>();
    pv1.add(false);
    pv1.add(false);
    pv1.add(false);

    List<Object> pk = new ArrayList<Object>();
    String pk_1 = "pk_1";
    pk.add(pk_1);
    AdminListDTO p1 = new AdminListDTO(pk, pv1);
    allParms.put(mpan, p1);

    List<Object> pv2 = new ArrayList<Object>();
    List<Object> pk2 = new ArrayList<Object>();
    String pk_2 = "pk_2";
    pk2.add(pk_2);
    AdminListDTO p2 = new AdminListDTO(pk2, pv2);
    oneSp04.put(mpan2, p2);

    //test method
    List<AdminListDTO> results = sut.combineTwoSourcesOfSp04Mpans(oneSp04, allParms);

    assertNotNull(results);
    assertEquals(1, results.size());

    assertEquals(1, results.get(0).getIdentifier().size());
    assertEquals("pk_1", results.get(0).getIdentifier().get(0));
    assertFalse(((Boolean)results.get(0).getValues().get(0)) );
    assertFalse(((Boolean)results.get(0).getValues().get(1)) );
    assertTrue(((Boolean)results.get(0).getValues().get(2)) );
  }

  /**
   *
   */
  @Test
  public void getAllSortedRecords_oneRowFromEach()
  {
    List<AdminListDTO> afmsDTO = new ArrayList<AdminListDTO>();
    List<AdminListDTO> parmsDTO = new ArrayList<AdminListDTO>();

    List<Object> pv1 = new ArrayList<Object>();
    List<Object> pk = new ArrayList<Object>();
    String pk_1 = "pk_1";
    pk.add(pk_1);
    final AdminListDTO p1 = new AdminListDTO(pk, pv1);
    afmsDTO.add(p1);

    List<Object> pv2 = new ArrayList<Object>();
    List<Object> pk2 = new ArrayList<Object>();
    String pk_2 = "pk_2";
    pk2.add(pk_2);
    final AdminListDTO p2 = new AdminListDTO(pk2, pv2);
    parmsDTO.add(p2);

    Sp04ServiceImpl sut = new Sp04ServiceImpl()
    {
      @Override
      public MultiHashMap<String, AdminListDTO> getRowsFromAfmsSp04Mpans(Long aSupplierId, List<String> aMpansToExclude,
                                                                         ParmsReportingPeriod aLastMonthPrp)
      {
        MultiHashMap<String, AdminListDTO> r1 = new MultiHashMap<String, AdminListDTO>();
        r1.put("r1", p1);
        return r1;
      }
      @Override
      public MultiHashMap<String, AdminListDTO> getAllP0028ForSp04SortedRecords(Long aSupplierId, List<String> aMpansToExclude,
                                                                         ParmsReportingPeriod aLastMonthPrp)
      {
        MultiHashMap<String, AdminListDTO> r2 = new MultiHashMap<String, AdminListDTO>();
        r2.put("r2", p2);
        return r2;
      }
    };

    //test method
    List<AdminListDTO> results = sut.getAllSortedRecords(1L);

    assertNotNull(results);
    assertEquals(2, results.size());

    // check for both entries
    boolean foundpk1 = false;
    boolean foundpk2 = false;

    AdminListDTO dto1 = results.get(0);
    assertNotNull(dto1);

    assertEquals(1L, dto1.getIdentifier().size());
    if (dto1.getIdentifier().get(0).equals("pk_1"))
    {
      foundpk1 = true;
    }
    else if ((dto1.getIdentifier().get(0).equals("pk_2")))
    {
      foundpk2 = true;
    }

    AdminListDTO dto2 = results.get(1);
    assertNotNull(dto2);

    assertEquals(1L, dto2.getIdentifier().size());
    if (dto2.getIdentifier().get(0).equals("pk_1"))
    {
      foundpk1 = true;
    }
    else if ((dto2.getIdentifier().get(0).equals("pk_2")))
    {
      foundpk2 = true;
    }
    assertTrue("not found entry from AFMS", foundpk1);
    assertTrue("not found entry from PARMS", foundpk2);
  }

  private Sp04Data getSp04Data()
  {
    Sp04Data sp04Data = new Sp04Data();
    sp04Data.setMpanCore(new MPANCore("1000000000002"));
    sp04Data.setStandard1(1L);
    sp04Data.setStandard2(2L);
    sp04Data.setStandard3(3F);
    sp04Data.setGspGroupId("_A");

    return sp04Data;
  }
}