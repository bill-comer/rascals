package uk.co.utilisoft.parms.file.sp04;

import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.easymock.IAnswer;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading.BSC_VALIDATION_STATUS;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.MeterRegisterType;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidatorImpl;
import uk.co.utilisoft.parms.web.service.sp04.Sp04Service;
import uk.co.utilisoft.utils.Freeze;

public class Sp04FromAfmsMpanBuilderMockTest
{
  private Sp04AfmsMpanValidator mSp04AfmsMpanValidator;

  /**
   * Create Sp04FromAFMSMpan record with potentially more than one linking meter and meter register
   * because of change of meter due to expiry.
   */
  @Test
  public void createNewSp04FromAFMSMpan()
  {
    try
    {
      Freeze.thaw();
      Freeze.freeze(new DateTime(2010, 1, 22, 0, 0, 0, 0));
      Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
      AFMSMpan mpan = new AFMSMpan();
      mpan.setMpanCore("0000000000001");
      mpan.setEffectiveFromDate(new DateTime().minusDays(40));

      AFMSAgent agent = new AFMSAgent();
      agent.setDataCollector("DC01");
      agent.setPk(1L);
      mpan.setAgent(agent);

      //example data note change of meters due to expiry are meter.pk=183094,202733
      // if newmeter.etdmsid==null and newmeter.j1254==oldmeter.etdmsid and oldmeter.j1254<newmeter.j1254
      /*
      meterpk   mpanlnk  etd_msid               j1254                   meterserialid    j0483 j0428
      176441    177882   2010-08-11 00:00:00.0  1997-10-01 00:00:00.0   NP96K04352       N      null
      183094    177882   2011-07-28 00:00:00.0  2010-12-14 00:00:00.0   E10BG28521       RCAMY  null
      202733    177882   null                   2011-07-28 00:00:00.0   E10BG39600       N      null
      */

      List<AFMSMeter> meters = new ArrayList<AFMSMeter>();
      AFMSMeter meter1 = new AFMSMeter();
      meter1.setEffectiveToDateMSID(new DateTime().plusDays(15));
      meter1.setPk(1L);
      meter1.setMeterSerialId("MTR_SRL_1");
      meter1.setMeterType(AFMSMeter.METER_TYPE.NCAMR.getValue());
      meter1.setMpan(mpan);
      meter1.setSettlementDate(new DateTime().minusDays(15));

      AFMSMeter meter2 = new AFMSMeter();
      meter2.setEffectiveToDateMSID(new DateTime().plusDays(15));
      meter2.setPk(2L);
      meter2.setMeterSerialId("MTR_SRL_2");
      meter2.setMpan(mpan);
      meter2.setSettlementDate(new DateTime().plusDays(15));
      meter2.setOutstationId("oustationid2");

      List<AFMSMeterRegister> registers1 = new ArrayList<AFMSMeterRegister>();
      AFMSMeterRegister register1 = new AFMSMeterRegister();
      register1.setMeter(meter1);
      register1.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
      register1.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue());
      register1.setEffectiveFromDate(new DateTime().minusMonths(1));
      register1.setEffectiveToDate(new DateTime().plusMonths(1));
      register1.setPk(1L);
      register1.setMeterRegType(MeterRegisterType.M.getValue());

      List<AFMSMeterRegister> registers2 = new ArrayList<AFMSMeterRegister>();
      AFMSMeterRegister register2 = new AFMSMeterRegister();
      register2.setMeter(meter2);
      register2.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());
      register2.setMeterRegisterId(AFMSMeterRegister.METER_REGISTER_ID.MD.getValue());
      register2.setEffectiveFromDate(new DateTime().plusDays(15));
      register2.setPk(2L);
      register2.setMeterRegType(MeterRegisterType.M.getValue());

      meter1.setMeterRegisters(registers1);
      meter2.setMeterRegisters(registers2);
      meters.add(meter1);
      meters.add(meter2);

