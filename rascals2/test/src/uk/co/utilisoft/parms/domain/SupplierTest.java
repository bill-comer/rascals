package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import org.junit.Test;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class SupplierTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      new Supplier().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void testEquals() throws Exception
  {
    Supplier s1 = new Supplier("s1");

    Supplier s2 = new Supplier("s1");

    assertTrue(s1.equals(s2));
  }


  @Test
  public void testNotEquals() throws Exception
  {
    Supplier s1 = new Supplier("s1");

    Supplier s2 = new Supplier("s2");

    assertFalse(s1.equals(s2));
  }
}