package uk.co.utilisoft.parms.file.sp04;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.BaseRowBuilder;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.parms.file.PoolHeader;


@Service("parms.sp04RowBuilder")
public class Sp04RowBuilder extends BaseRowBuilder
{
  private Supplier mSupplier;

  private String mSeperator = "|";

  String mSp04Prefix = "SP4";

  public Sp04RowBuilder()
  {
  }

  public List<String> buildAllRows(Supplier aSupplier, List<Sp04Data> activeRows)
  {
    PoolChecksumCalculator checksumCalculator = getChecksumCalculator();
    mSupplier = aSupplier;

    IterableMap<String, Boolean> gspMap = getMapAllGspDefinitions();

    List<String> rows = new ArrayList<String>();
    //add header
    rows.add(getHeader());
    // add sub record
    rows.add(getSubRecord());

    for (Sp04Data sp04Serial : activeRows)
    {
      rows.add(createLineForSerial(sp04Serial));
      gspMap.put(sp04Serial.getGspGroupId(), true);   //mark this GSP as added to the serial
    }

    //remove rows for serials that have been found
    MapIterator<String, Boolean> it = gspMap.mapIterator();
    while (it.hasNext())
    {
      it.next();
      if (it.getValue())
      {
        it.remove();
      }
    }

    //add rows for serials not found
    TreeSet<String> treeset = new TreeSet(gspMap.keySet());
    Iterator<String> treeit = treeset.iterator();
    while (treeit.hasNext())
    {
      String gsp = treeit.next();
      {
        //no GSP row has been added for this GSP so add a NULL row
        rows.add(createLineForValues(mSp04Prefix, gsp, "", "", "", ""));
      }
    }

    //calc checksum for all rows in file
    for (String row : rows)
    {
      checksumCalculator.addLineToCheckSum(row);
    }

    //add footer
    rows.add(getFooter(checksumCalculator));
    return rows;
  }


  String createLineForSerial(Sp04Data sp04Serial)
  {
    String mpan = sp04Serial.getMpanCore() != null ? sp04Serial.getMpanCore().getValue() : null;
    String standard1 = sp04Serial.getStandard1() != null ? sp04Serial.getStandard1().toString() : null;
    String standard2 = sp04Serial.getStandard2() != null ? sp04Serial.getStandard2().toString() : null;
    String standard3 = sp04Serial.getStandard3() != null ? sp04Serial.getStandard3().toString() : null;

    return createLineForValues(mSp04Prefix, sp04Serial.getGspGroupId(), mpan, standard1, standard2, standard3);
  }

  private String createLineForValues(String aSp04Prefix, String aGsp, String aMpan, String aS1, String aS2, String aS3)
  {
    return aSp04Prefix  + mSeperator
      + aGsp + mSeperator
      + aMpan + mSeperator
      + aS1 + mSeperator
      + aS2 + mSeperator
      + aS3;
  }

  private String getSubRecord()
  {
    Sp04SubRecord subRecord = new Sp04SubRecord();
    return subRecord.create(mSupplier.getSupplierId());
  }

  private String getHeader()
  {
    Sp04PoolHeader header = new Sp04PoolHeader();
    return header.createHeader(mSupplier.getSupplierId());
  }


}

class Sp04SubRecord
{

  public String create(String supplierId)
  {
    return "SUB|H|X|" + supplierId + "|" + getPeriodEndDate() + "|M";
  }

  String getPeriodEndDate()
  {

    DateTime endOfPeriod = new DateTime().minusMonths(1).dayOfMonth().withMaximumValue();

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
    return endOfPeriod.toString(fmt);

    /*
    String getPeriodEndDate()
    DateTime now = new DateTime();
    DateTime endOfPeriod = null;
    if (now.getDayOfMonth() > 7)
    {
      endOfPeriod = new DateTime().dayOfMonth().withMaximumValue();
    }
    else
    {
      endOfPeriod = new DateTime().minusMonths(1).dayOfMonth().withMaximumValue();
    }

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
    return endOfPeriod.toString(fmt);*/
  }

}


class Sp04PoolHeader extends PoolHeader
{
  @Override
  public String getFileType()
  {
    return "P0142001";
  }

  @Override
  public String getRecordType()
  {
    return "ZHD";
  }
}
