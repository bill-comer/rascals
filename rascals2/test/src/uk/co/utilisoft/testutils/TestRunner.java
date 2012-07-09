package uk.co.utilisoft.testutils;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.util.DirectorySuiteBuilder;
import junitx.util.TestFilter;


public class TestRunner
{

  public static final String TESTS = System.getProperty("tests", "*/*Test*");
  public static final boolean DEBUG = (System.getProperty("debug") != null);

  public static Test suite(String packageMatch, String classMatch, String directory) throws Exception {

    File searchPath = new File(directory);
    
/*    DirectorySuiteBuilder builder = new DirectorySuiteBuilder();
    SimpleTestFilter filter = new SimpleTestFilter();
    //builder.setSuffix("Test");
    builder.setFilter(filter);
    Test suite = builder.suite(searchPath.getCanonicalPath());*/
    
      DirectorySuiteBuilder builder = new DirectorySuiteBuilder();
      builder.setFilter(new RegexTestFilter(directory, packageMatch, classMatch));

      TestSuite suite = new TestSuite(packageMatch + "." + classMatch);
      
      walk(directory);
      suite.addTest(builder.suite(searchPath));


    System.out.println("Found " + suite.countTestCases() + " tests matching " + packageMatch + "/" + classMatch + " in " + searchPath.getCanonicalPath());
      return suite;
  }
  
  public static void walk( String path ) {

    File root = new File( path );
    File[] list = root.listFiles();

    for ( File f : list ) {
        if ( f.isDirectory() ) {
            walk( f.getAbsolutePath() );
            System.err.println( "Dir:" + f.getAbsoluteFile() );
        }
        else {
            System.err.println( "File:" + f.getAbsoluteFile() );
        }
    }
}

  public static Test suite(String tests) throws Exception {
      String[] tokens = tests.split("/");
      return TestRunner.suite(tokens[0], tokens[1], tokens[2]);
  }

  public static Test suite() throws Exception {
      return TestRunner.suite(TESTS);
  }

  public static void main(String[] args) throws Exception {
      junit.textui.TestRunner.run(suite(args[0]));
  }

  private static class RegexTestFilter  extends PlatformAgnosticSimpleTestFilter implements TestFilter{
      String packageMatch = "";
      String classMatch = "";

      public RegexTestFilter(String directory, String packageMatch, String classMatch) {
          this.packageMatch = directory + "\\." + packageMatch + "(\\.[^\\.]*)*";
          this.classMatch = classMatch.replaceAll("\\*", ".*");

          if (DEBUG) {
              System.out.println("directory   : " + directory);
              System.out.println("packageMatch: " + packageMatch + " -> " + this.packageMatch);
              System.out.println("classMatch  : " + classMatch + " -> " + this.classMatch);
          }
      }

      public boolean include(String path) {
          String packageName = getPackageName(path);
          String className = getClassName(path);
          boolean packageMatched = packageName.matches(packageMatch);
          boolean classMatched = className.matches(classMatch);

          if (DEBUG)
              System.out.println(packageName + " -> " + packageMatched + "\t" + className + " -> " + classMatched);

          return packageMatched && classMatched;
      }

      @Override
      public boolean include(Class arg0)
      {
        throw new UnsupportedOperationException("Not implemented yet Bill");
      }
  }
}
