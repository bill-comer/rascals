package uk.co.utilisoft.parms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Philip Lau
 * @version 1.0
 */
public final class PropertyLoader
{
  private static final Logger LOGGER = Logger.getLogger(PropertyLoader.class);

  /**
   * Private Constructor.
   */
  private PropertyLoader()
  { }

  /**
   * Load a resource from a properties file.
   *
   * @param aResourceName the resource name
   * @return the Properties
   */
  public static Properties loadProperties (String aResourceName)

  {
    if (aResourceName == null)
    {
      LOGGER.error("Resource Name cannot be null");
      throw new IllegalArgumentException ("Resource Name cannot be null");
    }

    Properties properties = null;
    InputStream inputStream = null;

    inputStream = ClassLoader.getSystemResourceAsStream(aResourceName);

    try
    {
      properties = new Properties();

      if (inputStream == null)
      {
        inputStream = new File(aResourceName).toURI().toURL().openStream();
      }

      properties.load(inputStream);
    }
    catch (IOException ioe)
    {
      LOGGER.error("Unable to read " + aResourceName + " resource", ioe);
    }
    finally
    {
      if (inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (IOException ioe)
        {
          LOGGER.error("Unable to close " + aResourceName + " resource", ioe);
        }
      }
    }

    return properties;
  }

}
