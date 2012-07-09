package uk.co.utilisoft.parms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class MPANTest
{
  @Test
  public void testValidMpan()
  {
    MPANCore mpan = new MPANCore("1111111111111");
    assertTrue(mpan.isValid());
  }

  @Test
  public void testInValidMpan()
  {
    MPANCore mpan = new MPANCore("WOMBAT");
    assertFalse(mpan.isValid());
  }

  @Test
  public void testNullMpan()
  {
    MPANCore mpan = new MPANCore(null);

    try
    {
      mpan.assertValue();
    }
    catch (RuntimeException rte)
    {
      assertTrue(rte instanceof IllegalArgumentException);
      assertTrue(rte.getMessage().trim().equals("MPAN must be a non null string"));
    }
  }

  @Test
  public void testEmptyMpan()
  {
    MPANCore mpan = new MPANCore("");

    try
    {
      mpan.assertValue();
    }
    catch (RuntimeException rte)
    {
      assertTrue(rte instanceof IllegalArgumentException);
      assertTrue(rte.getMessage().trim().equals("MPAN must be a non null string"));
    }
  }

  @Test
  public void testIllegalLengthMpanValue()
  {
    MPANCore mpan = new MPANCore("123");

    try
    {
      mpan.assertValue();
    }
    catch (RuntimeException rte)
    {
      assertTrue(rte instanceof IllegalArgumentException);
      assertTrue(rte.getMessage().trim().equals("MPAN must be of length " + 13 + "," + mpan.getValue() + " is "
        + mpan.getValue().length() + " chars."));
    }
  }

  @Test
  public void testMpanValueContainsOnlyDigits()
  {
    MPANCore mpan = new MPANCore("111111111111W");

    try
    {
      mpan.assertValue();
    }
    catch (RuntimeException rte)
    {
      assertTrue(rte instanceof IllegalArgumentException);
      assertTrue(rte.getMessage().trim().equals("MPAN must contain only digits"));
    }
  }

  @Test
  public void testGoodMpan() throws Exception
  {
    try
    {
      MPANCore mpan = new MPANCore("1234567890123");
    }
    catch (Exception e)
    {
      fail("no exception should have been thrown");
    }
  }

  @Test
  public void Equals_notEqual() throws Exception
  {
    MPANCore mpan1 = new MPANCore("1234567890123");
    MPANCore mpan2 = new MPANCore("1234567890124");

    assertFalse(mpan1.equals(mpan2));
  }


  @Test
  public void Equals_Equal() throws Exception
  {
    MPANCore mpan1 = new MPANCore("1234567890123");
    MPANCore mpan2 = new MPANCore("1234567890123");

    assertTrue(mpan1.equals(mpan2));
  }

  @Test
  public void serilizing_deserializing() throws Exception
  {
    MPANCore mpan1 = new MPANCore("1234567890123");
    FileOutputStream fileOut = new FileOutputStream("mpan_test1");
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(mpan1);
    out.close();
    fileOut.close();

    FileInputStream fileIn = new FileInputStream("mpan_test1");
    ObjectInputStream in = new ObjectInputStream(fileIn);
    MPANCore mpan1Desrialized = (MPANCore) in.readObject();
    in.close();
    fileIn.close();

    assertEquals(mpan1.getValue(), mpan1Desrialized.getValue());

    File file = new File("mpan_test1");
    assertTrue(file.exists());
    file.delete();
  }
}
