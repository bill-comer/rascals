package uk.co.utilisoft.parms.web.security;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Alison Verkroost
 * @version 1.0
 * @param <T> the <code>java.lang.Object</code> type
 * @param <PK> the <code>java.io.Serizable</code> type
 */
public abstract class ReadOnlyGenericDaoHibernate
<T extends Object, PK extends Serializable>
    extends HibernateDaoSupport implements ReadOnlyGenericDao<T, PK>
{
  private static final Logger LOGGER = Logger
      .getLogger(ReadOnlyGenericDaoHibernate.class);

  /**
   * the Class type.
   */
  protected Class<T> mClassType;

  /**
   * @param aClassType the Class Type
   */
  public ReadOnlyGenericDaoHibernate(Class<T> aClassType)
  {
    mClassType = aClassType;
  }

  /**
   * @see uk.co.formfill.sem.bdtool.data.GenericService
   *      #getById(java.io.Serializable) {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public T getById(PK aId)
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("getById(" + aId + ")");
    }

    try
    {
      return (T) getHibernateTemplate().get(mClassType, aId);
    }
    catch (HibernateException he)
    {
      LOGGER.error("Could not getById(" + aId + ")", he);
      throw convertHibernateAccessException(he);
    }
  }

  /**
   * @see uk.co.formfill.sem.synergen.bdtoolcommon.data.GenericService#getAll()
   *      {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<T> getAll()
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("getAll()");
    }

    try
    {
      List<T> result = getHibernateTemplate().loadAll(mClassType);
      return result;
    }
    catch (HibernateException he)
    {
      LOGGER.error("Could not getAll()", he);
      throw convertHibernateAccessException(he);
    }
  }
}
