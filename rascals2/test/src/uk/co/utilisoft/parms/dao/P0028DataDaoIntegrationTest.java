package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
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


/**
 *
 */
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = RuntimeException.class)
@SuppressWarnings("unchecked")
public class P0028DataDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required = true)
  @Qualifier("parms.p0028DataDao")
  private P0028DataDaoHibernate mP0028DataDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDaoHibernate mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  /**
   * Retrieve only P0028Data records filtered by failure reason. 
   */
  @Test
  public void testCustomHqlGetP0028DataByFailureReason()
  {
	// load some test data
	Supplier supplier = new Supplier("FRED");
	mSupplierDao.makePersistent(supplier);
	P0028File p28 = new P0028File("x_filename", "dc",  supplier, "ernie", new DateTime(),
	  new ParmsReportingPeriod(new DateTime().toDateMidnight()));

	List<P0028Data> p28Datas = new ArrayList<P0028Data>();
	MPANCore mpan = new MPANCore("1000008880003");
	P0028Data p28Data01 = new P0028Data(123L, "1111", mpan, new DateTime(), p28);
	p28Data01.setDcAgentName("bert");
	p28Datas.add(p28Data01);
	p28.setP0028Data(p28Datas);
	Long p28Pk = mP0028FileDao.makePersistent(p28).getPk();
	mP0028FileDao.getHibernateTemplate().flush();

	List<P0028Data> existingP28Datas = mP0028DataDao.getAll();
	assertEquals(1, existingP28Datas.size());
	P0028Data savedP28Data = existingP28Datas.get(0);
	assertEquals(p28Pk, savedP28Data.getP0028File().getPk());
	assertNotNull(savedP28Data);
	List<P0028UploadError> p28DataErrors = savedP28Data.getP0028UploadError();
	assertTrue(p28DataErrors.isEmpty());

	P0028UploadError p28UploadErr01 = new P0028UploadError(savedP28Data, Sp04FaultReasonType.INVALID_MPAN);
	p28UploadErr01.setLastUpdated(new DateTime());
	P0028UploadError p28UploadErr02 = new P0028UploadError(savedP28Data, Sp04FaultReasonType.DC_IS_NOT_CORRECT);
	p28UploadErr02.setLastUpdated(new DateTime());
	P0028UploadError p28UploadErr03
	  = new P0028UploadError(savedP28Data, Sp04FaultReasonType.METER_MPAN_DOES_NOT_MATCH_P0028);
	p28UploadErr03.setLastUpdated(new DateTime());
	p28DataErrors.add(p28UploadErr01);
	p28DataErrors.add(p28UploadErr02);
	p28DataErrors.add(p28UploadErr03);
	savedP28Data.setP0028UploadError(p28DataErrors);
	Long p28DataPk = mP0028DataDao.makePersistent(savedP28Data).getPk();
	mP0028DataDao.getHibernateTemplate().flush();

    String hql = "select pd from P0028Data pd left join pd.p0028UploadError pue where pue.failureReason = :sp04FailureReason";
	Query query = mP0028DataDao.getSessionFactory().getCurrentSession().createQuery(hql);
	query.setParameter("sp04FailureReason", Sp04FaultReasonType.INVALID_MPAN);
	List results = query.list();
	assertNotNull(results);
	assertEquals(1, results.size());
	P0028Data p28Data = (P0028Data) results.get(0);
	assertNotNull(p28Data);
	Boolean containsExpectedFailureReason = Boolean.FALSE;
	
	for (P0028UploadError uploadErr : p28Data.getP0028UploadError())
	{
	  if (uploadErr.getFailureReason().equals(Sp04FaultReasonType.INVALID_MPAN))
	  {
		containsExpectedFailureReason = Boolean.TRUE;
		break;
	  }
	}
	
	assertEquals(Boolean.TRUE, containsExpectedFailureReason);
  }
  
  /**
   * Test P0028Data.p0028UploadError collection mapping.
   */
  @Test
  public void testP0028DataP0028UploadErrorCollectionMapping()
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);
    P0028File p28 = new P0028File("x_filename", "dc",  supplier, "ernie", new DateTime(),
                                  new ParmsReportingPeriod(new DateTime().toDateMidnight()));

    List<P0028Data> p28Datas = new ArrayList<P0028Data>();
    MPANCore mpan = new MPANCore("1000008880003");
    P0028Data p28Data01 = new P0028Data(123L, "1111", mpan, new DateTime(), p28);
    p28Data01.setDcAgentName("bert");
    p28Datas.add(p28Data01);
    p28.setP0028Data(p28Datas);
    Long p28Pk = mP0028FileDao.makePersistent(p28).getPk();
    mP0028FileDao.getHibernateTemplate().flush();

    List<P0028Data> existingP28Datas = mP0028DataDao.getAll();
    assertEquals(1, existingP28Datas.size());
    P0028Data savedP28Data = existingP28Datas.get(0);
    assertEquals(p28Pk, savedP28Data.getP0028File().getPk());
    assertNotNull(savedP28Data);
    List<P0028UploadError> p28DataErrors = savedP28Data.getP0028UploadError();
    assertTrue(p28DataErrors.isEmpty());

    P0028UploadError p28UploadErr01 = new P0028UploadError(savedP28Data, Sp04FaultReasonType.INVALID_MPAN);
    p28UploadErr01.setLastUpdated(new DateTime());
    P0028UploadError p28UploadErr02 = new P0028UploadError(savedP28Data, Sp04FaultReasonType.DC_IS_NOT_CORRECT);
    p28UploadErr02.setLastUpdated(new DateTime());
    P0028UploadError p28UploadErr03
      = new P0028UploadError(savedP28Data, Sp04FaultReasonType.METER_MPAN_DOES_NOT_MATCH_P0028);
    p28UploadErr03.setLastUpdated(new DateTime());
    p28DataErrors.add(p28UploadErr01);
    p28DataErrors.add(p28UploadErr02);
    p28DataErrors.add(p28UploadErr03);
    savedP28Data.setP0028UploadError(p28DataErrors);
    Long p28DataPk = mP0028DataDao.makePersistent(savedP28Data).getPk();
    mP0028DataDao.getHibernateTemplate().flush();

    P0028Data existingErrP28Data = mP0028DataDao.getById(p28DataPk);
    assertNotNull(existingErrP28Data);
    assertEquals(p28DataPk, existingErrP28Data.getPk());
    List<P0028UploadError> existingUploadErrs = existingErrP28Data.getP0028UploadError();
    assertEquals(3, existingUploadErrs.size());
  }

  /**
   *
   */
  @Test
  public void testCustomHqlGetP0028DataForP0028FilePk1()
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc",  supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate, p0028File);

    //test method
    mP0028DataDao.makePersistent(p0028Data);
    assertNotNull(p0028Data.getPk());
    assertTrue(p0028Data.getPk() > 0);

    String hqlQuery = "from P0028Data pfd where mpan = :id0 and pfd.p0028File.pk = :id1 order by mpan asc";
    Long p0028FilePk = p0028Data.getP0028File().getPk();
    Session session = mP0028DataDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
    Query query = session.createQuery(hqlQuery).setParameter("id0", new MPANCore("1000000000003"))
      .setParameter("id1", p0028FilePk);
    List results = query.list();
    assertFalse(results.isEmpty());
  }

  /**
   *
   */
  @Test
  public void testCustomHqlGetP0028DataForP0028FilePk2()
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc",  supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate, p0028File);

    //test method
    mP0028DataDao.makePersistent(p0028Data);
    assertNotNull(p0028Data.getPk());
    assertTrue(p0028Data.getPk() > 0);

    String hqlQuery = "select distinct pfd from P0028Data pfd where pfd.p0028File.pk = ? order by pfd.mpan asc";
    Long p0028FilePk = p0028Data.getP0028File().getPk();
    List results = mP0028DataDao.getHibernateTemplate().find(hqlQuery, p0028FilePk);
    assertFalse(results.isEmpty());
  }

  /**
   *
   */
  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testP0028FileIsNullConstraintsValidation()
  {
    P0028Data data = new P0028Data();
    mP0028DataDao.makePersistent(data);
  }

  /**
   * @throws Exception
   */
  @Test
  public void getAll() throws Exception
  {
    List<P0028Data> all = mP0028DataDao.getAll();

    //test method
    assertTrue(all.size() == 0);
  }


  /**
   * @throws Exception
   */
  @Test
  public void createAndSaveP0028Data() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc",  supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate, p0028File);

    //test method
    mP0028DataDao.makePersistent(p0028Data);
    assertNotNull(p0028Data.getPk());
    assertTrue(p0028Data.getPk() > 0);

    P0028Data fetchP0028Data = mP0028DataDao.getById(p0028Data.getPk());
    assertNotNull(fetchP0028Data);
    assertEquals(p0028Data.getPk(), fetchP0028Data.getPk());
    assertEquals(p0028Data.getMaxDemand(), fetchP0028Data.getMaxDemand());
    assertEquals(p0028Data.getMeterSerialId(), fetchP0028Data.getMeterSerialId());
    assertEquals(p0028Data.getMpan(), fetchP0028Data.getMpan());
    assertEquals(p0028Data.getP0028File(), fetchP0028Data.getP0028File());
    assertEquals(p0028Data.getReadingDate(), fetchP0028Data.getReadingDate());
  }

  /**
   * @throws Exception
   */
  @Test
  public void gettingDatafromFile() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc", supplier, "bert", receiptDate, period);
    MPANCore mpan = new MPANCore("1000000000003");

    DateTime readingDate = new DateTime();
    P0028Data p0028Data = new P0028Data(123L, "45678", mpan, readingDate, p0028File);

    //mP0028DataDao.makePersistent(p0028Data);
    p0028File.getP0028Data().add(p0028Data);
    mP0028FileDao.makePersistent(p0028File);

    P0028File fetchedP0028File = mP0028FileDao.getById(p0028File.getPk());

    List<P0028Data> p0028DataSet =  fetchedP0028File.getP0028Data();
    assertNotNull("oops - not found any P0028Data", p0028DataSet);
    assertTrue("should be one P0028Data", p0028DataSet.size() == 1);
  }
}
