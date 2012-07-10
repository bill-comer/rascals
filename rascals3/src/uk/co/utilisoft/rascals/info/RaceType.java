package uk.co.utilisoft.rascals.info;


public enum RaceType
{
  INDIVIDUAL("Individual"),      // indevidual
  RELAY("Relay"),                // all same age, can be same or difft strokes
  CANON("Canon");                // 2 boys/2 girls difft ages
  
  private String stroke;
  
  private RaceType(String aStroke)
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
