package uk.co.utilisoft.parms.file.dpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.afms.dao.AFMSAgentDao;
import uk.co.utilisoft.afms.dao.AFMSAgentHistoryDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.AgentDTO;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;


@Service("parms.agentBuilder")
public class AgentBuilder
{

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDao mAfmsMpanDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsAgentHistoryDao")
  private AFMSAgentHistoryDao mAFMSAgentHistoryDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsAgentDao")
  private AFMSAgentDao mAfmsAgentDao;

  @Autowired(required=true)
  @Qualifier("agentDTO")
  private AgentDTO mAgentDTO;

  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDao mAgentDao;

  Map<String, MOP> mMops;
  Map<String, DataCollector> mDataCollectors;


  public AgentBuilder()
  {
    resetData();
  }


  public void resetData()
  {
    mMops = new HashMap<String, MOP>();
    mDataCollectors = new HashMap<String, DataCollector>();
  }


  /**
   *
   * @param aParmsReportingPeriod
   * @param supplier
   * @param dpiFile
   * @param isMonthT TODO
   */
  public void buildAgentsForAfmsMpansForAPeriod(ParmsReportingPeriod aParmsReportingPeriod,
      Supplier supplier, DpiFile dpiFile, boolean isMonthT)
  {
    System.out.println("\nbuildAgentsForAfmsMpansForAPeriod - START, " + new DateTime());
    List<AFMSMpan> afmsMpans = getActiveMpans(aParmsReportingPeriod, supplier, isMonthT);
    System.out.println("\nfound " + afmsMpans.size()
        + " afms MPANs for month[" + aParmsReportingPeriod + "], " + new DateTime());

    MultiHashMap<String, AFMSAgent> afmsDCAgents = mAfmsAgentDao.getDataCollectorAgents(aParmsReportingPeriod, isMonthT);
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = mAfmsAgentDao.getMOPAgents(aParmsReportingPeriod, isMonthT);
    MultiHashMap<String, AFMSAgentHistory> allAfmsAgentHistories =
        mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(aParmsReportingPeriod, supplier, isMonthT);

    System.out.println("found " + allAfmsAgentHistories.size() + " allAfmsAgentHistories, " + new DateTime());

    mAgentDTO.setBaseParams(aParmsReportingPeriod, dpiFile);

    for (AFMSMpan afmsMpan : afmsMpans)
    {

      if (afmsDCAgents.containsKey(afmsMpan.getMpanCore())
          || afmsMOPAgents.containsKey(afmsMpan.getMpanCore())
          || allAfmsAgentHistories.containsKey(afmsMpan.getMpanCore()))
      {

        //DCs from agent table
        if (afmsDCAgents.containsKey(afmsMpan.getMpanCore()))
        {
          AFMSAgent[] dcAgents = getAgents(afmsDCAgents, afmsMpan.getMpanCore());
          for (AFMSAgent dcAgent : dcAgents)
          {
            if (dcAgent.getDataCollector() != null && afmsMpan.getAgent() != null && afmsMpan.getAgent().getDataCollector() != null)
            {
              if (dcAgent.getDataCollector().equals(afmsMpan.getAgent().getDataCollector()))
              {
                mAgentDTO.buildDataCollectorsForMpan(afmsMpan, mDataCollectors, dcAgent, isMonthT);
              }
            }
          }
        }

        //get DC agents from agent history
        Collection<AFMSAgentHistory> mpanDCHistories =  getDCHistories(allAfmsAgentHistories.getCollection(afmsMpan.getMpanCore()));
        if (mpanDCHistories.size() > 0)
        {
          mAgentDTO.buildDCsFromAgentHistoriesForMpan(afmsMpan, mDataCollectors, mpanDCHistories, isMonthT);
        }

        // mops from mop table
        if (afmsMOPAgents.containsKey(afmsMpan.getMpanCore()))
        {
          AFMSAgent[] mopAgents = getAgents(afmsMOPAgents, afmsMpan.getMpanCore());
          for (AFMSAgent mopAgent : mopAgents)
          {
            if (mopAgent.getMeterOperator() != null && afmsMpan.getAgent() != null && afmsMpan.getAgent().getMeterOperator() != null)
            {
              if (mopAgent.getMeterOperator().equals(afmsMpan.getAgent().getMeterOperator()))
              {
                mAgentDTO.buildMOPsForMpan(afmsMpan, mMops, mopAgent, isMonthT);
              }
            }
          }
        }


        //get MOP agents from agent history
        Collection<AFMSAgentHistory> mpanMOPHistories =  getMOPHistories(allAfmsAgentHistories.getCollection(afmsMpan.getMpanCore()));
        if (mpanMOPHistories.size() > 0)
        {
          mAgentDTO.buildMOPsFromAgentHistoriesForMpan(afmsMpan, mMops, mpanMOPHistories, isMonthT);
        }
      }
    }
    System.out.println("\nbuildAgentsForAfmsMpansForAPeriod - DONE, " + new DateTime());
  }


