package uk.co.utilisoft.parms.web.command.p0028;

import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.web.command.SearchFilterCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class Sp04FileViewCommand extends SearchFilterCommand
{
  private Sp04File mFile;
  private Long mFilePk;

  public Sp04FileViewCommand() { }

  /**
   * @return the P0028 file
   */
  public Sp04File getFile()
  {
    return mFile;
  }

  /**
   * @param aFile the P0028 file
   */
  public void setFile(Sp04File aFile)
  {
    mFile = aFile;
  }

  public Long getFilePk()
  {
    return mFilePk;
  }

  public void setFilePk(Long aFilePk)
  {
    this.mFilePk = aFilePk;
  }

}