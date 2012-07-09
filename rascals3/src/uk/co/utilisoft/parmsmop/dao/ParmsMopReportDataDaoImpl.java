package uk.co.utilisoft.parmsmop.dao;

import java.math.BigInteger;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReportData;

@Repository("parms.mopReportDataDao")
public class ParmsMopReportDataDaoImpl  extends MOPGenericDao<ParmsMopReportData, BigInteger> implements ParmsMopReportDataDao<ParmsMopReportData>
{
}
