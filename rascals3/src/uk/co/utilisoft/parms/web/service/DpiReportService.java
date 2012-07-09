package uk.co.utilisoft.parms.web.service;

import java.util.List;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;

/**
 * @version 1.0
 */
public interface DpiReportService extends AdminService
{
  /**
   * @param aDpiFilePk the DPI File primary key
   * @return a List of DPI File Data
   */
  List<DpiFileData> getByDpiFilePk(Long aDpiFilePk);

  /**
   * @param dpiFilePk the DPI File primary key
   * @return the Associated DpiFile
   */
  DpiFile getDpiFile(Long dpiFilePk);

  /**
   * @param aDpiFile the Dpi File
   * @return the GridSupplyPoint(s)
   */
  List<GridSupplyPoint> getAllGSPsDpi(DpiFile aDpiFile);

  /**
   * Gets agents for a DpiFile
   * @param aDpiFile the Dpi File
   * @return the Agent(s)
   */
  List<GenericAgent> getAgents(DpiFile aDpiFile);

  /**
   * Replicates all data associated with a DpiFile apart from the DpiFileData
   * @param aDpiFile the dpi file
   * @return the Dpi File
   */
  DpiFile replicateDpiFile(DpiFile aDpiFile);

  /**
   * Look up Agent by identifier.
   * @param aAgentId the Agent id
   * @return the Agent
   */
  GenericAgent getAgentById(Long aAgentId);

  /**
   * @param aDpiFilePk the Dpi File pk
   * @return the downloaded Dpi File name
   */
  String downloadDpiFileReport(Long aDpiFilePk) throws Exception;

  /**
   * @param aDpiFilePk the dpi file primary key
   * @return the file data
   */
  DpiFileData getFileToDownload(String aDpiFilePk);

  /**
   * @param aGridSuppyPoint the grid supply point
   * @param aUseMonthTData true if using month T data
   * @return true if the GridSupplyPoint information is to be included DPI Report
   */
  Boolean isIncludedInDpiReport(GridSupplyPoint aGridSuppyPoint, boolean aUseMonthTData);
}
