package uk.co.utilisoft.parmsmop.dao;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReportData;
import uk.co.utilisoft.parmsmop.domain.ParmsMopSourceData;

public interface ParmsMopSourceDataDao  extends UtilisoftGenericDao<ParmsMopSourceData, BigInteger>
{

  List<ParmsMopSourceData> getMopReportSourceData(final String aSerial, final DateTime aStartDate, final DateTime aEndDate, ParmsMopReport aParmsMopReport);
}
