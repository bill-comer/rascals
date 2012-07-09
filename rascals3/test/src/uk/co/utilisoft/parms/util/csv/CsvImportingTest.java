package uk.co.utilisoft.parms.util.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.co.utilisoft.parms.util.csv.ColumnMappingName.DC_AGENT_NAME;
import static uk.co.utilisoft.parms.util.csv.ColumnMappingName.MAX_DEMAND;
import static uk.co.utilisoft.parms.util.csv.ColumnMappingName.METER_REGISTER_ID;
import static uk.co.utilisoft.parms.util.csv.ColumnMappingName.MPAN;
import static uk.co.utilisoft.parms.util.csv.ColumnMappingName.READING_DATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import uk.co.utilisoft.BaseTest;


/**
 *
 */
public class CsvImportingTest extends BaseTest
{
  /**
   * Test a line of p0028 file which contains non ASCII alphanumeric characters and symbols.
   * Note: This test just proves that the CsvReader will accept and process non ascii characters.
   *
   * @throws Exception
   */
  @Test
  public void testCsvReaderReadingALineOfIllegalP28FileData() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/util/csv/" + "illegal_chars_in_p0028_file.csv";
    File file = new File(filePath);
    assertTrue(file.exists());
    InputStreamReader fileReader = new FileReader(file);

    CSVReader csvReader = new CSVReader(fileReader);
    String[] expTxtLineP28DataArr = new String[] {"BMET ","108","K08M00817","1630000722977 ","20/08/2011"};
    String alphaNumericAndSymbolsAndWhitespaceAsciiChars = "^[a-zA-Z_0-9/\\[\\^\\$\\.\\|\\?\\*\\+\\(\\) ]*$";
    String[] txtArr = null;

    do
    {
      txtArr = csvReader.readNext();

      if (txtArr != null)
      {
        assertEquals(expTxtLineP28DataArr.length, txtArr.length);

        for (String txt : txtArr)
        {
          if (txt.contains("1630000722977"))
          {
            // because CSVReader allows reading in of non ascii characters we are testing here
            // if the read in line of p0028 data contains valid ascii alphanumeric chars and symbols
            assertFalse(txt.matches(alphaNumericAndSymbolsAndWhitespaceAsciiChars));
          }
        }

        txtArr = null;
      }
    }
    while (txtArr != null);
  }

  /**
   * @throws Exception
   */
  @Test
  public void importOnly_validFile() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/util/csv/" + "valid.csv";
    FileInputStream fis = new FileInputStream(filePath);
    File file = new File(filePath);
    assertTrue(file.exists());

    boolean isColumnNullable = false;
    ColumnMapping[] columnMappings = new ColumnMapping[]
       {
        new ColumnMapping(DC_AGENT_NAME, isColumnNullable),
        new ColumnMapping(MAX_DEMAND, isColumnNullable),
        new ColumnMapping(METER_REGISTER_ID, isColumnNullable),
        new ColumnMapping(MPAN, isColumnNullable),
        new ColumnMapping(READING_DATE, isColumnNullable)
       };

    CsvImporter<ClassToImport> importer = new CsvImporter<ClassToImport>(ClassToImport.class, columnMappings);

    //10:22:33 11/2/2010
    DateTime ecpectedDateTime = new DateTime(2010, 2, 11, 0, 0, 0, 0);

