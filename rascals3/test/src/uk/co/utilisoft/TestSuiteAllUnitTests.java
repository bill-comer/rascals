package uk.co.utilisoft;

import org.junit.runner.RunWith;

import uk.co.utilisoft.testutils.TestRunner;
import junit.framework.Test;


@RunWith(org.junit.runners.AllTests.class )
public class TestSuiteAllUnitTests
{
  public static Test suite() throws Exception 
  {
    return TestRunner.suite("*", "*Test", "WebRoot\\WEB-INF\\classes");
  }
}
