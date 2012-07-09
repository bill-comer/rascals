package uk.co.utilisoft.afms.dao;

import java.util.Collection;

import org.joda.time.DateMidnight;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;

public interface AFMSMeterDao extends UtilisoftGenericDao<AFMSMeter, Long>
{
  AFMSMeter getLatestMeterForMpanUniqId(Long aMpanUniqId);

  AFMSMeter getLatestMeterForMeterSerialId(String aMeterSerialId);

  AFMSMeter getLatestMeterForMeterSerialIdAndMpanUniqId(String aMeterSerialId, Long aMpanUniqId);

  /**
   * @param aMpanUniqueId the Mpan link id
   * @return the AFMSMeter records for an Mpan link id
   */
  Collection<AFMSMeter> getByMpanUniqueId(final Long aMpanUniqueId);

  Collection<AFMSMeter> getNonHalfHourlyMetersForPeriod(AFMSMpan aAfmsMpan, DateMidnight startOfNextMonthInPeriod);
}
