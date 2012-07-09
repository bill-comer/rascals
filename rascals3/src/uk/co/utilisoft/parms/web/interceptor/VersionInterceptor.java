package uk.co.utilisoft.parms.web.interceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Intercepts a handler to put a version attribute into the session.
 *
 * @author ashrafim
 * @version 1.0
 */
public class VersionInterceptor implements HandlerInterceptor,
                                           ApplicationContextAware
{
  private static final String VERSION = "version";
  private ApplicationContext mCtx;

  /**
   * Intercepts before the handler is called.
   *
   * @param aRequest HTTP request
   * @param aResponse HTTP response
   * @param aHandler Handler to which the call is being passed
   * @return true if the call should continue, otherwise false
   * @throws Exception if problem occurs
   */
  public boolean preHandle(HttpServletRequest aRequest,
                           HttpServletResponse aResponse,
                           Object aHandler) throws Exception
  {
    return true;
  }

  /**
   * Intercepts after the handler is called.
   *
   * @param aRequest HTTP request
   * @param aResponse HTTP response
   * @param aHandler Handler from which the call has been passed
   * @param aModelAndView ModelAndView being returned by the handler
   * @throws Exception if problem occurs
   */
  public void postHandle(HttpServletRequest aRequest,
                         HttpServletResponse aResponse,
                         Object aHandler,
                         ModelAndView aModelAndView) throws Exception
  {
    if (aRequest.getSession().getAttribute(VERSION) == null)
    {
      Resource manifestResource = mCtx.getResource("/META-INF/MANIFEST.MF");
      String version = getAttribute(manifestResource);
      if (version == null)
      {
        version = "unknown";
      }
      aRequest.getSession().setAttribute(VERSION, version);
    }
  }

  /**
   * Intercepts after the handler has completed.
   *
   * @param aRequest HTTP request
   * @param aResponse HTTP response
   * @param aHandler Handler from which the call has been passed
   * @param anException if exception has occurred
   * @throws Exception if problem occurs
   */
  public void afterCompletion(HttpServletRequest aRequest,
                              HttpServletResponse aResponse,
                              Object aHandler,
                              Exception anException) throws Exception
  { }

  /**
   * Allows the framework to set the application context
   * so that the method is able to access the manifest file.
   *
   * @param aCtx ApplicationContext
   */
  public void setApplicationContext(ApplicationContext aCtx)
  {
    mCtx = aCtx;
  }

  /**
   * Gets an attribute from the manifest file.
   *
   * @param aManifestResource Manifest file as a resource
   * @return The value of the required attribute or null if its not found.
   * @throws IOException If an IO error occurs.
   */
  private String getAttribute(Resource aManifestResource) throws IOException
  {
    Manifest manifest = null;
    String version = null;

    InputStream stream = aManifestResource.getInputStream();
    if (stream != null)
    {
      manifest = new Manifest(stream);
      Attributes atts = manifest.getMainAttributes();
      version = atts.getValue("Implementation-version");
    }
    return version;
  }
}

