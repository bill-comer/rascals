package uk.co.utilisoft.parms.web.util;

import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Listener  Class to load system properties pre-Spring load.
 * Note this has to run pre-Spring therefore cannot make use
 * of Spring features.
*
* @author Nigel Furber
*/
public class WebSystemPropertyListener implements javax.servlet.ServletContextListener
{
  private static final String PATH = "RootPathPropertyName";

  private static final String VALUE = "RootPathPropertyValue";

  private static final String JAVA_CONTEXT = "java:comp/env";

  private static final String PATH_ERROR =
    "Path root name not set - check web.xml and context config - Should "
    +  " be String env-ref variable";

  private static final String VALUE_ERROR =
    "Path root value not set - check web.xml and context config - Should "
    + " be String env-ref variable ";

  private static final String START_ABORTED = "START ABORTED";

  private static final String CONTEXT_ENDED = " CONTEXT ENDED";

  /**
   * @see javax.servlet.ServletContextListener
   * #contextInitialized(javax.servlet.ServletContextEvent)
   * {@inheritDoc}
   */
  public void contextInitialized(ServletContextEvent aEvent)
  {

      ServletContext context = aEvent.getServletContext();
      String contextName = context.getServletContextName();
      try
      {
        // Get the value of the context name
        context.getServletContextName();

        InitialContext ic = new InitialContext();
        Context ctx = (Context) ic.lookup(JAVA_CONTEXT);
        String pathName = (String) (ctx.lookup(PATH));
        String pathValue = (String) (ctx.lookup(VALUE));
        if ((pathName == null) || (pathName.trim().equals("")))
        {
          StringBuilder message =
              new StringBuilder(JAVA_CONTEXT).append(contextName).append(PATH_ERROR)
                  .append(PATH);
          System.out.println(message.toString());
          System.err.println(message.toString());
          System.out.println(START_ABORTED);
          System.err.println(START_ABORTED);
          throw new RuntimeException(message.toString());

        }
        if ((pathValue == null) || (pathValue.trim().equals("")))
        {
          StringBuilder message =
              new StringBuilder(JAVA_CONTEXT).append(contextName).append(VALUE_ERROR)
                  .append(PATH);
          System.out.println(message.toString());
          System.err.println(message.toString());
          System.out.println(START_ABORTED);
          System.err.println(START_ABORTED);
          throw new RuntimeException(message.toString());
        }

        String value = System.getProperty(pathName);

        if (value == null)
        {
          System.setProperty(pathName, pathValue);
        }
      }
      catch (NamingException ne)
      {
        System.out.println(ne.getMessage());
        System.err.println(ne.getMessage());
        System.out.println(START_ABORTED);
        System.err.println(START_ABORTED);
        throw new RuntimeException(ne.getMessage(), ne);
      }

      Properties properties = System.getProperties();
      // get an enumeration of all property names that exist
      Enumeration< ? > names = properties.propertyNames();
      while (names.hasMoreElements())
      {
        // for each name, display its name=value pair
        String name = (String) names.nextElement();
        String value = properties.getProperty(name);
        System.out.println("System property: [" + name + "] = [" + value + "]");
      }
    }

  /**
   * @see javax.servlet.ServletContextListener
   * #contextDestroyed(javax.servlet.ServletContextEvent)
   * {@inheritDoc}
   */
  public void contextDestroyed(ServletContextEvent aEvent)
  {
    ServletContext context = aEvent.getServletContext();
    String contextName = context.getServletContextName();
    System.out.println(contextName + CONTEXT_ENDED);
    System.err.println(contextName + CONTEXT_ENDED);
  }
}

