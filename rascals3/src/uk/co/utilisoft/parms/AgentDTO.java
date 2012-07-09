package uk.co.utilisoft.parms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.afms.domain.MeasurementClassification;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;

@Service
public class AgentDTO
{

  //data needed to build Mpan
  private ParmsReportingPeriod mParmsReportingPeriod;
  void setParmsReportingMonth(ParmsReportingPeriod aParmsReportingPeriod)
  {
    this.mParmsReportingPeriod = aParmsReportingPeriod;
  }
  private ParmsReportingPeriod getParmsReportingPeriod()
  {
    return mParmsReportingPeriod;
  }

  private AFMSMpan mAfmsMpan;
  public void setAfmsMpan(AFMSMpan aAfmsMpan)
  {
    this.mAfmsMpan = aAfmsMpan;
  }

  private DpiFile mDpiFile;
  void setDpiFile(DpiFile aDpiFile)
  {
    this.mDpiFile = aDpiFile;
  }
  public AgentDTO()
  {
  }

  /**
   * Builds the DataCollectors
   * @param isMonthT TODO
   */
  public void buildDataCollectorsForMpan(
      AFMSMpan aAfmsMpan,
      Map<String,DataCollector> dataCollectors,
      AFMSAgent agent,
      boolean isMonthT)
  {
    this.mAfmsMpan = aAfmsMpan;
    boolean isMop = false;

    MeasurementClassification classification = new MeasurementClassification(mAfmsMpan.getMeasurementClassification());

    if (agent.getDCEffectiveFromDate().isBefore(getParmsReportingPeriod().getEndOfMonth(isMonthT)))
    {
      //this DC is valid for atleast part of the month
      addToAgentDCMapIfNotThereAlready(aAfmsMpan.getGridSupplyPoint(), dataCollectors, classification.isHalfHourly(), agent.getDataCollector(), isMop, isMonthT);
    }
  }


  /**
   * Builds the DataCollectors
   * @param isMonthT TODO
   */
  public void buildDCsFromAgentHistoriesForMpan(
      AFMSMpan aAfmsMpan,
      Map<String,DataCollector> dataCollectors,
      Collection<AFMSAgentHistory> possibleAgents, boolean isMonthT)
  {
    this.mAfmsMpan = aAfmsMpan;
    boolean isMop = false;

    MeasurementClassification classification = new MeasurementClassification(mAfmsMpan.getMeasurementClassification());

    if (possibleAgents != null)
    {
      /*
       * go thru agent history & check if agents are already there
       * If they are then check the EffectiveFromDate and update if the newer one is newer
       * If they are not then add them
       */
      for (AFMSAgentHistory afmsAgentHistory : possibleAgents)
      {
        if ( (classification.isHalfHourly() &&  afmsAgentHistory.getAgentRoleCode().isHHDCType())
             || (!classification.isHalfHourly() && afmsAgentHistory.getAgentRoleCode().isNonHHDCType())
             )
        {
          if (afmsAgentHistory.getAgentEffectiveFromDate().isBefore(getParmsReportingPeriod().getEndOfMonth(isMonthT)))
          {
            addToAgentDCMapIfNotThereAlready(aAfmsMpan.getGridSupplyPoint(), dataCollectors, classification.isHalfHourly(), afmsAgentHistory.getAgentId(), isMop, isMonthT);
          }
        }
        else if (isMeasurementClassChange(afmsAgentHistory.getReasonForChange())
                 || ReasonForChange.isChangeOfAgent(afmsAgentHistory.getReasonForChange()))
        {
          AgentRoleCodeType agentRoleCode = afmsAgentHistory.getAgentRoleCode();
          addToAgentDCMapIfNotThereAlready(aAfmsMpan.getGridSupplyPoint(), dataCollectors, agentRoleCode.isHHDCType(), afmsAgentHistory.getAgentId(), isMop, isMonthT);
        }
      }
    }
  }



