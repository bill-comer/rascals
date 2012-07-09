package uk.co.utilisoft.genericutils.web.util;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Philip Lau
 * @version 1.0
 *
 * @param <ParmsCommandType> the subclass of ParmsCommand
 */
public interface ResponseMessageLoaderInterface
{
  /**
   * @param aRequest the HttpServletRequest
   * @param aResponseMessageKeys the response message keys
   */
  Map<String, String> appendResponseMessageData(HttpServletRequest aRequest,
                                                String ... aResponseMessageKeys);
}
