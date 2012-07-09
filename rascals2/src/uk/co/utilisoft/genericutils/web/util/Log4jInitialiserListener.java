package uk.co.utilisoft.genericutils.web.util;

import java.io.FileNotFoundException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.util.Log4jConfigurer;

/**
 * @author furbern
 *
 * This class is a listener to initialise the log4j system within a
 * web environment. Unlike the std Spring listener this uses env
 * entries to hold config details and so can be used with Websphere
 * etc where context params cannot be amended
 *
 * If the location env entry starts with the file separator then the
 * config root path is prepended. If the location env is null or empty
 * then the config root path +'log4j.properties' is used Otherwise the
 * location is assumed to be the full path
 */
public class Log4jInitialiserListener extends Log4jConfigurer
    implements javax.servlet.ServletContextListener
{

  private static final String VALUE = "RootPathPropertyValue";

  private static final String JAVA_CONTEXT = "java:comp/env";

  private static final String LOCATION = "Log4jConfigLocation";

  private static final String FILE_SEPARATOR =
      System.getProperty("file.separator");

  /**
   * @see javax.servlet.ServletContextListener#
   * contextDestroyed(javax.servlet.ServletContextEvent)
   *      {@inheritDoc}
   */
  public void contextInitialized(ServletContextEvent aEvent)
  {
    ServletContext context = aEvent.getServletContext();
    try
    {
      // Get the value of the context name
      context.getServletContextName();

      InitialContext ic = new InitialContext();
      Context ctx = (Context) ic.lookup(JAVA_CONTEXT);
      String logLocation = ((String) (ctx.lookup(LOCATION))).trim();
      StringBuilder buf = new StringBuilder();
      if ((logLocation != null) && (!logLocation.equals("")))
      {
        String first = logLocation.substring(0, 1);
        if (FILE_SEPARATOR.equals(first))
        {
          String pathValue = ((String) (ctx.lookup(VALUE))).trim();
          buf.append(pathValue).append(logLocation);
        }
        else
        {
          buf.append(logLocation);
        }
      }
      else
      {
        String pathValue = ((String) (ctx.lookup(VALUE))).trim();
        buf.append(pathValue).append("log4j.properties");
      }

      initLogging(buf.toString());
    }
    catch (NamingException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  /**
   * @see javax.servlet.ServletContextListener
   * #contextInitialized(javax.servlet.ServletContextEvent)
   *      {@inheritDoc}
   */
  public void contextDestroyed(ServletContextEvent aEvent)
  {
    shutdownLogging();
  }

}