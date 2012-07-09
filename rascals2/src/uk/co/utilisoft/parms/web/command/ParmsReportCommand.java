package uk.co.utilisoft.parms.web.command;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class ParmsReportCommand extends PagingCommand
{
  private DpiFile mDpiFile;
  private Integer mDpiFilePk;
  private DpiFileData mDpiFileData;
  private String mReplicateRequest;

  public String getReplicateRequest()
  {
    return mReplicateRequest;
  }
  public void setReplicateRequest(String aReplicateRequest)
  {
    this.mReplicateRequest = aReplicateRequest;
  }
  public ParmsReportCommand() { }

  /**
   * @return the DPI file
   */
  public DpiFile getDpiFile()
  {
    return mDpiFile;
  }

  /**
   * @param aDpiFile the DPI file
   */
  public void setDpiFile(DpiFile aDpiFile)
  {
    mDpiFile = aDpiFile;
  }

  public DpiFileData getDpiFileData()
  {
    return mDpiFileData;
  }

  public void setDpiFileData(DpiFileData aDpiFileData)
  {
    this.mDpiFileData = aDpiFileData;
  }

  public Integer getDpiFilePk()
  {
    return mDpiFilePk;
  }

  public void setDpiFilePk(Integer aDpiFilePk)
  {
    this.mDpiFilePk = aDpiFilePk;
  }

}
