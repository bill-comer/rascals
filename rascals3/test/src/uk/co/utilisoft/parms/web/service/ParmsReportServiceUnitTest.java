package uk.co.utilisoft.parms.web.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.DpiFileDaoHibernate;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import static uk.co.utilisoft.parms.util.DateUtil.*;
import static uk.co.utilisoft.parms.web.controller.WebConstants.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class ParmsReportServiceUnitTest
{
  private DpiFileDaoHibernate mDpiFileDao;
  private DpiReportServiceImpl mParmsReportService;

  @Test
  public void getAllSortedRecordsForNoRecordsFound()
  {
    expect(mDpiFileDao.getAll()).andReturn(new ArrayList<DpiFile>()).once();
    replay(mDpiFileDao);
    List<AdminListDTO> records =  mParmsReportService.getAllSortedRecords();
    verify(mDpiFileDao);
    assertTrue(records.isEmpty());
  }

  @Test
  public void getAllSortedRecordsForRecordsFound()
  {
    List<DpiFile> expRecords = new ArrayList<DpiFile>();
    DateTime created = new DateTime();
    String fileName = "OVOE1351.JAN";
    Long pk = 3658L;
    Long version = 0L;
    ParmsReportingPeriod endPeriod = new ParmsReportingPeriod(new DateMidnight(2011, 1, 31));
    String id = "OVOE";
    Supplier supplier = new Supplier(id);
    supplier.setLastUpdated(created);
    supplier.setPk(pk);
    supplier.setVersion(version);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setDateCreated(created);
    dpiFile.setFileName(fileName);
    dpiFile.setLastUpdated(created);
    dpiFile.setPk(pk);
    dpiFile.setSupplier(supplier);
    dpiFile.setVersion(version);
    dpiFile.setReportingPeriod(endPeriod);
    expRecords.add(dpiFile);
    expect(mDpiFileDao.getAll()).andReturn(expRecords).once();
    replay(mDpiFileDao);
    List<AdminListDTO> records =  mParmsReportService.getAllSortedRecords();
    verify(mDpiFileDao);
    assertTrue(!records.isEmpty() && records.size() == 1);

    AdminListDTO dto = records.get(0);
    List<Object> pkObj = dto.getIdentifier();
    assertNotNull(pkObj);
    List<Object> valuesObj = dto.getValues();
    assertNotNull(valuesObj);

    assertEquals((Long) pkObj.get(0), pk);

    for (int i = 0; i < valuesObj.size(); i++)
    {
      assertTrue(valuesObj.get(0) instanceof String);
    }

    String endPeriodResult = (String) valuesObj.get(0);
    assertEquals(formatLongDate(MONTH_YEAR_DATE_FORMAT, endPeriod.getStartOfNextMonthInPeriod()), endPeriodResult);
    String createdResult = (String) valuesObj.get(1);
    assertEquals(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, created), createdResult);
    String fileNameResult = (String) valuesObj.get(2);
    assertEquals(fileName, fileNameResult);
  }

  @Before
  public void setup()
  {
    mDpiFileDao = createMock(DpiFileDaoHibernate.class);
    mParmsReportService = new DpiReportServiceImpl();
    mParmsReportService.setDpiFileDao(mDpiFileDao);
  }
}
