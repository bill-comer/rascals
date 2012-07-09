package uk.co.utilisoft.parms.web.dto;

import uk.co.utilisoft.parms.domain.P0028File;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028ListDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private P0028File mP0028File;

  /**
   * @param aP0028File the P0028File
   */
  public P0028ListDataSearchWrapper(P0028File aP0028File)
  {
    mP0028File = aP0028File;
  }

  /**
   * @return the P0028File
   */
  public P0028File getP0028File()
  {
    return mP0028File;
  }

  /**
   * @param aP0028File the P0028File
   */
  public void setP0028File(P0028File aP0028File)
  {
    mP0028File = aP0028File;
  }
}
