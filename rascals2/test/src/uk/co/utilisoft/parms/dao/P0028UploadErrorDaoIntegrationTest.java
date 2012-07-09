package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028UploadError;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class P0028UploadErrorDaoIntegrationTest extends BaseDaoIntegrationTest
{

  @Autowired(required=true)
  @Qualifier("parms.p0028UploadErrorDao")
  P0028UploadErrorDao mP0028UploadErrorDao;
  
  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDaoHibernate mP0028FileDao;
  
  @Test
  public void createOneUploadError() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc", supplier, "bert",
        receiptDate, period);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate,
        p0028File);

    // mP0028DataDao.makePersistent(p0028Data);
    p0028File.getP0028Data().add(p0028Data);
    mP0028FileDao.makePersistent(p0028File);

    P0028UploadError uploadError = new P0028UploadError(p0028Data, Sp04FaultReasonType.getNoAfmsMeterForMeterSerialId());
    mP0028UploadErrorDao.makePersistent(uploadError);
    
    P0028UploadError fetchedP0028UploadError = mP0028UploadErrorDao.getById(uploadError.getPk());

    assertNotNull(fetchedP0028UploadError);
    assertEquals(uploadError.getP0028Data().getPk(), fetchedP0028UploadError.getP0028Data().getPk());
    assertEquals(uploadError.getFailureReason(), fetchedP0028UploadError.getFailureReason());

  }
  

  @Test
  public void createTwoUploadErrorsOnOneP0028Data() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc", supplier, "bert",
        receiptDate, period);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate,
        p0028File);

    // mP0028DataDao.makePersistent(p0028Data);
    p0028File.getP0028Data().add(p0028Data);
    mP0028FileDao.makePersistent(p0028File);

    P0028UploadError uploadError1 = new P0028UploadError(p0028Data, Sp04FaultReasonType.getNoAfmsMeterForMeterSerialId());
    mP0028UploadErrorDao.makePersistent(uploadError1);
    
    P0028UploadError uploadError2 = new P0028UploadError(p0028Data, Sp04FaultReasonType.getMpanIsAlreadyHalfHourlyMetering());
    mP0028UploadErrorDao.makePersistent(uploadError2);
    
    P0028UploadError fetchedP0028UploadError1 = mP0028UploadErrorDao.getById(uploadError1.getPk());

    assertNotNull(fetchedP0028UploadError1);
    assertEquals(uploadError1.getP0028Data().getPk(), fetchedP0028UploadError1.getP0028Data().getPk());
    assertEquals(uploadError1.getFailureReason(), fetchedP0028UploadError1.getFailureReason());
    
    
    P0028UploadError fetchedP0028UploadError2 = mP0028UploadErrorDao.getById(uploadError2.getPk());

    assertNotNull(fetchedP0028UploadError2);
    assertEquals(uploadError2.getP0028Data().getPk(), fetchedP0028UploadError2.getP0028Data().getPk());
    assertEquals(uploadError2.getFailureReason(), fetchedP0028UploadError2.getFailureReason());

  }

}
