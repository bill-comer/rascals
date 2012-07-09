package uk.co.utilisoft.parms.web.dto;

import uk.co.utilisoft.parms.domain.DpiFile;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiListDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private DpiFile mDpiFile;

  /**
   * @param aDpiFile the DpiFile
   */
  public DpiListDataSearchWrapper(DpiFile aDpiFile)
  {
    mDpiFile = aDpiFile;
  }

  /**
   * @return the DpiFile
   */
  public DpiFile getDpiFile()
  {
    return mDpiFile;
  }

  /**
   * @param aDpiFile the DpiFile
   */
  public void setDpiFile(DpiFile aDpiFile)
  {
    this.mDpiFile = aDpiFile;
  }


}
