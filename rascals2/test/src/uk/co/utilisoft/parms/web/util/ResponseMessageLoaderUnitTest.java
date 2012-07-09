package uk.co.utilisoft.parms.web.util;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;
import static uk.co.utilisoft.parms.web.controller.WebConstants.P0028_FILE_UPLOADED_SUCCESS;
import static uk.co.utilisoft.parms.web.controller.WebConstants.PARMS_RESPONSE_MESSAGE_DATA;


/**
 * @author Philip Lau
 * @version 1.0
 */
public class ResponseMessageLoaderUnitTest
{
  private ResponseMessageLoader mLoader = new ResponseMessageLoader();

  /**
   * AppendResponseMessageData if P0028 file uploaded success request attribute not found.
   */
  @Test
  public void appendResponseMessageDataRequestAttributeNotFound()
  {
    assertNotNull(mLoader);
    HttpServletRequest request = new MockHttpServletRequest();
    Map<String, String> responses = mLoader.appendResponseMessageData(request, P0028_FILE_UPLOADED_SUCCESS);
    assertNotNull(responses);
    assertTrue(responses.isEmpty());
    assertNull(request.getSession().getAttribute(P0028_FILE_UPLOADED_SUCCESS));
  }

  /**
   * AppendResponseMesageData if P0028 file uploaded success request attribute found
   * of type comma delimited text.
   */
  @Test
  public void appendResponseMessageDataRequestAttributeFoundWithValueTypeStringArray()
  {
    assertNotNull(mLoader);
    HttpServletRequest request = new MockHttpServletRequest();
    String fileName = "testP0028File001.csv";
    request.getSession(true).setAttribute(P0028_FILE_UPLOADED_SUCCESS, new String[] {fileName});
    Map<String, String> responses = mLoader.appendResponseMessageData(request, P0028_FILE_UPLOADED_SUCCESS);
    assertEquals(responses.values().iterator().next(), fileName);
    assertNull(request.getAttribute(PARMS_RESPONSE_MESSAGE_DATA));
  }

  /**
   * AppendResponseMesageData if P0028 file uploaded success request attribute found
   * of wrong array type.
   */
  @Test
  public void appendResponseMessageDataRequestAttributeFoundWithValueOfWrongArrayType()
  {
    assertNotNull(mLoader);
    HttpServletRequest request = new MockHttpServletRequest();
    Integer[] arguments = new Integer[] {new Integer(5)};
    request.setAttribute(P0028_FILE_UPLOADED_SUCCESS, arguments);
    Map<String, String> responses = mLoader.appendResponseMessageData(request, P0028_FILE_UPLOADED_SUCCESS);
    assertNotNull(responses);
    assertTrue(responses.isEmpty());
    assertNull(request.getSession().getAttribute(P0028_FILE_UPLOADED_SUCCESS));
  }
}
