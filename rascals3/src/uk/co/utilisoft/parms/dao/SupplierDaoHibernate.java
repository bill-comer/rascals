package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

@Repository("parms.supplierDao")
public class SupplierDaoHibernate  extends ParmsGenericDao<Supplier, Long> implements SupplierDao
{

  @Override
  @SuppressWarnings("unchecked")
  public List<Supplier> getAllSuppliersForDpi(final DpiFile aDpiFile)
  {
    List<Supplier> results = (List<Supplier>) getHibernateTemplate().executeFind(
        new HibernateCallback()
        {
          /**
           * @see org.springframework.orm.hibernate3.HibernateCallback
           *      #doInHibernate(org.hibernate.Session)
           */
          public List<Supplier> doInHibernate(Session aSession)
                                 throws HibernateException, SQLException
          {
            Query query = aSession.getNamedQuery("getSuppliersForDpi");
            query.setLong("dpifile",aDpiFile.getPk());

            return (List<Supplier>)query.list();
          }
        });

    return results;
  }

  /**
   *  Gets the supplier from the mpan db.
   *  If there is not one it needs to create one.
   *
   * @param supplierId
   * @return
   */
  @Override
  public Supplier getSupplier(String supplierId)
  {
    // this will always be a small list so is not inefficient
    List<Supplier> suppliers = getAll();
    for (Supplier supplier : suppliers)
    {
      if (supplier.getSupplierId().equals(supplierId))
      {
        // found the match
        return supplier;
      }
    }

    // no match found so create one
    Supplier supplier = new Supplier(supplierId);
    makePersistent(supplier);
    return supplier;
  }

}