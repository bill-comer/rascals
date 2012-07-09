package uk.co.utilisoft.parms.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.Supplier;

import static junit.framework.Assert.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class DpiFileDataDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.dpiFileDataDao")
  private DpiFileDataDaoHibernate mDpiFileDataDao;

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testDpiFileDataDataIsNullConstraintsValidation()
  {
    DpiFileData dfData = new DpiFileData(null, null);
    mDpiFileDataDao.makePersistent(dfData);
  }

  @Test
  public void testSaveDpiFileDataSuccess()
  {
    String dpiHeader = "ZHD|P0135001|X|OVOE|Z|POOL|20100929165434\n\r";
    String dpiRecord = "DPI|_C|SP04|OVOE|X|20100831\n\rDPI|_D|SP04|OVOE|X|20100831\n\rDPI|_E|SP04|OVOE|X|20100831\n\r"
      + "DPI|_F|SP04|OVOE|X|20100831\n\rDPI|_G|SP04|OVOE|X|20100831\n\rDPI|_H|SP04|OVOE|X|20100831\n\r"
      + "DPI|_J|SP04|OVOE|X|20100831\n\rDPI|_K|SP04|OVOE|X|20100831\n\rDPI|_L|SP04|OVOE|X|20100831\n\r"
      + "DPI|_M|SP04|OVOE|X|20100831\n\rDPI|_N|SP04|OVOE|X|20100831\n\rDPI|_P|SP04|OVOE|X|20100831\n\r"
      + "DPI|_A|SP05|LBSL|D|20100831\n\rDPI|_B|SP05|LBSL|D|20100831\n\rDPI|_C|SP05|LBSL|D|20100831\n\r"
      + "DPI|_D|SP05|LBSL|D|20100831\n\rDPI|_E|SP05|LBSL|D|20100831\n\rDPI|_F|SP05|LBSL|D|20100831\n\r"
      + "DPI|_G|SP05|LBSL|D|20100831\n\rDPI|_H|SP05|LBSL|D|20100831\n\rDPI|_J|SP05|LBSL|D|20100831\n\r"
      + "DPI|_K|SP05|LBSL|D|20100831\n\rDPI|_L|SP05|LBSL|D|20100831\n\rDPI|_M|SP05|LBSL|D|20100831\n\r"
      + "DPI|_N|SP05|LBSL|D|20100831\n\rDPI|_B|SP05|SPOW|C|20100831\n\rDPI|_D|SP05|SPOW|C|20100831\n\r"
      + "DPI|_F|SP05|SPOW|C|20100831\n\rDPI|_H|SP05|SPOW|C|20100831\n\rDPI|_P|SP05|NEEB|C|20100831\n\r"
      + "DPI|_A|SP06|LBSL|D|20100831\n\rDPI|_B|SP06|LBSL|D|20100831\n\rDPI|_C|SP06|LBSL|D|20100831\n\r"
      + "DPI|_D|SP06|LBSL|D|20100831\n\rDPI|_E|SP06|LBSL|D|20100831\n\rDPI|_F|SP06|LBSL|D|20100831\n\r"
      + "DPI|_G|SP06|LBSL|D|20100831\n\rDPI|_H|SP06|LBSL|D|20100831\n\rDPI|_J|SP06|LBSL|D|20100831\n\r"
      + "DPI|_K|SP06|LBSL|D|20100831\n\rDPI|_L|SP06|LBSL|D|20100831\n\rDPI|_M|SP06|LBSL|D|20100831\n\r"
      + "DPI|_N|SP06|LBSL|D|20100831\n\rDPI|_B|SP06|SPOW|C|20100831\n\rDPI|_D|SP06|SPOW|C|20100831\n\r"
      + "DPI|_F|SP06|SPOW|C|20100831\n\rDPI|_H|SP06|SPOW|C|20100831\n\rDPI|_P|SP06|NEEB|C|20100831\n\r"
      + "DPI|_A|NC02|LBSL|D|20100831\n\rDPI|_B|NC02|LBSL|D|20100831\n\rDPI|_C|NC02|LBSL|D|20100831\n\r"
      + "DPI|_D|NC02|LBSL|D|20100831\n\rDPI|_E|NC02|LBSL|D|20100831\n\rDPI|_F|NC02|LBSL|D|20100831\n\r"
      + "DPI|_G|NC02|LBSL|D|20100831\n\rDPI|_H|NC02|LBSL|D|20100831\n\rDPI|_J|NC02|LBSL|D|20100831\n\r"
      + "DPI|_K|NC02|LBSL|D|20100831\n\rDPI|_L|NC02|LBSL|D|20100831\n\rDPI|_M|NC02|LBSL|D|20100831\n\r"
      + "DPI|_N|NC02|LBSL|D|20100831\n\rDPI|_A|NC03|LBSL|D|20100831\n\rDPI|_B|NC03|LBSL|D|20100831\n\r"
      + "DPI|_C|NC03|LBSL|D|20100831\n\rDPI|_D|NC03|LBSL|D|20100831\n\rDPI|_E|NC03|LBSL|D|20100831\n\r"
      + "DPI|_F|NC03|LBSL|D|20100831\n\rDPI|_G|NC03|LBSL|D|20100831\n\rDPI|_H|NC03|LBSL|D|20100831\n\r"
      + "DPI|_J|NC03|LBSL|D|20100831\n\rDPI|_K|NC03|LBSL|D|20100831\n\rDPI|_L|NC03|LBSL|D|20100831\n\r"
      + "DPI|_M|NC03|LBSL|D|20100831\n\rDPI|_N|NC03|LBSL|D|20100831\n\rDPI|_B|HC02|SPOW|C|20100831\n\r"
      + "DPI|_D|HC02|SPOW|C|20100831\n\rDPI|_F|HC02|SPOW|C|20100831\n\rDPI|_H|HC02|SPOW|C|20100831\n\r"
      + "DPI|_P|HC02|NEEB|C|20100831\n\rDPI|_A|NM01|LBSL|D|20100831\n\rDPI|_B|NM01|LBSL|D|20100831\n\r"
      + "DPI|_C|NM01|LBSL|D|20100831\n\rDPI|_D|NM01|LBSL|D|20100831\n\rDPI|_E|NM01|LBSL|D|20100831\n\r"
      + "DPI|_F|NM01|LBSL|D|20100831\n\rDPI|_G|NM01|LBSL|D|20100831\n\rDPI|_H|NM01|LBSL|D|20100831\n\r"
      + "DPI|_J|NM01|LBSL|D|20100831\n\rDPI|_K|NM01|LBSL|D|20100831\n\rDPI|_L|NM01|LBSL|D|20100831\n\r"
      + "DPI|_M|NM01|LBSL|D|20100831\n\rDPI|_N|NM01|LBSL|D|20100831\n\rDPI|_A|NM02|LBSL|D|20100831\n\r"
      + "DPI|_B|NM02|LBSL|D|20100831\n\rDPI|_C|NM02|LBSL|D|20100831\n\rDPI|_D|NM02|LBSL|D|20100831\n\r"
      + "DPI|_E|NM02|LBSL|D|20100831\n\rDPI|_F|NM02|LBSL|D|20100831\n\rDPI|_G|NM02|LBSL|D|20100831\n\r"
      + "DPI|_H|NM02|LBSL|D|20100831\n\rDPI|_J|NM02|LBSL|D|20100831\n\rDPI|_K|NM02|LBSL|D|20100831\n\r"
      + "DPI|_L|NM02|LBSL|D|20100831\n\rDPI|_M|NM02|LBSL|D|20100831\n\rDPI|_N|NM02|LBSL|D|20100831\n\r"
      + "DPI|_B|HM01|SPOW|C|20100831\n\rDPI|_D|HM01|SPOW|C|20100831\n\rDPI|_F|HM01|SPOW|C|20100831\n\r"
      + "DPI|_H|HM01|SPOW|C|20100831\n\rDPI|_P|HM01|NEEB|C|20100831\n\rDPI|_B|HM02|SPOW|C|20100831\n\r"
      + "DPI|_D|HM02|SPOW|C|20100831\n\rDPI|_F|HM02|SPOW|C|20100831\n\rDPI|_H|HM02|SPOW|C|20100831\n\r"
      + "DPI|_P|HM02|NEEB|C|20100831\n\rDPI|_B|HM03|SPOW|C|20100831\n\rDPI|_D|HM03|SPOW|C|20100831\n\r"
      + "DPI|_F|HM03|SPOW|C|20100831\n\rDPI|_H|HM03|SPOW|C|20100831\n\rDPI|_P|HM03|NEEB|C|20100831\n\r"
      + "DPI|_B|HM06|SPOW|C|20100831\n\rDPI|_D|HM06|SPOW|C|20100831\n\rDPI|_F|HM06|SPOW|C|20100831\n\r"
      + "DPI|_H|HM06|SPOW|C|20100831\n\rDPI|_P|HM06|NEEB|C|20100831\n\rDPI|_A|SP05|LBSL|M|20100831\n\r"
      + "DPI|_B|SP05|LBSL|M|20100831\n\rDPI|_B|SP05|NATP|M|20100831\n\rDPI|_C|SP05|LBSL|M|20100831\n\r"
      + "DPI|_D|SP05|LBSL|M|20100831\n\rDPI|_D|SP05|EELC|M|20100831\n\rDPI|_E|SP05|LBSL|M|20100831\n\r"
      + "DPI|_F|SP05|LBSL|M|20100831\n\rDPI|_F|SP05|NATP|M|20100831\n\rDPI|_G|SP05|LBSL|M|20100831\n\r"
      + "DPI|_G|SP05|NORW|M|20100831\n\rDPI|_H|SP05|LBSL|M|20100831\n\rDPI|_H|SP05|EELC|M|20100831\n\r"
      + "DPI|_H|SP05|NATP|M|20100831\n\rDPI|_J|SP05|LBSL|M|20100831\n\rDPI|_K|SP05|LBSL|M|20100831\n\r"
      + "DPI|_L|SP05|LBSL|M|20100831\n\rDPI|_M|SP05|LBSL|M|20100831\n\rDPI|_N|SP05|SPOW|M|20100831\n\r"
      + "DPI|_P|SP05|SOUT|M|20100831\n\rDPI|_A|SP06|LBSL|M|20100831\n\rDPI|_B|SP06|LBSL|M|20100831\n\r"
      + "DPI|_B|SP06|NATP|M|20100831\n\rDPI|_C|SP06|LBSL|M|20100831\n\rDPI|_D|SP06|LBSL|M|20100831\n\r"
      + "DPI|_D|SP06|EELC|M|20100831\n\rDPI|_E|SP06|LBSL|M|20100831\n\rDPI|_F|SP06|LBSL|M|20100831\n\r"
      + "DPI|_F|SP06|NATP|M|20100831\n\rDPI|_G|SP06|LBSL|M|20100831\n\rDPI|_G|SP06|NORW|M|20100831\n\r"
      + "DPI|_H|SP06|LBSL|M|20100831\n\rDPI|_H|SP06|EELC|M|20100831\n\rDPI|_H|SP06|NATP|M|20100831\n\r"
      + "DPI|_J|SP06|LBSL|M|20100831\n\rDPI|_K|SP06|LBSL|M|20100831\n\rDPI|_L|SP06|LBSL|M|20100831\n\r"
      + "DPI|_M|SP06|LBSL|M|20100831\n\rDPI|_N|SP06|SPOW|M|20100831\n\rDPI|_P|SP06|SOUT|M|20100831\n\r"
      + "DPI|_A|NM03|LBSL|M|20100831\n\rDPI|_B|NM03|LBSL|M|20100831\n\rDPI|_C|NM03|LBSL|M|20100831\n\r"
      + "DPI|_D|NM03|LBSL|M|20100831\n\rDPI|_E|NM03|LBSL|M|20100831\n\rDPI|_F|NM03|LBSL|M|20100831\n\r"
      + "DPI|_G|NM03|LBSL|M|20100831\n\rDPI|_G|NM03|NORW|M|20100831\n\rDPI|_H|NM03|LBSL|M|20100831\n\r"
      + "DPI|_J|NM03|LBSL|M|20100831\n\rDPI|_K|NM03|LBSL|M|20100831\n\rDPI|_L|NM03|LBSL|M|20100831\n\r"
      + "DPI|_M|NM03|LBSL|M|20100831\n\rDPI|_N|NM03|LBSL|M|20100831\n\rDPI|_N|NM03|SPOW|M|20100831\n\r"
      + "DPI|_A|NM04|LBSL|M|20100831\n\rDPI|_B|NM04|LBSL|M|20100831\n\rDPI|_C|NM04|LBSL|M|20100831\n\r"
      + "DPI|_D|NM04|LBSL|M|20100831\n\rDPI|_E|NM04|LBSL|M|20100831\n\rDPI|_F|NM04|LBSL|M|20100831\n\r"
      + "DPI|_G|NM04|LBSL|M|20100831\n\rDPI|_G|NM04|NORW|M|20100831\n\rDPI|_H|NM04|LBSL|M|20100831\n\r"
      + "DPI|_J|NM04|LBSL|M|20100831\n\rDPI|_K|NM04|LBSL|M|20100831\n\rDPI|_L|NM04|LBSL|M|20100831\n\r"
      + "DPI|_M|NM04|LBSL|M|20100831\n\rDPI|_N|NM04|LBSL|M|20100831\n\rDPI|_N|NM04|SPOW|M|20100831\n\r"
      + "DPI|_B|HM04|NATP|M|20100831\n\rDPI|_D|HM04|EELC|M|20100831\n\rDPI|_F|HM04|NATP|M|20100831\n\r"
      + "DPI|_H|HM04|EELC|M|20100831\n\rDPI|_H|HM04|NATP|M|20100831\n\rDPI|_P|HM04|SOUT|M|20100831\n\r"
      + "DPI|_B|HM05|NATP|M|20100831\n\rDPI|_D|HM05|EELC|M|20100831\n\rDPI|_F|HM05|NATP|M|20100831\n\r"
      + "DPI|_H|HM05|EELC|M|20100831\n\rDPI|_H|HM05|NATP|M|20100831\n\rDPI|_P|HM05|SOUT|M|20100831\n\r";
    String dpiFooter = "ZPT|211|1769165066";
    List<String> dfdRows = new ArrayList<String>();
    dfdRows.add(dpiHeader);
    dfdRows.add(dpiRecord);
    dfdRows.add(dpiFooter);

    Serializable dpiFilePk = getPersistedObjectPks().get(DpiFile.class).get(0);
    DpiFile dpiFile = (DpiFile) mDpiFileDataDao.getHibernateTemplate().get(DpiFile.class, dpiFilePk);
    assertNotNull(dpiFile);
    DpiFileData dfData = new DpiFileData(dfdRows.toString(), dpiFile);
    mDpiFileDataDao.makePersistent(dfData);
    assertNotNull(dfData.getPk());
  }

  @Test
  public void getByDpiFilePkFail()
  {
    Long dpiFilePk = 6489L;
    List<Serializable> existDpiFilePks = getPersistedObjectPks().get(DpiFileData.class);
    assertNotNull(existDpiFilePks);
    assertFalse(existDpiFilePks.isEmpty());
    assertFalse(existDpiFilePks.contains(dpiFilePk));
    List<DpiFileData> dfData = mDpiFileDataDao.getByDpiFilePk(dpiFilePk);
    assertTrue(dfData.isEmpty());
  }

  @BeforeTransaction
  public void init()
  {
    Supplier supplier = new Supplier("SUP1");
    List<Supplier> suppliers = new ArrayList<Supplier>();
    supplier.setLastUpdated(new DateTime());
    suppliers.add(supplier);
    insertTestData(suppliers, mDpiFileDataDao, Supplier.class);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile();
    List<DpiFile> dpiFiles = new ArrayList<DpiFile>();
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    dpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    dpiFile.setSupplier(supplier);
    dpiFiles.add(dpiFile);
    insertTestData(dpiFiles, mDpiFileDataDao, DpiFile.class);
    assertNotNull(dpiFile.getPk());

    DpiFileData dfData = new DpiFileData("***test dpi file data clob***", dpiFile);
    List<DpiFileData> dfDatas = new ArrayList<DpiFileData>();
    dfDatas.add(dfData);
    insertTestData(dfDatas, mDpiFileDataDao, DpiFileData.class);
    assertNotNull(dfData.getPk());
  }

  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mDpiFileDataDao, DpiFileData.class);
    deleteTestData(mDpiFileDataDao, DpiFile.class);
    deleteTestData(mDpiFileDataDao, Supplier.class);
  }
}