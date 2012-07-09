package uk.co.utilisoft.rascals.web.listSwimmers;

import uk.co.utilisoft.genericutils.web.searchfilter.SearchWrapper;
import uk.co.utilisoft.rascals.domain.Swimmer;

/**
 */
public class ListSwimmersDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private Swimmer swimmer;

  /**
   */
  public ListSwimmersDataSearchWrapper(Swimmer aSwimmer)
  {
    swimmer = aSwimmer;
  }

  /**
   */
  public Swimmer getSwimmer()
  {
    return swimmer;
  }

  /**
   */
  public void setSwimmer(Swimmer aSwimmer)
  {
    swimmer = aSwimmer;
  }


  
  
}
