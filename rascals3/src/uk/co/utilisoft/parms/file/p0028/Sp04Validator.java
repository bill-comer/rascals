package uk.co.utilisoft.parms.file.p0028;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.dao.P0028DataDao;
import uk.co.utilisoft.parms.dao.P0028UploadErrorDao;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028UploadError;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;
import uk.co.utilisoft.parms.util.PropertyLoader;


@Service("parms.sp04Validator")
public class Sp04Validator
{
  public static final String MAX_DEMAND_THRESHOLD_PROPERTY_NAME = "sp04.exception.report.max.demand.threshold";
  public static final String ROOT_PATH_PROPERTY_VALUE_PROPERTY_NAME = "uk.co.utilisoft.parms.path";

  @Autowired(required = true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028UploadErrorDao")
  P0028UploadErrorDao mP0028UploadErrorDao;

  @Autowired(required = true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  Sp04FromAFMSMpanDao mSp04FromAFMSMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028DataDao")
  P0028DataDao mP0028DataDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMpanDao")
  AFMSMpanDao mAFMSMpanDao;

  /**
   * Validate P0028Active mpans against afms mpans
   *
   * @param aSupplierPk the supplier pk
   * @param aDataCollectorName the data collector
   * @param aP28ActivesMap the p0028 active records
   * @return the errors / warnings information
   */
  @SuppressWarnings("unchecked")
  public List<Object> validate(Long aSupplierPk, String aDataCollectorName,
                               IterableMap<String, P0028Active> aP28ActivesMap)
  {
    IterableMap<String, Sp04FromAFMSMpan> afmsMpansMap
      = mSp04FromAFMSMpanDao.getByDataCollector(aSupplierPk, aDataCollectorName);

    // get mpans for p0028Actives and afms
    Set<String> p28ActiveMpans = aP28ActivesMap.keySet();
    Set<String> afmsMpans = afmsMpansMap.keySet();

    boolean afmsMpansIsDifferentToP0028ActiveMpans = new HashSet<String>(afmsMpans).retainAll(p28ActiveMpans)
      && !p28ActiveMpans.isEmpty() && !afmsMpans.isEmpty();
    boolean p28ActiveMpansIsDifferentToAfmsMpans = new HashSet<String>(p28ActiveMpans).retainAll(afmsMpans)
      && !p28ActiveMpans.isEmpty() && !afmsMpans.isEmpty();

    if (afmsMpansIsDifferentToP0028ActiveMpans || p28ActiveMpansIsDifferentToAfmsMpans)
    {
      // oops some mismatch between p0028active mpans and afms mpans

      // identify only mpans in afms map
      Set<String> mpansOnlyInAfms = new HashSet<String>(afmsMpans);
      mpansOnlyInAfms.removeAll(p28ActiveMpans);

      // identify only mpans in p0028actives map
      Set<String> mpansOnlyInP28Actives = new HashSet<String>(p28ActiveMpans);
      mpansOnlyInP28Actives.removeAll(afmsMpans);

      // create error / warning data
      IterableMap<String, P0028Active> onlyInP28Actives = new HashedMap<String, P0028Active>();
      for (String p0028ActiveMpan : mpansOnlyInP28Actives)
      {
        onlyInP28Actives.put(p0028ActiveMpan, aP28ActivesMap.get(p0028ActiveMpan));
      }

      IterableMap<String, Sp04FromAFMSMpan> onlyInAfms = new HashedMap<String, Sp04FromAFMSMpan>();
      for (String afmsMpan : mpansOnlyInAfms)
      {
        onlyInAfms.put(afmsMpan, afmsMpansMap.get(afmsMpan));
      }

      return Arrays.asList(new Object[] {onlyInP28Actives, onlyInAfms});
    }

    return Collections.EMPTY_LIST;
  }

  /**
   * Validate all the P0028Data in a P0028File. Only currently active mpan
   *
   * @param aP0028File the p0028File
   * @param aSupplier the supplier
   * @param aP0028Received the P0028 File Receipt date
   * @return the p0028File upload status
   */
  UploadStatus validate(P0028File aP0028File, Supplier aSupplier)
  {
    UploadStatus status = UploadStatus.UPLOAD_OK;

    for (P0028Data p0028Data : aP0028File.getP0028Data())
    {
      if (!validatedOK(p0028Data, aSupplier))
      {
        status = UploadStatus.UPLOADED_WITH_ROW_ERRORS;
      }
    }
    return status;
  }


  /**
   * validate an individual P0028Data
   *
   * @param p0028Data
   * @param aSupplier TODO
   * @param status
   */
  boolean validatedOK(P0028Data p0028Data, Supplier aSupplier)
  {
    //check the AFMSMeter
    AFMSMeter meter = getMeter(p0028Data.getMeterSerialId());
    p0028Data.setValidated(true);

    // bug#6455 - check mpan is not currently active
    if (!isAFMSMpanActive(p0028Data))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getNoMpanForSearchCriteria());
      return p0028Data.isValidated();
    }

    if (isMeterSerialIdInvalid(meter))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getNoAfmsMeterForMeterSerialId());

