package uk.co.utilisoft.parms;

import org.hibernate.HibernateException;
import org.junit.Test;

import uk.co.utilisoft.parms.util.HibernateSchemaProcessor;

import static junit.framework.Assert.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class SchemaValidatorIntegrationTest
{
  /**
   * Validate generated script against a running oracle test database instance.
   *
   * TODO currently disabled for oracle databases
   */
  @Test
  public void validateDatabaseSchemaAgainstOracleDbTestInstanceForParmsDomains()
  {
    HibernateSchemaProcessor processor
      = new HibernateSchemaProcessor("config/test-oracle-hibernate.cfg.xml", "uk.co.utilisoft.parms.domain");

    try
    {
      // only validate parms domain objects
      processor.validateSchema("config/test-oracle-hibernate.cfg.xml", "uk.co.utilisoft.parms.domain");
    }
    catch (Exception e)
    {
      assertTrue(e instanceof HibernateException);

      // Note: ignore assertion error junit.framework.AssertionFailedError:Wrong column type Found: number, expected: float for PARMS_SP04_DATA table
      // What this error means is that the dba has chosen to use for example a number(19,2) rather than a float type, so that
      // the database will truncate saved float values to the database by two decimal places.
      String expErrMsgSuffix = "PARMS_SP04_DATA for column STANDARD_3. Found: number, expected: float";

      if (!e.getMessage().endsWith(expErrMsgSuffix))
      {
        fail(((HibernateException) e).getMessage());
      }
    }
  }

   /**
    * Validate generated script against a running sqlserver test database instance.
    */
   @Test
   public void validateDatabaseSchemaAgainstSqlServerDbTestInstanceForParmsDomains()
   {
     HibernateSchemaProcessor processor
       = new HibernateSchemaProcessor("config/test-sqlserver-hibernate.cfg.xml", "uk.co.utilisoft.parms.domain");

     try
     {
       // only validate parms domain objects
       processor.validateSchema("config/test-sqlserver-hibernate.cfg.xml", "uk.co.utilisoft.parms.domain");
     }
     catch (Exception e)
     {
       assertTrue(e instanceof HibernateException);
       fail(((HibernateException) e).getMessage());
     }
   }

}
