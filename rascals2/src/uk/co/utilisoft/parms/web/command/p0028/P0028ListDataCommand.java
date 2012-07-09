package uk.co.utilisoft.parms.web.command.p0028;

import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.web.command.SearchFilterCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028ListDataCommand extends SearchFilterCommand
{
  private static final long serialVersionUID = 1L;
  private P0028File mP0028File;
  /**
   * @return the P0028File
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
    mP0028File = aP0028File;
  }
}
