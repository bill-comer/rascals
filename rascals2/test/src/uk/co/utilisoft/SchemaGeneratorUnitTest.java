package uk.co.utilisoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import uk.co.utilisoft.parms.util.PropertyLoader;
import uk.co.utilisoft.parms.util.SchemaGenerator;
import uk.co.utilisoft.parms.util.SchemaGenerator.Dialect;

import static junit.framework.Assert.*;

/**
 * NOTE: For tests to successfully run. The parms/build.properties property
 * classes.dir=C:/eclipse_workspaces/indigo_workspaces/trunks/workspace_1/parms/WebRoot/WEB-INF/classes
 * must reflect where the source files are compiled
 *
 * @author Philip Lau
 * @version 1.0
 */
public class SchemaGeneratorUnitTest
{
  private String mTestFilePath = "test" + File.separator + "file";
  private List<File> mTestFiles = new ArrayList<File>();

  /**
   * Export database scripts for the given domain locations.
   */
  @Test
  public void exportOracleDatabaseScriptFromAnnotations() throws Exception
  {
    Properties props = PropertyLoader.loadProperties("./build.properties");
    String classesDirPath = props.getProperty("classes.dir");
    SchemaGenerator sgen = new SchemaGenerator(new String[] {"uk.co.utilisoft.parms.domain"},
                                               classesDirPath,
                                               new String[] {".*UnitTest.*", ".*Test.*", ".*Interface.*", ".*\\$.*"});
    sgen.generate(Dialect.ORACLE, mTestFilePath);
    mTestFiles = getSavedFiles(mTestFilePath);
    assertTrue(!mTestFiles.isEmpty());

    for (int i = 0; i < mTestFiles.size(); i++)
    {
      File file = mTestFiles.get(i);
      InputStream is = new FileInputStream(file);
      long length = file.length();

      if (length > Integer.MAX_VALUE)
      {
        fail("Failed to read file " + file.getName() + " because file length " + length + " is too long");
      }

      byte[] bytes = new byte[(int)length];
      int offset = 0;
      int numRead = 0;

      while (offset < bytes.length
             && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
      {
          offset += numRead;
      }

      if (offset < bytes.length)
      {
        fail("Failed to finish reading file " + file.getName());
      }

      is.close();

      // test file contains some data
      assertNotNull(bytes);
      assertTrue("File " + file.getName() + " contains zero bytes", bytes.length > 0);
    }
  }

  /**
   * Export database scripts for the given domain locations.
   */
  @Test
  public void exportSQLServerDatabaseScriptFromAnnotations() throws Exception
  {
    Properties props = PropertyLoader.loadProperties("./build.properties");
    String classesDirPath = props.getProperty("classes.dir");
    SchemaGenerator sgen = new SchemaGenerator(new String[] {"uk.co.utilisoft.parms.domain"},
                                               classesDirPath,
                                               new String[] {".*UnitTest.*", ".*Test.*", ".*Interface.*", ".*\\$.*"});
    sgen.generate(Dialect.SQLSERVER, mTestFilePath);
    mTestFiles = getSavedFiles(mTestFilePath);
    assertTrue(!mTestFiles.isEmpty());

    for (int i = 0; i < mTestFiles.size(); i++)
    {
      File file = mTestFiles.get(i);
      InputStream is = new FileInputStream(file);
      long length = file.length();

      if (length > Integer.MAX_VALUE)
      {
        fail("Failed to read file " + file.getName() + " because file length " + length + " is too long");
      }

      byte[] bytes = new byte[(int)length];
      int offset = 0;
      int numRead = 0;

      while (offset < bytes.length
             && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
      {
          offset += numRead;
      }

      if (offset < bytes.length)
      {
        fail("Failed to finish reading file " + file.getName());
      }

      is.close();

      // test file contains some data
      assertNotNull(bytes);
      assertTrue("File " + file.getName() + " contains zero bytes", bytes.length > 0);
    }
  }

  @After
  public void cleanUp()
  {
    if (!mTestFiles.isEmpty())
    {
      deleteTestFiles(mTestFiles);
    }
  }

  private void deleteTestFiles(List<File> aFiles)
  {
    for (File file : aFiles)
    {
      if (file.isFile() && file.exists())
      {
        file.delete();
      }
    }
  }

  private List<File> getSavedFiles(String aPath) throws IllegalAccessException
  {
    List<File> files = new ArrayList<File>();
    String filePrefix = "ddl_";
    String fileSuffix = ".sql";

    File directory = new File(aPath);

    if (directory.isDirectory())
    {
      String[] fileNames = directory.list();

      for (String fileName : fileNames)
      {
        if (fileName.startsWith(filePrefix) && fileName.endsWith(fileSuffix))
        {
          File file = new File(directory.getAbsolutePath() + File.separator + fileName);

          if (file.isFile() && file.exists())
          {
            files.add(file);
          }
        }
      }
    }

    return files;
  }


}
