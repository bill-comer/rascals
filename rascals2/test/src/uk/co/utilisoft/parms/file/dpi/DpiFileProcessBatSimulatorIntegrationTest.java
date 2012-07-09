package uk.co.utilisoft.parms.file.dpi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;

@TransactionConfiguration(defaultRollback=false, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class DpiFileProcessBatSimulatorIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.dpiFileProcess")
  private DpiFileProcess mBuilder;

/**
 * DO NOT PUT OTHER TESTS IN HERE
 */
  
  @Test
  public void test_BatSimulator_RunBuilder() throws Exception
  {
    assertNotNull("dpiFileBuilder is null", mBuilder);

    //needs to be commented out as this is not really a test
    //it is really generating data
    
    DateTime start = new DateTime();
    
    mBuilder.buildFiles(8, 2010);
    
    DateTime end = new DateTime();
    
    System.out.println("elapsed time = " + new Long((end.getMillis() - start.getMillis())));

    assertTrue(true);

  }
  
  @Override
  public void freezeTime()
  {
    System.out.println("do not freeze time");
    //Freeze.freeze(new DateTime());
  }
}
