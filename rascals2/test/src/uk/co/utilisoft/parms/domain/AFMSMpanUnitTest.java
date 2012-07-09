package uk.co.utilisoft.parms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.util.StringUtils;

import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.afms.domain.AFMSMeter.METER_TYPE;
import uk.co.utilisoft.parms.MeterRegisterType;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class AFMSMpanUnitTest
{
  /**
   * Get NHH meters for an AFMSMpan. Meters without an outstationid, or are Smart meters are identified as NHH meters.
   */
  @Test
  public void getNonHalfHourlyMeters()
  {
    AFMSMpan mpan = new AFMSMpan();
    DateTime efd = new DateTime(2010, 1, 15, 0, 0, 0, 0);
    DateTime etd = efd.plusMonths(3);
    Collection<AFMSMeter> meters = new ArrayList<AFMSMeter>();

    AFMSMeter meter1 = new AFMSMeter();
    meter1.setMeterType(METER_TYPE.NCAMR.getValue()); // NHH meter
    meter1.setPk(1L);

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setOutstationId(null); // NHH meter
    meter2.setPk(2L);

    meters.add(meter1);
    meters.add(meter2);

    mpan.setMeters(meters);

    List<AFMSMeter> result = mpan.getNonHalfHourlyMeters(efd, etd);

    assertNotNull(result);
    assertEquals(2, result.size());

    for (int i = 1; i < (result.size() + 1); i++)
    {
      assertEquals(i, result.get(i - 1).getPk().intValue());
    }
  }

  /**
   * Get NHH meter registers for an AFMSMpan. Registers identified with measurement quantity KW, id M, and type 'MD' are NHH.
   */
  @Test
  public void getNonHalfHourlyRegisters()
  {
    DateTime now = new DateTime(2010, 1, 15, 0, 0, 0, 0);
    Freeze.freeze(now);
    AFMSMpan mpan = new AFMSMpan();
    DateTime efd = now.minusMonths(1);
    DateTime etd = now.plusMonths(3);
    Collection<AFMSMeter> meters = new ArrayList<AFMSMeter>();

    AFMSMeter meter1 = new AFMSMeter();
    meter1.setMeterType(METER_TYPE.NCAMR.getValue()); // NHH meter
    meter1.setSettlementDate(now.minusDays(5));
    meter1.setMpan(mpan);
    meter1.setPk(1L);

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setOutstationId(null); // NHH meter
    meter2.setSettlementDate(now);
    meter2.setMpan(mpan);
    meter2.setPk(2L);

    AFMSMeter meter3 = new AFMSMeter();
    meter3.setOutstationId(null); // NHH meter
    meter3.setSettlementDate(now);
    meter3.setMpan(mpan);
    meter3.setEffectiveToDateMSID(now.plusDays(1000));

    Collection<AFMSMeterRegister> registers1 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register1 = new AFMSMeterRegister();
    register1.setEffectiveFromDate(now);
    register1.setEffectiveToDate(null);
    register1.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
    register1.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue()); // max demand meter register id
    register1.setMeterRegType(MeterRegisterType.M.getValue()); // max demand meter register type
    register1.setPk(1L);
    registers1.add(register1);

    meter1.setMeterRegisters(registers1);

    Collection<AFMSMeterRegister> registers2 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register2 = new AFMSMeterRegister();
    register2.setEffectiveFromDate(now);
    register2.setEffectiveToDate(null);
    register2.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
    register2.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue()); // max demand meter register id
    register2.setMeterRegType(MeterRegisterType.M.getValue()); // max demand meter register type
    register2.setPk(2L);
    registers2.add(register2);

    meter2.setMeterRegisters(registers2);

    Collection<AFMSMeterRegister> registers3 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register3 = new AFMSMeterRegister();
    register3.setEffectiveFromDate(now);
    register3.setEffectiveToDate(null);
    register3.setPk(3L);
    registers3.add(register3);

    meter3.setMeterRegisters(registers3);

    meters.add(meter1);
    meters.add(meter2);
    meters.add(meter3);

    mpan.setMeters(meters);

    List<AFMSMeterRegister> result = mpan.getNonHalfHourlyMeterRegisters(efd, etd);

    assertNotNull(result);
    assertEquals(2, result.size());

    Freeze.thaw();
  }

  /**
   * Get NHH meter register readings from more than one meter. This test case represents a change of NHH meter due to expiry.
   */
  @Test
  public void getNonHalfHourlyReadings()
  {
    DateTime now = new DateTime(2010, 1, 15, 0, 0, 0, 0);
    Freeze.freeze(now);
    AFMSMpan mpan = new AFMSMpan();
    DateTime efd = now.minusMonths(1);
    DateTime etd = now.plusMonths(3);
    Collection<AFMSMeter> meters = new ArrayList<AFMSMeter>();

    AFMSMeter meter1 = new AFMSMeter();
    meter1.setMeterType(METER_TYPE.NCAMR.getValue()); // NHH meter
    meter1.setMeterSerialId("msid1");
    meter1.setPk(1L);
    meter1.setMpan(mpan);
    meter1.setSettlementDate(now.minusDays(20));

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setOutstationId(null); // NHH meter
    meter2.setMeterSerialId("msid2");
    meter2.setPk(2L);
    meter2.setMpan(mpan);
    meter2.setSettlementDate(now.minusDays(20));

    AFMSMeter meter3 = new AFMSMeter();
    meter3.setOutstationId("oustationid3"); // HH meter
    meter3.setMeterSerialId("msid3");
    meter3.setPk(3L);
    meter3.setMpan(mpan);
    meter3.setSettlementDate(now.minusDays(20));
    meter3.setEffectiveToDateMSID(now.plusDays(15));

    Collection<AFMSMeterRegister> registers1 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register1 = new AFMSMeterRegister();
    register1.setEffectiveFromDate(now);
    register1.setEffectiveToDate(null);
    register1.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
    register1.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue()); // max demand meter register id
    register1.setMeterRegType(MeterRegisterType.M.getValue()); // max demand meter register type
    register1.setPk(1L);
    register1.setMeter(meter1);

    List<AFMSMeterRegReading> readings1 = new ArrayList<AFMSMeterRegReading>();
    AFMSMeterRegReading read1 = new AFMSMeterRegReading();
    read1.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    read1.setDateReceived(now.plusDays(5));
    read1.setPk(1L);
    read1.setRegisterReading(111F);

    AFMSMeterRegReading read2 = new AFMSMeterRegReading();
    read2.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    read2.setDateReceived(now.plusDays(10));
    read2.setPk(2L);
    read2.setRegisterReading(222F);

    readings1.add(read1);
    readings1.add(read2);

    register1.setMeterRegReadings(readings1);

    registers1.add(register1);

    meter1.setMeterRegisters(registers1);

    Collection<AFMSMeterRegister> registers2 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register2 = new AFMSMeterRegister();
    register2.setEffectiveFromDate(now);
    register2.setEffectiveToDate(null);
    register2.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
    register2.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue()); // max demand meter register id
    register2.setMeterRegType(MeterRegisterType.M.getValue()); // max demand meter register type
    register2.setPk(2L);
    register2.setMeter(meter2);
    registers2.add(register2);

    List<AFMSMeterRegReading> readings2 = new ArrayList<AFMSMeterRegReading>();
    AFMSMeterRegReading read3 = new AFMSMeterRegReading();
    read3.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    read3.setDateReceived(now.plusDays(15));
    read3.setPk(3L);
    read3.setRegisterReading(333F);

    AFMSMeterRegReading read4 = new AFMSMeterRegReading();
    read4.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    read4.setDateReceived(now.plusDays(20));
    read4.setPk(4L);
    read4.setRegisterReading(444F);

    readings2.add(read3);
    readings2.add(read4);

    register2.setMeterRegReadings(readings2);

    meter2.setMeterRegisters(registers2);

    Collection<AFMSMeterRegister> registers3 = new ArrayList<AFMSMeterRegister>();
    AFMSMeterRegister register3 = new AFMSMeterRegister();
    register3.setEffectiveFromDate(now);
    register3.setEffectiveToDate(null);
    register3.setPk(3L);
    register3.setMeter(meter3);
    registers3.add(register3);

    meter3.setMeterRegisters(registers3);

    meters.add(meter1);
    meters.add(meter2);
    meters.add(meter3);

    mpan.setMeters(meters);

    Map<String, List<AFMSMeterRegReading>> result = mpan.getNonHalfHourlyMeterRegReadings(efd, etd);

    assertNotNull(result);
    assertEquals(1, result.size());

    String msids = result.keySet().iterator().next();

    assertNotNull(msids);

    String[] msidsArr = StringUtils.commaDelimitedListToStringArray(msids);

    assertEquals(2, msidsArr.length);

    Set<String> expMsids = new HashSet<String>();
    expMsids.add("msid1");
    expMsids.add("msid2");

    for (String msid : msidsArr)
    {
      assertTrue(expMsids.contains(msid.trim()));
    }

    List<AFMSMeterRegReading> resultReadings = result.get(msids);

    assertNotNull(resultReadings);
    assertEquals(4, resultReadings.size());

    Freeze.thaw();
  }
}
