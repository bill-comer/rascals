package uk.co.utilisoft.parmsmop.dao;

import java.math.BigInteger;
import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public interface ParmsReportSummaryDao<T> extends UtilisoftGenericDao<ParmsReportSummary, BigInteger>
{
  List<T> getSp11DataForStandard1(final ParmsMopReport aReport);

  List<T> getSp11ActiveSuppliers(final ParmsMopReport aReport);
  
  List<T> getSp11MissingActiveSuppliersForNHH(final ParmsMopReport aReport);

  List<T> getSp11MissingActiveSuppliersForHH(final ParmsMopReport aReport);
}
