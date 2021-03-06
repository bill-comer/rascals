package uk.co.utilisoft.parmsmop.dao;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.parmsmop.domain.MOPDomainObject;


public abstract class MOPGenericDao<T extends MOPDomainObject,ID extends Serializable> 
    extends UtilisoftGenericDaoHibernate<T, ID>
{
  @Autowired(required=true)
  @Qualifier("hibernateTemplate")
  private HibernateTemplate hibernateTemplate;
  
  
  public MOPGenericDao()
  {
    super();
    setHibernateTemplate(hibernateTemplate);
  }
  

  /**
   * Persist an Entity to the database, and set LastUpdated
   * @param aEntity the entity to save
   * @return the saved entity Identity
   */
  public T makePersistent(final T aEntity)
  {
    aEntity.setLastUpdated(new DateTime());
    return super.makePersistent(aEntity);
  }
}
