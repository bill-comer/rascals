package uk.co.utilisoft.parmsmop.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.afms.dao.AfmsGenericDao;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;


@Repository("parms.mopReportDao")
public class ParmsMopReportDaoImpl  extends AfmsGenericDao<ParmsMopReport, Long>  implements ParmsMopReportDao
{

  @Override
  @SuppressWarnings("unchecked")
  public List<ParmsMopReport> getAllMopReports()
  {
    return getAll();
  }

  @Override
  public List<ParmsReportSummary> getDataForReport(Long aId)
  {
    ParmsMopReport report = getById(aId);
    return report.getParmsReportSummary();
  }
  
  public String getParticipant()
  {
    //TODO - I think this might be sqlServer specific
    String participant = (String) getSession().createSQLQuery("SELECT cast([MOP_MPID] as varchar) from System_Details").uniqueResult();
    
    return participant;
  }

}
