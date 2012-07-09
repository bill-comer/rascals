package uk.co.utilisoft.parms.web.command.p0028;

import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.web.command.SearchFilterCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
@SuppressWarnings("serial")
public class P0028FileViewCommand extends SearchFilterCommand
{
  private P0028File mFile;
  private Long mFilePk;
  private P0028FileData mP0028FileData;

  /**
   * Default constructor
   */
  public P0028FileViewCommand()
  { }

  /**
   * @return the P0028 file
   */
  public P0028File getFile()
  {
    return mFile;
  }

  /**
   * @param aFile the P0028 file
   */
  public void setFile(P0028File aFile)
  {
    mFile = aFile;
  }

  /**
   * @return the P0028File pk
   */
  public Long getFilePk()
  {
    return mFilePk;
  }

  /**
   * @param aFilePk the P0028File pk
   */
  public void setFilePk(Long aFilePk)
  {
    this.mFilePk = aFilePk;
  }

  /**
   * @return the P0028File lob data
   */
  public P0028FileData getP0028FileData()
  {
    return mP0028FileData;
  }

  /**
   * @param aP0028FileData the P0028File lob data
   */
  public void setP0028FileData(P0028FileData aP0028FileData)
  {
    this.mP0028FileData = aP0028FileData;
  }

}