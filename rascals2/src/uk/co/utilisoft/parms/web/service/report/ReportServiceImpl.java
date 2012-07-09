package uk.co.utilisoft.parms.web.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.IterableMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.dao.HalfHourlyQualifyingMpansReportDao;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.HalfHourlyQualifyingMpansReport;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.file.sp04.AFMSMeterRegReadingSortByReadingReverse;
import uk.co.utilisoft.parms.web.dto.HalfHourlyQualifyingMpanDTO;
import uk.co.utilisoft.parms.web.util.ResponseOutputStreamWriter;

import static ch.lambdaj.Lambda.sort;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.Matchers.equalTo;

import static uk.co.utilisoft.parms.domain.HalfHourlyQualifyingMpansReport.HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER;
import static uk.co.utilisoft.parms.domain.HalfHourlyQualifyingMpansReport.P0028_HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER;


/**
 * @author Philip Lau
 * @version 1.0
 */
@Service("parms.reportService")
public class ReportServiceImpl implements ReportService
{
  @Autowired(required = true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  private Sp04FromAFMSMpanDao mSp04FromAfmsMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.halfHourlyQualifyingMpanReportDao")
  private HalfHourlyQualifyingMpansReportDao mHalfHourlyQualifyingMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028ActiveDao")
  private P0028ActiveDao mP0028ActiveDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDao mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDao mAFMSMpanDao;

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.report.ReportService#downloadHalfHourlyQualifyingMpansReport(java.lang.Long,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public String downloadHalfHourlyQualifyingMpansReport(Long aSupplierPk, HttpServletResponse aResponse) throws IOException
  {
    DateTime now = new DateTime();
    DateTimeFormatter fileNameDateTimefmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");
    String fileName = "halfHourlyQualifyingMpans." + now.toString(fileNameDateTimefmt) + ".csv";

    DateTimeFormatter ddMMyyyyHHmmssFmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    // afms database hh qualified mpan data
    List<HalfHourlyQualifyingMpanDTO> afmsHHQualifiedMpanDtos
      = transformSp04FromAFMSMpanData(mSp04FromAfmsMpanDao.getAll(aSupplierPk), ddMMyyyyHHmmssFmt);
    String afmsMpanData = asCommaSeparatedHHQualifiedMpans(
      createHeader("DATABASE", HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER), afmsHHQualifiedMpanDtos);

    // p28 hh qualified mpan data
    IterableMap<String, P0028Active> p28Actives = mP0028ActiveDao.getAllForSupplier(mSupplierDao.getById(aSupplierPk));
    List<HalfHourlyQualifyingMpanDTO> p28HHQualifiedMpanDtos
      = transformP28ActiveData(p28Actives.values(), aSupplierPk, ddMMyyyyHHmmssFmt);
    String p28MpanData = asCommaSeparatedP28HHQualifiedMpans(
      createHeader("P0028", P0028_HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER), p28HHQualifiedMpanDtos);

    // combine afms database and p28 hh qualified mpan data
    String fmtData = new StringBuffer().append(afmsMpanData).append(p28MpanData).toString();

    if (org.apache.commons.lang.StringUtils.isNotBlank(fmtData))
    {
      ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
      writer.writeCsvFileToResponseOutputStream(aResponse, fmtData, fileName);

      // save report
      saveHalfHourlyQualifyingMpanReport(fileName, fmtData, now);
      return fileName;
    }

    return null;
  }

  public void saveHalfHourlyQualifyingMpanReport(String aFileName, String aReportData, DateTime aCurrentDate)
  {
    HalfHourlyQualifyingMpansReport hhQualifyingMpanReport = new HalfHourlyQualifyingMpansReport();
    hhQualifyingMpanReport.setDateCreated(aCurrentDate);
    hhQualifyingMpanReport.setData(aReportData);
    hhQualifyingMpanReport.setFileName(aFileName);
    mHalfHourlyQualifyingMpanDao.makePersistent(hhQualifyingMpanReport);
  }

  /**
   * @param aTitle the title description
   * @param aColumnHeaders the column headers
   * @return the title with column headers
   */
  protected String createHeader(String aTitle, String ... aColumnHeaders)
  {
    StringBuffer data = new StringBuffer();
    String lineSeparator = System.getProperty("line.separator");

    if (org.apache.commons.lang.StringUtils.isNotBlank(aTitle))
    {
      data.append("[" + aTitle + "]");
      data.append(lineSeparator);
      data.append(lineSeparator);
    }

    String headers = null;

    if (aColumnHeaders != null && aColumnHeaders.length > 0)
    {
      headers = StringUtils.arrayToCommaDelimitedString(aColumnHeaders);
    }

    if (org.apache.commons.lang.StringUtils.isNotBlank(headers))
    {
      data.append(headers);
      data.append(lineSeparator);
    }

    return data.toString();
  }

  /**
   * @param aHHQualifiedMpans the mpans qualifying for half hourly metering
   * @return the data as text for mpans qualifying for half hourly metering
   */
  protected String asCommaSeparatedHHQualifiedMpans(String aHeader, List<HalfHourlyQualifyingMpanDTO> aHHQualifiedMpans)
  {
    StringBuffer data = new StringBuffer();
    String lineSeparator = System.getProperty("line.separator");
    String commaSeparator = ", ";

    if (org.apache.commons.lang.StringUtils.isNotBlank(aHeader))
    {
      data.append(aHeader);
    }

    for (HalfHourlyQualifyingMpanDTO hhQualifiedMpan : aHHQualifiedMpans)
    {
      data.append(hhQualifiedMpan.getNHHDCId() != null ? hhQualifiedMpan.getNHHDCId() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMpan() != null ? hhQualifiedMpan.getMpan() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterSerialId() != null ? hhQualifiedMpan.getMeterSerialId() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterInstallationDeadline() != null ? hhQualifiedMpan.getMeterInstallationDeadline() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMaximumDemand() != null ? hhQualifiedMpan.getMaximumDemand() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReadingDate1() != null ? hhQualifiedMpan.getMeterReadingDate1() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReading1() != null ? hhQualifiedMpan.getMeterReading1() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReadingDate2() != null ? hhQualifiedMpan.getMeterReadingDate2() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReading2() != null ? hhQualifiedMpan.getMeterReading2() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReadingDate3() != null ? hhQualifiedMpan.getMeterReadingDate3() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReading3() != null ? hhQualifiedMpan.getMeterReading3() : "");
      data.append(lineSeparator);
    }

    data.append(lineSeparator);
    data.append(lineSeparator);

    return data.toString();
  }

  /**
   * @param aHHQualifiedMpans the P0028 reported mpans qualifying for half hourly metering
   * @return the data as text for P0028 reported mpans qualifying for half hourly metering
   */
  protected String asCommaSeparatedP28HHQualifiedMpans(String aHeader, List<HalfHourlyQualifyingMpanDTO> aHHQualifiedMpans)
  {
    StringBuffer data = new StringBuffer();
    String lineSeparator = System.getProperty("line.separator");
    String commaSeparator = ", ";

    if (org.apache.commons.lang.StringUtils.isNotBlank(aHeader))
    {
      data.append(aHeader);
    }

    for (HalfHourlyQualifyingMpanDTO hhQualifiedMpan : aHHQualifiedMpans)
    {
      data.append(hhQualifiedMpan.getNHHDCId() != null ? hhQualifiedMpan.getNHHDCId() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMpan() != null ? hhQualifiedMpan.getMpan() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterSerialId() != null ? hhQualifiedMpan.getMeterSerialId() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterInstallationDeadline() != null ? hhQualifiedMpan.getMeterInstallationDeadline() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMaximumDemand() != null ? hhQualifiedMpan.getMaximumDemand() : "").append(commaSeparator);
      data.append(hhQualifiedMpan.getMeterReadingDate1() != null ? hhQualifiedMpan.getMeterReadingDate1() : "");
      data.append(lineSeparator);
    }

    data.append(lineSeparator);
    data.append(lineSeparator);

    return data.toString();
  }

  protected List<HalfHourlyQualifyingMpanDTO> transformSp04FromAFMSMpanData(List<Sp04FromAFMSMpan> aSp04FromAFMSMpans, DateTimeFormatter aDateTimeFmt)
  {
    List<HalfHourlyQualifyingMpanDTO> dtos = new ArrayList<HalfHourlyQualifyingMpanDTO>();

    for (Sp04FromAFMSMpan sp04FromAFMSMpan : aSp04FromAFMSMpans)
    {
      HalfHourlyQualifyingMpanDTO dto = new HalfHourlyQualifyingMpanDTO();
      dto.setMaximumDemand(sp04FromAFMSMpan.getMaxDemand());
      dto.setMeterInstallationDeadline(sp04FromAFMSMpan.getCalculatedMeterInstallationDeadline()
                                       .toString(aDateTimeFmt));
      dto.setMeterSerialId(sp04FromAFMSMpan.getMeterId());
      dto.setMpan(sp04FromAFMSMpan.getMpan().getValue());
      dto.setNHHDCId(sp04FromAFMSMpan.getDataCollector());

      if (sp04FromAFMSMpan.getMeterReadingDate1() != null)
      {
        dto.setMeterReadingDate1(sp04FromAFMSMpan.getMeterReadingDate1().toString(aDateTimeFmt));
      }

      dto.setMeterReading1(sp04FromAFMSMpan.getMeterRegisterReading1());

      if (sp04FromAFMSMpan.getMeterReadingDate2() != null)
      {
        dto.setMeterReadingDate2(sp04FromAFMSMpan.getMeterReadingDate2().toString(aDateTimeFmt));
      }

      dto.setMeterReading2(sp04FromAFMSMpan.getMeterRegisterReading2());

      if (sp04FromAFMSMpan.getMeterReadingDate3() != null)
      {
        dto.setMeterReadingDate3(sp04FromAFMSMpan.getMeterReadingDate3().toString(aDateTimeFmt));
      }

      dto.setMeterReading3(sp04FromAFMSMpan.getMeterRegisterReading3());

      dtos.add(dto);
    }

    return sort(dtos, on(HalfHourlyQualifyingMpanDTO.class).getNHHDCId());
  }

  protected List<HalfHourlyQualifyingMpanDTO> transformP28ActiveData(Collection<P0028Active> aP28Actives, Long aSupplierPk, DateTimeFormatter aDateTimeFmt)
  {
    List<HalfHourlyQualifyingMpanDTO> dtos = new ArrayList<HalfHourlyQualifyingMpanDTO>();

    for (P0028Active p28Active : aP28Actives)
    {
      HalfHourlyQualifyingMpanDTO dto = new HalfHourlyQualifyingMpanDTO();
      dto.setMaximumDemand(p28Active.getMaxDemand().floatValue());
      dto.setMeterInstallationDeadline(p28Active.getMeterInstallationDeadline()
                                       .toString(aDateTimeFmt));
      dto.setMeterSerialId(p28Active.getMeterSerialId());
      dto.setMpan(p28Active.getMpanCore().getValue());
      dto.setNHHDCId(p28Active.getDataCollectorName());

      findMeterReadingData(dto, p28Active, aSupplierPk, aDateTimeFmt);

      dtos.add(dto);
    }

    return sort(dtos, on(HalfHourlyQualifyingMpanDTO.class).getNHHDCId());
  }

  protected HalfHourlyQualifyingMpanDTO findMeterReadingData(HalfHourlyQualifyingMpanDTO aHHQualifiedMpanDTO,
                                                             P0028Active aP28Active, Long aSupplierPk,
                                                             DateTimeFormatter aFmt)
  {
    if (aHHQualifiedMpanDTO != null)
    {
      if (aP28Active.getMeterReadingDate() != null)
      {
        aHHQualifiedMpanDTO.setMeterReadingDate1(aP28Active.getMeterReadingDate().toString(aFmt));
      }
    }

    return aHHQualifiedMpanDTO;
  }

  private void findMeterReadingDataFromAFMSDatabase(HalfHourlyQualifyingMpanDTO aHHQualifiedMpanDTO,
                                                    P0028Active aP28Active, Long aSupplierPk,
                                                    DateTimeFormatter aDateTimeFmt)
  {
    // get meter reading data from the database for mpans reported in p28
    AFMSMpan afmsMpan = mAFMSMpanDao.getAfmsMpan(aP28Active.getMpanCore(), mSupplierDao.getById(aSupplierPk));

    if (afmsMpan != null)
    {
      // get latest meter for meter serial id, uniq id using the last updated date
      AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialIdAndMpanUniqId(aP28Active.getMeterSerialId(),
                                                                                  afmsMpan.getPk());

      if (meter != null)
      {
        AFMSMeterRegister meterRegister = meter.getMaxDemandMeterRegister();
        Collection<AFMSMeterRegReading> readings = meterRegister != null ? meterRegister.getMeterRegReadings() : null;

        if (readings != null)
        {
          List<AFMSMeterRegReading> validReadings = filter(having(on(AFMSMeterRegReading.class)
            .isReadingBscValidated(), equalTo(true)), readings);

          if (validReadings != null)
          {
            // need at least 3 readings
            int noReadingsReq = 3;
            if (validReadings.size() >= noReadingsReq)
            {
              Collections.sort(validReadings, new AFMSMeterRegReadingSortByReadingReverse());
              List<AFMSMeterRegReading> threeHighestReadings = new ArrayList<AFMSMeterRegReading>();

              for (int i = 0; i <= (noReadingsReq - 1); i++)
              {
                threeHighestReadings.add(validReadings.get(i));
              }

              // order by J0016 meter reading date asc
              threeHighestReadings = sort(threeHighestReadings, on(AFMSMeterRegReading.class).getMeterReadingDate());

              AFMSMeterRegReading reading1 = threeHighestReadings.get(0);
              AFMSMeterRegReading reading2 = threeHighestReadings.get(1);
              AFMSMeterRegReading reading3 = threeHighestReadings.get(2);

              aHHQualifiedMpanDTO.setMeterReadingDate1(reading1.getMeterReadingDate().toString(aDateTimeFmt));
              aHHQualifiedMpanDTO.setMeterReading1(reading1.getRegisterReading());

              aHHQualifiedMpanDTO.setMeterReadingDate2(reading2.getMeterReadingDate().toString(aDateTimeFmt));
              aHHQualifiedMpanDTO.setMeterReading2(reading2.getRegisterReading());

              aHHQualifiedMpanDTO.setMeterReadingDate3(reading3.getMeterReadingDate().toString(aDateTimeFmt));
              aHHQualifiedMpanDTO.setMeterReading3(reading3.getRegisterReading());
            }
          }
        }
      }
    }
  }

  /**
   * @param aSp04FromAfmsMpanDao the Sp04FromAFMSMpanDao
   */
  public void setSp04FromAfmsMpanDao(Sp04FromAFMSMpanDao aSp04FromAfmsMpanDao)
  {
    mSp04FromAfmsMpanDao = aSp04FromAfmsMpanDao;
  }

  /**
   * @param aHalfHourlyQualifyingMpanDao the HalfHourlyQualifyingMpanDao
   */
  public void setHalfHourlyQualifyingMpanDao(HalfHourlyQualifyingMpansReportDao aHalfHourlyQualifyingMpanDao)
  {
    mHalfHourlyQualifyingMpanDao = aHalfHourlyQualifyingMpanDao;
  }

  /**
   * @param aP0028ActiveDao the P0028ActiveDao
   */
  public void setP0028ActiveDao(P0028ActiveDao aP0028ActiveDao)
  {
    mP0028ActiveDao = aP0028ActiveDao;
  }

  /**
   * @param aSupplierDao the SupplierDao
   */
  public void setSupplierDao(SupplierDao aSupplierDao)
  {
    mSupplierDao = aSupplierDao;
  }
}
