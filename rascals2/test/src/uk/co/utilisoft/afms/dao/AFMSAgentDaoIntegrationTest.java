package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.ParmsReportingPeriod;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class AFMSAgentDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.afmsAgentDao")
  private AFMSAgentDaoHibernate mAgentDao;

  private Long mExistsMpanPk;


  @Test
  public void get_getDataCollectorAgents_DCAgentSuccess()
  {
    DateTime expectedDCEFD = new DateMidnight(2010, 8, 1).toDateTime();
    String expectedDCAgentId = "DCID";
    String expectedMpanCore = "0444444444444";
    boolean isMonthT = false;

    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(new DateMidnight(2010, 8, 29));

    //test method
    MultiHashMap<String, AFMSAgent> agents = mAgentDao.getDataCollectorAgents(parmsReportingPeriod, isMonthT);

    assertNotNull("should have returned a dc agent map", agents);

    Collection<AFMSAgent> dcAgentsForMpan = agents.get(expectedMpanCore);
    assertNotNull(dcAgentsForMpan);
    assertEquals(1, dcAgentsForMpan.size());

    for (AFMSAgent afmsAgent : dcAgentsForMpan)
    {
      assertEquals(expectedMpanCore, afmsAgent.getMpan().getMpanCore());
      assertEquals(expectedDCAgentId, afmsAgent.getDataCollector());
      assertEquals(expectedDCEFD, afmsAgent.getDCEffectiveFromDate());
    }

  }


  @Test
  public void saveSuccess()
  {
    AFMSAgent agent = new AFMSAgent();
    agent.setDataCollector("DICK");
    agent.setDCEffectiveFromDate(new DateMidnight().toDateTime());
    agent.setLastUpdated(new DateTime());
    agent.setMeterOperator("MIRA");
    agent.setMOEffectiveFromDate(new DateMidnight().toDateTime());
    Long mpanPk = (Long) getPersistedObjectPks().get((getPersistedObjectPks().keySet().iterator().next())).get(0);
    AFMSMpan mpan = (AFMSMpan) mAgentDao.getHibernateTemplate()
      .load(AFMSMpan.class, mpanPk);
    agent.setMpan(mpan);
    Long agentPk = (Long) mAgentDao.getHibernateTemplate().save(agent);
    assertNotNull(agentPk);
  }

  @BeforeTransaction
  public void init()
  {
    // create test data
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(new DateMidnight().toDateTime());
    mpan.setMpanCore("0444444444444");
    mpan.setLastUpdated(new DateTime());
    List<AFMSMpan> mpans = new ArrayList<AFMSMpan>();
    mpans.add(mpan);
    insertTestData(mpans, mAgentDao, AFMSMpan.class);
    mExistsMpanPk = mpan.getPk();

    AFMSAgent agent = new AFMSAgent();
    agent.setDataCollector("DCID");
    agent.setDCEffectiveFromDate(new DateMidnight(2010, 8, 1).toDateTime());
    agent.setLastUpdated(new DateTime());
    agent.setMeterOperator("MOID");
    agent.setMOEffectiveFromDate(new DateMidnight(2010, 8, 15).toDateTime());

    //Long existsMpanPk = (Long) getPersistedObjectPks().get((getPersistedObjectPks().keySet().iterator().next())).get(0);
    assertNotNull(mExistsMpanPk);
    AFMSMpan existsMpan = (AFMSMpan) mAgentDao.getHibernateTemplate()
      .load(AFMSMpan.class, mExistsMpanPk);
    assertNotNull(existsMpan);
    agent.setMpan(existsMpan);
    List<AFMSAgent> agents = new ArrayList<AFMSAgent>();
    agents.add(agent);
    insertTestData(agents, mAgentDao, AFMSAgent.class);
  }

  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mAgentDao, AFMSAgent.class);
    deleteTestData(mAgentDao, AFMSMpan.class);
  }
}
