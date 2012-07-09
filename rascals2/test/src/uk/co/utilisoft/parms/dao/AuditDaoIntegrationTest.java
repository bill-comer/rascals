package uk.co.utilisoft.parms.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.domain.Audit;

import static org.junit.Assert.*;
import static uk.co.utilisoft.parms.domain.Audit.TYPE;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
public class AuditDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.auditDao")
  private AuditDaoHibernate mAuditDao;
  
  @Test
  public void saveAnAudit()
  {
    assertNotNull(mAuditDao);
    Audit audit = new Audit(Audit.TYPE.ADMIN_PARAM_CHANGE);
    audit.setDetails(TYPE.ADMIN_PARAM_CHANGE.name() + ": test audit message...");
    audit.setUserFk(Long.valueOf(1L));
    Long pk = mAuditDao.makePersistent(audit).getPk();
    assertNotNull(pk);
  }
}
