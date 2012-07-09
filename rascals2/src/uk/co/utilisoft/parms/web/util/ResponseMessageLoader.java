package uk.co.utilisoft.parms.web.util;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;


/**
 * @author Philip Lau
 * @version 1.0
 *
 * @param <ParmsCommandType> the subclass of ParmsCommand
 */
public class ResponseMessageLoader implements ResponseMessageLoaderInterface
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.util.ResponseMessageLoaderInterface#appendResponseMessageData(
   * javax.servlet.http.HttpServletRequest, java.lang.String[])
   */
  @Override
  public Map<String, String> appendResponseMessageData(HttpServletRequest aRequest, String ... aResponseMessageKeys)
  {
    Map<String, String> responseMessageData = new HashMap<String, String>();
    for (String key : aResponseMessageKeys)
    {
      Object value = aRequest.getSession().getAttribute(key);
      if (value != null)
      {
        String[] arguments = value.getClass().isArray() && value.getClass().getComponentType().equals(String.class)
          && ((String[]) value).length > 0 ? ((String[]) value) : new String[] {};

        responseMessageData.put(key, StringUtils.arrayToCommaDelimitedString(arguments));
        WebUtils.setSessionAttribute(aRequest, key, null); // remove attribute from session
      }
    }
    return responseMessageData;
  }
}
