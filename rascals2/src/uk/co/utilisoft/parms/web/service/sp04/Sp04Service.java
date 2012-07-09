package uk.co.utilisoft.parms.web.service.sp04;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;

/**
 * @version 1.0
 */
public interface Sp04Service
{

  /**
   * Get list of suppliers for Sp04Generate page
   *
   * @return the AdminListDTO(s)
   */
  List<AdminListDTO> getAllSortedSupplierRecords();

  /**
   * @param aSupplierId the supplier id
   * @return the P0028Active(s)
   */
  IterableMap<String, P0028Active> getAllP0028Active(Long aSupplierId);

  /**
   * @param aSupplierId the supplier id
   * @param aCurrentDate the current date
   * @return the AdminListDTO(s)
   */
  List<AdminListDTO> getAllSortedRecords(Long aSupplierId);

  /**
   * Combine currently active mpans from P0028Active and Sp04FromAFMSMpan records, and remove duplicate mpans.
   *
   * @param aAfmsMpanDatas the Mpans from Afms database
   * @param aP0028MpanDatas the Mpans from P0028Active records
   * @return the P0028Active and Sp04FromAFMSMpan records for currently active mpans
   */
  IterableMap<String, Object> combineP0028AndAfmsMpans(MultiHashMap<String, Sp04FromAFMSMpan> aAfmsMpanDatas,
                                                       MultiHashMap<String, P0028Active> aP0028MpanDatas);

  /**
   * @param aSupplierId the supplier id
   * @param aMpansToExclude list of mpans to exlude from list
   * @param aLastMonthPrp the reporting period offset by 1 month
   * @return the AdminListDTO(s)
   */
  List<AdminListDTO> getAllSortedRecords(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp);

  /**
   * @param aSupplierId the supplier id
   * @return the AdminListDTO(s)
   */
  List<AdminListDTO> getAllErroredRecords(Long aSupplierId);

  /**
   * @param aSupplierId the supplier id
   * @return the supplier
   */
  Supplier getSupplier(Long aSupplierId);

  /**
   * bug#5835 - combine Sp04FromAfmsMpan and P0028Active records
   * @param supplier the supplier
   * @param activeMapToUse the P0028Active(s) and Sp04FromAFMSMpan(s)
   * @return the SP04File pk
   */
  Long buildFile(Supplier supplier, IterableMap<String, Object> activeMapToUse);

  /**
   * @param aSupplierId the supplier id
   * @param aResponse the response
   * @return the downloaded sp04 error report file name
   * @throws Exception
   */
  String downloadSp04ErrorReport(Long aSupplierId, HttpServletResponse aResponse) throws Exception;

  /**
   * @return the AdminListDTO(s)
   */
  List<AdminListDTO> getAllSp04Records();

  /**
   * @param filePk the Sp04 file pk
   * @return the Sp04File
   */
  Sp04File getSp04File(Long filePk);

  /**
   * @param aSp04FilePk the Sp04File pk
   * @param aResponse the response
   * @return the downloaded Sp04File file name
   * @throws Exception
   */
  String downloadSp04FileReport(Long aSp04FilePk, HttpServletResponse aResponse, boolean aDownloadAsCsv) throws Exception;

  Sp04FromAFMSMpan saveSp04FromAFMSMpan(Sp04FromAFMSMpan sp04FromAFMSMpan);

  void deleteSp04FromAFMSMpan(Set<MPANCore> aMpans);

  /**
   * Get Active AFMS Mpans eligible for SP04 reporting in the future.
   *
   * @param aSupplierId the supplier id
   * @param aMpansToExclude the mpans to exclude
   * @param aLastMonthPrp the reporting period offset by 1 month
   * @return the collection of AdminListDTO data for Sp04FromAFMSMpan records
   */
  MultiHashMap<String, AdminListDTO> getRowsFromAfmsSp04Mpans(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp);

  /**
   * Get Active P0028 Mpans eligible for SP04 reporting.
   *
   * @param aSupplierId the supplier id
   * @param aMpansToExclude the mpans to exclude
   * @param aLastMonthPrp the reporting period offset by 1 month
   * @return the collection of AdminListDTO data for P0028Active records
   */
  MultiHashMap<String, AdminListDTO> getAllP0028ForSp04SortedRecords(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp);

  /**
   * Get Sp04FromAFMSMpan records for building sp04 file and exclude records with Meter Installation Deadline
   * ahead of the current date.
   *
   * @param aSupplierId the supplier id
   * @param aCurrentDate the current date
   * @return the Sp04FromAFMSMpan records
   */
  MultiHashMap<String, Sp04FromAFMSMpan> getEligibleMpansForSp04FromAfmsMpans(Long aSupplierId, DateTime aCurrentDate);

  /**
   * bug#5835 need list of Sp04FromAFMSMpan records for building sp04 file.
   *
   * @param aSupplierId the supplier id
   * @return the collection of Sp04FromAfmsMpan records mapped by Mpan
   */
  MultiHashMap<String, Sp04FromAFMSMpan> getEligibleMpansForSp04FromAfmsMpans(Long aSupplierId);

  /**
   * Get P0028active records for building sp04 file and exclude records with Meter Installation Deadline
   *
   * @param aSupplierId the supplier id
   * @param aLastMonthPrp the reporting period offset to previous month
   * @param aCurrentDate the current date
   * @return a collection of P0028Active records
   */
  MultiHashMap<String, P0028Active> getEligibleMpansForP0028Active(Long aSupplierId, ParmsReportingPeriod aLastMonthPrp, DateTime aCurrentDate);

  /**
   * bug#5835 - getlist of elligible p0028active(s) for building sp04 file.
   *
   * @param aSupplierId the supplier id
   * @param aLastMonthPrp the reporting period offset to previous month
   * @return a collection of P0028Active records by Mpan
   */
  MultiHashMap<String, P0028Active> getEligibleMpansForP0028Active(Long aSupplierId, ParmsReportingPeriod aLastMonthPrp);

  /**
   * @param aSupplierId the supplier id
   * @return a map of elligible mpans from P0028 and PARMS_SP04_FROM_AFMS_MPANS table
   */
  IterableMap<String, AdminListDTO> getCombinedSortedRecords(Long aSupplierId);
}