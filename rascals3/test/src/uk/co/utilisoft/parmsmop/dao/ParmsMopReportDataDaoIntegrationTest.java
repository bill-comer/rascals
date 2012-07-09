package uk.co.utilisoft.parmsmop.dao;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReportData;

public class ParmsMopReportDataDaoIntegrationTest extends BaseDaoIntegrationTest
{

  @Autowired(required = true)
  @Qualifier("parms.mopReportDataDao")
  private ParmsMopReportDataDaoImpl mParmsMopReportDataDao;

  @Test
  public void test_getById() throws Exception
  {
    ParmsMopReportData row = mParmsMopReportDataDao.getById(new BigInteger("596273"));
    assertNotNull("oops - no row found", row);
  }
  

}