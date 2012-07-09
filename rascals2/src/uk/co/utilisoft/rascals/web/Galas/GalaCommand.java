package uk.co.utilisoft.rascals.web.Galas;

import uk.co.utilisoft.genericutils.web.util.controllers.CrudCommand;
import uk.co.utilisoft.rascals.domain.Gala;

/**
 * @author winstanleyd
 * @version 1.0
 */
public class GalaCommand extends CrudCommand
{
  private Gala gala;
  
  private String homeAway;

  public String getHomeAway()
  {
    return homeAway;
  }
  public void setHomeAway(String homeAway)
  {
    this.homeAway = homeAway;
  }
  



  /**
   * Default constructor.
   */
  public GalaCommand()
  {
    super();
    gala = new Gala();
  }



  public Gala getGala()
  {
    return gala;
  }



  public void setGala(Gala aGala)
  {
    this.gala = aGala;
  }
  
  public void setHomeAway(boolean atHome)
  {
    if (atHome)
    {
      setHomeAway("home");
    }
    else
    {
      setHomeAway("away");
    }
    
  }



  
}
