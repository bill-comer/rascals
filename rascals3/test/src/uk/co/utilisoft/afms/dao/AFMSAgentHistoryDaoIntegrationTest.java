package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.AgentRoleCodeType;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor= {RuntimeException.class})
@SuppressWarnings("unchecked")
public class AFMSAgentHistoryDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.afmsAgentHistoryDao")
  private AFMSAgentHistoryDaoHibernate mAFMSAgentHistoryDao;


  

  @Test
  public void saveSuccess() throws Throwable
  {
    AFMSAgentHistory agentHistory = new AFMSAgentHistory();
    agentHistory.setAgentEffectiveFromDate(new DateMidnight().toDateTime());
    agentHistory.setAgentEffectiveToDate(new DateMidnight().toDateTime());
    agentHistory.setAgentId("TEST");
    agentHistory.setAgentRoleCode(AgentRoleCodeType.C);
    agentHistory.setLastUpdated(new DateTime());
    agentHistory.setReasonForChange("TEST");
    Long mpanPk = (Long) getPersistedObjectPks().get((getPersistedObjectPks().keySet().iterator().next())).get(0);
    AFMSMpan mpan = (AFMSMpan) mAFMSAgentHistoryDao.getHibernateTemplate()
      .load(AFMSMpan.class, mpanPk);
    agentHistory.setMpan(mpan);
    mAFMSAgentHistoryDao.getHibernateTemplate().save(agentHistory);
    assertNotNull(agentHistory.getPk());
  }

  /**
   * Insert test data.
   */
  @BeforeTransaction
  public void init()
  {
    // create afms mpans test data
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(new DateMidnight().toDateTime());
    mpan.setMpanCore("0666666666666");
    mpan.setLastUpdated(new DateTime());
    List<AFMSMpan> mpans = new ArrayList<AFMSMpan>();
    mpans.add(mpan);
    insertTestData(mpans, mAFMSAgentHistoryDao, AFMSMpan.class);

    // create afms agent history test data
    List<AFMSAgentHistory> agentHistorys = new ArrayList<AFMSAgentHistory>();

    Long savedMpanPk = (Long) getPersistedObjectPks().get((getPersistedObjectPks().keySet().iterator().next())).get(0);
    assertNotNull(savedMpanPk);
    AFMSMpan savedMpan = (AFMSMpan) mAFMSAgentHistoryDao.getHibernateTemplate()
      .load(AFMSMpan.class, savedMpanPk);
    assertNotNull(savedMpan);

    // mo m role code agent history, but type OS so ignored
    AFMSAgentHistory agentHistory = new AFMSAgentHistory();
    agentHistory.setAgentEffectiveFromDate(new DateMidnight(2010, 1, 15).toDateTime());
    agentHistory.setAgentEffectiveToDate(new DateMidnight(2010, 1, 15).toDateTime());
    agentHistory.setAgentId("MOID");
    agentHistory.setAgentRoleCode(AgentRoleCodeType.M);
    agentHistory.setReasonForChange("OS");
    agentHistory.setLastUpdated(new DateTime());
    agentHistory.setMpan(savedMpan);
    agentHistorys.add(agentHistory);

    // dc c role code agent history but type OB  so ignored
    AFMSAgentHistory agentHistory0 = new AFMSAgentHistory();
    agentHistory0.setAgentEffectiveFromDate(new DateMidnight(2010, 2, 25).toDateTime());
    agentHistory0.setAgentEffectiveToDate(new DateMidnight(2010, 2, 25).toDateTime());
    agentHistory0.setAgentId("DCID");
    agentHistory0.setAgentRoleCode(AgentRoleCodeType.C);
    agentHistory0.setReasonForChange("OB");
    agentHistory0.setLastUpdated(new DateTime());
    agentHistory0.setMpan(savedMpan);
    agentHistorys.add(agentHistory0);

    // dc d role code agent history
    AFMSAgentHistory agentHistory1 = new AFMSAgentHistory();
    agentHistory1.setAgentEffectiveFromDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory1.setAgentEffectiveToDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory1.setAgentId("MOI1");
    agentHistory1.setAgentRoleCode(AgentRoleCodeType.getMOPRoleCodeType());
    agentHistory1.setReasonForChange("OO");
    agentHistory1.setLastUpdated(new DateTime());
    agentHistory1.setMpan(savedMpan);
    agentHistorys.add(agentHistory1);
    

    // dc d role code agent history
    AFMSAgentHistory agentHistory2 = new AFMSAgentHistory();
    agentHistory2.setAgentEffectiveFromDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory2.setAgentEffectiveToDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory2.setAgentId("DCI2");
    agentHistory2.setAgentRoleCode(AgentRoleCodeType.getHalfHourlyDCRoleCodeType());
    agentHistory2.setReasonForChange("OO");
    agentHistory2.setLastUpdated(new DateTime());
    agentHistory2.setMpan(savedMpan);
    agentHistorys.add(agentHistory2);

    // dc d role code agent history
    AFMSAgentHistory agentHistory3 = new AFMSAgentHistory();
    agentHistory3.setAgentEffectiveFromDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory3.setAgentEffectiveToDate(new DateMidnight(2010, 3, 29).toDateTime());
    agentHistory3.setAgentId("DCI3");
    agentHistory3.setAgentRoleCode(AgentRoleCodeType.getNonHalfHourlyDCRoleCodeType());
    agentHistory3.setReasonForChange("OO");
    agentHistory3.setLastUpdated(new DateTime());
    agentHistory3.setMpan(savedMpan);
    agentHistorys.add(agentHistory3);

    insertTestData(agentHistorys, mAFMSAgentHistoryDao, AFMSAgentHistory.class);
  }

  /**
   * Clean up test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mAFMSAgentHistoryDao, AFMSAgentHistory.class);
    deleteTestData(mAFMSAgentHistoryDao, AFMSMpan.class);
  }
}
