package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.configurationParameterDao")
public class ConfigurationParameterDaoHibernate extends ParmsGenericDao<ConfigurationParameter, Long> implements ConfigurationParameterDao
{
  /**
   * @see uk.co.utilisoft.parms.dao.ConfigurationParameterDao#getByName(uk.co.utilisoft.parms.ConfigurationParameterName)
   */
  @Override
  public ConfigurationParameter getByName(final ConfigurationParameter.NAME aName)
  {
    return (ConfigurationParameter) getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(ConfigurationParameter.class)
          .add(Restrictions.eq("name", aName)).uniqueResult();
      }
    });
  }

  /**
   * @return the configured DPI File Location
   */
  public String getDpiFileLocation()
  {
    ConfigurationParameter dpiFileLoc = getByName(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION);
    return dpiFileLoc != null ? dpiFileLoc.getValue() : null;
  }
  

  /**
   * @return the configured DPI File Location
   */
  public String getP0028UploadErrorLocation()
  {
    ConfigurationParameter fileLoc = getByName(ConfigurationParameter.NAME.P0028_UPLOAD_ERROR_FILE_LOCATION);
    return fileLoc != null ? fileLoc.getValue() : null;
  }
}