  private AFMSAgent[] getAgents(MultiHashMap<String, AFMSAgent> afmsAgents,
      String mpanCore)
  {
    AFMSAgent[] foundAgents = new AFMSAgent[0];
    Collection<AFMSAgent> agents = afmsAgents.getCollection(mpanCore);
    if (agents != null && agents.size() > 0)
    {
      foundAgents = agents.toArray(new AFMSAgent[0]);
    }
    return foundAgents;
  }


  /**
   * gets a list of just the DC agent histories for this MPAN
   * @param collectionOfAllHistories
   * @return
   */
  Collection<AFMSAgentHistory> getDCHistories(
      Collection<AFMSAgentHistory> collectionOfAllHistories)
  {
    Collection<AFMSAgentHistory> mpanDCHistories = new ArrayList<AFMSAgentHistory>();

    if (collectionOfAllHistories != null)
    {
      for (AFMSAgentHistory afmsAgentHistory : collectionOfAllHistories)
      {
        if (afmsAgentHistory.getAgentRoleCode().isHHDCType() || afmsAgentHistory.getAgentRoleCode().isNonHHDCType())
        {
          mpanDCHistories.add(afmsAgentHistory);
        }
      }
    }
    return mpanDCHistories;
  }



  /**
   * gets a list of just the MOP agent histories for this MPAN
   * @param collectionOfAllHistories
   * @return
   */
  private Collection<AFMSAgentHistory> getMOPHistories(
      Collection<AFMSAgentHistory> collectionOfAllHistories)
  {
    Collection<AFMSAgentHistory> mpanDCHistories = new ArrayList<AFMSAgentHistory>();

    if (collectionOfAllHistories != null)
    {
      for (AFMSAgentHistory afmsAgentHistory : collectionOfAllHistories)
      {
        if (afmsAgentHistory.getAgentRoleCode().isMOPType())
        {
          mpanDCHistories.add(afmsAgentHistory);
        }
      }
    }
    return mpanDCHistories;
  }


  List<AFMSMpan> getActiveMpans(
      ParmsReportingPeriod aParmsReportingPeriod, Supplier supplier, boolean isMonthT)
  {
    return mAfmsMpanDao.getActiveMpans(aParmsReportingPeriod, supplier, isMonthT);
  }


  @Transactional
  public void saveAllAgents()
  {
    mAgentDao.batchMakePersistent(new ArrayList<GenericAgent>(mDataCollectors.values()));
    mAgentDao.batchMakePersistent(new ArrayList<GenericAgent>(mMops.values()));
  }


  /**
   * @param aAfmsMpanDao
   */
  public void setAfmsMpanDao(AFMSMpanDao aAfmsMpanDao)
  {
    this.mAfmsMpanDao = aAfmsMpanDao;
  }


  /**
   * @param aAFMSAgentHistoryDao
   */
  public void setAFMSAgentHistoryDao(AFMSAgentHistoryDao aAFMSAgentHistoryDao)
  {
    this.mAFMSAgentHistoryDao = aAFMSAgentHistoryDao;
  }


  /**
   * @param aAfmsAgentDao
   */
  public void setAfmsAgentDao(AFMSAgentDao aAfmsAgentDao)
  {
    this.mAfmsAgentDao = aAfmsAgentDao;
  }


  public void setAgentDao(AgentDao mAgentDao)
  {
    this.mAgentDao = mAgentDao;
  }


  public void setAgentDTO(AgentDTO aAgentDTO)
  {
    this.mAgentDTO = aAgentDTO;
  }
}
