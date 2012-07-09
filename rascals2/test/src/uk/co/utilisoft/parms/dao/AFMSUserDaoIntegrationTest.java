package uk.co.utilisoft.parms.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.dao.UserDaoHibernate;
import uk.co.utilisoft.afms.domain.User;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
public class AFMSUserDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.userDao")
  private UserDaoHibernate mUserDao;
  
  @Test
  public void getEmptyUsers()
  {
    List<User> users = mUserDao.getAllUsers();
    assertNotNull(users);
    assertTrue(users.isEmpty());
  }
}
