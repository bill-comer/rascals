package uk.co.utilisoft.testutils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junitx.util.DirectorySuiteBuilder;
import junitx.util.TestFilter;

/**
 * Supports Tests written using junit annotations.
 *
 * @author Philip Lau
 * @version 1.0
 */
public class CustomDirectorySuiteBuilder extends DirectorySuiteBuilder
{
  private TestFilter filter;

  public CustomDirectorySuiteBuilder(TestFilter aTestFilter)
  {
    filter = aTestFilter;
    Assert.assertNotNull("Error an instance of TestFilter class is required.", filter);
    super.setFilter(filter);
  }

  /**
   * (Copied and modified from original)
   * Supports check and include classes in directory that extend junit.framework.TestCase,
   * and classes which use org.junit.Test annotation.
   *
   * @see junitx.util.DirectorySuiteBuilder#suite(java.io.File)
   */
  @Override
  @SuppressWarnings("unchecked")
  public Test suite(File directory) throws Exception
  {
    // check and include classes in directory that extend junit.framework.TestCase
    // check and include classes in directory that use org.junit.Test annotation
    TestSuite suite = new TestSuite(directory.getName());
    List classnames = browse(directory);
    merge(classnames, suite);
    return suite;
  }

  /**
   * (Copied and modified from original)
   *
   * @see junitx.util.DirectorySuiteBuilder#suite(java.lang.String)
   */
  @Override
  public Test suite(String directoryName) throws Exception
  {
    File dir = new File(directoryName);
    return suite(dir);
  }

  /**
   * (Copied and modified from junitx.util.AbstractSuiteBuilder)
   *
   * {@inheritDoc}
   * @see junitx.util.AbstractSuiteBuilder#merge(java.util.List, junit.framework.TestSuite)
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void merge(List classenames, TestSuite suite) throws ClassNotFoundException, IllegalAccessException,
      InvocationTargetException
  {
    for (int i = 0; i < classenames.size(); i++)
    {
      String classname = (String) classenames.get(i);
      Class cls = Class.forName(classname);

      // check that the class can be included in the test suite
      if ((junit.framework.TestCase.class.isAssignableFrom(cls)) && this.filter.include(cls))
      {
        /* Because a 'suite' method doesn't always exist in a TestCase,
         * we need to use the try/catch so that tests can also be
         * automatically extracted */

        try
        {
          Method suiteMethod = cls.getMethod("suite", new Class[0]);
          if (!Modifier.isPublic(suiteMethod.getModifiers()))
          {
            suite.addTest(warning("Method 'suite' should be public (class " + cls.getName() + ")"));
          }
          else if (!Modifier.isStatic(suiteMethod.getModifiers()))
          {
            suite.addTest(warning("Method 'suite' should be static (class " + cls.getName() + ")"));
          }
          else if (!Test.class.isAssignableFrom(suiteMethod.getReturnType()))
          {
            suite.addTest(warning("Method 'suite' should have a Test return type (class " + cls.getName() + ")"));
          }
          else if (suiteMethod.getParameterTypes().length != 0)
          {
            suite.addTest(warning("Method 'suite' should have no arguments (class " + cls.getName() + ")"));
          }
          else
          {
            Test test = (Test) suiteMethod.invoke(null, (Object) new Class[0]);
            suite.addTest(test);
          }
        }
        catch (NoSuchMethodException e)
        {
          suite.addTest(new TestSuite(cls));
        }
      }
      else if (isJunitClass(cls) && this.filter.include(cls))
      {
        // adapt junit.framework.Test to handle Junit 4 annotated test classes
        suite.addTest(new JUnit4TestAdapter(cls));
      }
    }
  }

  /**
   * (Copied from junitx.util.AbstractSuiteBuilder)
   *
   * Returns a test which will fail and log a warning message.
   */
  private static Test warning(final String message) {
      return new TestCase("warning") {
          protected void runTest() {
              fail(message);
          }
      };
  }

  /**
   *  Determine if a Class contains any methods annotated with org.junit.Test.
   *
   * @param aCls the Class
   * @return true if a Class which does not extend junit.framework.TestCase; otherwise, false
   */
  protected boolean isJunitClass(Class<?> aCls)
  {
    boolean isJunitClass = false;
    if (aCls != null)
    {
      Method[] methods = aCls.getDeclaredMethods();
      for (Method method : methods)
      {
        Annotation[] ants = method.getDeclaredAnnotations();
        for (Annotation ant : ants)
        {
          isJunitClass = (ant != null && ant.annotationType().equals(org.junit.Test.class)) ? true : false;
          if (isJunitClass)
          {
            return isJunitClass;
          }
        }
      }
    }
    else
    {
      return false;
    }
    return false;
  }
}
