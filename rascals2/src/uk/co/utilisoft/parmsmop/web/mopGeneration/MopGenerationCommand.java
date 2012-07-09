package uk.co.utilisoft.parmsmop.web.mopGeneration;

import static uk.co.utilisoft.parms.web.controller.WebConstants.REPORT_MOP_GEN_REQUESTED_ACTION;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class MopGenerationCommand
{
  private String mMopGenerationRequestedAction;
  private String mReportingPeriod;
  private String mErrorMessage;

  /**
   *  Constructor.
   */
  public MopGenerationCommand()
  {
    mMopGenerationRequestedAction = REPORT_MOP_GEN_REQUESTED_ACTION;
  }

  /**
   * @return the report requested action
   */
  public String getMopGenerationRequestedAction()
  {
    return mMopGenerationRequestedAction;
  }

  /**
   * @param aReportRequestedAction
   */
  public void setMopGenerationRequestedAction(String aReportRequestedAction)
  {
    mMopGenerationRequestedAction = aReportRequestedAction;
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
