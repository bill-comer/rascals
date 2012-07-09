package uk.co.utilisoft.parms.dao;

import org.apache.commons.collections15.IterableMap;
import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 *
 */
public interface P0028ActiveDao extends UtilisoftGenericDao<P0028Active, Long>
{
  /**
   * Updates the P0028Active table following upload of a new P0028File
   *
   * @param aP0028Data the P0028 Data
   * @param aDataCollector the Data Collector
   * @param aDcAgentName the DC Agent Name
   */
  void storeNewP0028Active(P0028Data aP0028Data, DataCollector aDataCollector, String aDcAgentName);

  /**
   * @param aSupplier the supplier
   * @param aCurrentDate the current date
   * @return the P0028Active records identified by mpan(J0003)
   */
  IterableMap<String, P0028Active> get(Supplier aSupplier, DateTime aCurrentDate);

  /**
   * @param aSupplier the supplier
   * @return the P0028Active records identified by mpan(J0003)
   */
  IterableMap<String, P0028Active> getAllForSupplier(Supplier aSupplier);

  /**
   * @param aSupplier the supplier
   * @return the P0028Active records identified by mpan(J0003)
   */
  IterableMap<String, P0028Active> getForSupplierWithinP28UploadAndMID(Supplier aSupplier);
}
