package uk.co.utilisoft.afms.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class ScrollableSearchIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Before
  @Override
  public void freezeTime(){}
  
  @Test
  public void testScrollable() throws Exception
  { 
    /*ParmsReportingMonth month = new ParmsReportingMonth(11, 2009);
    Supplier supplier = new Supplier();
    supplier.setSupplierId("OVOE");
    
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(month, supplier);
    
    System.out.println("found " + agentHistMap.size() + " agent histories");*/
    /*for (AgentHistorySearch key : agentHistMap.keySet())
    {
      System.out.println("key[" + key + "]");
    }*/
    
  }
}
