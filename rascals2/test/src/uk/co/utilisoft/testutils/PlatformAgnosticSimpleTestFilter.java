package uk.co.utilisoft.testutils;

import java.io.File;

public class PlatformAgnosticSimpleTestFilter 
{
  public static String getClassName(String classpath)
  {
    int pos = classpath.lastIndexOf(File.separator) + 1;
    return classpath.substring(pos, classpath.length() - 6);
  }

  public static String getPackageName(String classpath)
  {
    int pos = classpath.lastIndexOf(File.separator);
    if (pos > 0)
    {
      return classpath.substring(0, pos).replace(File.separatorChar, '.');
    }
    return "";
  }
}
