package uk.co.utilisoft.parms.file.p0028;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.HashedMap;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;

/**
 * Validation for incoming P0028File.
 */
public class Sp04ValidatorTest
{
  /**
   * Validate P0028Active records for a supplier and Data Collector against those from the Sp04FromAFMSMpan(s)
   * by mpan(J0003). Expect a errors / warnings for mismatch with additional P0028Active and Sp04FromAFMSMpan mpans.
   */
  @Test
  public void validateForDataCollectorMisMatchedWithAdditionalSp04FromAFMSMpansAndP0028ActiveMpans()
  {
    Sp04Validator validator = new Sp04Validator();

    Long supplierPk = 1L;
    String dcName = "FROG";

    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "3333333333333";

    String mpan4 = "4444444444444";
    String mpan5 = "5555555555555";
    String mpan6 = "6666666666666";

    String mpan7 = "7777777777777";
    String mpan8 = "8888888888888";
    String mpan9 = "9999999999999";

    Supplier supplier = new Supplier("SCAMPI");

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    P0028File p28File = new P0028File("p28file1", dcName, supplier, dcName, new DateTime(),
                                      new ParmsReportingPeriod(new DateMidnight()));

    // p28active 1
    Long maxDemand1 = 100L;
    String msid1 = "000001";
    P0028Data p28Data1 = new P0028Data(maxDemand1, msid1, new MPANCore(mpan1), new DateTime(), p28File);
    P0028Active p28Active1 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data1, maxDemand1, new DateTime(), msid1, new MPANCore(mpan1),
                                             new DateTime());

