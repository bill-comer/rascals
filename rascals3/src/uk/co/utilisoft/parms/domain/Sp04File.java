package uk.co.utilisoft.parms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.file.dpi.DpiFileExtensions;

@Entity
@AccessType(value="property")
@Table(name="PARMS_SP04_FILE")
@SuppressWarnings("serial")
public class Sp04File extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  //name of file created
  private String mFilename;

  // time stamp of creation
  private DateTime mDateCreated;

  //Supplier associated with this SP04 Report
  private Supplier mSupplier;

  //the Reporting Month for this File
  private ParmsReportingPeriod mReportingPeriod;

  // the contents of the File
  private String mData;


  private List<Sp04Data> mSp04Data;



  public Sp04File()
  {
    super();
    setDateCreated(new DateTime());
    mSp04Data = new ArrayList<Sp04Data>();
  }

  public Sp04File(String aFilename, Supplier aSupplier,
      ParmsReportingPeriod aReportingPeriod)
  {
    this();
    this.mFilename = aFilename;
    this.mSupplier = aSupplier;
    this.mReportingPeriod = aReportingPeriod;
  }



  @Column(name="FILENAME")
  @NotEmpty
  @Length( max = 100 )
  public String getFilename()
  {
    return mFilename;
  }

  public void setFilename(String filename)
  {
    this.mFilename = filename;
  }

  @Column(name="DATE_CREATED")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getDateCreated()
  {
    return mDateCreated;
  }

  public void setDateCreated(DateTime dateCreated)
  {
    this.mDateCreated = dateCreated;
  }

  @ManyToOne
  @JoinColumn(name = "SUPPLIER_FK")
  @NotNull
  public Supplier getSupplier()
  {
    return mSupplier;
  }

  public void setSupplier(Supplier supplier)
  {
    this.mSupplier = supplier;
  }

  @Column(name="REPORTING_PERIOD")
  @Type(type="uk.co.utilisoft.parms.ParmsReportingPeriod")
  @NotNull
  public ParmsReportingPeriod getReportingPeriod()
  {
    return mReportingPeriod;
  }

  public void setReportingPeriod(ParmsReportingPeriod reportingPeriod)
  {
    this.mReportingPeriod = reportingPeriod;
  }

  @Lob
  @Column(name="DATA",nullable=true)
  public String getData()
  {
    return mData;
  }

  public void setData(String data)
  {
    this.mData = data;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "sp04File")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  public List<Sp04Data> getSp04Data()
  {
    return mSp04Data;
  }

  public void setSp04Data(List<Sp04Data> sp04Data)
  {
    this.mSp04Data = sp04Data;
  }


  @Transient
  public static String createFileName(String aParticipantID, ParmsReportingPeriod aParmsReportingPeriod)
  {
   return aParticipantID + "142"
     + getLastDigitOfYear(aParmsReportingPeriod.getStartOfFirstMonthInPeriod().getYear())
     + "." + DpiFileExtensions.getExtension(aParmsReportingPeriod.getStartOfFirstMonthInPeriod().getMonthOfYear());
  }


  @Transient
  static String getLastDigitOfYear(int aYear)
  {
    String year = String.valueOf(aYear);
    Assert.isTrue(year.length() == 4);
    return year.substring(3);
  }


  @Transient
  public StringBuffer getDisplayableData()
  {
    StringBuffer data = new StringBuffer(getData().replaceAll("\\r\\n", "<BR>"));

    /*data.append("Oooops - no SP04 File data, At the moment the CLOB is not saved\n");
    data.append("   - Should it be?\n");
    data.append("   - or should we hust Display the Sp04Data rows we have ?");*/

    //StringBuffer displayData = getData().replaceAll("\\r\\n", "<BR>");
    return data;

    //return data;
  }
}