      ArrayList<AFMSMeterRegReading> reads1 = new ArrayList<AFMSMeterRegReading>();
      AFMSMeterRegReading read1 = new AFMSMeterRegReading();
      read1.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
      read1.setDateReceived(new DateTime().minusDays(20));
      read1.setMeterReadingDate(new DateTime().minusDays(21));
      read1.setRegisterReading(105F);
      read1.setMeterRegister(register1);
      read1.setPk(1L);

      AFMSMeterRegReading read2 = new AFMSMeterRegReading();
      read2.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
      read2.setDateReceived(new DateTime().minusDays(10));
      read2.setMeterReadingDate(new DateTime().minusDays(11));
      read2.setRegisterReading(143F);
      read2.setMeterRegister(register1);
      read2.setPk(2L);

      ArrayList<AFMSMeterRegReading> reads2 = new ArrayList<AFMSMeterRegReading>();
      AFMSMeterRegReading read3 = new AFMSMeterRegReading();
      read3.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
      read3.setDateReceived(new DateTime().minusDays(5));
      read3.setMeterReadingDate(new DateTime().minusDays(6));
      read3.setRegisterReading(266F);
      read3.setMeterRegister(register2);
      read3.setPk(3L);

      reads1.add(read1);
      reads1.add(read2);
      reads2.add(read3);

      register1.setMeterRegReadings(reads1);
      register2.setMeterRegReadings(reads2);

      registers1.add(register1);
      registers2.add(register2);

      ThreeHighestReadingsAndAverage results = new ThreeHighestReadingsAndAverage();
      results.avgThreeHighest = 111F;
      results.mostRecent = read3;

      ArrayList<AFMSMeterRegReading> reads = new ArrayList<AFMSMeterRegReading>();
      reads.addAll(reads1);
      reads.addAll(reads2);

      results.threeHighestReadings = reads;

      Supplier supplier = new Supplier();
      supplier.setPk(1L);
      supplier.setSupplierId("EBES");

      int noMonthsToCalcMID = 3;

      HalfHourlySettlementDate mockHalfHourlySettlementDate = createMock(HalfHourlySettlementDate.class);
      builder.setHalfHourlySettlementDate(mockHalfHourlySettlementDate);

      expect(mockHalfHourlySettlementDate.getHalfHourlySettlementDate(mpan)).andReturn(null).once();

      replay(mockHalfHourlySettlementDate);

      Sp04FromAFMSMpan sp04FromAFMSMpan
        = builder.createNewSp04FromAFMSMpan(mpan, results, supplier, noMonthsToCalcMID);

      verify(mockHalfHourlySettlementDate);

      assertNotNull(sp04FromAFMSMpan);

      assertEquals(true, StringUtils.isNotBlank(sp04FromAFMSMpan.getMeterId()));
      String[] meterSerialIds = org.springframework.util.StringUtils
        .commaDelimitedListToStringArray(sp04FromAFMSMpan.getMeterId());
      assertEquals(true, meterSerialIds != null && meterSerialIds.length > 0);

