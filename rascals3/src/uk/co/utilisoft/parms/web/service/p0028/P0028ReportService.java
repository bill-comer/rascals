package uk.co.utilisoft.parms.web.service.p0028;

import javax.servlet.http.HttpServletResponse;

import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.web.service.AdminService;

/**
 *
 */
public interface P0028ReportService extends AdminService
{
  /**
   * @param aFilePk the file identifier
   * @return the P0028File
   */
  P0028File getP0028File(Long aFilePk);

  /**
   * downloads the error report for the chosen P0028File to
   * a pre defined directory
   *
   * @param aP0028FileId the P0028File identifier
   * @param aResponse the response
   */
  void downloadErrorReport(String aP0028FileId, HttpServletResponse aResponse) throws Exception;

  /**
   * @param aP0028FileId the P0028File identifier
   * @return the P0028FileData clob
   */
  P0028FileData getP0028FileData(Long aP0028FileId);

  /**
   * Retrieve the P0028 File Upload warnings if they exists.
   *
   * @param aP0028FileId the P0028File identifier
   * @param aResponse the response
   * @return the p0028 upload warnings file name
   * @throws Exception
   */
  String downloadP0028UploadWarnings(Long aP0028FileId, HttpServletResponse aResponse) throws Exception;
}
