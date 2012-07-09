package uk.co.utilisoft.parms.web.command.sp04;

import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.web.command.SearchFilterCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class Sp04ListDataCommand extends SearchFilterCommand
{
  private static final long serialVersionUID = 1L;
  private Sp04File mSp04File;

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
