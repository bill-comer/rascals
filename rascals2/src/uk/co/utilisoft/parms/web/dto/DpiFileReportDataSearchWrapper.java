package uk.co.utilisoft.parms.web.dto;

import uk.co.utilisoft.parms.domain.GenericAgent;


/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiFileReportDataSearchWrapper extends SearchWrapper
{
  private static final long serialVersionUID = 1L;

  private GenericAgent mAgent;

  /**
   * @param aAgent the DataCollector or MOP Agent
   */
  public DpiFileReportDataSearchWrapper(GenericAgent aAgent)
  {
    mAgent = aAgent;
  }

  /**
   * @return the DataCollector or MOP Agent
   */
  public GenericAgent getAgent()
  {
    return mAgent;
  }

  /**
   * @param aAgent the DataCollector or MOP Agent
   */
  public void setAgent(GenericAgent aAgent)
  {
    mAgent = aAgent;
  }

}