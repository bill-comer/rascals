package uk.co.utilisoft.parms.web.interceptor;

import static uk.co.utilisoft.parms.web.controller.WebConstants.PARMS_RESPONSE_MESSAGE_DATA;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.web.util.ResponseMessageLoader;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class ResponseMessageInterceptor implements HandlerInterceptor
{
  private Map<String, List<String>> mUrlMappedMessageKeys = new HashMap<String, List<String>>();

  /**
   * {@inheritDoc}
   * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, java.lang.Object)
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
  {
    return true;
  }

  /**
   * {@inheritDoc}
   * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
   */
  @Override
  public void afterCompletion(HttpServletRequest aRequest, HttpServletResponse aResponse, Object aHandler,
                              Exception aException) throws Exception
  {
    // nothing to clean up
  }

  /**
   * {@inheritDoc}
   * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax
   * .servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
   */
  @Override
  public void postHandle(HttpServletRequest aRequest, HttpServletResponse aResponse, Object aHandler,
                         ModelAndView aModelAndView) throws Exception
  {
    ResponseMessageLoader messageLoader = new ResponseMessageLoader();

    if (aRequest.getMethod().equalsIgnoreCase(RequestMethod.GET.toString()))
    {
      String url = aRequest.getRequestURI();
      if (url.contains("/") && url.contains("htm"))
      {
        url = url.substring(url.lastIndexOf('/'), (url.lastIndexOf("htm") + "htm".length()));
      }

      if (!mUrlMappedMessageKeys.keySet().isEmpty())
      {
        Iterator<String> filterUrlsIter = mUrlMappedMessageKeys.keySet().iterator();

        while (filterUrlsIter.hasNext())
        {
          String filteredUrl = filterUrlsIter.next();

          if (filteredUrl.equals(url))
          {
              aRequest.setAttribute(PARMS_RESPONSE_MESSAGE_DATA,
                                    messageLoader
                                    .appendResponseMessageData(aRequest, mUrlMappedMessageKeys
                                                               .get(filteredUrl).toArray(new String[] {})));
          }
        }
      }
    }
  }

  /**
   * @return the Url and mapped response message keys
   */
  public Map<String, List<String>> getUrlMappedMessageKeys()
  {
    return mUrlMappedMessageKeys;
  }

  /**
   * @param aUrlMappedMessageKeys the Url and mapped response message keys
   */
  public void setUrlMappedMessageKeys(Map<String, List<String>> aUrlMappedMessageKeys)
  {
    mUrlMappedMessageKeys = aUrlMappedMessageKeys;
  }
}
