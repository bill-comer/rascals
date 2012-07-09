package uk.co.utilisoft.parms.web.service.report;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface ReportService
{
  /**
   * @param aSupplierPk the supplier primary key
   * @param aResponse the response
   * @return the report file name
   * @throws IOException
   */
  String downloadHalfHourlyQualifyingMpansReport(Long aSupplierPk, HttpServletResponse aResponse) throws IOException;
}
