package uk.co.utilisoft.rascals.web.listGalas;

import uk.co.utilisoft.genericutils.web.searchfilter.SearchWrapper;
import uk.co.utilisoft.rascals.domain.Gala;

/**
 */
public class ListGalasDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private Gala gala;

  /**
   */
  public ListGalasDataSearchWrapper(Gala aGala)
  {
    gala = aGala;
  }

  /**
   */
  public Gala getGala()
  {
    return gala;
  }

  /**
   */
  public void setGala(Gala aGala)
  {
    gala = aGala;
  }


  
  
}
