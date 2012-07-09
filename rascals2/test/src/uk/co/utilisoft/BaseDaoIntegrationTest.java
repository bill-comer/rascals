package uk.co.utilisoft;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/test-parms.xml"})
@TransactionConfiguration(transactionManager="test.parms.transactionManager", defaultRollback=true)
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BaseDaoIntegrationTest<DAO extends UtilisoftGenericDaoHibernate>
{
  private Map<Class<?>,List<Serializable>> mPersistedObjectPks = new HashMap<Class<?>, List<Serializable>>();

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Before
  public void freezeTime()
  {
    Freeze.freeze(new DateTime());
  }

  public void freezeTime(DateTime aTimeToFreezeAt)
  {
    Freeze.freeze(aTimeToFreezeAt);
  }

  /**
   * @return the persisted object primary keys
   */
  protected Map<Class<?>, List<Serializable>> getPersistedObjectPks()
  {
    return mPersistedObjectPks;
  }

  protected void cleanUpAfterTest()
  {
    // this is required because SupplierDao.getSupplier(String) creates a record if one does not exist
    List<Supplier> supps = mSupplierDao.getAll();
    if (!supps.isEmpty())
    {
      deleteTestData((DAO) mSupplierDao, Supplier.class, true);
    }
  }

  /**
   * Insert test data.
   *
   * @param aObjectsToSave the objects to insert
   * @param aDao the data access object
   * @param aObjectClassType the object class type to insert
   * @return a Map of primary key references for successfully saved objects
   */
  protected Map<Class<?>, List<Serializable>> insertTestData(List<?> aObjectsToSave, DAO aDao, Class<?> aObjectClassType)
  {
    Assert.notNull(aDao, "Data Access Object is required");
    Assert.notNull(aDao.getSessionFactory(), "Session Factory is required");
    Session session = aDao.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    Assert.isTrue(tx.isActive(), "An active Hibernate Transaction is required");

    for (Object obj : aObjectsToSave)
    {
      Serializable pk = session.save(obj);
      Assert.notNull(pk, "Object could not be saved to the database");

      if (mPersistedObjectPks.containsKey(aObjectClassType))
      {
        mPersistedObjectPks.get(aObjectClassType).add(pk);
      }
      else
      {
        List<Serializable> persistedObjectPks = new ArrayList<Serializable>();
        persistedObjectPks.add(pk);
        mPersistedObjectPks.put(aObjectClassType, persistedObjectPks);
      }
    }

    tx.commit();
    session.close();
    Assert.isTrue(!tx.isActive(), "Hibernate Transaction is still active");
    Assert.isTrue(mPersistedObjectPks.containsKey(aObjectClassType)
                  && !mPersistedObjectPks.get(aObjectClassType).isEmpty(),
                  "Failed to insert test data in the database");
    return mPersistedObjectPks;
  }

  /**
   * Delete test data.
   *
   * @param aDao the data access object
   * @param aObjectClassType the object class type to insert
   * @param aDeleteAllRecords true if all records should be deleted
   */
  protected void deleteTestData(DAO aDao, Class<?> aObjectClassType, boolean aDeleteAllRecords)
  {
    Assert.notNull(aDao, "Data Access Object is required");
    Assert.notNull(aDao.getSessionFactory(), "Session Factory is required");
    Session session = aDao.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    assertTrue(tx.isActive());

    if (!aDeleteAllRecords)
    {
      // delete objects saved and maintain a reference to successfully deleted object pks
      Map<Class<?>, List<Serializable>> objectPksToRemove = deleteObjects(session, aObjectClassType,
                                                                          mPersistedObjectPks);

      // clear reference to all saved object pks that have been successfully deleted
      List<Serializable> deletedObjectPks = objectPksToRemove.get(aObjectClassType);

      if (mPersistedObjectPks.containsKey(aObjectClassType))
      {
        mPersistedObjectPks.get(aObjectClassType).removeAll(deletedObjectPks);
        Assert.isTrue(mPersistedObjectPks.get(aObjectClassType).isEmpty(), "Failed to delete objects with pks["
                      + StringUtils.collectionToCommaDelimitedString(mPersistedObjectPks.get(aObjectClassType)) + "]");
      }

      mPersistedObjectPks.remove(aObjectClassType);
    }
    else
    {
      // delete all objects
      deleteAllObjects(session, aObjectClassType);
    }

    tx.commit();
    session.close();
    Assert.isTrue(!tx.isActive(), "Hibernate Transaction is still active");
  }

  /**
   * Delete test data.
   *
   * @param aDao the data access object
   * @param aObjectClassType the object class type to insert
   */
  protected void deleteTestData(DAO aDao, Class<?> aObjectClassType)
  {
    deleteTestData(aDao, aObjectClassType, false);
  }

  private boolean deleteAllObjects(Session aSession, Class<?> aObjectClassType)
  {
    Boolean status = null;

    try
    {
      if (aObjectClassType != null)
      {
        long objectsDeleted = aSession.createQuery("delete from " + aObjectClassType.getName()).executeUpdate();

        status = objectsDeleted > 0 ? true : false;
      }
    }
    catch (HibernateException he)
    {
      status = false;
    }

    return status;
  }

  private Map<Class<?>, List<Serializable>> deleteObjects(Session aSession,
      Class<?> aObjectClassType, Map<Class<?>, List<Serializable>> aPersistedObjectPks)
  {
    Map<Class<?>, List<Serializable>> objectPksToRemove = new HashMap<Class<?>, List<Serializable>>();
    List<Serializable> persistedObjectPks = aPersistedObjectPks.get(aObjectClassType);

    if (persistedObjectPks != null)
    {
      for (Serializable persistedObjectPk : persistedObjectPks)
      {
        Object objectSaved = aSession.get(aObjectClassType, persistedObjectPk);

        if (objectSaved != null)
        {
          aSession.delete(objectSaved);
        }

        if (objectPksToRemove.containsKey(aObjectClassType))
        {
          objectPksToRemove.get(aObjectClassType).add(persistedObjectPk);
        }
        else
        {
          List<Serializable> pksToDelete = new ArrayList<Serializable>();
          pksToDelete.add(persistedObjectPk);
          objectPksToRemove.put(aObjectClassType, pksToDelete);
        }
      }
    }


    return objectPksToRemove;
  }
}
