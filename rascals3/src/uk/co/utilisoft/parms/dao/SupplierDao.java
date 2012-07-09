package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

public interface SupplierDao extends UtilisoftGenericDao<Supplier, Long>
{
  /**
   * Gets a distinct list of all suppliers associated with a DPI file and therefore a TimePeriod
   * @param aDpiFile
   * @return
   */
  List<Supplier> getAllSuppliersForDpi(DpiFile aDpiFile);

  Supplier getSupplier(String supplierId);
}
