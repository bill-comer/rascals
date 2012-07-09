package uk.co.utilisoft.parms.file.sp04;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSAregiProcessDao;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading.BSC_VALIDATION_STATUS;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister.MEASUREMENT_QUANTITY_ID;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister.METER_REGISTER_ID;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MeterRegisterType;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidatorImpl;
import uk.co.utilisoft.utils.Freeze;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class Sp04RowCalculatorUnitTest
{
  /**
   * Build Sp04Data object with error for MD <= 100kw
   */
  @Test
  public void buildAfmsSp04DataRowMDLessThanOrEqualToThresholdDefault()
  {
    Sp04RowCalculator calc = new Sp04RowCalculator();

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    calc.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator= new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    calc.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setAFMSMeterDao(mockAFMSMeterDao);

    calc.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    DateMidnight startOf20110901 = new DateMidnight();
    ParmsReportingPeriod reportingPeriod = new ParmsReportingPeriod(startOf20110901);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String mpan = "2222222222222";
    calc.setParmsReportingPeriod(reportingPeriod);
    calc.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    AFMSMpan afmsMpan = new AFMSMpan();
    long mpanPk = 9999L;
    afmsMpan.setPk(mpanPk);
    afmsMpan.setMpanCore(mpan);
    afmsMpan.setGridSupplyPoint("_GSP");
    afmsMpan.setMeasurementClassification("A");
    afmsMpan.setSupplierId(supplierId);
    AFMSMeter afmsMeter = new AFMSMeter();
    afmsMeter.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter.setMpan(afmsMpan);
    afmsMeter.setMeterSerialId("ameterserialid");

    List<AFMSMeterRegister> meterRegisters = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister meterRegister1 = new AFMSMeterRegister();
    meterRegister1.setMeterRegType(MeterRegisterType.M.getValue());
    meterRegister1.setEffectiveFromDate(startOf20110901.toDateTime());
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

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading2.setRegisterReading(200F);
    DateTime received20110616 = new DateTime(2011, 6, 16, 0, 0 ,0 ,0);
    reading2.setMeterReadingDate(received20110616);
    reading2.setDateReceived(received20110616);
    readings.add(reading2);

    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading3.setRegisterReading(155F);
    DateTime received20110617 = new DateTime(2011, 6, 17, 0, 0 ,0 ,0);
    reading3.setDateReceived(received20110617);
    reading3.setMeterReadingDate(received20110617);
    readings.add(reading3);

    meterRegister1.setMeterRegReadings(readings);
    meterRegisters.add(meterRegister1);
    afmsMeter.setMeterRegisters(meterRegisters);
    afmsMpan.getMeters().add(afmsMeter);
    Sp04FromAFMSMpan sp04FromAfmsMpan = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan.setMpanFk(afmsMpan.getPk());
    sp04FromAfmsMpan.setCalculatedMeterInstallationDeadline(received20110617.plusMonths(3));
    sp04FromAfmsMpan.setMaxDemand(100F); // md <= default threshold
    sp04FromAfmsMpan.setMeterId("ameterserialid");

    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    calc.setAFMSMpanDao(mockAFMSMpanDao);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(startOf20110901.toDateTime());
    aregiProcs.add(aregiProc1);

    expect(mockAFMSMpanDao.getById(mpanPk)).andReturn(afmsMpan).once();
    expect(mockAFMSMeterDao.getByMpanUniqueId(mpanPk)).andReturn(afmsMpan.getMeters()).anyTimes();
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mpanPk)).andReturn(aregiProcs).anyTimes();

    replay(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    Sp04Data sp04Data = calc.buildAfmsSp04DataRecord(sp04FromAfmsMpan, reportingPeriod, supplier);

    verify(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    assertNotNull(sp04Data);
    assertEquals(sp04Data.getSp04FaultReason(), Sp04FaultReasonType.getMDLessThanOneHundred());
    assertNull(sp04Data.getStandard1());
    assertNull(sp04Data.getStandard2());
    assertNull(sp04Data.getStandard3());
  }

  /**
   * Build Sp04Data object with error for MD >= configured threshold
   */
  @Test
  public void buildAfmsSp04DataRowMDExceededThresholdConfigured()
  {
    Sp04RowCalculator calc = new Sp04RowCalculator();

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    calc.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return 200F;
      }
    };

    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    calc.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setAFMSMeterDao(mockAFMSMeterDao);

    calc.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    DateMidnight startOf20110901 = new DateMidnight();
    ParmsReportingPeriod reportingPeriod = new ParmsReportingPeriod(startOf20110901);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String mpan = "2222222222222";
    calc.setParmsReportingPeriod(reportingPeriod);
    calc.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    AFMSMpan afmsMpan = new AFMSMpan();
    long mpanPk = 9999L;
    afmsMpan.setPk(mpanPk);
    afmsMpan.setMpanCore(mpan);
    afmsMpan.setGridSupplyPoint("_GSP");
    afmsMpan.setMeasurementClassification("A");
    afmsMpan.setSupplierId(supplierId);
    AFMSMeter afmsMeter = new AFMSMeter();
    afmsMeter.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter.setMpan(afmsMpan);
    afmsMeter.setMeterSerialId("ameterserialid");

    List<AFMSMeterRegister> meterRegisters = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister meterRegister1 = new AFMSMeterRegister();
    meterRegister1.setMeterRegType(MeterRegisterType.M.getValue());
    meterRegister1.setEffectiveFromDate(startOf20110901.toDateTime());
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

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading2.setRegisterReading(200F);
    DateTime received20110616 = new DateTime(2011, 6, 16, 0, 0 ,0 ,0);
    reading2.setMeterReadingDate(received20110616);
    reading2.setDateReceived(received20110616);
    readings.add(reading2);

    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading3.setRegisterReading(155F);
    DateTime received20110617 = new DateTime(2011, 6, 17, 0, 0 ,0 ,0);
    reading3.setDateReceived(received20110617);
    reading3.setMeterReadingDate(received20110617);
    readings.add(reading3);

    meterRegister1.setMeterRegReadings(readings);
    meterRegisters.add(meterRegister1);
    afmsMeter.setMeterRegisters(meterRegisters);
    afmsMpan.getMeters().add(afmsMeter);
    Sp04FromAFMSMpan sp04FromAfmsMpan = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan.setMpanFk(afmsMpan.getPk());
    sp04FromAfmsMpan.setCalculatedMeterInstallationDeadline(received20110617.plusMonths(3));
    sp04FromAfmsMpan.setMaxDemand(201F); // md > threshold
    sp04FromAfmsMpan.setMeterId("ameterserialid");

    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    calc.setAFMSMpanDao(mockAFMSMpanDao);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(startOf20110901.toDateTime());
    aregiProcs.add(aregiProc1);

    expect(mockAFMSMpanDao.getById(mpanPk)).andReturn(afmsMpan).once();
    expect(mockAFMSMeterDao.getByMpanUniqueId(mpanPk)).andReturn(afmsMpan.getMeters()).anyTimes();
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mpanPk)).andReturn(aregiProcs).anyTimes();

    replay(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    Sp04Data sp04Data = calc.buildAfmsSp04DataRecord(sp04FromAfmsMpan, reportingPeriod, supplier);

    verify(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    assertNotNull(sp04Data);
    assertEquals(sp04Data.getSp04FaultReason(), Sp04FaultReasonType.getMDMatchesOrExceedsThreshold());
    assertNull(sp04Data.getStandard1());
    assertNull(sp04Data.getStandard2());
    assertNull(sp04Data.getStandard3());
  }

  /**
   * Build Sp04Data object for afms
   */
  @Test
  public void buildAfmsSp04DataRowCalculatesStandards()
  {
    Sp04RowCalculator calc = new Sp04RowCalculator();

    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    calc.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    sp04AfmsMpanValidator.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);

    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    calc.setAFMSMeterDao(mockAFMSMeterDao);
    sp04AfmsMpanValidator.setAFMSMeterDao(mockAFMSMeterDao);

    calc.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    DateMidnight startOf20110901 = new DateMidnight();
    ParmsReportingPeriod reportingPeriod = new ParmsReportingPeriod(startOf20110901);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);
    String mpan = "2222222222222";
    calc.setParmsReportingPeriod(reportingPeriod);
    calc.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    AFMSMpan afmsMpan = new AFMSMpan();
    long mpanPk = 9999L;
    afmsMpan.setPk(mpanPk);
    afmsMpan.setMpanCore(mpan);
    afmsMpan.setGridSupplyPoint("_GSP");
    afmsMpan.setMeasurementClassification("A");
    afmsMpan.setSupplierId(supplierId);
    AFMSMeter afmsMeter = new AFMSMeter();
    afmsMeter.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter.setMpan(afmsMpan);
    afmsMeter.setMeterSerialId("ameterserialid");

    List<AFMSMeterRegister> meterRegisters = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister meterRegister1 = new AFMSMeterRegister();
    meterRegister1.setMeterRegType(MeterRegisterType.M.getValue());
    meterRegister1.setEffectiveFromDate(startOf20110901.toDateTime());
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

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading2.setRegisterReading(200F);
    DateTime received20110616 = new DateTime(2011, 6, 16, 0, 0 ,0 ,0);
    reading2.setMeterReadingDate(received20110616);
    reading2.setDateReceived(received20110616);
    readings.add(reading2);

    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading3.setRegisterReading(155F);
    DateTime received20110617 = new DateTime(2011, 6, 17, 0, 0 ,0 ,0);
    reading3.setDateReceived(received20110617);
    reading3.setMeterReadingDate(received20110617);
    readings.add(reading3);

    meterRegister1.setMeterRegReadings(readings);
    meterRegisters.add(meterRegister1);
    afmsMeter.setMeterRegisters(meterRegisters);
    afmsMpan.getMeters().add(afmsMeter);
    Sp04FromAFMSMpan sp04FromAfmsMpan = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan.setMpanFk(afmsMpan.getPk());
    sp04FromAfmsMpan.setCalculatedMeterInstallationDeadline(received20110617.plusMonths(3));
    sp04FromAfmsMpan.setMaxDemand(101F);
    sp04FromAfmsMpan.setMeterId("ameterserialid");

    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    calc.setAFMSMpanDao(mockAFMSMpanDao);

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(startOf20110901.toDateTime());
    aregiProcs.add(aregiProc1);

    expect(mockAFMSMpanDao.getById(mpanPk)).andReturn(afmsMpan).once();
    expect(mockAFMSMeterDao.getByMpanUniqueId(mpanPk)).andReturn(afmsMpan.getMeters()).anyTimes();
    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mpanPk)).andReturn(aregiProcs).anyTimes();

    replay(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    Sp04Data sp04Data = calc.buildAfmsSp04DataRecord(sp04FromAfmsMpan, reportingPeriod, supplier);

    verify(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    assertNotNull(sp04Data);
    assertNotNull(sp04Data.getStandard1());
    assertNotNull(sp04Data.getStandard2());
    assertNotNull(sp04Data.getStandard3());
  }

  /**
   * Build Sp04Data object for an afms mpan
   */
  @Test
  public void buildSp04DataRecord()
  {
    Sp04RowCalculator calc = new Sp04RowCalculator();
    Sp04AfmsMpanValidator sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public Float getMaxDemandThreshold()
      {
        return null;
      }
    };

    calc.setSp04AfmsMpanValidator(sp04AfmsMpanValidator);
    AFMSMpanDao mockAFMSMpanDao = createMock(AFMSMpanDao.class);
    calc.setAFMSMpanDao(mockAFMSMpanDao);
    AFMSMeterDao mockAFMSMeterDao = createMock(AFMSMeterDao.class);
    calc.setAFMSMeterDao(mockAFMSMeterDao);
    AFMSAregiProcessDao mockAFMSAregiProcessDao = createMock(AFMSAregiProcessDao.class);
    calc.setAFMSAregiProcessDao(mockAFMSAregiProcessDao);
    calc.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateMidnight startOf20110901 = new DateMidnight();
    ParmsReportingPeriod reportingPeriod = new ParmsReportingPeriod(startOf20110901);
    calc.setParmsReportingPeriod(reportingPeriod);
    calc.setParmsReportingPeriod(reportingPeriod);
    String supplierId = "EBES";
    Supplier supplier = new Supplier(supplierId);

    String mpan = "2222222222222";

    AFMSMpan afmsMpan = new AFMSMpan();
    long mpanPk = 9999L;
    afmsMpan.setPk(mpanPk);
    afmsMpan.setMpanCore(mpan);
    afmsMpan.setGridSupplyPoint("_GSP");
    afmsMpan.setMeasurementClassification("A");
    afmsMpan.setSupplierId(supplierId);
    AFMSMeter afmsMeter = new AFMSMeter();
    afmsMeter.setSettlementDate(new DateTime(1971, 1, 1, 0, 0, 0, 0));
    afmsMeter.setMpan(afmsMpan);
    afmsMeter.setMeterSerialId("ameterserialid");

    List<AFMSMeterRegister> meterRegisters = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister meterRegister1 = new AFMSMeterRegister();
    meterRegister1.setMeterRegType(MeterRegisterType.M.getValue());
    meterRegister1.setEffectiveFromDate(startOf20110901.toDateTime());
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

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading2.setRegisterReading(200F);
    DateTime received20110616 = new DateTime(2011, 6, 16, 0, 0 ,0 ,0);
    reading2.setMeterReadingDate(received20110616);
    reading2.setDateReceived(received20110616);
    readings.add(reading2);

    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    reading3.setRegisterReading(155F);
    DateTime received20110617 = new DateTime(2011, 6, 17, 0, 0 ,0 ,0);
    reading3.setDateReceived(received20110617);
    reading3.setMeterReadingDate(received20110617);
    readings.add(reading3);

    meterRegister1.setMeterRegReadings(readings);
    meterRegisters.add(meterRegister1);
    afmsMeter.setMeterRegisters(meterRegisters);
    afmsMpan.getMeters().add(afmsMeter);

    DateTime mid = received20110617.plusMonths(3);
    Float maxDemand = 150F;

    Collection<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc1 = new AFMSAregiProcess();
    aregiProc1.setD0150Received(startOf20110901.toDateTime());
    aregiProcs.add(aregiProc1);

    expect(mockAFMSMeterDao.getByMpanUniqueId(mpanPk)).andReturn(afmsMpan.getMeters()).once();

    expect(mockAFMSAregiProcessDao.getByMpanUniqueId(mpanPk)).andReturn(aregiProcs).once();


    replay(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    Sp04Data sp04Data = calc.buildP0028Sp04DataRecord(reportingPeriod, afmsMpan, supplier, maxDemand, mid, "ameterserialid");

    verify(mockAFMSMpanDao, mockAFMSMeterDao, mockAFMSAregiProcessDao);

    assertNotNull(sp04Data);
    assertNotNull(sp04Data.getStandard1());
    assertNotNull(sp04Data.getStandard2());
    assertNotNull(sp04Data.getStandard3());
  }

  /**
   * Check calculated standards 1, 2, 3 for Sp04FromAfmsMpan are not empty.
   */
  @Test
  public void calculateStandards1_2_3AreNotNull()
  {
    Freeze.thaw();
    Freeze.freeze(1, 9, 2011);
    Sp04RowCalculator calc = new Sp04RowCalculator();

    Sp04FromAFMSMpan sp04FromAfmsMpan = new Sp04FromAFMSMpan();
    DateMidnight startOf20110901 = new DateMidnight();

    assertEquals(new DateTime(2011, 9, 1, 0, 0, 0, 0), startOf20110901.toDateTime());

    // simulate a reporting period created from current date 1/9/11
    ParmsReportingPeriod reportingPeriod = new ParmsReportingPeriod(startOf20110901);

    calc.setParmsReportingPeriod(reportingPeriod);
    calc.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime startOf1stMonthInPeriod = reportingPeriod.getStartOfFirstMonthInPeriod().toDateTime();
    assertEquals(new DateTime(2011, 9, 1, 0, 0, 0, 0), startOf1stMonthInPeriod);

    DateTime endOf1stMonthInPeriod = reportingPeriod.getStartOfNextMonthInPeriod().toDateTime();
    assertEquals(new DateTime(2011, 10, 1, 0, 0, 0, 0), endOf1stMonthInPeriod);

    DateTime date20110912 = new DateTime(2011, 9, 12, 0, 0, 0, 0);

    DateTime d0268Date = new DateTime().minusMonths(2);
    AFMSMeter meter = createHalfHourlyMeter(d0268Date);
    meter.setSettlementDate(date20110912);

    DateTime date20110914 = new DateTime(2011, 9, 14, 0, 0, 0, 0);
    sp04FromAfmsMpan.setCalculatedMeterInstallationDeadline(date20110914);
    Long standard1 = calc.calculateStandard1(sp04FromAfmsMpan.getCalculatedMeterInstallationDeadline(), reportingPeriod);
    Long standard2 = calc.calculateStandard2(sp04FromAfmsMpan.getCalculatedMeterInstallationDeadline(), meter.getMpan(), reportingPeriod);
    assertNotNull(standard1);
    assertNotNull(standard2);
    Float standard3 = calc.calculateStandard3(standard1, standard2);
    assertNotNull(standard3);

    Freeze.thaw();
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

