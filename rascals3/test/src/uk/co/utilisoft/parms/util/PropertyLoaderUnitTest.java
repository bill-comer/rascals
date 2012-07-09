package uk.co.utilisoft.parms.util;

import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class PropertyLoaderUnitTest
{
  /**
   * Load properties from file.
   *
   * @throws Exception
   */
  @Test
  public void successfullyLoadProperties() throws Exception
  {
    Properties props = PropertyLoader.loadProperties("test/file/testbuild.properties");
    assertNotNull(props);
  }
}
