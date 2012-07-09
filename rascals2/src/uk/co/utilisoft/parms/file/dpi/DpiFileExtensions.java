package uk.co.utilisoft.parms.file.dpi;


public class DpiFileExtensions
{
  private static final String[] extensions = {
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN", 
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };
  
  public static String getExtension(int aMonth)
  {
    if (aMonth < 1 || aMonth > 12) 
    {
      throw new RuntimeException("Months must be between 1 & 12." 
          + aMonth + " is not valid.");
    }
    
    return extensions[aMonth -1];
  }
}
