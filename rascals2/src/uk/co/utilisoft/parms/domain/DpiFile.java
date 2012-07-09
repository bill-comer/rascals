package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.file.dpi.DpiFileExtensions;

@Entity
@AccessType(value="property")
@Table(name="PARMS_DPI_FILE")
@SuppressWarnings("serial")
@NamedQuery(name="getDpiFilesForSupplier",
            query="SELECT supplier FROM DpiFile d WHERE d.supplier = :supplier")
public class DpiFile extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  /**
   * the supplier for this and many other MPANs
   */
  private Supplier mSupplier;
  private DateTime mDateCreated;
  private String mFileName;
  private ParmsReportingPeriod mReportingPeriod;

  /**
   * For Spring only
   */
  public DpiFile()
  {
    setDateCreated(new DateTime());
    setFileName("");
  }

  public DpiFile(ParmsReportingPeriod aReportingPeriod, Supplier aSupplier)
  {
    super();
    setDateCreated(new DateTime());
    setFileName("");
    this.mReportingPeriod = aReportingPeriod;
    this.mSupplier = aSupplier;
  }

  /**
   * @return the date created
   */
  @Column(name="DATE_CREATED")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getDateCreated()
  {
    return mDateCreated;
  }

  public void setDateCreated(DateTime aDateCreated)
  {
    this.mDateCreated = aDateCreated;
  }

  /**
   * @return the file name
   */
  @Column(name="FILENAME")
  @Length( max = 100 )
  public String getFileName()
  {
    return mFileName;
  }

  /**
   * @param aFileName the file name
   */
  public void setFileName(String aFileName)
  {
    this.mFileName = aFileName;
  }

  /**
   * @return the reporting end period
   */
  @Column(name="REPORTING_MONTH")
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

  @ManyToOne
  @JoinColumn(name = "SUPPLIER_FK")
  @NotNull
  public Supplier getSupplier()
  {
    return mSupplier;
  }

  public void setSupplier(Supplier aSupplier)
  {
    mSupplier = aSupplier;
  }

  @Transient
  public static String createFileName(String aParticipantID, ParmsReportingPeriod aParmsReportingPeriod)
  {
   return aParticipantID + "135"
     + getLastDigitOfYear(aParmsReportingPeriod.getStartOfFirstMonthInPeriod().getYear())
     + "." + DpiFileExtensions.getExtension(aParmsReportingPeriod.getNextReportingPeriod().getStartOfFirstMonthInPeriod().getMonthOfYear());
  }

  @Transient
  static String getLastDigitOfYear(int aYear)
  {
    String year = String.valueOf(aYear);
    Assert.isTrue(year.length() == 4);
    return year.substring(3);
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof DpiFile))
      return false;

    DpiFile ga = (DpiFile)o;

    if (ga.getFileName() != null && !ga.getFileName().equals(getFileName())) {
      System.out.println("getFileName[" + getFileName() + "], is difft to [" + ga.getFileName() + "]");
    }
    if (!ga.getReportingPeriod().equals(getReportingPeriod())) {
      System.out.println("getReportingMonth[" + getReportingPeriod() + "], is difft to [" + ga.getReportingPeriod() + "]");
    }
    if (!ga.getSupplier().equals(getSupplier())) {
      System.out.println("getSupplier[" + getSupplier() + "], is difft to [" + ga.getSupplier() + "]");
    }

    return (ga.getFileName() != null && ga.getFileName().equals(getFileName()))
      && ga.getReportingPeriod().equals(getReportingPeriod())
      && ga.getSupplier().equals(getSupplier())
    ;
  }

  @Override
  public int hashCode()
  {
    int result = 17;

    result = 31 * result + (null == getFileName() ? 0 : getFileName().hashCode());
    result = 31 * result + (null == getReportingPeriod() ? 0 : getReportingPeriod().hashCode());
    result = 31 * result + (null == getSupplier() ? 0 : getSupplier().hashCode());

    return result;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "getFileName:" + getFileName() + ",getReportingMonth:" + getReportingPeriod() + ",getSupplier:" + getSupplier();
  }

  public DpiFile replicate()
  {
    DpiFile replicar = new DpiFile(this.getReportingPeriod(), this.getSupplier());
    return replicar;
  }
}
