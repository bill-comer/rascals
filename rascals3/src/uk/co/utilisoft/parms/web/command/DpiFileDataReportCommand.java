package uk.co.utilisoft.parms.web.command;

import java.util.List;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.GenericAgent;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiFileDataReportCommand extends SearchFilterCommand
{
  private static final long serialVersionUID = 1L;
  private DpiFile mDpiFile;
  private Long mDpiFilePk;
  private DpiFileData mDpiFileData;
  private List<GenericAgent> mAgents;

  /**
   *
   */
  public DpiFileDataReportCommand()
  {
  }

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

  /**
   * @return the dpi file data
   */
  public DpiFileData getDpiFileData()
  {
    return mDpiFileData;
  }

  /**
   * @param aDpiFileData the dpi file data
   */
  public void setDpiFileData(DpiFileData aDpiFileData)
  {
    this.mDpiFileData = aDpiFileData;
  }

  /**
   * @return the dpi file pk
   */
  public Long getDpiFilePk()
  {
    return mDpiFilePk;
  }

  /**
   * @param aDpiFilePk the dpi file pk
   */
  public void setDpiFilePk(Long aDpiFilePk)
  {
    this.mDpiFilePk = aDpiFilePk;
  }

  /**
   * @return the agents
   */
  public List<GenericAgent> getAgents()
  {
    return mAgents;
  }

  /**
   * @param aAgents the agents
   */
  public void setAgents(List<GenericAgent> aAgents)
  {
    mAgents = aAgents;
  }
}