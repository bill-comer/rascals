package uk.co.utilisoft.parms.file.sp04;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.joda.time.DateMidnight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.Sp04FileDao;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.FileBuilder;
import uk.co.utilisoft.parms.file.FileDataWriter;

@Service("parms.sp04FileBuilder")
public class Sp04FileBuilder extends FileBuilder
{
  @Autowired(required=true)
  @Qualifier("parms.sp04FileDao")
  private Sp04FileDao mSp04FileDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04Calculator")
  public Sp04Calculator mSp04Calculator;

  @Autowired(required=true)
  @Qualifier("parms.sp04RowBuilder")
  public Sp04RowBuilder mSp04RowBuilder;

  @Autowired(required=true)
  @Qualifier("parms.sp04FileDataWriter")
  private FileDataWriter mFileDataWriter;

  /**
   * bug#5835 - build an sp04 file report for P0028Active and Sp04FromAFMSMpan records currently active and eligible for reporting.
   *
   * note: same as buildFile(Supplier aSupplier, IterableMap<String, Object> activeList) but considers also afms mpans
   *
   * @param aSupplier the supplier
   * @param aCombinedActiveMpansMap the current active mpans in the afms mpans
   * @return the Sp04 File record's primary key
   */
  public Long buildFile(Supplier aSupplier, IterableMap<String, Object> aCombinedActiveMpansMap)
  {
    // PRP is always for the previous month
    ParmsReportingPeriod prpLastMonth = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    Sp04File sp04File = new Sp04File(Sp04File.createFileName(aSupplier.getSupplierId(), prpLastMonth), aSupplier,
                                     prpLastMonth);

    List<Sp04Data> combinedSp04Data = new ArrayList<Sp04Data>();

    MapIterator<String, Object> activeMpansIter = aCombinedActiveMpansMap.mapIterator();
    while (activeMpansIter.hasNext())
    {
      activeMpansIter.next();
      Object mpanObj = activeMpansIter.getValue();
      P0028Active p28Active = mpanObj instanceof P0028Active ? (P0028Active) mpanObj : null;
      Sp04FromAFMSMpan sp04FromAFMSMpan = mpanObj instanceof Sp04FromAFMSMpan ? (Sp04FromAFMSMpan) mpanObj : null;
      Sp04Data sp04Data = null;

      if (p28Active != null)
      {
        sp04Data = mSp04Calculator.calculate(p28Active, aSupplier);
      }

      if (sp04FromAFMSMpan != null)
      {         
        // bug#6038
//        sp04Data = mSp04Calculator.mSp04RowCalculator.buildAfmsSp04DataRow(sp04FromAFMSMpan, prpLastMonth, aSupplier);
        sp04Data = mSp04Calculator.mSp04RowCalculator.buildAfmsSp04DataRecord(sp04FromAFMSMpan, prpLastMonth,
                                                                              aSupplier);
      }

      // Remove mpans that are not valid for sp04 reporting. That is mpans with fault reasons
      if (sp04Data != null && sp04Data.getSp04FaultReason() == null)
      {
        sp04Data.setSp04File(sp04File);
        combinedSp04Data.add(sp04Data);
      }
    }

    return buildRows(sp04File, aSupplier, combinedSp04Data);
  }

  /**
   * @param aSp04File the sp04 file
   * @param aSupplier the supplier
   * @param aSp04Datas the sp04 data
   * @return the sp04 file's primary key
   */
  @Transactional
  Long buildRows(Sp04File aSp04File, Supplier aSupplier, List<Sp04Data> aSp04Datas)
  {
    List<String> rows = mSp04RowBuilder.buildAllRows(aSupplier, aSp04Datas);

    aSp04File.setSp04Data(aSp04Datas);
    aSp04File.setData(formatData(rows));

    mFileDataWriter.save(aSp04File.getFilename(), aSp04File.getData());
    //mFileDataWriter.save(sp04File.getFilename(), formatData(rows));

    mSp04FileDao.makePersistent(aSp04File);

    return aSp04File.getPk();
  }

  /**
   * Test only
   * @param testSp04RowBuilder
   */
  void setSp04RowBuilder(Sp04RowBuilder aTestSp04RowBuilder)
  {
    mSp04RowBuilder = aTestSp04RowBuilder;

  }
}