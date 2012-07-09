package uk.co.utilisoft.parms.file.dpi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

import uk.co.utilisoft.parms.dao.ConfigurationParameterDao;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static uk.co.utilisoft.parms.domain.ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiFileDataWriterUnitTest
{
  private DpiFileDataWriterImpl mDpiFileDataWriter;
  private ConfigurationParameterDao mConfigurationParameterDao;
  

  String path = "c:\\DpiFileWriterUnitTest";

  /**
   * This will now copy the existing file to the archive folder and create a new file in its place.
   */
  @Test
  public void testSaveSuccessOnDestinationFileAlreadyExists()
  {
    String existFileName = "MARK3333";
    String tempPath = "temp";

    String[] responses = mDpiFileDataWriter.save(tempPath, path, existFileName, "preexisting data");
    assertNull(responses[1]);
    assertNotNull(responses[0]);
    File checkDuplicateFile = new File(path + File.separator + existFileName);
    assertTrue(checkDuplicateFile.exists() && checkDuplicateFile.isFile() && checkDuplicateFile.canRead());

    //test method
    responses = mDpiFileDataWriter.save(tempPath, path, existFileName, "duplicate dpi file");
    
    assertNotNull("should have a fileName", responses[0]);
    String fileName = responses[0];
    assertNotNull("Expected file name", fileName);
    assertEquals(path + File.separator + existFileName, fileName);
    
    //error response to show file Archived
    assertNull("should NOT have an error response for the archive", responses[1]);
    
    //check contents of new file
    File createdFile = new File(fileName);
    assertTrue(createdFile.exists());
  }
  

  /**
   * temp file already existing should result in the temp file being overwritten
   */
  @Test
  public void testSaveSuccessOnTempFileAlreadyExists()
  {
    String existFileName = "MARK3333";
    String tempPath = "temp";

    File dir = new File(path + "\\temp");
    dir.mkdirs();
    File preExistingTempFile = new File(path + "\\temp", existFileName);
    mDpiFileDataWriter.write(preExistingTempFile, "rubbish");

    //test method
    String[] responses = mDpiFileDataWriter.save(tempPath, path, existFileName, "duplicate temp dpi file");
    assertNull(responses[1]);
    String fileName = responses[0];
    assertNotNull("Expected file name", fileName);
    assertEquals(path + File.separator + existFileName, fileName);

  }
  
  @After
  public void cleanUp()
  {
    try
    {
      cleanUpFilePath(path);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @ExpectedException(RuntimeException.class)
  public void testSaveDefaultPathSuccess()
  {
    String existFileName = "GREG5555";
    ConfigurationParameter expConfigParam = new ConfigurationParameter(PARMS_DPI_FILE_LOCATION,
                                                                       path, "default dpi file location");
    List<String> dfdRows = getDataRows();

    expect(mConfigurationParameterDao.getByName(PARMS_DPI_FILE_LOCATION)).andReturn(expConfigParam).once();
    replay(mConfigurationParameterDao);
    String[] responses = mDpiFileDataWriter.save(existFileName, dfdRows.toString());
    assertNull(responses[1]);
    assertNotNull("Expected file name", responses[0]);
    assertEquals(path + File.separator + existFileName, responses[0]);

    File checkFile = new File(responses[0]);
    assertTrue(checkFile.exists() && checkFile.isFile() && checkFile.canRead());


    verify(mConfigurationParameterDao);
  }

  @Test
  public void testSaveDefaultTempPathSuccess()
  {
    String existFileName = "OVOE2222";
    List<String> dfdRows = getDataRows();

    String[] responses = mDpiFileDataWriter.save(path, existFileName, dfdRows.toString());
    assertNull(responses[1]);
    String fileName = responses[0];
    assertNotNull("Expected file name", fileName);
    assertEquals(path + File.separator + existFileName, fileName);

    File checkFile = new File(fileName);
    assertTrue(checkFile.exists() && checkFile.isFile() && checkFile.canRead());
  }

  @Test
  public void testSaveSuccess()
  {
    String existFileName = "JOHN3333";
    String tempPath = "temp";

    List<String> dfdRows = getDataRows();

    String[] responses = mDpiFileDataWriter.save(tempPath, path, existFileName, dfdRows.toString());
    assertNull(responses[1]);
    String fileName = responses[0];
    assertNotNull("Expected file name", fileName);
    assertEquals(path + File.separator + existFileName, fileName);

    File checkFile = new File(fileName);
    assertTrue(checkFile.exists() && checkFile.isFile() && checkFile.canRead());

  }

  private void cleanUpFilePath(String aPath) throws IOException
  {
    File directory = new File(aPath);

    if (directory.exists() && directory.isDirectory())
    {
      File[] files = new File(aPath).listFiles();

      for (File file : files)
      {
        if (file.isDirectory()) 
        {
          cleanUpFilePath(file.getCanonicalPath());
        }
        if (file.exists() && file.isFile())
        {
          file.delete();
        }
        directory.delete();
      }
      
    }


    if (directory.listFiles() != null && directory.listFiles().length == 0)
    {
      new File(aPath).delete();
    }
  }

  private List<String> getDataRows()
  {
    String dpiHeader = "ZHD|P0135001|X|OVOE|Z|POOL|20100929165434\n\r";
    String dpiRecords = "DPI|_C|SP04|OVOE|X|20100831\n\rDPI|_D|SP04|OVOE|X|20100831\n\rDPI|_E|SP04|OVOE|X|20100831\n\r"
      + "DPI|_F|SP04|OVOE|X|20100831\n\rDPI|_G|SP04|OVOE|X|20100831\n\rDPI|_H|SP04|OVOE|X|20100831\n\r"
      + "DPI|_J|SP04|OVOE|X|20100831\n\rDPI|_K|SP04|OVOE|X|20100831\n\rDPI|_L|SP04|OVOE|X|20100831\n\r"
      + "DPI|_M|SP04|OVOE|X|20100831\n\rDPI|_N|SP04|OVOE|X|20100831\n\rDPI|_P|SP04|OVOE|X|20100831\n\r"
      + "DPI|_A|SP05|LBSL|D|20100831\n\rDPI|_B|SP05|LBSL|D|20100831\n\rDPI|_C|SP05|LBSL|D|20100831\n\r"
      + "DPI|_D|SP05|LBSL|D|20100831\n\rDPI|_E|SP05|LBSL|D|20100831\n\rDPI|_F|SP05|LBSL|D|20100831\n\r"
      + "DPI|_G|SP05|LBSL|D|20100831\n\rDPI|_H|SP05|LBSL|D|20100831\n\rDPI|_J|SP05|LBSL|D|20100831\n\r"
      + "DPI|_K|SP05|LBSL|D|20100831\n\rDPI|_L|SP05|LBSL|D|20100831\n\rDPI|_M|SP05|LBSL|D|20100831\n\r"
      + "DPI|_N|SP05|LBSL|D|20100831\n\rDPI|_B|SP05|SPOW|C|20100831\n\rDPI|_D|SP05|SPOW|C|20100831\n\r"
      + "DPI|_F|SP05|SPOW|C|20100831\n\rDPI|_H|SP05|SPOW|C|20100831\n\rDPI|_P|SP05|NEEB|C|20100831\n\r"
      + "DPI|_A|SP06|LBSL|D|20100831\n\rDPI|_B|SP06|LBSL|D|20100831\n\rDPI|_C|SP06|LBSL|D|20100831\n\r"
      + "DPI|_D|SP06|LBSL|D|20100831\n\rDPI|_E|SP06|LBSL|D|20100831\n\rDPI|_F|SP06|LBSL|D|20100831\n\r"
      + "DPI|_G|SP06|LBSL|D|20100831\n\rDPI|_H|SP06|LBSL|D|20100831\n\rDPI|_J|SP06|LBSL|D|20100831\n\r"
      + "DPI|_K|SP06|LBSL|D|20100831\n\rDPI|_L|SP06|LBSL|D|20100831\n\rDPI|_M|SP06|LBSL|D|20100831\n\r"
      + "DPI|_N|SP06|LBSL|D|20100831\n\rDPI|_B|SP06|SPOW|C|20100831\n\rDPI|_D|SP06|SPOW|C|20100831\n\r"
      + "DPI|_F|SP06|SPOW|C|20100831\n\rDPI|_H|SP06|SPOW|C|20100831\n\rDPI|_P|SP06|NEEB|C|20100831\n\r"
      + "DPI|_A|NC02|LBSL|D|20100831\n\rDPI|_B|NC02|LBSL|D|20100831\n\rDPI|_C|NC02|LBSL|D|20100831\n\r"
      + "DPI|_D|NC02|LBSL|D|20100831\n\rDPI|_E|NC02|LBSL|D|20100831\n\rDPI|_F|NC02|LBSL|D|20100831\n\r"
      + "DPI|_G|NC02|LBSL|D|20100831\n\rDPI|_H|NC02|LBSL|D|20100831\n\rDPI|_J|NC02|LBSL|D|20100831\n\r"
      + "DPI|_K|NC02|LBSL|D|20100831\n\rDPI|_L|NC02|LBSL|D|20100831\n\rDPI|_M|NC02|LBSL|D|20100831\n\r"
      + "DPI|_N|NC02|LBSL|D|20100831\n\rDPI|_A|NC03|LBSL|D|20100831\n\rDPI|_B|NC03|LBSL|D|20100831\n\r"
      + "DPI|_C|NC03|LBSL|D|20100831\n\rDPI|_D|NC03|LBSL|D|20100831\n\rDPI|_E|NC03|LBSL|D|20100831\n\r"
      + "DPI|_F|NC03|LBSL|D|20100831\n\rDPI|_G|NC03|LBSL|D|20100831\n\rDPI|_H|NC03|LBSL|D|20100831\n\r"
      + "DPI|_J|NC03|LBSL|D|20100831\n\rDPI|_K|NC03|LBSL|D|20100831\n\rDPI|_L|NC03|LBSL|D|20100831\n\r"
      + "DPI|_M|NC03|LBSL|D|20100831\n\rDPI|_N|NC03|LBSL|D|20100831\n\rDPI|_B|HC02|SPOW|C|20100831\n\r"
      + "DPI|_D|HC02|SPOW|C|20100831\n\rDPI|_F|HC02|SPOW|C|20100831\n\rDPI|_H|HC02|SPOW|C|20100831\n\r"
      + "DPI|_P|HC02|NEEB|C|20100831\n\rDPI|_A|NM01|LBSL|D|20100831\n\rDPI|_B|NM01|LBSL|D|20100831\n\r"
      + "DPI|_C|NM01|LBSL|D|20100831\n\rDPI|_D|NM01|LBSL|D|20100831\n\rDPI|_E|NM01|LBSL|D|20100831\n\r"
      + "DPI|_F|NM01|LBSL|D|20100831\n\rDPI|_G|NM01|LBSL|D|20100831\n\rDPI|_H|NM01|LBSL|D|20100831\n\r"
      + "DPI|_J|NM01|LBSL|D|20100831\n\rDPI|_K|NM01|LBSL|D|20100831\n\rDPI|_L|NM01|LBSL|D|20100831\n\r"
      + "DPI|_M|NM01|LBSL|D|20100831\n\rDPI|_N|NM01|LBSL|D|20100831\n\rDPI|_A|NM02|LBSL|D|20100831\n\r"
      + "DPI|_B|NM02|LBSL|D|20100831\n\rDPI|_C|NM02|LBSL|D|20100831\n\rDPI|_D|NM02|LBSL|D|20100831\n\r"
      + "DPI|_E|NM02|LBSL|D|20100831\n\rDPI|_F|NM02|LBSL|D|20100831\n\rDPI|_G|NM02|LBSL|D|20100831\n\r"
      + "DPI|_H|NM02|LBSL|D|20100831\n\rDPI|_J|NM02|LBSL|D|20100831\n\rDPI|_K|NM02|LBSL|D|20100831\n\r"
      + "DPI|_L|NM02|LBSL|D|20100831\n\rDPI|_M|NM02|LBSL|D|20100831\n\rDPI|_N|NM02|LBSL|D|20100831\n\r"
      + "DPI|_B|HM01|SPOW|C|20100831\n\rDPI|_D|HM01|SPOW|C|20100831\n\rDPI|_F|HM01|SPOW|C|20100831\n\r"
      + "DPI|_H|HM01|SPOW|C|20100831\n\rDPI|_P|HM01|NEEB|C|20100831\n\rDPI|_B|HM02|SPOW|C|20100831\n\r"
      + "DPI|_D|HM02|SPOW|C|20100831\n\rDPI|_F|HM02|SPOW|C|20100831\n\rDPI|_H|HM02|SPOW|C|20100831\n\r"
      + "DPI|_P|HM02|NEEB|C|20100831\n\rDPI|_B|HM03|SPOW|C|20100831\n\rDPI|_D|HM03|SPOW|C|20100831\n\r"
      + "DPI|_F|HM03|SPOW|C|20100831\n\rDPI|_H|HM03|SPOW|C|20100831\n\rDPI|_P|HM03|NEEB|C|20100831\n\r"
      + "DPI|_B|HM06|SPOW|C|20100831\n\rDPI|_D|HM06|SPOW|C|20100831\n\rDPI|_F|HM06|SPOW|C|20100831\n\r"
      + "DPI|_H|HM06|SPOW|C|20100831\n\rDPI|_P|HM06|NEEB|C|20100831\n\rDPI|_A|SP05|LBSL|M|20100831\n\r"
      + "DPI|_B|SP05|LBSL|M|20100831\n\rDPI|_B|SP05|NATP|M|20100831\n\rDPI|_C|SP05|LBSL|M|20100831\n\r"
      + "DPI|_D|SP05|LBSL|M|20100831\n\rDPI|_D|SP05|EELC|M|20100831\n\rDPI|_E|SP05|LBSL|M|20100831\n\r"
      + "DPI|_F|SP05|LBSL|M|20100831\n\rDPI|_F|SP05|NATP|M|20100831\n\rDPI|_G|SP05|LBSL|M|20100831\n\r"
      + "DPI|_G|SP05|NORW|M|20100831\n\rDPI|_H|SP05|LBSL|M|20100831\n\rDPI|_H|SP05|EELC|M|20100831\n\r"
      + "DPI|_H|SP05|NATP|M|20100831\n\rDPI|_J|SP05|LBSL|M|20100831\n\rDPI|_K|SP05|LBSL|M|20100831\n\r"
      + "DPI|_L|SP05|LBSL|M|20100831\n\rDPI|_M|SP05|LBSL|M|20100831\n\rDPI|_N|SP05|SPOW|M|20100831\n\r"
      + "DPI|_P|SP05|SOUT|M|20100831\n\rDPI|_A|SP06|LBSL|M|20100831\n\rDPI|_B|SP06|LBSL|M|20100831\n\r"
      + "DPI|_B|SP06|NATP|M|20100831\n\rDPI|_C|SP06|LBSL|M|20100831\n\rDPI|_D|SP06|LBSL|M|20100831\n\r"
      + "DPI|_D|SP06|EELC|M|20100831\n\rDPI|_E|SP06|LBSL|M|20100831\n\rDPI|_F|SP06|LBSL|M|20100831\n\r"
      + "DPI|_F|SP06|NATP|M|20100831\n\rDPI|_G|SP06|LBSL|M|20100831\n\rDPI|_G|SP06|NORW|M|20100831\n\r"
      + "DPI|_H|SP06|LBSL|M|20100831\n\rDPI|_H|SP06|EELC|M|20100831\n\rDPI|_H|SP06|NATP|M|20100831\n\r"
      + "DPI|_J|SP06|LBSL|M|20100831\n\rDPI|_K|SP06|LBSL|M|20100831\n\rDPI|_L|SP06|LBSL|M|20100831\n\r"
      + "DPI|_M|SP06|LBSL|M|20100831\n\rDPI|_N|SP06|SPOW|M|20100831\n\rDPI|_P|SP06|SOUT|M|20100831\n\r"
      + "DPI|_A|NM03|LBSL|M|20100831\n\rDPI|_B|NM03|LBSL|M|20100831\n\rDPI|_C|NM03|LBSL|M|20100831\n\r"
      + "DPI|_D|NM03|LBSL|M|20100831\n\rDPI|_E|NM03|LBSL|M|20100831\n\rDPI|_F|NM03|LBSL|M|20100831\n\r"
      + "DPI|_G|NM03|LBSL|M|20100831\n\rDPI|_G|NM03|NORW|M|20100831\n\rDPI|_H|NM03|LBSL|M|20100831\n\r"
      + "DPI|_J|NM03|LBSL|M|20100831\n\rDPI|_K|NM03|LBSL|M|20100831\n\rDPI|_L|NM03|LBSL|M|20100831\n\r"
      + "DPI|_M|NM03|LBSL|M|20100831\n\rDPI|_N|NM03|LBSL|M|20100831\n\rDPI|_N|NM03|SPOW|M|20100831\n\r"
      + "DPI|_A|NM04|LBSL|M|20100831\n\rDPI|_B|NM04|LBSL|M|20100831\n\rDPI|_C|NM04|LBSL|M|20100831\n\r"
      + "DPI|_D|NM04|LBSL|M|20100831\n\rDPI|_E|NM04|LBSL|M|20100831\n\rDPI|_F|NM04|LBSL|M|20100831\n\r"
      + "DPI|_G|NM04|LBSL|M|20100831\n\rDPI|_G|NM04|NORW|M|20100831\n\rDPI|_H|NM04|LBSL|M|20100831\n\r"
      + "DPI|_J|NM04|LBSL|M|20100831\n\rDPI|_K|NM04|LBSL|M|20100831\n\rDPI|_L|NM04|LBSL|M|20100831\n\r"
      + "DPI|_M|NM04|LBSL|M|20100831\n\rDPI|_N|NM04|LBSL|M|20100831\n\rDPI|_N|NM04|SPOW|M|20100831\n\r"
      + "DPI|_B|HM04|NATP|M|20100831\n\rDPI|_D|HM04|EELC|M|20100831\n\rDPI|_F|HM04|NATP|M|20100831\n\r"
      + "DPI|_H|HM04|EELC|M|20100831\n\rDPI|_H|HM04|NATP|M|20100831\n\rDPI|_P|HM04|SOUT|M|20100831\n\r"
      + "DPI|_B|HM05|NATP|M|20100831\n\rDPI|_D|HM05|EELC|M|20100831\n\rDPI|_F|HM05|NATP|M|20100831\n\r"
      + "DPI|_H|HM05|EELC|M|20100831\n\rDPI|_H|HM05|NATP|M|20100831\n\rDPI|_P|HM05|SOUT|M|20100831\n\r";
    String dpiFooter = "ZPT|211|1769165066";
    List<String> dfdRows = new ArrayList<String>();
    dfdRows.add(dpiHeader);
    dfdRows.add(dpiRecords);
    dfdRows.add(dpiFooter);

    return dfdRows;
  }

  @Before
  public void init()
  {
    mConfigurationParameterDao = createMock(ConfigurationParameterDao.class);
    mDpiFileDataWriter = new DpiFileDataWriterImpl();
    mDpiFileDataWriter.setConfigurationParameterDao(mConfigurationParameterDao);
  }
}
