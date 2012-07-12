package uk.co.utilisoft.rascals.web.listGalaRaces;

import uk.co.utilisoft.genericutils.web.searchfilter.SearchWrapper;
import uk.co.utilisoft.rascals.domain.Race;

public class ListGalaRacesDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;
  private Race race;

  /**
   */
  public ListGalaRacesDataSearchWrapper(Race aRace)
  {
    race = aRace;
  }

  /**
   */
  public Race getRace()
  {
    return race;
  }

  /**
   */
  public void setRace(Race aRace)
  {
    race = aRace;
  }


  
  
}
