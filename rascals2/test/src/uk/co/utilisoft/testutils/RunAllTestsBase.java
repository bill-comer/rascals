package uk.co.utilisoft.testutils;

import junit.framework.Test;

/**
 * Class to run all JunitX Tests using a Custom TestFilter to locate
 * Test classes with the specified class name suffixes e.g. *UnitTest.class ...
 *
 * Notes: Create a test class and extend org.junit.runners.AllTests.class or use org.junit.runner.RunWith(org.junit.runners.AllTests.class) annotation
 *
 * @author Philip Lau
 * @version 1.0
 */
public final class RunAllTestsBase
{
  private static String mFileDirectoryPath;
  private static String[] mClassNameSuffixes;

  public static Test runAllTests(String aFileDirectoryPath, String ... aClassNameSuffixes) throws Exception
  {
    mFileDirectoryPath = aFileDirectoryPath;
    mClassNameSuffixes = aClassNameSuffixes;
    RunAllTestSuiteBuilder runAll = new RunAllTestSuiteBuilder(mClassNameSuffixes);
    String directory = mFileDirectoryPath;
    Test tests = runAll.suite(directory);
    return tests;
  }
}
