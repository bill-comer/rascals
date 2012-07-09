package uk.co.utilisoft.parms.file.dpi;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.DpiFileDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;


@Service("parms.dpiDataCreator")
public class DpiDataCreator
{

  @Autowired(required=true)
  @Qualifier("parms.agentBuilder")
  private AgentBuilder mAgentBuilder;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDao mDpiFileDao;

  @Transactional
  public DpiFile createAndStoreDpiData(ParmsReportingPeriod parmsReportingPeriod,
      Supplier supplier)
  {
    DpiFile dpiFile = createDpiFile(parmsReportingPeriod, supplier);

    //Map<String, GridSupplyPoint> gsps = buildGSPs(parmsReportingPeriod, supplier, dpiFile);

    buildAgents(parmsReportingPeriod, supplier, dpiFile);

    return dpiFile;
  }
  


  public DpiFile createDpiFile(ParmsReportingPeriod aParmsReportingPeriod, Supplier supplier)
  {
    DpiFile dpiFile = new DpiFile(aParmsReportingPeriod, supplier);
    dpiFile.setFileName(DpiFile.createFileName(supplier.getSupplierId(), aParmsReportingPeriod));

    makePersistent(dpiFile);
    return dpiFile;
  }
  

  

  private void buildAgents(ParmsReportingPeriod parmsReportingPeriod,
      Supplier supplier, DpiFile dpiFile)
  {

    System.out.println("started buildAgentsForAfmsMpansForAMonth " + new DateTime());
    mAgentBuilder.resetData();
    // do for month T
    mAgentBuilder.buildAgentsForAfmsMpansForAPeriod(parmsReportingPeriod, supplier, dpiFile, true);

    System.out.println("start T-1 buildAgentsForAfmsMpansForAMonth " + new DateTime());
    //repeat for T-1
    mAgentBuilder.buildAgentsForAfmsMpansForAPeriod(parmsReportingPeriod, supplier, dpiFile, false);

    System.out.println("finished buildAgentsForAfmsMpansForAMonth " + new DateTime());
    /**
     * persist all discovered DC & MOP agents to the DB
     */
    mAgentBuilder.saveAllAgents();
  }
  

  DpiFile makePersistent(DpiFile dpiFile)
  {
    return mDpiFileDao.makePersistent(dpiFile);
  }
  

  /**
   * @param aDpiFileDao the DpiFileDao
   */
  public void setDpiFileDao(DpiFileDao aDpiFileDao)
  {
    mDpiFileDao = aDpiFileDao;
  }
  

  /**
   * @param aAgentBuilder
   */
  public void setAgentBuilder(AgentBuilder aAgentBuilder)
  {
    this.mAgentBuilder = aAgentBuilder;
  }
}
