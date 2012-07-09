package uk.co.utilisoft.parms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;

import static junit.framework.Assert.*;
import static uk.co.utilisoft.parms.domain.ConfigurationParameter.NAME.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class ConfigurationParameterDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.configurationParameterDao")
  private ConfigurationParameterDaoHibernate mConfigurationParameterDao;

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testConfigurationParameterNameIsNullConstraintsValidation()
  {
    ConfigurationParameter cp = new ConfigurationParameter(null, "c:/", "not defined");
    mConfigurationParameterDao.makePersistent(cp);
  }

  @Test
  public void getByNameFail()
  {
    assertNull(mConfigurationParameterDao.getByName(null));
  }

  @Test
  public void getByNameSuccess()
  {
    assertNotNull(mConfigurationParameterDao.getByName(PARMS_DPI_FILE_LOCATION));
  }

  @Test
  public void getDpiFileLocationSuccess()
  {
    String location = mConfigurationParameterDao.getDpiFileLocation();
    assertNotNull(location);
  }

  @Test
  public void getByIdFailure()
  {
    assertNull(mConfigurationParameterDao.getById(55555L));
  }

  /**
   * Bill. Test fails on oracle
   */
  @Test
  @ExpectedException(DataIntegrityViolationException.class)
  public void saveFailOnDuplicateNameExists()
  {
    ConfigurationParameter cp
      = new ConfigurationParameter(PARMS_DPI_FILE_LOCATION, "c:\\temp",
                                   "DPI File Location");
    mConfigurationParameterDao.makePersistent(cp);
  }

  /**
   * Bill. Test fails on oracle
   */
  @Test
  @ExpectedException(DataIntegrityViolationException.class)
  public void saveFailOnNullValue()
  {
    ConfigurationParameter cp
      = new ConfigurationParameter();
    cp.setName(PARMS_DPI_FILE_LOCATION);
    cp.setDescription("Test DPI File Location");
    cp.setValue("c:\\");
    mConfigurationParameterDao.makePersistent(cp);
  }

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void saveFailOnExceedMaxLengthOfValue()
  {
    ConfigurationParameter cp = new ConfigurationParameter(PARMS_DPI_FILE_LOCATION,
      "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "Test DPI File Location");
    mConfigurationParameterDao.makePersistent(cp);
  }

  @Test
  public void saveSuccess()
  {
    mConfigurationParameterDao.makeTransient(mConfigurationParameterDao.getByName(PARMS_DPI_FILE_LOCATION));
    mConfigurationParameterDao.getHibernateTemplate().flush();
    assertNull(mConfigurationParameterDao.getByName(PARMS_DPI_FILE_LOCATION));

    String locationValue = "c:\\zzzz";
    ConfigurationParameter cp = new ConfigurationParameter(PARMS_DPI_FILE_LOCATION,
                                                           locationValue, "Test DPI File Location 6");
    mConfigurationParameterDao.makePersistent(cp);
    mConfigurationParameterDao.getHibernateTemplate().flush();
    ConfigurationParameter newCp = mConfigurationParameterDao.getByName(PARMS_DPI_FILE_LOCATION);
    assertNotNull(newCp.getPk());
    assertEquals(locationValue, newCp.getValue());
  }

  @BeforeTransaction
  public void init()
  {
    List<ConfigurationParameter> cps = mConfigurationParameterDao.getAll();
    assertNotNull(cps);

    if (cps.isEmpty())
    {
      ConfigurationParameter cp
        = new ConfigurationParameter(PARMS_DPI_FILE_LOCATION, "c:\\temp", "DPI File location");
      List<ConfigurationParameter> configParams = new ArrayList<ConfigurationParameter>();
      configParams.add(cp);
      insertTestData(configParams, mConfigurationParameterDao, ConfigurationParameter.class);
    }
  }

  @AfterTransaction
  public void cleanUp()
  {
    if (getPersistedObjectPks().get(ConfigurationParameter.class) != null)
    {
      deleteTestData(mConfigurationParameterDao, ConfigurationParameter.class);
    }
  }
}
