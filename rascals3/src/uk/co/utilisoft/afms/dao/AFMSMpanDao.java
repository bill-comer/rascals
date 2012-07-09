package uk.co.utilisoft.afms.dao;

import java.util.List;

import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AFMSMpanDao extends UtilisoftGenericDao<AFMSMpan, Long>
{

  /**
   * Find Mpan records for the Year and Month time period specified.
   *
   * @param aTimeperiod the time period
   * @param isMonthT TODO
   */
  List<AFMSMpan> getActiveMpans(final ParmsReportingPeriod aTimeperiod, Supplier aSupplier, boolean isMonthT);

  /**
   * Find active Mpan records for the the last complete 12 months
   *
   * @param aSupplier the supplier
   * @param aValidEffectiveFromDate
   * @param aValidStartOf12MonthMonitoringDate date when AFMSMpans are to be considered for SP04 reporting
   * @param aMinimumNoOfMonthsInSupply the minimum number of months an AFMSMpan has been in supply
   */
  List<AFMSMpan> getActiveMpansForLast12Months(Supplier aSupplier,
                                               DateTime aValidEffectiveFromDate,
                                               DateTime aValidStartOf12MonthMonitoringDate,
                                               Integer aMinimumNoOfMonthsInSupply);

  /**
   * Gets distinct list of suppliers for a reporting month & Preceding Month
   * @param aTimeperiod
   * @return
   */
  List<String> getSupplierIdsForTwoMonths(final ParmsReportingPeriod aTimeperiod);

  /**
   * gets the latest AFMS MPAN for a supplier and time period & mpan
   * @param mpanCore
   * @param supplier
   * @param mParmsReportingPeriod
   * @return
   */
  AFMSMpan getAfmsMpan(MPANCore mpanCore, Supplier supplier,
      ParmsReportingPeriod mParmsReportingPeriod);

  /**
   * gets the latest AFMS MPAN for a supplier and current time & mpan
   * @param aMpanCore the mpan
   * @param aSupplier the supplier
   * @param aCurrentTime the current time
   * @return the currently active mpan
   */
  AFMSMpan getAfmsMpan(MPANCore aMpanCore, Supplier aSupplier,
      DateTime aCurrentTime);

  /**
   * gets the latest AFMS MPAN for a supplier and mpan
   * @param mpanCore
   * @param supplier
   * @return
   */
  AFMSMpan getAfmsMpan(MPANCore mpanCore, Supplier supplier);
}
