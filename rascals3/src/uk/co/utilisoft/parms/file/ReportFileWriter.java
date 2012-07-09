package uk.co.utilisoft.parms.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.parms.dao.ConfigurationParameterDao;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;

public abstract class ReportFileWriter implements FileDataWriter
{
  private static final String DEFAULT_TEMP_PATH = "temp";
  private static final String DEFAULT_ARCHIVE_PATH = "archive";

  @Autowired(required=true)
  @Qualifier("parms.configurationParameterDao")
  private ConfigurationParameterDao mConfigurationParameterDao;

  public ConfigurationParameterDao getConfigurationParameterDao()
  {
    return mConfigurationParameterDao;
  }

  private String mTempPath;

  public abstract ConfigurationParameter.NAME getConfigParamName();


  /**
   * @see uk.co.utilisoft.parms.file.FileDataWriter#save(java.lang.String, java.lang.String)
   */
  public String[] save(String aFileName, String aData)
  {
    ConfigurationParameter fileLoc = getConfigurationParameterDao().getByName(getConfigParamName());
    if (fileLoc == null || fileLoc.getValue().trim().equals(""))
    {
      throw new RuntimeException("The DPI File Location must be configured for PARMS_DPI_FILE_LOCATION");
    }

    return save(DEFAULT_TEMP_PATH, fileLoc.getValue(), aFileName, aData);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.file.FileDataWriter#save(java.lang.String, java.lang.String, java.lang.String)
   */
  public String[] save(String aPath, String aFileName, String aData)
  {
    return save(DEFAULT_TEMP_PATH, aPath, aFileName, aData);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.file.FileDataWriter#save(java.lang.String, java.lang.String, java.lang.String,
   * java.lang.String)
   */
  public String[] save(String aTempPath, String aPath,
                           String aFileName, String aData)
  {
    File parent = new File(aPath);
    boolean parentExists = createDir(parent);
    File file = null;
    String[] log = new String[2];

    if (parentExists)
    {
      mTempPath = mTempPath == null ? aTempPath : mTempPath;
      String tempPath = aPath + (mTempPath != null ? File.separator + mTempPath : "");

      if (tempPath.equals(aPath))
      {
        log[1] = "Failed to set a temporary file directory: " + aPath;
        return log;
      }

      File tempParent = new File(tempPath);
      boolean tempParentExists = (!tempParent.exists() || (tempParent.exists() && tempParent.isFile())) ? tempParent.mkdir() : true;
      File tempFile = null;

      if (tempParentExists)
      {
        tempFile = new File(tempParent, aFileName);

        if (tempFile.exists())
        {
          tempFile.delete();
        }

        if (!tempFile.exists())
        {
          write(tempFile, aData);
          file = new File(parent, tempFile.getName());

          if (file.exists())
          {
            DateTime now = new DateTime();
            String archivePath = aPath + (DEFAULT_ARCHIVE_PATH != null ? File.separator + DEFAULT_ARCHIVE_PATH : "");
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");
            File archiveDir = new File(archivePath);
            if (!archiveDir.exists())
            {
              archiveDir.mkdirs();
            }
            File archiveFile = new File(archivePath, tempFile.getName() + "." + now.toString(fmt));
            boolean archiveState = file.renameTo(archiveFile);
            System.out.println(archiveState);
          }

          boolean isMoved = tempFile.renameTo(file);

          if (!isMoved)
          {
            log[1] = "Failed to move file " + tempFile.getAbsolutePath() + " to " + file.getAbsolutePath();
            return log;
          }
        }
        else
        {
          log[1] = "Duplicate file already exists :" + tempFile.getAbsolutePath() + " AND should have been deleted.";
          return log;
        }
      }
      else
      {
        log[1] = "Failed to create temp directory " + tempParent.getAbsolutePath();
        return log;
      }
    }
    else
    {
      log[1] = "Failed to create parent directory " + parent.getAbsolutePath();
      return log;
    }

    log[0] = file.getAbsolutePath();
    return log;
  }

  /**
   * Write out a file.
   *
   * @param aFile the file
   * @param aData the data
   */
  public void write(File aFile, String aData)
  {
    try
    {
      BufferedWriter out = new BufferedWriter(new FileWriter(aFile));
      out.write(aData);
      out.close();
    }
    catch (IOException ioe)
    {
      throw new RuntimeException("Failed to write to file " + aFile.getAbsolutePath(), ioe);
    }
  }

  /**
   * Create file directory.
   *
   * @param aFile the file
   * @return true if directory created
   */
  protected boolean createDir(File aFile)
  {
    boolean pathExists = (!aFile.exists() || (aFile.exists() && aFile.isFile())) ? aFile.mkdir() : true;

    if (!pathExists)
    {
      throw new RuntimeException("Failed to create directory " + aFile.getAbsolutePath());
    }

    return pathExists;
  }

  /**
   * @param aConfigurationParameterDao the configuration parameter data access object
   */
  public void setConfigurationParameterDao(ConfigurationParameterDao aConfigurationParameterDao)
  {
    mConfigurationParameterDao = aConfigurationParameterDao;
  }

}
