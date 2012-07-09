package uk.co.utilisoft.parms.web.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.file.dpi.DpiFileProcess;

import static uk.co.utilisoft.parms.domain.Audit.TYPE;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Service("parms.DpiGenerationService")
public class DpiGenerationService
{
  @Autowired(required = true)
  @Qualifier("parms.dpiFileProcess")
  private DpiFileProcess mDpiFileProcess;

  /**
   * Generate a Parms Report.
   *
   * @param aStartReportMonthDate the Start date for a report
   * @return any errors; otherwise null
   */
  @ParmsAudit(auditType = TYPE.DPI_GENERATE_REPORT)
  public String generateParmsReport(DateTime aStartReportMonthDate)
  {
    if (aStartReportMonthDate != null)
    {
      return buildFiles(aStartReportMonthDate);
    }
    else
    {
      return mDpiFileProcess.buildFiles()[1];
    }
  }

  private String buildFiles(DateTime aStartReportMonthDate)
  {
    String[] errors = mDpiFileProcess.buildFiles(aStartReportMonthDate.getMonthOfYear(),
                                                 aStartReportMonthDate.getYear());

    if (errors != null && errors.length > 1 && StringUtils.isNotBlank(errors[1]))
    {
      return errors[1];
    }

    return null;
  }

  /**
   * @param aDpiFileProcess the DpiFileProcess
   */
  public void setDpiFileProcess(DpiFileProcess aDpiFileProcess)
  {
    mDpiFileProcess = aDpiFileProcess;
  }
}
