package uk.co.utilisoft.parms.web.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class ResponseOutputStreamWriter
{
  private static final String PLAIN_CONTENT_TYPE = "text/plain";

  public void writeCsvFileToResponseOutputStream(HttpServletResponse aResponse,
      String data, String filename) throws IOException
  {
    String PLAIN_CONTENT_TYPE = "text/CSV";
    writeFileToResponseOutputStream(aResponse, data, filename, PLAIN_CONTENT_TYPE);
  }

  public void writePlainFileToResponseOutputStream(HttpServletResponse aResponse,
      String data, String filename) throws IOException
  {
    String PLAIN_CONTENT_TYPE = "text/plain";
    writeFileToResponseOutputStream(aResponse, data, filename, PLAIN_CONTENT_TYPE);
  }

  private void writeFileToResponseOutputStream(HttpServletResponse aResponse,
      String data, String filename, String contentType) throws IOException
  {
    aResponse.reset();
    aResponse.setContentType(StringUtils.isNotBlank(contentType) ? contentType : PLAIN_CONTENT_TYPE);

    aResponse.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    aResponse.setHeader("Content-length", "" + data.length());

    ServletOutputStream bos = null;
    try
    {
      bos = aResponse.getOutputStream();

      ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());

      int temp = 0;

      while ( (temp = is.read()) != -1)
      {
        bos.write(temp);
      }

      bos.flush();

    }
    finally
    {
      try
      {
        if (bos != null)

        {
            bos.close();
        }
      }
      catch (IOException e)
      {
      }
    }
  }
}
