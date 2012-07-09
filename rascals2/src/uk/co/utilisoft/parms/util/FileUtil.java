package uk.co.utilisoft.parms.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Philip Lau
 * @version 1.0
 */
public final class FileUtil
{
  private FileUtil()
  { }

  /**
   * @param aInputStream the input stream to read
   * @return the input stream as a byte array
   * @throws IOException
   */
  public static byte[] getBytes(InputStream aInputStream) throws IOException
  {
    if (aInputStream.markSupported())
    {
      aInputStream.reset();
    }

    InputStream is = new BufferedInputStream(aInputStream);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int nextLine = is.read();
    while (nextLine > -1)
    {
      baos.write(nextLine);
      nextLine = is.read();
    }
    baos.flush();
    return baos.toByteArray();
  }

  /**
   * @param aFile the file
   * @return the file as a byte array
   * @throws IOException
   */
  public static byte[] getBytesFromFile(File aFile) throws IOException
  {
    InputStream is = new FileInputStream(aFile);
    long length = aFile.length();

    if (length > Integer.MAX_VALUE)
    {
      throw new IOException("Unable to read file " + aFile.getName() + " of length " + length);
    }

    byte[] bytes = new byte[(int)length];

    int offset = 0;
    int numRead = 0;

    while (offset < bytes.length
           && (numRead = is.read(bytes, offset, bytes.length-offset)) >= 0)
    {
      offset += numRead;
    }

    if (offset < bytes.length)
    {
      throw new IOException("Unable to finish reading file " + aFile.getName());
    }

    is.close();
    return bytes;
  }
}
