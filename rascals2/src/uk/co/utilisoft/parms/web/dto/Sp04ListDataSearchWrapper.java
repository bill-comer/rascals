package uk.co.utilisoft.parms.web.dto;

import uk.co.utilisoft.parms.domain.Sp04File;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class Sp04ListDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private Sp04File mSp04File;

  /**
   * @param aSpo4File the Sp04 File
   */
  public Sp04ListDataSearchWrapper(Sp04File aSpo4File)
  {
    mSp04File = aSpo4File;
  }

  /**
   * @return the Sp04 File
   */
  public Sp04File getSp04File()
  {
    return mSp04File;
  }

  /**
   * @param aSp04File the Sp04 File
   */
  public void setSp04File(Sp04File aSp04File)
  {
    mSp04File = aSp04File;
  }
}
