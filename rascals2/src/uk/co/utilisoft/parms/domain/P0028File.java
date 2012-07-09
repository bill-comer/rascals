package uk.co.utilisoft.parms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.ParmsReportingPeriod;

@Entity
@AccessType(value="property")
@Table(name="PARMS_P0028_FILE")
@SuppressWarnings("serial")
public class P0028File extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  // name of file imported
  private String mFilename;

  // time stamp of the import
  private DateTime mDateImported;

  //Supplier selected to upload the file
  private Supplier mSupplier;

  // Agent DC Collector Id as it appears in the P0028 File
  private String mDcAgentName;

  // Receipt date of P0028 - the date the P0028 is received - NOT when it is loaded into the system
  private DateTime mReceiptDate;

  // the Reporting Month for this File
  private ParmsReportingPeriod mReportingPeriod;

  private List<P0028Data> mP0028Data;

  private boolean mIsErrored;

  public P0028File()
  {
    super();
    setDateImported(new DateTime());
    mP0028Data = new ArrayList<P0028Data>();
    mIsErrored = false;
  }

  public P0028File(String aFilename, String aDcName,
      Supplier aSupplier, String aDcAgentName,
      DateTime aReceiptDate, ParmsReportingPeriod aReportingPeriod)
  {
    this();
    this.mFilename = aFilename;
    this.mDcAgentName = aDcName;
    this.mSupplier = aSupplier;
    this.mDcAgentName = aDcAgentName;
    this.mReceiptDate = aReceiptDate;
    this.mReportingPeriod = aReportingPeriod;
    mIsErrored = false;
  }

  @Column(name="HAS_ERRORS")
  @NotNull
  public boolean isErrored()
  {
    return mIsErrored;
  }

  public void setErrored(boolean isErrored)
  {
    mIsErrored = isErrored;
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

  /**
   * @return the date imported
   */
  @Column(name="DATE_IMPORTED")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getDateImported()
  {
    return mDateImported;
  }

  public void setDateImported(DateTime dateImported)
  {
    this.mDateImported = dateImported;
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

  @Column(name="DC_AGENT_NAME")
  @NotEmpty
  @Length( max = 100 )
  public String getDcAgentName()
  {
    return mDcAgentName;
  }

  public void setDcAgentName(String dcAgentName)
  {
    this.mDcAgentName = dcAgentName;
  }

  /**
   * @return The Receipt date of the P0028 - Not the upload time.
   */
  @Column(name="RECEIPT_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getReceiptDate()
  {
    return mReceiptDate;
  }

  public void setReceiptDate(DateTime receiptDate)
  {
    this.mReceiptDate = receiptDate;
  }

  @Column(name="REPORTING_PERIOD")
  @Type(type="uk.co.utilisoft.parms.ParmsReportingPeriod")
  @NotNull
  public ParmsReportingPeriod getReportingPeriod()
  {
    return mReportingPeriod;
  }

  /**
   * @param aReportingEndPeriod the reporting end period
   */
  public void setReportingPeriod(ParmsReportingPeriod aReportingEndPeriod)
  {
    this.mReportingPeriod = aReportingEndPeriod;
  }


  @OneToMany(mappedBy = "p0028File")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  @LazyCollection(LazyCollectionOption.FALSE)
  public List<P0028Data> getP0028Data()
  {
    return mP0028Data;
  }
  /*
    */
  public void setP0028Data(List<P0028Data> aP0028Data)
  {
    mP0028Data = aP0028Data;
  }


  @Transient
  public DateTime getMeterInstallationDeadline()
  {
    return getReceiptDate().plusMonths(3);
  }


  @Transient
  public StringBuffer getDisplayableData()
  {
    StringBuffer data = new StringBuffer();

    data.append("Oooops - no P0028 File data, At the moment the CLOB is not saved\n");
    data.append("   - Should it be?\n");
    data.append("   - or should we hust Display the P0028data rows we have ?");

    return data;
  }

}
