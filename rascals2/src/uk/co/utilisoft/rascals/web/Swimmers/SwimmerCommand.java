package uk.co.utilisoft.rascals.web.Swimmers;

import uk.co.utilisoft.genericutils.web.util.controllers.CrudCommand;
import uk.co.utilisoft.rascals.domain.Swimmer;

/**
 * @author winstanleyd
 * @version 1.0
 */
public class SwimmerCommand extends CrudCommand
{
  private Swimmer swimmer;
  
  private String male;

  public String getMale()
  {
    return male;
  }
  public void setMale(String aMaleFemale)
  {
    this.male = aMaleFemale;
  }

  /**
   * Default constructor.
   */
  public SwimmerCommand()
  {
    super();
    swimmer = new Swimmer();
  }



  public Swimmer getSwimmer()
  {
    return swimmer;
  }



  public void setSwimmer(Swimmer aSwimmer)
  {
    this.swimmer = aSwimmer;
  }

  public void setMale(boolean aMaleFemale)
  {
    if (aMaleFemale)
    {
      setMale("male");
    }
    else
    {
      setMale("female");
    }
    
  }

  
}