      assertEquals(true, StringUtils.isNotBlank(sp04FromAFMSMpan.getMeterRegisterFks()));
      String[] registerFks = org.springframework.util.StringUtils
        .commaDelimitedListToStringArray(sp04FromAFMSMpan.getMeterRegisterFks());
      assertEquals(true, registerFks != null && registerFks.length > 0);
      for (String registerFk : registerFks)
      {
        try
        {
          Long.valueOf(registerFk.trim());
        }
        catch (NumberFormatException nfe)
        {
          fail("Sp04FromAFMSMpan.registerFks contains an invalid register pk value: " + registerFk);
        }
      }
    }
    finally
    {
      Freeze.thaw();
    }
  }

  @Test
  public void getAfmsMpansForSp04Inclusion_none_toGet_no_supplier() throws Exception
  {
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);

    expect((mSupplierDao).getById(1L)).andReturn(null).anyTimes();
    replay(mSupplierDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(1L);

    verify(mSupplierDao);

  }

  @Test
  public void getAfmsMpansForSp04Inclusion_none_toGet_with_supplier() throws Exception
  {
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    replay(mSupplierDao, mAFMSMPanDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao);

  }

  @Test
  public void getAfmsMpansForSp04Inclusion_mpanWithNoMeters() throws Exception
  {
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("177777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect(mSupplierDao.getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect(mAFMSMPanDao.getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    mSp04Service.deleteSp04FromAFMSMpan(new HashSet<MPANCore>());
    expectLastCall().andAnswer(new IAnswer<Object>()
    {
      @Override
      public Object answer() throws Throwable
      {
        return null;
      }
    });

    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

  }

  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_NoMeterRegisters() throws Exception
  {
    DateTime now = new DateTime(2010, 5, 10, 0, 0, 0, 0);
    Freeze.freeze(now);
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("177777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    AFMSMeter meter1_1 = new AFMSMeter();
    meter1_1.setMpan(mpan1);
    meter1_1.setSettlementDate(efd);
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    mSp04Service.deleteSp04FromAFMSMpan(new HashSet<MPANCore>());
    expectLastCall().andAnswer(new IAnswer<Object>()
    {
      @Override
      public Object answer() throws Throwable
      {
        return null;
      }
    });

    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

    Freeze.thaw();
  }

  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_OneMeterRegisterWrongType() throws Exception
  {
    DateTime now = new DateTime(2010, 5, 10, 0, 0, 0, 0);
    Freeze.freeze(now);
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);
    builder.setSp04AfmsMpanValidator(mSp04AfmsMpanValidator);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("177777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    AFMSMeter meter1_1 = new AFMSMeter();
    AFMSMeterRegister register1_1 = new AFMSMeterRegister();
    register1_1.setMeterRegType(MeterRegisterType.C.getValue());
    meter1_1.getMeterRegisters().add(register1_1);
    meter1_1.setMpan(mpan1);
    meter1_1.setSettlementDate(efd);
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    mSp04Service.deleteSp04FromAFMSMpan(new HashSet<MPANCore>());
    expectLastCall().andAnswer(new IAnswer<Object>()
    {
      @Override
      public Object answer() throws Throwable
      {
        return null;
      }
    });

    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao, mSp04Service);

    Freeze.thaw();
  }

  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_OneMeterRegisterInDate_NoReadings() throws Exception
  {
    DateTime now = new DateTime(2010, 5, 10, 0, 0, 0, 0);
    Freeze.freeze(now);
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);
    builder.setSp04AfmsMpanValidator(mSp04AfmsMpanValidator);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("1777777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    AFMSMeter meter1_1 = new AFMSMeter();
    AFMSMeterRegister register1_1 = new AFMSMeterRegister();
    register1_1.setMeterRegType(MeterRegisterType.M.getValue());
    register1_1.setEffectiveFromDate(efd.minusDays(1));
    register1_1.setEffectiveToDate(etd.plusDays(1));
    meter1_1.getMeterRegisters().add(register1_1);
    meter1_1.setMpan(mpan1);
    meter1_1.setSettlementDate(efd);
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    Set<MPANCore> mpans = new HashSet<MPANCore>();
    mpans.add(new MPANCore("1777777777777"));
    (mSp04Service).deleteSp04FromAFMSMpan(mpans);
    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    Freeze.thaw();
  }

  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_OneMeterRegisterInDate_NoReadings_no_etd() throws Exception
  {
    DateTime now = new DateTime(2010, 5, 10, 0, 0, 0, 0);
    Freeze.freeze(now);
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);
    builder.setSp04AfmsMpanValidator(mSp04AfmsMpanValidator);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("1777777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    AFMSMeter meter1_1 = new AFMSMeter();
    AFMSMeterRegister register1_1 = new AFMSMeterRegister();
    register1_1.setMeterRegType(MeterRegisterType.M.getValue());
    register1_1.setEffectiveFromDate(efd.minusDays(1));
    meter1_1.getMeterRegisters().add(register1_1);
    meter1_1.setMpan(mpan1);
    meter1_1.setSettlementDate(efd);
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    Set<MPANCore> mpans = new HashSet<MPANCore>();
    mpans.add(new MPANCore("1777777777777"));
    (mSp04Service).deleteSp04FromAFMSMpan(mpans);
    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    Freeze.thaw();
  }

  /**
   * 6 readings, 4 in valid date
   * @throws Exception
   */
  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_OneMeterRegisterInDate_SixReadings_AvgLess100() throws Exception
  {
    DateTime now = new DateTime(2010, 5, 10, 0, 0, 0, 0);
    Freeze.freeze(now);
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);
    builder.setSp04AfmsMpanValidator(mSp04AfmsMpanValidator);

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("1777777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");

    AFMSMeter meter1_1 = new AFMSMeter();
    AFMSMeterRegister register1_1 = new AFMSMeterRegister();
    register1_1.setMeterRegType(MeterRegisterType.M.getValue());
    register1_1.setEffectiveFromDate(efd.minusDays(1));

    AFMSMeterRegReading read1 = makeAReading(10.0F, new DateTime().minusMonths(2));
    AFMSMeterRegReading read2 = makeAReading(20.0F, new DateTime().minusMonths(3));
    AFMSMeterRegReading read3 = makeAReading(30.0F, new DateTime().minusMonths(4));
    AFMSMeterRegReading read4 = makeAReading(40.0F, new DateTime());   // too recent
    AFMSMeterRegReading read5 = makeAReading(50.0F, new DateTime().minusMonths(6));
    AFMSMeterRegReading read6 = makeAReading(60.0F, new DateTime().minusMonths(16));

    List<AFMSMeterRegReading> readings = new ArrayList<AFMSMeterRegReading>();
    readings.add(read1);
    readings.add(read2);
    readings.add(read3);
    readings.add(read4);
    readings.add(read5);
    readings.add(read6);
    register1_1.setMeterRegReadings(readings);

    meter1_1.getMeterRegisters().add(register1_1);
    meter1_1.setMpan(mpan1);
    meter1_1.setSettlementDate(efd);
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    Set<MPANCore> mpans = new HashSet<MPANCore>();
    mpans.add(new MPANCore("1777777777777"));
    (mSp04Service).deleteSp04FromAFMSMpan(mpans);
    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    Freeze.thaw();
  }

  /**
   * 6 readings, 4 in valid date
   * @throws Exception
   */
  @Test
  public void getAfmsMpansForSp04Inclusion_mpan1Meter_OneMeterRegisterInDate_SixReadings_AvgGreater100() throws Exception
  {
    Sp04FromAfmsMpanBuilder builder = new Sp04FromAfmsMpanBuilder();
    builder.setSupplierDao(mSupplierDao);
    builder.setAFMSMPanDao(mAFMSMPanDao);
    builder.setSp04FromAFMSMpanDao(mSp04FromAFMSMpanDao);
    builder.setSp04Service(mSp04Service);
    builder.setSp04AfmsMpanValidator(mSp04AfmsMpanValidator);
    builder.setHalfHourlySettlementDate(new HalfHourlySettlementDate());

    DateTime efd = new DateTime(2010, 5, 1, 0, 0, 0, 0);
    DateTime etd = new DateTime(2009, 5, 1, 0, 0, 0, 0);

    //prepare test db
    Supplier supplier = new Supplier("FRED");
    supplier.setPk(99L);

    AFMSAgent agent = new AFMSAgent();
    agent.setDataCollector("dc");
    agent.setPk(103L);

    AFMSMpan mpan1 = new AFMSMpan();
    mpan1.setEffectiveFromDate(new DateTime());
    mpan1.setMpanCore("1777777777777");
    mpan1.setLastUpdated(new DateTime());
    mpan1.setSupplierId("FRED");
    mpan1.setAgent(agent);
    mpan1.setPk(99L);

    AFMSMeter meter1_1 = new AFMSMeter();
    meter1_1.setOutstationId("OUT");
    meter1_1.setMeterType("H");
    meter1_1.setSettlementDate(new DateTime().minusDays(1));
    meter1_1.setPk(102L);
    AFMSMeterRegister register1_1 = new AFMSMeterRegister();
    register1_1.setMeterRegType(MeterRegisterType.M.getValue());
    register1_1.setEffectiveFromDate(efd.minusDays(1));
    register1_1.setPk(101L);

    AFMSMeterRegReading read1 = makeAReading(110.0F, new DateTime().minusMonths(2));
    read1.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    AFMSMeterRegReading read2 = makeAReading(120.0F, new DateTime().minusMonths(3));
    read2.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    AFMSMeterRegReading read3 = makeAReading(130.0F, new DateTime().minusMonths(4));
    read3.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    AFMSMeterRegReading read4 = makeAReading(140.0F, new DateTime());   // too recent
    read4.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    AFMSMeterRegReading read5 = makeAReading(150.0F, new DateTime().minusMonths(6));
    read5.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());
    AFMSMeterRegReading read6 = makeAReading(160.0F, new DateTime().minusMonths(16));
    read6.setBSCValidationStatus(BSC_VALIDATION_STATUS.V.getValue());

    List<AFMSMeterRegReading> readings = new ArrayList<AFMSMeterRegReading>();
    readings.add(read1);
    readings.add(read2);
    readings.add(read3);
    readings.add(read4);
    readings.add(read5);
    readings.add(read6);
    register1_1.setMeterRegReadings(readings);

    meter1_1.getMeterRegisters().add(register1_1);
    meter1_1.setMeterSerialId("meter1_1");
    Collection<AFMSMeter> listMeters = new ArrayList<AFMSMeter>();
    listMeters.add(meter1_1);
    mpan1.setMeters(listMeters);

    List<AFMSMpan> listMpans = new ArrayList<AFMSMpan>();
    listMpans.add(mpan1);

    List<AFMSMeter> meters = new ArrayList<AFMSMeter>();
    meters.add(meter1_1);

    List<AFMSMeterRegister> registers = new ArrayList<AFMSMeterRegister>();
    registers.add(register1_1);

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put("meter_serial_id_1", readings);

    Sp04FromAFMSMpan sp04FromAFMSMpan1 = builder.createNewSp04FromAFMSMpan(mpan1,
      mSp04AfmsMpanValidator.getAverageOfThreeHighest(efd, etd, msidReadings, 3), supplier, 3);

    //expectations
    expect((mSupplierDao).getById(supplier.getPk())).andReturn(supplier).anyTimes();
    expect((mAFMSMPanDao).getActiveMpansForLast12Months(supplier, efd, etd, 3)).andReturn(listMpans).anyTimes();
    expect((mSp04FromAFMSMpanDao).exists(new MPANCore("1777777777777"))).andReturn(false).anyTimes();
    expect((mSp04Service).saveSp04FromAFMSMpan(sp04FromAFMSMpan1)).andReturn(sp04FromAFMSMpan1);
    replay(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

    //test method
    builder.getAfmsMpansForSp04Inclusion(supplier.getPk());

    verify(mSupplierDao, mAFMSMPanDao, mSp04FromAFMSMpanDao);

  }



  private AFMSMeterRegReading makeAReading(Float value, DateTime date)
  {
    AFMSMeterRegReading read = new AFMSMeterRegReading();
    read.setDateReceived(date);
    read.setMeterReadingDate(date);
    read.setRegisterReading(value);
    return read;
  }

  @After
  public void cleanup()
  {
    Freeze.thaw();
  }

  @Before
  public void init()
  {
    Freeze.freeze(22, 5, 2010);
    try
    {
      mSupplierDao = createMock(SupplierDao.class);
      mAFMSMPanDao = createMock(AFMSMpanDao.class);
      mSp04FromAFMSMpanDao = createMock(Sp04FromAFMSMpanDao.class);
      mSp04Service = createMock(Sp04Service.class);
    } catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected NoSuchMethodException");
    }

    mSp04AfmsMpanValidator = mSp04AfmsMpanValidator == null ? new Sp04AfmsMpanValidatorImpl() : mSp04AfmsMpanValidator;
  }

  private SupplierDao mSupplierDao;
  private AFMSMpanDao mAFMSMPanDao;
  private Sp04FromAFMSMpanDao mSp04FromAFMSMpanDao;
  private Sp04Service mSp04Service;

}
