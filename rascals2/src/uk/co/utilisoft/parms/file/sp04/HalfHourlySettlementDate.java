package uk.co.utilisoft.parms.file.sp04;

import org.hibernate.collection.PersistentBag;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;

/**
 * This is the SettlementDate for an MPAN that has been converted to HalfHourly metering.
 * @author comerb
 *
 */

@Service("parms.halfHourlySettlementDate")
public class HalfHourlySettlementDate
{
  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  /**
   * The Date that the MPAN was converted to HalfHourly metering.
   * Null if it has not been done.
   * @param meter
   * @return
   */
  public DateTime getHalfHourlySettlementDate(AFMSMpan mpan)
  {
    loadAFMSMeters(mpan);

    AFMSMeter meter1 = mpan != null ? mpan.getFirstCreatedHalfHourlyMeter() : null;

    if (meter1 == null || !meter1.isHalfHourlyMeter())
    {
      return null;
    }

    AFMSAregiProcess aregiProcess = mpan.getLatestAregiProcess();

    if (aregiProcess != null)
    {
      if (isD0268DateSameAsMeterLastUpdOrGreater(meter1.getLastUpdated(), aregiProcess.getLatestD0268ReceiptDate()))
      {
        return meter1.getSettlementDate();
      }
    }

    return null;

  }

  private void loadAFMSMeters(AFMSMpan aMpan)
  {
    if (aMpan != null && aMpan.getMeters() instanceof PersistentBag)
    {
      aMpan.setMeters(mAFMSMeterDao.getByMpanUniqueId(aMpan.getPk()));
    }
  }


  /**
   * returns true if both these dates are for the same day
   * @param lastUpdated
   * @param latestD0268ReceiptDate
   * @return
   */
  boolean isD0268DateSameAsMeterLastUpdOrGreater(DateTime lastUpdated, DateTime latestD0268ReceiptDate) {

    DateMidnight meterLastUpdated = new DateMidnight(lastUpdated);
    DateMidnight latestD0268Receipt = new DateMidnight(latestD0268ReceiptDate);

    if (meterLastUpdated.isEqual(latestD0268Receipt) || meterLastUpdated.isAfter(latestD0268Receipt)){
      return true;
    }

    return false;
  }

  /**
   * @param aAFMSMeterDao the AFMS Meter dao
   */
  public void setAFMSMeterDao(AFMSMeterDao aAFMSMeterDao)
  {
    mAFMSMeterDao = aAFMSMeterDao;
  }
}
