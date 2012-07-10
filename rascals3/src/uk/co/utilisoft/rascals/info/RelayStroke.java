package uk.co.utilisoft.rascals.info;


public enum RelayStroke
{
  MEDLEY("Medley"),
  TWO_BREAST_TWO_FREE("2Breast/2Free"),
  FREE_STYLE("Freestyle");
  

  private String stroke;
  
  private RelayStroke(String aStroke)
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
