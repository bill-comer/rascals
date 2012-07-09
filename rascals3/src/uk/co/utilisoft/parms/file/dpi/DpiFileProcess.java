package uk.co.utilisoft.parms.file.dpi;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

@Service("parms.dpiFileProcess")
public class DpiFileProcess
{
  private static Logger mLogger = Logger.getLogger(DpiFileProcess.class);

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDao mAfmsMpanDao;  //aFMSMpanDao

  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileBuilder")
  private DpiFileBuilder mDpiFileBuilder;

  @Autowired(required=true)
  @Qualifier("parms.dpiDataCreator")
  private DpiDataCreator mDpiDataCreator;

  public String[] startBuild(Integer aStartReportMonth, Integer aYearForStartReportMonth)
  {
    Integer startOfReportMonth = aStartReportMonth;
    Integer yearOfReportMonth = aYearForStartReportMonth;

    if (startOfReportMonth == null || yearOfReportMonth == null)
    {
      return buildFiles();
    }
    else
    {
      return buildFiles(startOfReportMonth, yearOfReportMonth);
    }
  }

  public DpiFileProcess()
  {
  }

  /**
   * Default function assumes current month and year.
   * Builds reports for all suppliers for the previous ReportingMonth.
   */
  public String[] buildFiles()
  {
    DateTime now = new DateTime();
    mLogger.warn("Default to using the Start Report month of " + now.getMonthOfYear() + ", and year " + now.getYear()
                 + " to generate the DPI File");
    return buildFiles(now.getMonthOfYear(), now.getYear());
  }

  /**
   * Builds data for all suppliers for a repoerting period
   * @param aMonth
   * @param aYear
   * @return
   */
  public String[] buildFiles(int aMonth, int aYear)
  {
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(aMonth, aYear);
    List<String> supplierIDs = mAfmsMpanDao.getSupplierIdsForTwoMonths(parmsReportingPeriod);

    if (supplierIDs.size() == 0)
    {
      String[] responses = new String[2];
      responses[1] = "No Suppliers found for the selected reporting period of " + parmsReportingPeriod;
      return responses;
    }
    
    for (String supplierID : supplierIDs)
    {
      if (StringUtils.isNotEmpty(supplierID))
      {
        Supplier supplier = mSupplierDao.getSupplier(supplierID);
        return buildFileForOneSupplier(parmsReportingPeriod, supplier);
      }

    }

    return null;
  }

  /**
   * Builds data for one supllier for one period
   * @param parmsReportingPeriod
   * @param supplier
   * @return
   */
  String[] buildFileForOneSupplier(ParmsReportingPeriod parmsReportingPeriod,
      Supplier supplier)
  {
    DpiFile dpiFile = mDpiDataCreator.createAndStoreDpiData(parmsReportingPeriod, supplier);

    // create, write out to directory location, and persist data for existing dpi file
    System.out.println("start buildFileData " + new DateTime());
    return mDpiFileBuilder.buildFileData(dpiFile, supplier);
  }

  /**
   * @param aAfmsMpanDao the AfmsMpanDao
   */
  public void setAfmsMpanDao(AFMSMpanDao aAfmsMpanDao)
  {
    this.mAfmsMpanDao = aAfmsMpanDao;
  }

  /**
   * @param aSupplierDao the SupplierDao
   */
  public void setSupplierDao(SupplierDao aSupplierDao)
  {
    this.mSupplierDao = aSupplierDao;
  }


  /**
   * @param aDpiDataCreator
   */
  public void setDpiDataCreator(DpiDataCreator aDpiDataCreator)
  {
    this.mDpiDataCreator = aDpiDataCreator;
  }

  /**
   * @param aDpiFileBuilder
   */
  public void setDpiFileBuilder(DpiFileBuilder aDpiFileBuilder)
  {
    this.mDpiFileBuilder = aDpiFileBuilder;
  }

}