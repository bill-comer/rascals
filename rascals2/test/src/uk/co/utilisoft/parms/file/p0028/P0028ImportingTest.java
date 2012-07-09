package uk.co.utilisoft.parms.file.p0028;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.BaseTest;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.P0028ActiveDaoHibernate;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Supplier;


public class P0028ImportingTest extends BaseTest
{
  private AFMSMeterDao mMockAFMSMeterDao;


  @Test
  public void importOnly_InvalidFile_noDC() throws Exception
  {
    String filename = "invalidP0028_noDC.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer();

    // test method
    List<P0028Data> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 0);
    assertTrue(errorData.size() == 2);
  }


  @Test
  public void importOnly_InvalidFile_invalidDate() throws Exception
  {
    String filename = "invalidDate.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer();

    // test method
    List<P0028Data> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 1);
    assertTrue(errorData.size() == 1);
  }



  @Test
  public void importOnly_InvalidFile_invalidMpan() throws Exception
  {
    String filename = "invalidP0028_badmpan.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer();

    // test method
    List<P0028Data> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 1);
    assertTrue(errorData.size() == 1);
  }



  @Test
  public void importOnly_InvalidFile_emptyField() throws Exception
  {
    String filename = "invalidP0028_emptyField.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer();

    // test method
    List<P0028Data> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 1);
    assertTrue(errorData.size() == 1);
  }


  @Test
  public void importOnly_validFile() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"validP0028.csv";
    FileInputStream fis = new FileInputStream(filePath);
    File file = new File(filePath);
    assertTrue(file.exists());

    P0028Importer importer = new P0028Importer();

    //10:22:33 11/2/2010
    DateTime ecpectedDateTime = new DateTime(2010,2,11, 0,0,0,0);

//    /11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0,0,0);

    //test method
    List<P0028Data> list = importer.importFile(fis);

    assertTrue(list.size() == 2);

    P0028Data first = list.get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(ecpectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = list.get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());
  }


  @Test
  public void importOnly_validFileAdditionalBlankLine() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"validP0028_additionalBlankLine.csv";
    FileInputStream fis = new FileInputStream(filePath);
    File file = new File(filePath);
    assertTrue(file.exists());

    P0028Importer importer = new P0028Importer();

    //10:22:33 11/2/2010
    DateTime ecpectedDateTime = new DateTime(2010,2,11, 0,0,0,0);

//    /11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0,0,0);

    //test method
    List<P0028Data> list = importer.importFile(fis);

    assertTrue(list.size() == 2);

    P0028Data first = list.get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(ecpectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = list.get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());
  }

  @Test
  public void importInvalidFile_noDC() throws Exception
  {
    String filename = "invalidP0028_noDC.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer();

    boolean exceptionThrown = false;
    try {
      //test method
      List<P0028Data> list = importer.importFile(fis);

      List<String> errorData = importer.getErrorData();

      assertTrue(list.size() == 0);
      assertTrue(errorData.size() == 2);

      //fail("P0028ImporterException should have been thrown");
    } catch (P0028ImporterException e) {
      exceptionThrown = true;
    }

    assertFalse("P0028ImporterException Exception should NOT have been thrown", exceptionThrown);
  }

  @Test
  public void importFile() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"validP0028.csv";
    FileInputStream fis = new FileInputStream(filePath);
    File file = new File(filePath);
    assertTrue(file.exists());

    P0028Importer importer = new P0028Importer();

    //10:22:33 11/2/2010
    DateTime ecpectedDateTime = new DateTime(2010,2,11, 0,0,0,0);

