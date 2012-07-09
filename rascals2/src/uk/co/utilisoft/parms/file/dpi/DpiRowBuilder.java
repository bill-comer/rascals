package uk.co.utilisoft.parms.file.dpi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.co.utilisoft.parms.AgentRoleCodeType;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.GridSupplyPointDao;
import uk.co.utilisoft.parms.dao.SerialConfigDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GSPDefinition;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.SerialConfiguration;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.BaseRowBuilder;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;

@Component
public class DpiRowBuilder extends BaseRowBuilder implements RowBuilder
{
  private Supplier mSupplier;
  public void setSupplier(Supplier aSupplier)
  {
    this.mSupplier = aSupplier;
  }

  private String mSeperator = "|";
  private String mDpiPrefix = "DPI";



  @Autowired(required=true)
  @Qualifier("parms.serialConfigDao")
  private SerialConfigDao mSerialConfigDao;
  public void setSerialConfigDao(SerialConfigDao aSerialConfigDao)
  {
    this.mSerialConfigDao = aSerialConfigDao;
  }

  @Autowired(required=true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDao mGridSupplyPointDao;
  public void setGridSupplyPointDao(
      GridSupplyPointDao aGridSupplyPointDao)
  {
    this.mGridSupplyPointDao = aGridSupplyPointDao;
  }


  public DpiRowBuilder()
  {
  }


  public List<String> buildAllRows(DpiFile aDpiFile)
  {
    PoolChecksumCalculator checksumCalculator = getChecksumCalculator();

    List<String> rows = new ArrayList<String>();
    //add header
    rows.add(getHeader());

    addSP04Records(rows, aDpiFile);

    //add dpi records
    addDpiRecords(rows, aDpiFile);

    //calc checksum for all rows in file
    for (String row : rows)
    {
      checksumCalculator.addLineToCheckSum(row);
    }
    //add footer
    rows.add(getFooter(checksumCalculator));
    return rows;
  }




  void addSP04Records(List<String> rows, DpiFile aDpiFile)
  {
    List<GSPDefinition> gsps = getAllGspDefinitions();
    //GSPs should only be listed once for a supplier
    List<String> gspNamesDone = new ArrayList<String>();

    for (GSPDefinition gsp : gsps)
    {
      if (!gspNamesDone.contains(gsp.getName()))
      {
        // There will be a SP04 row for every possible GSP, not just the ones for this supplier that are Non HH
        {
          gspNamesDone.add(gsp.getName());
          String row = createLineForSerial(
              gsp.getName(),
              aDpiFile.getSupplier().getSupplierId(),
              AgentRoleCodeType.getSupplierType().getValue(),
              "SP04",
              aDpiFile.getReportingPeriod().getNextReportingPeriod())   // needs to be month T - second month
              ;
          rows.add(row);
        }
      }
    }

  }




  void addDpiRecords(List<String> aRows, DpiFile aDpiFile)
  {
    List<GridSupplyPoint> gsps = mGridSupplyPointDao.getAllGSPsDpi(aDpiFile);

    for (GridSupplyPoint gsp : gsps)
    {
      List<Boolean> months = new ArrayList<Boolean>();
      months.add(true);  //month T
      months.add(false); //month T-1

      ParmsReportingPeriod reportingPeriod = aDpiFile.getReportingPeriod().getNextReportingPeriod();

      for (Boolean isMonthT : months)
      {
        if (gsp.hasHalfHourlyMpansForMonth(isMonthT))
        {
          addHalfHourlyMopDpiRecords(aRows, gsp, reportingPeriod, aDpiFile, isMonthT);
          addHalfHourlyDcDpiRecords(aRows, gsp, reportingPeriod, aDpiFile, isMonthT);
        }

        if (gsp.hasNonHalfHourlyMpansForMonth(isMonthT))
        {
          addNonHalfHourlyMopDpiRecords(aRows, gsp, reportingPeriod, aDpiFile, isMonthT);
          addNonHalfHourlyDcDpiRecords(aRows, gsp, reportingPeriod, aDpiFile, isMonthT);
        }

      }
    }

  }

  void addHalfHourlyMopDpiRecords(List<String> aDpiRecords, GridSupplyPoint aGsp,
                                          ParmsReportingPeriod aReportingPeriod, DpiFile aDpiFile,
                                          boolean aIsMonthT)
  {
    List<SerialConfiguration> serialConfigs = mSerialConfigDao.getAllHHMopSerials(aIsMonthT);
    boolean isHH = true;
    boolean isMOP = true;
    addRowsForGspAndMonth(aDpiRecords, aGsp, isHH, isMOP, aDpiFile, aReportingPeriod, serialConfigs, aIsMonthT);
  }

  private void addNonHalfHourlyMopDpiRecords(List<String> aDpiRecords,  GridSupplyPoint aGsp,
                                             ParmsReportingPeriod aReportingPeriod, DpiFile aDpiFile,
                                             boolean aIsMonthT)
  {
    List<SerialConfiguration> serialConfigs = mSerialConfigDao.getAllNonHHMopSerials(aIsMonthT);

    boolean isHH = false;
    boolean isMOP = true;

    addRowsForGspAndMonth(aDpiRecords, aGsp, isHH, isMOP, aDpiFile, aReportingPeriod, serialConfigs, aIsMonthT);
  }

  private void addHalfHourlyDcDpiRecords(List<String> aDpiRecords,  GridSupplyPoint aGsp,
                                         ParmsReportingPeriod aReportingPeriod, DpiFile aDpiFile,
                                         boolean aIsMonthT)
  {
    List<SerialConfiguration> serialConfigs = mSerialConfigDao.getAllHHDCSerials(aIsMonthT);
    boolean isHH = true;
    boolean isMOP = false;
    addRowsForGspAndMonth(aDpiRecords, aGsp, isHH, isMOP, aDpiFile, aReportingPeriod, serialConfigs, aIsMonthT);
  }

  private void addNonHalfHourlyDcDpiRecords(List<String> aDpiRecords,  GridSupplyPoint aGsp,
                                            ParmsReportingPeriod aReportingPeriod, DpiFile aDpiFile,
                                            boolean aIsMonthT)
  {
    List<SerialConfiguration> serialConfigs = mSerialConfigDao.getAllNonHHDCSerials(aIsMonthT);
    boolean isHH = false;
    boolean isMOP = false;
    addRowsForGspAndMonth(aDpiRecords, aGsp, isHH, isMOP, aDpiFile, aReportingPeriod, serialConfigs, aIsMonthT);
  }


  public void addRowsForGspAndMonth(
      List<String> rows, GridSupplyPoint gsp,
      boolean isHH,
      boolean isMOP,
      DpiFile dpiFile,
      ParmsReportingPeriod parmsReportingPeriod,
      List<SerialConfiguration> aSerials,
      boolean isMonthT)
  {

    GenericAgent agent = gsp.getAgent();

    //this is true if HH request is same as the agents for appropitate Month
    boolean isAgentHHSameAsrequested = getIsAgentHHSameAsRequested(isHH, isMonthT, agent);

    if (agent.isMop() == isMOP && isAgentHHSameAsrequested )
    {
      // agent must be of the correct type for the report row we are inserting
      for (SerialConfiguration serial : aSerials)
      {
        String row = createLineForSerial(
            gsp.getName(), agent.getName(),
            agent.getRoleCode(isMonthT, isHH).getValue(), serial.getName(), parmsReportingPeriod);

        //ensure row is unique
        if (!rows.contains(row))
        {
          rows.add(row);
        }
      }
    }
  }

  boolean getIsAgentHHSameAsRequested(boolean isHH, boolean isMonthT,
      GenericAgent agent)
  {
    if (agent.isHalfHourlyForAppropiateMonth(isMonthT, isHH) )
    {
      return true;
    }
    return false;
  }


  private String getHeader()
  {
    DpiPoolHeader header = new DpiPoolHeader();
    return header.createHeader(mSupplier.getSupplierId());
  }


  public String createLineForSerial(String aGSP, String aAgentName, String aRoleCode,
      String aSerial, ParmsReportingPeriod aParmsReportingPeriod)
  {
    return mDpiPrefix + mSeperator
      + aGSP + mSeperator
      + aSerial + mSeperator
      + aAgentName + mSeperator
      + aRoleCode + mSeperator
      + aParmsReportingPeriod.getDpiRowDateFormat();
  }
}

