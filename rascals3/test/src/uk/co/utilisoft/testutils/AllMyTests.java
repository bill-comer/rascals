package uk.co.utilisoft.testutils;

import org.junit.runners.AllTests;

import junit.framework.Test;

public class AllMyTests extends AllTests
{
  public AllMyTests() throws Throwable
  {
    super(AllMyTests.class);
  }

  public static Test suite() throws Exception
  {
    return RunAllTestsBase.runAllTests("test/classes", "Test", "IntegrationTest", "UnitTest");
  }

}
