package uk.co.utilisoft.parmsmop.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public interface ParmsMopReportDao  extends UtilisoftGenericDao<ParmsMopReport, Long>
{
  public List<ParmsMopReport> getAllMopReports();
  
  public List<ParmsReportSummary> getDataForReport(Long aId);
  
  public String getParticipant();
}
