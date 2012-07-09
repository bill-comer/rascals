package uk.co.utilisoft.parms.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Auto generate database scripts.
 *
 * @author Philip Lau
 * @version 1.0
 */
public class SchemaGenerator
{
  private Configuration mConfiguration;

  /**
   * @param aPackageNames the package names of annotated domain objects
   * @param aClassesDirPath the path location where java source files are compiled to
   * @param aWildCardClassNamesToIgnore the class names or wild cards the generator should ignore
   * @throws Exception
   */
  public SchemaGenerator(String[] aPackageNames, String aClassesDirPath,
                         String ... aWildCardClassNamesToIgnore) throws Exception
  {
    mConfiguration = new Configuration();
    mConfiguration.setProperty("hibernate.hbm2ddl.auto", "validate");

    for (String pkgName : aPackageNames)
    {
      mConfiguration.addPackage(pkgName);
      for (Class<?> clazz : getClasses(pkgName, aClassesDirPath))
      {
        if (!ignoreClass(clazz, aWildCardClassNamesToIgnore))
        {
          mConfiguration.addAnnotatedClass(clazz);
        }
      }
    }
  }

  /**
   * Include class if match found
   */
  private boolean ignoreClass(Class<?> aClazz, String ... aWildCardClassNamesToIgnore)
  {
    for (String wildCardClassName : aWildCardClassNamesToIgnore)
    {
      if (aClazz.getName().matches(wildCardClassName))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @param aDialect the database type
   * @param aPath the directory path
   */
  public void generate(Dialect aDialect, String aPath)
  {
    mConfiguration.setProperty("hibernate.dialect", aDialect.getDialectClass());
    SchemaExport export = new SchemaExport(mConfiguration);
    export.setDelimiter(";");
    export.setFormat(true);
    String fileName = aPath + File.separator + "ddl_" + aDialect.name().toLowerCase() + ".sql";
    export.setOutputFile(fileName);
    export.create(true, false);
  }

  private List<Class<?>> getClasses(String aPackageName, String aClassesDirPath) throws Exception
  {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    File directory = null;

    try
    {
      String path = aPackageName.replace('.', '/');

      String classesDirPath = StringUtils.isNotBlank(aClassesDirPath) ? aClassesDirPath : "";

      if (!classesDirPath.endsWith("./") || !classesDirPath.endsWith("/") || !classesDirPath.endsWith("\\"))
      {
        classesDirPath = classesDirPath + '/';
      }

      if (!path.startsWith("./") || !path.startsWith("/") || !path.startsWith("\\"))
      {
        path = '/' + path;
      }

      // parse and test uri is of valid syntax
      URI validUriPath = parseAsUri(classesDirPath + path);

//      URL resource = getClass().getResource(validUriPath.getPath());
      URL resource = new File(validUriPath.getPath()).toURI().toURL();

      if (resource == null)
      {
        throw new ClassNotFoundException("No resource for " + path);
      }

      directory = new File(resource.getFile());
    }
    catch (URISyntaxException use)
    {
      throw new IllegalArgumentException(use.getMessage());
    }
    catch (ClassNotFoundException cnfe)
    {
      throw cnfe;
    }
    catch (NullPointerException npe)
    {
      throw new ClassNotFoundException(aPackageName + " (" + directory + ") is not a valid package");
    }

    if (directory.exists())
    {
      String[] files = directory.list();

      for (int i = 0; i < files.length; i++)
      {
        if (files[i].endsWith(".class"))
        {
          // remove .class extension
          classes.add(Class.forName(aPackageName + '.' + files[i].substring(0, files[i].length() - 6)));
        }
      }
    }
    else
    {
      throw new ClassNotFoundException(aPackageName + " is not a valid package");
    }

    return classes;
  }

  private URI parseAsUri(String aPath) throws URISyntaxException
  {
    URI validPath = null;

    if (StringUtils.isNotBlank(aPath))
    {
      String path = aPath.replace("//", "/").replace("\\", "/");
      validPath = new URI(path);
    }

    return validPath;
  }

  public static enum Dialect
  {
    ORACLE("org.hibernate.dialect.Oracle10gDialect"),
    SQLSERVER("org.hibernate.dialect.SQLServerDialect"),
    MYSQL("org.hibernate.dialect.MySQLDialect");

    private String dialectClass;

    private Dialect(String dialectClass)
    {
      this.dialectClass = dialectClass;
    }

    public String getDialectClass()
    {
      return dialectClass;
    }
  }
}