  private boolean isMeasurementClassChange(String aReasonForChange)
  {
    if (ReasonForChange.isMeterChange(aReasonForChange))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

  void addToAgentMOPMapIfNotThereAlready(String gspName,
      Map<String, MOP> agentsMap,
      boolean isHalfHourly, String agentName, boolean isMop, boolean isMonthT)
  {
    if (!agentsMap.containsKey(agentName))
    {
      // new MOP
      MOP newAgent = new MOP(
          agentName, isHalfHourly, mDpiFile, isMonthT);

      //new DC must mean a new GSP
      GridSupplyPoint gsp = new GridSupplyPoint(gspName, mDpiFile);
      gsp.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);

      newAgent.getGridSupplyPoints().add(gsp);
      agentsMap.put(newAgent.getName(), newAgent);
    }
    else
    {
      MOP agent = agentsMap.get(agentName);
      checkGspOnAgentMap(gspName, isHalfHourly, isMonthT, agent);

      agent.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);
    }
  }


  private void checkGspOnAgentMap(String gspName,
      boolean isHalfHourly, boolean isMonthT, GenericAgent agent)
  {
    GridSupplyPoint gspFromList = getGspInList(agent.getGridSupplyPoints(), gspName);
    if (gspFromList == null)
    {
      //create new GSP
      GridSupplyPoint gsp = new GridSupplyPoint(gspName, mDpiFile);
      gsp.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);

      // and add to list on MOP
      agent.getGridSupplyPoints().add(gsp);
    }
    else
    {
      gspFromList.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);
    }
  }


  void addToAgentDCMapIfNotThereAlready(String gspName,
      Map<String, DataCollector> agentsMap,
      boolean isHalfHourly, String agentName, boolean isMop, boolean isMonthT)
  {
    if (!agentsMap.containsKey(agentName))
    {
      // new DC
      DataCollector newAgent = new DataCollector(
          agentName, isHalfHourly, mDpiFile, isMonthT);

      //new DC must mean a new GSP
      GridSupplyPoint gsp = new GridSupplyPoint(gspName, mDpiFile);
      gsp.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);

      newAgent.getGridSupplyPoints().add(gsp);
      agentsMap.put(newAgent.getName(), newAgent);
    }
    else
    {
      DataCollector agent = agentsMap.get(agentName);
      checkGspOnAgentMap(gspName, isHalfHourly, isMonthT, agent);

      agent.setAppropiateHalfHourlyFlag(isMonthT, isHalfHourly);
    }
  }


  GridSupplyPoint getGspInList(Collection<GridSupplyPoint> gridSupplyPoints,
      String gspName)
  {
    for (GridSupplyPoint gridSupplyPoint : gridSupplyPoints)
    {
      if (gridSupplyPoint.getName().equals(gspName))
      {
        return gridSupplyPoint;
      }
    }
    return null;
  }


  /**
   * Builds the MOPs
   * @param isMonthT TODO
   */
  public void buildMOPsForMpan(
      AFMSMpan aAfmsMpan,
      Map<String, MOP> mops,
      AFMSAgent mopAgent,
      boolean isMonthT)
  {
    this.mAfmsMpan = aAfmsMpan;
    boolean isMop = true;

    MeasurementClassification classification = new MeasurementClassification(mAfmsMpan.getMeasurementClassification());

    if (mopAgent.getMOEffectiveFromDate().isBefore(getParmsReportingPeriod().getEndOfMonth(isMonthT)))
    {
      //this MOP is valid for atleast part of the month
      addToAgentMOPMapIfNotThereAlready(aAfmsMpan.getGridSupplyPoint(), mops, classification.isHalfHourly(), mopAgent.getMeterOperator(), isMop, isMonthT);
    }
  }


  /**
   * Builds the MOPs from agent histories
   * @param isMonthT TODO
   */
  public void buildMOPsFromAgentHistoriesForMpan(
      AFMSMpan aAfmsMpan,
      Map<String, MOP> mops,
      Collection<AFMSAgentHistory> possibleAgents, boolean isMonthT)
  {
    this.mAfmsMpan = aAfmsMpan;
    boolean isMop = true;

    MeasurementClassification classification = new MeasurementClassification(mAfmsMpan.getMeasurementClassification());

    if (possibleAgents != null)
    {
      for (AFMSAgentHistory afmsAgentHistory : possibleAgents)
      {
        if ( afmsAgentHistory.getAgentRoleCode().isMOPType())
       {
          //no DC with that name exists so create a new one
          if (afmsAgentHistory.getAgentEffectiveFromDate().isBefore(getParmsReportingPeriod().getEndOfMonth(isMonthT)))
          {
           boolean isHalfHourly  = classification.isHalfHourly();
           if (isMeasurementClassChange(afmsAgentHistory.getReasonForChange()))
           {
             isHalfHourly  = !classification.isHalfHourly();
           }

           addToAgentMOPMapIfNotThereAlready(aAfmsMpan.getGridSupplyPoint(),
                mops, isHalfHourly, afmsAgentHistory.getAgentId(),
                isMop, isMonthT);
          }
       }
      }
    }
  }
  public void setBaseParams(ParmsReportingPeriod aParmsReportingPeriod,
      DpiFile aDpiFile)
  {
    mParmsReportingPeriod = aParmsReportingPeriod;
    mDpiFile = aDpiFile;
  }

}
