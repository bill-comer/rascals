package uk.co.utilisoft.utils;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import uk.co.utilisoft.parms.util.FileUtil;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class FileUtilUnitTest
{
  /**
   * @throws Exception
   */
  @Test
  public void testGetBytesFromInputStream() throws Exception
  {
    String testFile = "test" + File.separator + "file" + File.separator + "p0028" + File.separator
      + "validP0028TestFile.csv";
    byte[] data = FileUtil.getBytes(new ByteArrayInputStream(FileUtil.getBytesFromFile(new File(testFile))));
    assertNotNull(data);
    String txtData = new String(data);
    assertFalse(StringUtils.isBlank(txtData));
  }

  /**
   * @throws Exception
   */
  @Test
  public void testGetBytesAlreadyRead() throws Exception
  {
    String testFile = "test" + File.separator + "file" + File.separator + "p0028" + File.separator
      + "validP0028TestFile.csv";
    InputStream is = new ByteArrayInputStream(FileUtil.getBytesFromFile(new File(testFile)));
    byte[] data1 = FileUtil.getBytes(is);

    // re read
    byte[] data2 = FileUtil.getBytes(is);
    assertNotNull(data2);
    String txtData = new String(data2);
    assertFalse(StringUtils.isBlank(txtData));
  }

  /**
   * @throws Exception
   */
  @Test
  public void testGetBytesFromFile() throws Exception
  {
    String testFile = "test" + File.separator + "file" + File.separator + "p0028" + File.separator
      + "validP0028TestFile.csv";
    byte[] data = FileUtil.getBytesFromFile(new File(testFile));
    assertNotNull(data);
    String txtData = new String(data);
    assertFalse(StringUtils.isBlank(txtData));
  }
}
