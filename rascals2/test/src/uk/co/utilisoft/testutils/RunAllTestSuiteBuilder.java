package uk.co.utilisoft.testutils;

import junit.framework.Assert;
import junit.framework.Test;
import junitx.util.SimpleTestFilter;

/**
 * A TestSuite Builder to suppoert Classes which extend junit.framework.TestCase, and Classes annotated with org.junit.Test.
 *
 * @author Philip Lau
 * @version 1.0
 */
public class RunAllTestSuiteBuilder
{
  CustomDirectorySuiteBuilder mBuilder;

  /**
   * @param aClassNameSuffixes the Class name suffixes used to filter for Junit Test Classes
   */
  public RunAllTestSuiteBuilder(String ... aClassNameSuffixes)
  {
    SimpleTestFilter filter = new CustomSimpleTestFilter(aClassNameSuffixes);
    Assert.assertNotNull("An instance of a TestFilter is required to filter for Junit Test Classes", filter);
    mBuilder = new CustomDirectorySuiteBuilder(filter);
    Assert.assertNotNull("An instance of a DirectorySuiteBuilder is required to filter for Junit Test Classes"
                         , mBuilder);
  }

  /**
   * @param aRootDirectoryToSearch the Root File Directory to start search
   * @return the junit.framework.Test
   * @throws Exception
   */
  protected Test suite(String aRootDirectoryToSearch) throws Exception
  {
    Assert.assertNotNull("A directory path is required", aRootDirectoryToSearch);
    if (aRootDirectoryToSearch.length() < 1)
    {
      // set default root directory
      aRootDirectoryToSearch = "\\";
    }
    return mBuilder.suite(aRootDirectoryToSearch);
  }

  private class CustomSimpleTestFilter extends SimpleTestFilter
  {
    private String[] mClassNameSuffixes;

    public CustomSimpleTestFilter(String ... aClassNameSuffixes)
    {
      Assert.assertNotNull("At least one Class Name Suffix is required", aClassNameSuffixes);
      Assert.assertTrue(getClass().getSimpleName() + ": The TestFilter needs at least one Class End Name Suffix"
                        + " for identifying Junit Test Classes", aClassNameSuffixes.length > 0);
      mClassNameSuffixes = aClassNameSuffixes;
    }

    /**
     * {@inheritDoc}
     * @see junitx.util.SimpleTestFilter#include(java.lang.String)
     */
    @Override
    public boolean include(String aCanonicalClassName)
    {
      boolean result = false;
      for (String clsNameSuffix : mClassNameSuffixes)
      {
        result = getClassName(aCanonicalClassName).endsWith(clsNameSuffix);
        if (result)
        {
          return result;
        }
      }
      return result;
    }
  }
}
