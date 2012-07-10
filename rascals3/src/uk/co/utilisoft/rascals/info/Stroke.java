package uk.co.utilisoft.rascals.info;


public enum Stroke
{
  BACK_STROKE("backstroke"),
  BUTTER_FLY("Butterfly"),
  BREAST_STROKE("Breaststroke"),
  FREE_STYLE("Freestyle");
  

  private String stroke;
  
  private Stroke(String aStroke)
  {
    stroke = aStroke;
  }

  /**
   * @return the description of the Audit
   */
  public String getDescription()
  {
    return stroke;
  }
 
}
