package uk.co.utilisoft.parms.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface Sp04FromAFMSMpanDao extends UtilisoftGenericDao<Sp04FromAFMSMpan, Long>
{
  boolean exists(MPANCore mpan);

  Sp04FromAFMSMpan getMpan(final MPANCore mpan);

  /**
   * @param aSupplierPk the supplier pk
   * @param aCurrentTime the current time
   * @return the Sp04FromAFMSMpan records
   */
  List<Sp04FromAFMSMpan> getInReportingPeriod(Long aSupplierPk, DateTime aCurrentTime);

  /**
   * @param aSupplierId the supplier pk
   * @param aCurrentDate the current date
   * @return the Sp04FromAFMSMpan records
   */
  List<Sp04FromAFMSMpan> get(Long aSupplierPk, DateTime aCurrentDate);

  /**
   * @param aSupplierId the supplier pk
   * @return the Sp04FromAFMSMpan records
   */
  List<Sp04FromAFMSMpan> getAll(Long aSupplierPk);

  /**
   * Get Sp04FromAFMSMpan records for a supplier and data collector.
   *
   * @param aSupplierPk the supplier pk
   * @param aDataCollectorName the DataCollector name
   * @return the Sp04FromAFMSMpan records
   */
  IterableMap<String, Sp04FromAFMSMpan> getByDataCollector(final Long aSupplierPk, final String aDataCollectorName);

  /**
   * @param aMpanToExcludeFromDeletion the mpan to exclude from deletion
   * @return true if records were removed, otherwise, false;
   */
  Boolean delete(Set<MPANCore> aMpanToExcludeFromDeletion);
}