    // p28active 2
    Long maxDemand2 = 200L;
    String msid2 = "000002";
    P0028Data p28Data2 = new P0028Data(maxDemand2, msid2, new MPANCore(mpan2), new DateTime(), p28File);
    P0028Active p28Active2 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data2, maxDemand2, new DateTime(), msid2, new MPANCore(mpan2),
                                             new DateTime());

    // p28active 3
    Long maxDemand3 = 300L;
    String msid3 = "000003";
    P0028Data p28Data3 = new P0028Data(maxDemand3, msid3, new MPANCore(mpan3), new DateTime(), p28File);
    P0028Active p28Active3 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data3, maxDemand3, new DateTime(), msid3, new MPANCore(mpan3),
                                             new DateTime());

    // p28active 4
    Long maxDemand4 = 400L;
    String msid4 = "000004";
    P0028Data p28Data4 = new P0028Data(maxDemand4, msid4, new MPANCore(mpan4), new DateTime(), p28File);
    P0028Active p28Active4 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data4, maxDemand4, new DateTime(), msid4, new MPANCore(mpan4),
                                             new DateTime());

    // p28active 5
    Long maxDemand5 = 500L;
    String msid5 = "000005";
    P0028Data p28Data5 = new P0028Data(maxDemand5, msid5, new MPANCore(mpan5), new DateTime(), p28File);
    P0028Active p28Active5 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data5, maxDemand5, new DateTime(), msid5, new MPANCore(mpan5),
                                             new DateTime());

    // p28active 6
    Long maxDemand6 = 600L;
    String msid6 = "000006";
    P0028Data p28Data6 = new P0028Data(maxDemand6, msid6, new MPANCore(mpan6), new DateTime(), p28File);
    P0028Active p28Active6 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data6, maxDemand6, new DateTime(), msid6, new MPANCore(mpan6),
                                             new DateTime());

    IterableMap<String, P0028Active> currentP28ActivesMap = new HashedMap<String, P0028Active>();
    currentP28ActivesMap.put(mpan1, p28Active1);
    currentP28ActivesMap.put(mpan2, p28Active2);
    currentP28ActivesMap.put(mpan3, p28Active3);
    currentP28ActivesMap.put(mpan4, p28Active4);
    currentP28ActivesMap.put(mpan5, p28Active5);
    currentP28ActivesMap.put(mpan6, p28Active6);

    Sp04FromAFMSMpan afmsMpan1 = new Sp04FromAFMSMpan();
    afmsMpan1.setMpan(new MPANCore(mpan1));
    Sp04FromAFMSMpan afmsMpan2 = new Sp04FromAFMSMpan();
    afmsMpan2.setMpan(new MPANCore(mpan2));
    Sp04FromAFMSMpan afmsMpan3 = new Sp04FromAFMSMpan();
    afmsMpan3.setMpan(new MPANCore(mpan3));
    Sp04FromAFMSMpan afmsMpan7 = new Sp04FromAFMSMpan();
    afmsMpan7.setMpan(new MPANCore(mpan7));
    Sp04FromAFMSMpan afmsMpan8 = new Sp04FromAFMSMpan();
    afmsMpan8.setMpan(new MPANCore(mpan8));
    Sp04FromAFMSMpan afmsMpan9 = new Sp04FromAFMSMpan();
    afmsMpan9.setMpan(new MPANCore(mpan9));

    IterableMap<String, Sp04FromAFMSMpan> expectedAfmsMpansMapFromQuery = new HashedMap<String, Sp04FromAFMSMpan>();
    expectedAfmsMpansMapFromQuery.put(mpan1, afmsMpan1);
    expectedAfmsMpansMapFromQuery.put(mpan2, afmsMpan2);
    expectedAfmsMpansMapFromQuery.put(mpan3, afmsMpan3);
    expectedAfmsMpansMapFromQuery.put(mpan7, afmsMpan7);
    expectedAfmsMpansMapFromQuery.put(mpan8, afmsMpan8);
    expectedAfmsMpansMapFromQuery.put(mpan9, afmsMpan9);

    Sp04FromAFMSMpanDao sp04FromAFMSMpanDaoMock = createMock(Sp04FromAFMSMpanDao.class);
    validator.setSp04FromAFMSMpanDao(sp04FromAFMSMpanDaoMock);

    expect(sp04FromAFMSMpanDaoMock.getByDataCollector(supplierPk, dcName)).andReturn(expectedAfmsMpansMapFromQuery);

    replay(sp04FromAFMSMpanDaoMock);

    List<Object> validationInfos =  validator.validate(supplierPk, dcName, currentP28ActivesMap);

    verify(sp04FromAFMSMpanDaoMock);

    assertNotNull(validationInfos);
    assertFalse(validationInfos.isEmpty());

    Set<String> expectedP0028ActiveOnlyMpans = new HashSet<String>(Arrays.asList(new String[] {mpan4, mpan5, mpan6}));
    Set<String> expectedAfmsOnlyMpans = new HashSet<String>(Arrays.asList(new String[] {mpan7, mpan8, mpan9}));

    for (Object validatedInfo : validationInfos)
    {
      assertTrue("expected validated information to contain IterableMap objects", validatedInfo instanceof IterableMap);

      IterableMap<?, ?> validatedInfoMap = ((IterableMap<?, ?>) validatedInfo);

      if (validatedInfoMap.values().iterator().next() instanceof Sp04FromAFMSMpan)
      {
        assertEquals("expected 3 Sp04FromAFMSMpan mpans", 3, validatedInfoMap.size());
        MapIterator<?, ?> validatedInfoIter = validatedInfoMap.mapIterator();
        if (validatedInfoIter.hasNext())
        {
          Object key = validatedInfoIter.next();
          assertTrue(key instanceof String);
          assertTrue(expectedAfmsOnlyMpans.contains((String) key));

          Object value = validatedInfoIter.getValue();
          assertTrue(value instanceof Sp04FromAFMSMpan);
          assertTrue(expectedAfmsOnlyMpans.contains(((Sp04FromAFMSMpan) value).getMpan().getValue()));
        }
      }
      else if (validatedInfoMap.values().iterator().next() instanceof P0028Active)
      {
        assertEquals("expected 3 P0028Active mpans", 3, validatedInfoMap.size());
        MapIterator<?, ?> validatedInfoIter = validatedInfoMap.mapIterator();
        if (validatedInfoIter.hasNext())
        {
          Object key = validatedInfoIter.next();
          assertTrue(key instanceof String);
          assertTrue(expectedP0028ActiveOnlyMpans.contains((String) key));

          Object value = validatedInfoIter.getValue();
          assertTrue(value instanceof P0028Active);
          assertTrue(expectedP0028ActiveOnlyMpans.contains(((P0028Active) value).getMpanCore().getValue()));
        }
      }
    }
  }

  /**
   * Validate P0028Active records for a supplier and Data Collector against those from the Sp04FromAFMSMpan(s)
   * by mpan(J0003). Expect a errors / warnings for mismatch with additional P0028Active mpans not in
   * Sp04FromAFMSMpan(s).
   */
  @Test
  public void validateForDataCollectorMisMatchedWithAdditionalP0028ActiveMpans()
  {
    Sp04Validator validator = new Sp04Validator();

    Long supplierPk = 1L;
    String dcName = "FROG";

    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "3333333333333";

    String mpan4 = "4444444444444";
    String mpan5 = "5555555555555";
    String mpan6 = "6666666666666";

    Supplier supplier = new Supplier("SCAMPI");

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    P0028File p28File = new P0028File("p28file1", dcName, supplier, dcName, new DateTime(),
                                      new ParmsReportingPeriod(new DateMidnight()));

    // p28active 1
    Long maxDemand1 = 100L;
    String msid1 = "000001";
    P0028Data p28Data1 = new P0028Data(maxDemand1, msid1, new MPANCore(mpan1), new DateTime(), p28File);
    P0028Active p28Active1 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data1, maxDemand1, new DateTime(), msid1, new MPANCore(mpan1),
                                             new DateTime());

    // p28active 2
    Long maxDemand2 = 200L;
    String msid2 = "000002";
    P0028Data p28Data2 = new P0028Data(maxDemand2, msid2, new MPANCore(mpan2), new DateTime(), p28File);
    P0028Active p28Active2 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data2, maxDemand2, new DateTime(), msid2, new MPANCore(mpan2),
                                             new DateTime());

    // p28active 3
    Long maxDemand3 = 300L;
    String msid3 = "000003";
    P0028Data p28Data3 = new P0028Data(maxDemand3, msid3, new MPANCore(mpan3), new DateTime(), p28File);
    P0028Active p28Active3 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data3, maxDemand3, new DateTime(), msid3, new MPANCore(mpan3),
                                             new DateTime());

    // p28active 4
    Long maxDemand4 = 400L;
    String msid4 = "000004";
    P0028Data p28Data4 = new P0028Data(maxDemand4, msid4, new MPANCore(mpan4), new DateTime(), p28File);
    P0028Active p28Active4 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data4, maxDemand4, new DateTime(), msid4, new MPANCore(mpan4),
                                             new DateTime());

    // p28active 5
    Long maxDemand5 = 500L;
    String msid5 = "000005";
    P0028Data p28Data5 = new P0028Data(maxDemand5, msid5, new MPANCore(mpan5), new DateTime(), p28File);
    P0028Active p28Active5 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data5, maxDemand5, new DateTime(), msid5, new MPANCore(mpan5),
                                             new DateTime());

    // p28active 6
    Long maxDemand6 = 600L;
    String msid6 = "000006";
    P0028Data p28Data6 = new P0028Data(maxDemand6, msid6, new MPANCore(mpan6), new DateTime(), p28File);
    P0028Active p28Active6 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data6, maxDemand6, new DateTime(), msid6, new MPANCore(mpan6),
                                             new DateTime());

    IterableMap<String, P0028Active> currentP28ActivesMap = new HashedMap<String, P0028Active>();
    currentP28ActivesMap.put(mpan1, p28Active1);
    currentP28ActivesMap.put(mpan2, p28Active2);
    currentP28ActivesMap.put(mpan3, p28Active3);
    currentP28ActivesMap.put(mpan4, p28Active4);
    currentP28ActivesMap.put(mpan5, p28Active5);
    currentP28ActivesMap.put(mpan6, p28Active6);

    Sp04FromAFMSMpan afmsMpan1 = new Sp04FromAFMSMpan();
    afmsMpan1.setMpan(new MPANCore(mpan1));
    Sp04FromAFMSMpan afmsMpan2 = new Sp04FromAFMSMpan();
    afmsMpan1.setMpan(new MPANCore(mpan2));
    Sp04FromAFMSMpan afmsMpan3 = new Sp04FromAFMSMpan();
    afmsMpan1.setMpan(new MPANCore(mpan3));

    IterableMap<String, Sp04FromAFMSMpan> expectedAfmsMpansMapFromQuery = new HashedMap<String, Sp04FromAFMSMpan>();
    expectedAfmsMpansMapFromQuery.put(mpan1, afmsMpan1);
    expectedAfmsMpansMapFromQuery.put(mpan2, afmsMpan2);
    expectedAfmsMpansMapFromQuery.put(mpan3, afmsMpan3);

    Sp04FromAFMSMpanDao sp04FromAFMSMpanDaoMock = createMock(Sp04FromAFMSMpanDao.class);
    validator.setSp04FromAFMSMpanDao(sp04FromAFMSMpanDaoMock);

    expect(sp04FromAFMSMpanDaoMock.getByDataCollector(supplierPk, dcName)).andReturn(expectedAfmsMpansMapFromQuery);

    replay(sp04FromAFMSMpanDaoMock);

    List<Object> validationInfos =  validator.validate(supplierPk, dcName, currentP28ActivesMap);

    verify(sp04FromAFMSMpanDaoMock);

    assertNotNull(validationInfos);
    assertFalse(validationInfos.isEmpty());

    Set<String> expectedP0028ActiveOnlyMpans = new HashSet<String>(Arrays.asList(new String[] {mpan4, mpan5, mpan6}));

    for (Object validatedInfo : validationInfos)
    {
      assertTrue("expected validated information to contain IterableMap objects", validatedInfo instanceof IterableMap);

      IterableMap<?, ?> validatedInfoMap = ((IterableMap<?, ?>) validatedInfo);

      if (validatedInfoMap.size() > 0
          && validatedInfoMap.values().iterator().next() instanceof P0028Active)
      {
        assertEquals("expected 3 P0028Active mpans", 3, validatedInfoMap.size());
        MapIterator<?, ?> validatedInfoIter = validatedInfoMap.mapIterator();
        if (validatedInfoIter.hasNext())
        {
          Object key = validatedInfoIter.next();
          assertTrue(key instanceof String);
          assertTrue(expectedP0028ActiveOnlyMpans.contains((String) key));

          Object value = validatedInfoIter.getValue();
          assertTrue(value instanceof P0028Active);
          assertTrue(expectedP0028ActiveOnlyMpans.contains(((P0028Active) value).getMpanCore().getValue()));
        }
      }
      else
      {
        assertTrue("expected 0 Sp04FromAFMSMpan mpans", validatedInfoMap.isEmpty());
      }
    }
  }

  /**
   * Validate P0028Active records for a supplier and Data Collector against those from the Sp04FromAFMSMpan(s)
   * by mpan(J0003).
   */
  @Test
  public void validateForDataCollectorWithMatchedP0028ActiveAndAfmsMpans()
  {
    Sp04Validator validator = new Sp04Validator();

    Long supplierPk = 1L;
    String dcName = "FROG";

    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "3333333333333";

    Supplier supplier = new Supplier("SCAMPI");

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    P0028File p28File = new P0028File("p28file1", dcName, supplier, dcName, new DateTime(),
                                      new ParmsReportingPeriod(new DateMidnight()));

    // p28active 1
    Long maxDemand1 = 100L;
    String msid1 = "000001";
    P0028Data p28Data1 = new P0028Data(maxDemand1, msid1, new MPANCore(mpan1), new DateTime(), p28File);
    P0028Active p28Active1 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data1, maxDemand1, new DateTime(), msid1, new MPANCore(mpan1),
                                             new DateTime());

    // p28active 2
    Long maxDemand2 = 200L;
    String msid2 = "000002";
    P0028Data p28Data2 = new P0028Data(maxDemand2, msid2, new MPANCore(mpan2), new DateTime(), p28File);
    P0028Active p28Active2 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data2, maxDemand2, new DateTime(), msid2, new MPANCore(mpan2),
                                             new DateTime());

    // p28active 3
    Long maxDemand3 = 300L;
    String msid3 = "000003";
    P0028Data p28Data3 = new P0028Data(maxDemand3, msid3, new MPANCore(mpan3), new DateTime(), p28File);
    P0028Active p28Active3 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data3, maxDemand3, new DateTime(), msid3, new MPANCore(mpan3),
                                             new DateTime());

    IterableMap<String, P0028Active> currentP28ActivesMap = new HashedMap<String, P0028Active>();
    currentP28ActivesMap.put(mpan1, p28Active1);
    currentP28ActivesMap.put(mpan2, p28Active2);
    currentP28ActivesMap.put(mpan3, p28Active3);

    Sp04FromAFMSMpan afmsMpan1 = new Sp04FromAFMSMpan();
    afmsMpan1.setMpan(new MPANCore(mpan1));
    Sp04FromAFMSMpan afmsMpan2 = new Sp04FromAFMSMpan();
    afmsMpan2.setMpan(new MPANCore(mpan2));
    Sp04FromAFMSMpan afmsMpan3 = new Sp04FromAFMSMpan();
    afmsMpan2.setMpan(new MPANCore(mpan3));

    IterableMap<String, Sp04FromAFMSMpan> expectedAfmsMpansMapFromQuery = new HashedMap<String, Sp04FromAFMSMpan>();
    expectedAfmsMpansMapFromQuery.put(mpan1, afmsMpan1);
    expectedAfmsMpansMapFromQuery.put(mpan2, afmsMpan2);
    expectedAfmsMpansMapFromQuery.put(mpan3, afmsMpan3);

    Sp04FromAFMSMpanDao sp04FromAFMSMpanDaoMock = createMock(Sp04FromAFMSMpanDao.class);
    validator.setSp04FromAFMSMpanDao(sp04FromAFMSMpanDaoMock);

    expect(sp04FromAFMSMpanDaoMock.getByDataCollector(supplierPk, dcName)).andReturn(expectedAfmsMpansMapFromQuery);

    replay(sp04FromAFMSMpanDaoMock);

    List<Object> validationInfo =  validator.validate(supplierPk, dcName, currentP28ActivesMap);

    verify(sp04FromAFMSMpanDaoMock);

    assertNotNull(validationInfo);
    assertTrue(validationInfo.isEmpty());
  }

  /**
   * Validate P0028Active records for a supplier and Data Collector against those from the Sp04FromAFMSMpan(s)
   * by mpan(J0003). A comparison of an empty Sp04FromAFMSMpan(s) collection against a populated P0028Actives
   * collection is not valid, and would expect to return an empty list.
   */
  @Test
  public void validateForDataCollectorWithEmptyAfmsMpans()
  {
    Sp04Validator validator = new Sp04Validator();

    Long supplierPk = 1L;
    String dcName = "FROG";

    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "3333333333333";

    Supplier supplier = new Supplier("SCAMPI");

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    P0028File p28File = new P0028File("p28file1", dcName, supplier, dcName, new DateTime(),
                                      new ParmsReportingPeriod(new DateMidnight()));

    // p28active 1
    Long maxDemand1 = 100L;
    String msid1 = "000001";
    P0028Data p28Data1 = new P0028Data(maxDemand1, msid1, new MPANCore(mpan1), new DateTime(), p28File);
    P0028Active p28Active1 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data1, maxDemand1, new DateTime(), msid1, new MPANCore(mpan1),
                                             new DateTime());

    // p28active 2
    Long maxDemand2 = 200L;
    String msid2 = "000002";
    P0028Data p28Data2 = new P0028Data(maxDemand2, msid2, new MPANCore(mpan2), new DateTime(), p28File);
    P0028Active p28Active2 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data2, maxDemand2, new DateTime(), msid2, new MPANCore(mpan2),
                                             new DateTime());

    // p28active 3
    Long maxDemand3 = 300L;
    String msid3 = "000003";
    P0028Data p28Data3 = new P0028Data(maxDemand3, msid3, new MPANCore(mpan3), new DateTime(), p28File);
    P0028Active p28Active3 = new P0028Active(supplier, new DataCollector(dcName, true, dpiFile, true), dcName,
                                             p28Data3, maxDemand3, new DateTime(), msid3, new MPANCore(mpan3),
                                             new DateTime());

    IterableMap<String, P0028Active> currentP28ActivesMap = new HashedMap<String, P0028Active>();
    currentP28ActivesMap.put(mpan1, p28Active1);
    currentP28ActivesMap.put(mpan2, p28Active2);
    currentP28ActivesMap.put(mpan3, p28Active3);

    IterableMap<String, Sp04FromAFMSMpan> expectedAfmsMpansMapFromQuery = new HashedMap<String, Sp04FromAFMSMpan>();

    Sp04FromAFMSMpanDao sp04FromAFMSMpanDaoMock = createMock(Sp04FromAFMSMpanDao.class);
    validator.setSp04FromAFMSMpanDao(sp04FromAFMSMpanDaoMock);

    expect(sp04FromAFMSMpanDaoMock.getByDataCollector(supplierPk, dcName)).andReturn(expectedAfmsMpansMapFromQuery);

    replay(sp04FromAFMSMpanDaoMock);

    List<Object> validationInfo =  validator.validate(supplierPk, dcName, currentP28ActivesMap);

    verify(sp04FromAFMSMpanDaoMock);

    assertNotNull(validationInfo);
    assertTrue(validationInfo.isEmpty());
  }

  /**
   * Check max demand for imported p0028 file data is less than 100.
   */
  @Test
  public void maxDemandLessThanOneHundred()
  {
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
    };
    P0028Data data = new P0028Data(99L, "NHHXXX", new MPANCore("1110000000001"), new DateMidnight().toDateTime(),
                                   new P0028File());

    assertEquals(Boolean.TRUE, validator.isMaxDemandLessThanOrEqualToOneHundred(data));
  }

  /**
   * Check max demand for imported p0028 file data is equal to 100.
   */
  @Test
  public void maxDemandEqualOneHundread()
  {
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
    };
    P0028Data data = new P0028Data(100L, "NHHXXX", new MPANCore("1110000000001"), new DateMidnight().toDateTime(),
                                   new P0028File());

    assertEquals(Boolean.TRUE, validator.isMaxDemandLessThanOrEqualToOneHundred(data));
  }

  /**
   * Check max demand for imported p0028 file data is greater than 100.
   */
  @Test
  public void maxDemandIsGreaterThanOneHundread()
  {
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
    };
    P0028Data data = new P0028Data(101L, "NHHXXX", new MPANCore("1110000000001"), new DateMidnight().toDateTime(),
                                   new P0028File());

    assertEquals(Boolean.FALSE, validator.isMaxDemandLessThanOrEqualToOneHundred(data));
  }

  /**
   * @throws Exception
   */
  @Test
  public void emptyP0028File() throws Exception
  {
    Sp04Validator validator = new Sp04Validator();
    Supplier supplier = new Supplier("FRED");

    P0028File file = new P0028File();

    //test method
    assertEquals(UploadStatus.UPLOAD_OK, validator.validate(file, supplier));
  }

  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_that_fails() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      boolean validatedOK(P0028Data p0028Data, Supplier aSupplier)
      {
        p0028Data.setValidated(false);
        return p0028Data.isValidated();
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("validated for this has failed so should now be false", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_validRegisterId() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        return new AFMSMeter();
      }
      @Override
      boolean isMpanInvalid(AFMSMpan mpan)
      {
        return false;  // we are not testing this here
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }

      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }

      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return false;
      }
      @Override
      boolean isAFMSMpanActive(P0028Data aP0028Data)
      {
        return true;
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOAD_OK, validator.validate(file, supplier));

    assertTrue("this data should be OK", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_invalidRegisterId() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        return null;
      }

      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {

      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("no meter register id should mark this P0028Data as bad", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_validMpan() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return false;
      }
      @Override
      boolean isAFMSMpanActive(P0028Data aP0028Data)
      {
        return true;
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOAD_OK, validator.validate(file, supplier));

    assertTrue("this data should be OK", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_invalidMpan() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }

      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {

      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return true;
      }

    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("this data should be OK", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_validMpan_isAlreadyHH() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        mpan.setMeasurementClassification("C");
        meter.setMpan(mpan);

        return meter;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }

      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }

      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {

      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return true;
      }


    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("is already HH so should fail", file.getP0028Data().get(0).isValidated());
  }



  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_SupplierIsStillValid() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        mpan.setMeasurementClassification("A");
        mpan.setSupplierId("FRED");
        mpan.setSupplierEffectiveToDate(new DateTime().plusYears(1));
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return false;
      }
      @Override
      boolean isAFMSMpanActive(P0028Data aP0028Data)
      {
        return true;
      }
    };

    P0028File file = new P0028File();
    ParmsReportingPeriod prp = new ParmsReportingPeriod(new DateMidnight());
    file.setReportingPeriod(prp);
    P0028Data data1 = new P0028Data();
    data1.setP0028File(file);
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOAD_OK, validator.validate(file, supplier));

    assertTrue("Supplier is good", file.getP0028Data().get(0).isValidated());
  }



  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_SupplierHasChanged_byname() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        mpan.setMeasurementClassification("A");
        mpan.setSupplierId("BERT");
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }

      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {

      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return true;
      }

    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("Supplier has changed so should have failed", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_SupplierHasChanged_byJ0117EffectiveToDate() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        mpan.setMeasurementClassification("A");
        mpan.setSupplierId("FRED");
        mpan.setSupplierEffectiveToDate(new DateTime().minusYears(1));
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }

      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {

      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
      {
        return true;
      }
      @Override
      boolean isAFMSMpanActive(P0028Data aP0028Data)
      {
        return true;
      }
    };

    P0028File file = new P0028File();
    ParmsReportingPeriod prp = new ParmsReportingPeriod(new DateMidnight());
    file.setReportingPeriod(prp);

    P0028Data data1 = new P0028Data();
    data1.setP0028File(file);
    //data1.setValidated(true);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));

    assertFalse("Supplier has changed so should have failed", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_validMaxDemand() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }

      @Override
      void validateMaxDemand(P0028Data aP0028Data)
      {
        // do not check for max demand errors
      }

      @Override
      boolean isAFMSMpanActive(P0028Data aP0028Data)
      {
        return true;
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    data1.setMaxDemand(101L);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOAD_OK, validator.validate(file, supplier));

    assertTrue("this data should be OK", file.getP0028Data().get(0).isValidated());
  }


  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_INvalidMaxDemandLess100() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {}

      @Override
      public Float getMaxDemandThreshold()
      {
        // do not set configured MD threshold
        return null;
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    data1.setMaxDemand(99L);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));
    assertFalse("MD <= 100 so should fail", file.getP0028Data().get(0).isValidated());


  }



  /**
   * @throws Exception
   */
  @Test
  public void oneP0028Data_INvalidMaxDemandEquals100() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    Sp04Validator validator = new Sp04Validator()
    {
      @Override
      AFMSMeter getMeter(String aMeterRegisterId)
      {
        AFMSMeter meter = new AFMSMeter();
        AFMSMpan mpan = new AFMSMpan();
        meter.setMpan(mpan);

        return meter;
      }

      @Override
      boolean isAlreadyHalfHourly(AFMSMpan mpan)
      {
        return false;
      }
      @Override
      boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
      {
        return true;
      }
      @Override
      boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
      {
        return true;
      }
      @Override
      void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
      {}

      @Override
      public Float getMaxDemandThreshold()
      {
        // do not set configured MD threshold
        return null;
      }
    };

    P0028File file = new P0028File();
    P0028Data data1 = new P0028Data();
    data1.setMaxDemand(99L);
    file.getP0028Data().add(data1);

    //test method
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, validator.validate(file, supplier));
    assertFalse("MD <= 100 so should fail", file.getP0028Data().get(0).isValidated());
  }
}
