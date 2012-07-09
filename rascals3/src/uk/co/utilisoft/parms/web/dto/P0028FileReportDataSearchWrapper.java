package uk.co.utilisoft.parms.web.dto;

import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028FileReportDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private P0028File mP0028File;
  private P0028Data mP0028Data;

  /**
   * @param aP0028Data the P0028Data
   */
  public P0028FileReportDataSearchWrapper(P0028Data aP0028Data)
  {
    setP0028File(aP0028Data.getP0028File());
    mP0028Data = aP0028Data;
  }

  /**
   * @return the P0028File.
   */
  public P0028File getP0028File()
  {
    return mP0028File;
  }

  /**
   * @param aP0028File The P0028File to set.
   */
  public void setP0028File(P0028File aP0028File)
  {
    this.mP0028File = aP0028File;
  }

  /**
   * @return the P0028Data
   */
  public P0028Data getP0028Data()
  {
    return mP0028Data;
  }

  /**
   * @param aP0028Data the P0028Data
   */
  public void setP0028Data(P0028Data aP0028Data)
  {
    mP0028Data = aP0028Data;
  }
}