//    /11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010, 3, 12, 0, 0, 0, 0);

    //test method
    List<ClassToImport> list = importer.importFile(fis);

    assertTrue(list.size() == 2);

    ClassToImport first = list.get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterRegisterId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(ecpectedDateTime, first.getReadingDate());

    //second row
    ClassToImport second = list.get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterRegisterId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());
  }

  /**
   * Imported file row data without Max Demand value will be ignored if blank.
   *
   * @throws Exception
   */
  @Test
  public void importOnly_validFile_withNull_mdvalue() throws Exception
  {
    String filePath = "test/src/uk/co/utilisoft/parms/util/csv/" + "validWithNulls.csv";
    FileInputStream fis = new FileInputStream(filePath);
    File file = new File(filePath);
    assertTrue(file.exists());

    boolean isColumnNullable = false;

    ColumnMapping[] columnMappings = new ColumnMapping[]
       {
        new ColumnMapping(DC_AGENT_NAME, isColumnNullable),
        new ColumnMapping(MAX_DEMAND, isColumnNullable),
        new ColumnMapping(METER_REGISTER_ID, isColumnNullable),
        new ColumnMapping(MPAN, isColumnNullable),
        new ColumnMapping(READING_DATE, isColumnNullable)
       };

    CsvImporter<ClassToImport> importer = new CsvImporter<ClassToImport>(ClassToImport.class, columnMappings);

    //10:22:33 11/2/2010
    DateTime ecpectedDateTime = new DateTime(2010, 2, 11, 0, 0, 0, 0);

//    /11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010, 3, 12, 0 , 0, 0, 0);

    //test method
    List<ClassToImport> list = importer.importFile(fis);
    List<String> errorData = importer.getErrorData();

    assertEquals(1, list.size());
    assertEquals(1, errorData.size());

    // disabled below because row with empty Max Demand value is ignored by csvimporter
//  assertEquals("first MD value is not specified", null, first.getMaxDemand());
//    ClassToImport first = list.get(0);
//    assertEquals("FRED", first.getDcAgentName());
//    assertEquals("12345", first.getMeterRegisterId());
//    assertEquals("1000000000003", first.getMpan().getValue());
//    assertEquals(ecpectedDateTime, first.getReadingDate());

    //second row
    ClassToImport second = list.get(0);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterRegisterId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());

    String expErrMsg = "ROW[1], COL[2]:MD value is Missing";
    assertEquals(expErrMsg, errorData.get(0));
  }

  /**
   * @throws Exception
   */
  @Test
  public void importOnly_InvalidFile_missingMandatoryColumn() throws Exception
  {
    String filename = "invalid_missingMandatoryColumn.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/util/csv/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    boolean isColumnNullable = false;
    ColumnMapping[] columnMappings = new ColumnMapping[]
       {
        new ColumnMapping(DC_AGENT_NAME, isColumnNullable),
        new ColumnMapping(MAX_DEMAND, isColumnNullable),
        new ColumnMapping(METER_REGISTER_ID, isColumnNullable),
        new ColumnMapping(MPAN, isColumnNullable),
        new ColumnMapping(READING_DATE, isColumnNullable)
       };

    CsvImporter<ClassToImport> importer = new CsvImporter<ClassToImport>(ClassToImport.class, columnMappings);

    // test method
    List<ClassToImport> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 0);
    assertTrue(errorData.size() == 2);
  }

  /**
   * @throws Exception
   */
  @Test
  public void importOnly_InvalidFile_invalidDate() throws Exception
  {
    String filename = "invalidDate.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/util/csv/" + filename;
    FileInputStream fis = new FileInputStream(filePath);

    boolean isColumnNullable = false;
    ColumnMapping[] columnMappings = new ColumnMapping[]
       {
        new ColumnMapping(DC_AGENT_NAME, isColumnNullable),
        new ColumnMapping(MAX_DEMAND, isColumnNullable),
        new ColumnMapping(METER_REGISTER_ID, isColumnNullable),
        new ColumnMapping(MPAN, isColumnNullable),
        new ColumnMapping(READING_DATE, isColumnNullable)
       };

    CsvImporter<ClassToImport> importer = new CsvImporter<ClassToImport>(ClassToImport.class, columnMappings);

    boolean exceptionThrown = false;
    // try {
    // test method
    List<ClassToImport> list = importer.importFile(fis);

    List<String> errorData = importer.getErrorData();

    assertTrue(list.size() == 1);
    assertTrue(errorData.size() == 1);
  }
}







