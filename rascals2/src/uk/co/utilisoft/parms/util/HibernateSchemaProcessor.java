package uk.co.utilisoft.parms.util;

import java.io.IOException;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;


/**
 * Process class domains using hibernate/jpa annotations and validate against an existing database.
 *
 * @author Philip Lau
 * @version 1.0
 */
public class HibernateSchemaProcessor
{
  private static final String RESOURCE_PATTERN = "**/*.class";
  private Configuration mConfiguration;
  private ResourcePatternResolver mResourcePatternResolver = new PathMatchingResourcePatternResolver();

  private TypeFilter[] mEntityTypeFilters = new TypeFilter[]
  {
    new AnnotationTypeFilter(Entity.class, false),
    new AnnotationTypeFilter(Embeddable.class, false),
    new AnnotationTypeFilter(MappedSuperclass.class, false)
  };

  /**
   * @param aHibernateConfigFileLocation the hibernate configuration file location
   * @param aPackages the packages to scan
   */
  public HibernateSchemaProcessor(String aHibernateConfigFileLocation, String ... aPackages)
  {
    mConfiguration = new Configuration();
    mConfiguration.configure(aHibernateConfigFileLocation);
    scanPackages(aPackages);
  }

  /**
   * Copied from
   * @see org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean
   */
  private void scanPackages(String ... aPackages)
  {
    if (aPackages != null)
    {
      try
      {
        for (String pkg : aPackages)
        {
          mConfiguration.addPackage(pkg);
          String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
          Resource[] resources = mResourcePatternResolver.getResources(pattern);
          MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(mResourcePatternResolver);
          for (Resource resource : resources)
          {
            if (resource.isReadable())
            {
              MetadataReader reader = readerFactory.getMetadataReader(resource);
              String className = reader.getClassMetadata().getClassName();
              if (matchesFilter(reader, readerFactory))
              {
                Class<?> annotatedClass = mResourcePatternResolver.getClassLoader().loadClass(className);
                mConfiguration.addAnnotatedClass(annotatedClass);
              }
            }
          }
        }
      }
      catch (IOException ex)
      {
        throw new MappingException("Failed to scan classpath for unlisted classes", ex);
      }
      catch (ClassNotFoundException ex)
      {
        throw new MappingException("Failed to load annotated classes from classpath", ex);
      }
    }
  }

  /**
   * Copied from
   * @see org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean
   */
  private boolean matchesFilter(MetadataReader aReader, MetadataReaderFactory aReaderFactory) throws IOException
  {
    if (mEntityTypeFilters != null)
    {
      for (TypeFilter filter : mEntityTypeFilters)
      {
        if (filter.match(aReader, aReaderFactory))
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Validate jpa, hibernate annotated domain objects against a hibernate auto generated schema script.
   *
   * @param aHibernateConfigFileLocation the hibernate configuration file class path location
   * @param aPackages the package locations to scan
   */
  public void validateSchema(String aHibernateConfigFileLocation, String ... aPackages)
  {
    mConfiguration.setProperty("hibernate.hbm2ddl.auto", "validate");
    mConfiguration.configure(aHibernateConfigFileLocation);
    scanPackages(aPackages);

    // triggers call to run Hibernate SchemaValidator
    mConfiguration.buildSessionFactory();
  }

  public static enum Dialect
  {
    ORACLE("org.hibernate.dialect.Oracle10gDialect"),
    SQLSERVER("org.hibernate.dialect.SQLServerDialect");

    private String mDialectClass;

    private Dialect(String aDialectClass)
    {
      this.mDialectClass = aDialectClass;
    }

    /**
     * @return the name of the dialect class
     */
    public String getDialectClass()
    {
      return mDialectClass;
    }
  }
}