//    /11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0,0,0);

    P0028File p0028File = new P0028File();

    //test method
    List<P0028Data> list = importer.getRowsForFile(fis, p0028File);

    assertTrue(list.size() == 2);

    P0028Data first = list.get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(ecpectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = list.get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());
  }

  @Test
  public void dateFormatting() throws Exception
  {
    String testDate = "10:22:33 11/2/2010";
    DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss dd/MM/yyyy");

    //test method
    DateTime date = fmt.parseDateTime(testDate);

    assertEquals(11, date.getDayOfMonth());
    assertEquals(2, date.getMonthOfYear());
    assertEquals(2010, date.getYear());
    assertEquals(10, date.getHourOfDay());
    assertEquals(22, date.getMinuteOfHour());
    assertEquals(33, date.getSecondOfMinute());
  }


  @Test
  public void uploadForSupplier() throws Exception
  {
    DateTime now = new DateTime(2010, 10, 5, 11, 11, 11, 0);
    freezeTime(now);
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(8, 2010);

    Supplier supplier = new Supplier("SUPP");
    String fileName = "uploadForSupplier.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + fileName;

    FileInputStream fis = new FileInputStream(filePath);
    //10:22:33 11/2/2010
    DateTime expectedDateTime = new DateTime(2010,2,11, 0,0, 0, 0);
    //11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0, 0, 0);

    DataCollector expectedFredCollector = new DataCollector("FRED", true, null, true);

    P0028Importer importer = new P0028Importer()
    {
      boolean areThereAnyUploadsNewerThanThis(Supplier supplier, String dcAgentName,
          DateTime aP0028Received)
      {
        return false;
      }
    };

    // set mock afmsmeterdao
    importer.setAFMSMeterDao(mMockAFMSMeterDao);
    // mock afmsmeterdao call in p0028importer
    expect(mMockAFMSMeterDao.getLatestMeterForMeterSerialId(not(eq("")))).andStubReturn(null);

    replay(mMockAFMSMeterDao);

    //test method
    P0028File p0028File =  importer.buildP0028File(supplier, fileName, fis, now.minusMonths(1));

    verify(mMockAFMSMeterDao);

    assertNotNull("a P0028File shopuld have been created", p0028File);
    assertEquals("uploadForSupplier.csv", p0028File.getFilename());
    assertEquals(supplier, p0028File.getSupplier());
    assertTrue(p0028File.getP0028Data() != null);
    assertTrue("should be two rows", p0028File.getP0028Data().size() == 2);

    //test the contents of the rows
    P0028Data first = p0028File.getP0028Data().get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(expectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = p0028File.getP0028Data().get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());

    assertEquals("FRED", p0028File.getDcAgentName());
    assertEquals(now.minusMonths(1), p0028File.getReceiptDate());
    assertEquals(now, p0028File.getDateImported());

    assertEquals("should have been the previous month to now", expectedPrp, p0028File.getReportingPeriod());
  }


  @Test
  public void uploadForSupplierBeforeSeventh() throws Exception
  {
    DateTime now = new DateTime(2010, 10, 6, 11, 11, 11, 0);
    freezeTime(now);
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(8, 2010);

    Supplier supplier = new Supplier("SUPP");
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"uploadForSupplier.csv";

    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer()
    {
      boolean areThereAnyUploadsNewerThanThis(Supplier supplier, String dcAgentName,
          DateTime aP0028Received)
      {
        return false;
      }
    };

    // set mock afmsmeterdao
    importer.setAFMSMeterDao(mMockAFMSMeterDao);
    // mock afmsmeterdao call in p0028importer
    expect(mMockAFMSMeterDao.getLatestMeterForMeterSerialId(not(eq("")))).andStubReturn(null);

    replay(mMockAFMSMeterDao);

    //test method
    P0028File p0028File =  importer.buildP0028File(supplier, filePath, fis, now.minusMonths(1));

    verify(mMockAFMSMeterDao);

    assertEquals("should have been the previous month to now", expectedPrp, p0028File.getReportingPeriod());
  }

  @Test
  public void uploadForSupplierAfterSeventh() throws Exception
  {
    DateTime now = new DateTime(2010, 10, 8, 11, 11, 11, 0);
    freezeTime(now);
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(9, 2010);

    Supplier supplier = new Supplier("SUPP");
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"uploadForSupplier.csv";
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer()
    {
      boolean areThereAnyUploadsNewerThanThis(Supplier supplier, String dcAgentName,
        DateTime aP0028Received)
      {
        return false;
      }
    };

    // set mock afmsmeterdao
    importer.setAFMSMeterDao(mMockAFMSMeterDao);
    // mock afmsmeterdao call in p0028importer
    expect(mMockAFMSMeterDao.getLatestMeterForMeterSerialId(not(eq("")))).andStubReturn(null);

    replay(mMockAFMSMeterDao);

    //test method
    P0028File p0028File =  importer.buildP0028File(supplier, filePath, fis, now.minusMonths(1));

    verify(mMockAFMSMeterDao);

    assertEquals("should have been the same month to now", expectedPrp, p0028File.getReportingPeriod());
  }


  @Test
  public void uploadForSupplierOnSeventh() throws Exception
  {
    DateTime now = new DateTime(2010, 10, 7, 11, 11, 11, 0);
    freezeTime(now);
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(9, 2010);

    Supplier supplier = new Supplier("SUPP");
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +"uploadForSupplier.csv";
    FileInputStream fis = new FileInputStream(filePath);

    P0028Importer importer = new P0028Importer()
    {
      boolean areThereAnyUploadsNewerThanThis(Supplier supplier, String dcAgentName,
          DateTime aP0028Received)
      {
        return false;
      }
    };

    // set mock afmsmeterdao
    importer.setAFMSMeterDao(mMockAFMSMeterDao);
    // mock afmsmeterdao call in p0028importer
    expect(mMockAFMSMeterDao.getLatestMeterForMeterSerialId(not(eq("")))).andStubReturn(null);

    replay(mMockAFMSMeterDao);

    //test method
    P0028File p0028File =  importer.buildP0028File(supplier, filePath, fis, now.minusMonths(1));

    verify(mMockAFMSMeterDao);

    assertEquals("should have been the same month to now", expectedPrp, p0028File.getReportingPeriod());
  }

  @Test
  public void updateActiveP0028s() throws Exception
  {
    P0028Importer importer = new P0028Importer();
    P0028ActiveDaoHibernate  p0028ActiveDao = new P0028ActiveDaoHibernate()
    {
      @Override
      public void storeNewP0028Active(P0028Data aP0028Data, DataCollector aDataCollector, String aDcAgentName)
      {};
    };
    importer.setP0028ActiveDao(p0028ActiveDao);
    MPANCore mpan1 = new MPANCore("1000000000001");
    MPANCore mpan2 = new MPANCore("1000000000002");
    MPANCore mpan3 = new MPANCore("1000000000003");

    //Active List with three rows
    IterableMap<String, P0028Active> allActives = new HashedMap<String, P0028Active>();
    P0028Active p0028Active1 = new P0028Active();
    p0028Active1.setMpanCore(mpan1);
    P0028Active p0028Active2 = new P0028Active();
    p0028Active2.setMpanCore(mpan2);
    P0028Active p0028Active3 = new P0028Active();
    p0028Active3.setMpanCore(mpan3);
    allActives.put(mpan1.getValue(), p0028Active1);
    allActives.put(mpan2.getValue(), p0028Active2);
    allActives.put(mpan3.getValue(), p0028Active3);

    //File with Two P0028s
    P0028File p0028File = new P0028File();
    P0028Data p0028Data1 = new P0028Data();
    p0028Data1.setMpan(mpan1);
    P0028Data p0028Data2 = new P0028Data();
    p0028Data2.setMpan(mpan2);
    p0028File.getP0028Data().add(p0028Data1);
    p0028File.getP0028Data().add(p0028Data2);


    //test method
    // should be just left with mpan3
    importer.updateActiveP0028s(p0028File, allActives);

    assertEquals("mpan3 should have been removed", 1, allActives.size());
    assertNotNull(allActives.get(mpan3.getValue()));
    assertNull(allActives.get(mpan1.getValue()));
    assertNull(allActives.get(mpan2.getValue()));
  }

  @Before
  public void setup()
  {
    mMockAFMSMeterDao = createMock(AFMSMeterDao.class);
  }
}

