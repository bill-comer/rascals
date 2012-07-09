package uk.co.utilisoft.parms.web.command;

import static uk.co.utilisoft.parms.web.controller.WebConstants.REPORT_ADMIN_REQUESTED_ACTION;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.ParmsReportingPeriod;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiGenerationCommand
{
  private String mDpiGenerationRequestedAction;
  private String mReportingPeriod;
  private String mErrorMessage;

  /**
   *  Constructor.
   */
  public DpiGenerationCommand()
  {
    mDpiGenerationRequestedAction = REPORT_ADMIN_REQUESTED_ACTION;
  }

  /**
   * @return the report requested action
   */
  public String getDpiGenerationRequestedAction()
  {
    return mDpiGenerationRequestedAction;
  }

  /**
   * @param aReportRequestedAction
   */
  public void setDpiGenerationRequestedAction(String aReportRequestedAction)
  {
    mDpiGenerationRequestedAction = aReportRequestedAction;
  }

  /**
   * @return the reporting period
   */
  public String getReportingPeriod()
  {
    return mReportingPeriod.trim();
  }


  /**
   * @param aReportingPeriod the reporting period
   */
  public void setReportingPeriod(String aReportingPeriod)
  {
    mReportingPeriod = aReportingPeriod;
  }

  /**
   * @return the error message
   */
  public String getErrorMessage()
  {
    return mErrorMessage;
  }

  /**
   * @param aErrorMessage the error message
   */
  public void setErrorMessage(String aErrorMessage)
  {
    mErrorMessage = aErrorMessage;
  }
}