      // no point checking any further if we have no meter
      return p0028Data.isValidated();
    }

    if (!isMeterMpanSameAsP0028(meter.getMpan(), p0028Data.getMpan()))
    {
      createUploadError(p0028Data, Sp04FaultReasonType.getMeterMpanDoesNotMatchP0028());
      p0028Data.setValidated(false);
    }

    //check the AFMSMpan
    AFMSMpan mpan = meter.getMpan();
    if (isMpanInvalid(mpan))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getInvalidMpan());
    }

    //if already HH then fail
    if (isAlreadyHalfHourly(mpan))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getMpanIsAlreadyHalfHourlyMetering());
    }

    //check the supplier is still the correct one
    if (!isSupplierCurrent(aSupplier, mpan, p0028Data.getP0028File()))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getSupplierIsNotCorrect());
    }

    if (!isDataCollectorOK(mpan, p0028Data.getP0028File()))
    {
      p0028Data.setValidated(false);
      createUploadError(p0028Data, Sp04FaultReasonType.getDCIsNotCorrect());
    }

    // bug#5744 - add check for MD >= configured threshold, otherwise defaults to check MD > 100kw
    validateMaxDemand(p0028Data);

    return p0028Data.isValidated();
  }

  /**
   * @param aP0028Data the P0028Data
   * @return true if an mpan is currently active based on the P0028File receipt date
   */
  boolean isAFMSMpanActive(P0028Data aP0028Data)
  {
    if (aP0028Data != null)
    {
      P0028File p28File = aP0028Data.getP0028File();

      if (p28File != null)
      {
        return mAFMSMpanDao.getAfmsMpan(aP0028Data.getMpan(), p28File.getSupplier(),
                                        p28File.getReceiptDate()) != null ? true : false;
      }
    }

    return false;
  }

  void validateMaxDemand(P0028Data aP0028Data)
  {
    if (aP0028Data.getMaxDemand() != null)
    {
      if (hasMaxDemandMatchedOrExceededThreshold(aP0028Data.getMaxDemand().floatValue()))
      {
        aP0028Data.setValidated(false);
        createUploadError(aP0028Data, Sp04FaultReasonType.getMDMatchesOrExceedsThreshold());
      }
      else if (isMaxDemandLessThanOrEqualToOneHundred(aP0028Data))
      {
        aP0028Data.setValidated(false);
        createUploadError(aP0028Data, Sp04FaultReasonType.getMDLessThanOneHundred());
      }
    }
  }

  /**
   * @param aMaxDemand the max demand value
   * @return true if the p28 reported mpan's MD >= configured threshold
   */
  boolean hasMaxDemandMatchedOrExceededThreshold(Float aMaxDemand)
  {
    if (aMaxDemand != null)
    {
      Float mdThreshold = getMaxDemandThreshold();

      if (mdThreshold != null)
      {
        if (aMaxDemand.compareTo(mdThreshold) >= 0)
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * To be correctly in the P0028 the value should be > 100
   * @param p0028Data
   * @return
   */
  boolean isMaxDemandLessThanOrEqualToOneHundred(P0028Data p0028Data)
  {
    if (p0028Data.getMaxDemand() != null)
    {
      if (p0028Data.getMaxDemand().compareTo(100L) <= 0)
      {
        return true;
      }
    }

    return false;
  }


  boolean isDataCollectorOK(AFMSMpan mpan, P0028File p0028File)
  {
    return mpan.getAgent().getDataCollector().equalsIgnoreCase(p0028File.getDcAgentName());
  }


  boolean isMeterMpanSameAsP0028(AFMSMpan meterMpan, MPANCore p0028Mpan)
  {
    if (meterMpan.getMpanCore().equals(p0028Mpan.getValue()))
    {
      return true;
    }
    return false;
  }


  void createUploadError(P0028Data p0028Data, Sp04FaultReasonType reason)
  {
    P0028UploadError uploadError = new P0028UploadError(p0028Data, reason);
    mP0028UploadErrorDao.makePersistent(uploadError);
    p0028Data.getP0028UploadError().add(uploadError);
  }

  boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, P0028File p0028File)
  {
    boolean isSupplierEffectiveToDateOK = true; /// assume OK
    if (mpan.getSupplierEffectiveToDate() != null)
    {
      isSupplierEffectiveToDateOK = mpan.getSupplierEffectiveToDate().isAfter(p0028File.getReportingPeriod().getStartOfNextMonthInPeriod());
    }

    return mpan.getSupplierId().equalsIgnoreCase(aSupplier.getSupplierId()) && isSupplierEffectiveToDateOK;
  }

  boolean isAlreadyHalfHourly(AFMSMpan mpan)
  {
    return mpan.isHalfHourly();
  }


  boolean isMpanInvalid(AFMSMpan mpan)
  {
    if (mpan == null)
    {
      return true;
    }
    return false;
  }


  boolean isMeterSerialIdInvalid(AFMSMeter meter)
  {
    if (meter == null)
    {
      return true;
    }
    return false;
  }

  AFMSMeter getMeter(String aMeterSerialId)
  {
    return mAFMSMeterDao.getLatestMeterForMeterSerialId(aMeterSerialId);
  }

  /**
   * @param aFaultReason the mpan reason for excluding from sp04 report
   * @return a message describing the fault reason
   */
  public String getFailureReasonAsText(Sp04FaultReasonType aFaultReason)
  {
    String failureReasonMsg = aFaultReason.getDescription();
    if (aFaultReason.equals(Sp04FaultReasonType.getMDMatchesOrExceedsThreshold()))
    {
      Float mdThresholdConfigured = getMaxDemandThreshold();

      if (mdThresholdConfigured != null)
      {
        failureReasonMsg = failureReasonMsg +  new BigDecimal(mdThresholdConfigured).stripTrailingZeros().toPlainString();
      }
    }

    return failureReasonMsg;
  }

  /**
   * @return the configured maximum demand threshold from a resource
   */
  public Float getMaxDemandThreshold()
  {
    String propertyPath = System.getProperty(ROOT_PATH_PROPERTY_VALUE_PROPERTY_NAME);
    Assert.notNull(propertyPath, "System property with name RootPathPropertyValue not found");
    Properties props = PropertyLoader.loadProperties(propertyPath + "/config/rascals.properties");

    String configuredMaxDemandThreshold = props != null ? props.getProperty(MAX_DEMAND_THRESHOLD_PROPERTY_NAME) : null;

    try
    {
      if (configuredMaxDemandThreshold != null)
      {
        return Float.valueOf(configuredMaxDemandThreshold);
      }
    }
    catch (NumberFormatException nfe)
    {
      // do nothing
    }

    return null;
  }

  /**
   * @param aSp04FromAFMSMpanDao the Sp04FromAFMSMpanDao
   */
  public void setSp04FromAFMSMpanDao(Sp04FromAFMSMpanDao aSp04FromAFMSMpanDao)
  {
    mSp04FromAFMSMpanDao = aSp04FromAFMSMpanDao;
  }
}

