package uk.co.utilisoft.parms.file;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface FileDataWriter
{
  /**
   * Write data out to a File using default and temporary directory paths.
   * @param aFileName the File Name
   * @param aData the Data
   * @return the name of the file saved or error message
   */
  String[] save(String aFileName, String aData);

  /**
   * Write data out to a File using a default temporary directory path.
   * @param aPath the File Directory Path
   * @param aFileName the File Name
   * @param aData the Data
   * @return the name of the file archived (if found), together with the file name saved, or an error message
   */
  String[] save(String aPath, String aFileName, String aData);

  /**
   * Write data out to a File using a temporary directory path.
   * @param aTempPath the File temporary Path
   * @param aPath the File Directory Path
   * @param aFileName the File Name
   * @param aData the Data
   * @return the name of the file saved or error message
   */
  String[] save(String aTempPath, String aPath, String aFileName, String aData);
}
