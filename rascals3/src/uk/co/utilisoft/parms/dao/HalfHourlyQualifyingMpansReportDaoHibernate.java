package uk.co.utilisoft.parms.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.HalfHourlyQualifyingMpansReport;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.halfHourlyQualifyingMpanReportDao")
public class HalfHourlyQualifyingMpansReportDaoHibernate extends
    ParmsGenericDao<HalfHourlyQualifyingMpansReport, Long> implements HalfHourlyQualifyingMpansReportDao
{

}