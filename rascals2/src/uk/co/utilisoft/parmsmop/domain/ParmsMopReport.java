package uk.co.utilisoft.parmsmop.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
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
import org.joda.time.DateTime;

import uk.co.utilisoft.afms.domain.AFMSDomainObject;
import uk.co.utilisoft.parmsmop.report.MopSerialTypes;

@Entity
@AccessType(value="property")
@Table(name="PARMS_REPORTS")
public class ParmsMopReport extends AFMSDomainObject
{

  private Long mReportPk;
  
  private String mSerial;
  private DateTime mRunDate;
  private String mUsername;
  

  private List<ParmsReportSummary> mParmsReportSummary;
  private List<ParmsMopReportData> mParmsMopReportData;
  
  @Id
  @Column(name="REPORT_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_FK")
  public Long getPk()
  {
    return mReportPk;
  }

  public void setPk(Long aPk)
  {
    mReportPk = aPk;
  }
  
  @Column(name="SERIAL")
  @Length( max = 50 )
  @NotNull
  public String getSerial()
  {
    return mSerial;
  }

  public void setSerial(String aSerial)
  {
    this.mSerial = aSerial;
  }


  @Column(name="RUN_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getRunDate()
  {
    return mRunDate;
  }

  public void setRunDate(DateTime aRunDate)
  {
    this.mRunDate = aRunDate;
  }

  @Column(name="USERNAME")
  @Length( max = 100 )
  @NotNull
  public String getUsername()
  {
    return mUsername;
  }

  public void setUsername(String aUsername)
  {
    this.mUsername = aUsername;
  }
  

  @OneToMany(mappedBy = "parmsMopReport")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  @LazyCollection(LazyCollectionOption.FALSE)
  public List<ParmsReportSummary> getParmsReportSummary()
  {
    return mParmsReportSummary;
  }
  /*
    */
  public void setParmsReportSummary(List<ParmsReportSummary> aParmsReportSummary)
  {
    mParmsReportSummary = aParmsReportSummary;
  }
  

  @OneToMany(mappedBy = "parmsMopReport")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  @LazyCollection(LazyCollectionOption.TRUE)
  public List<ParmsMopReportData> getParmsMopReportData()
  {
    return mParmsMopReportData;
  }
  /*
    */
  public void setParmsMopReportData(List<ParmsMopReportData> aParmsMopReportData)
  {
    mParmsMopReportData = aParmsMopReportData;
  }
  
  @Transient
  public boolean isSerialSP11()
  {
    return getSerial().toUpperCase().equals(MopSerialTypes.SP11.name()) ? true : false; 
  }

  @Transient
  public boolean isSerialSP14()
  {
    return getSerial().toUpperCase().equals(MopSerialTypes.SP14.name()) ? true : false; 
  }

  @Transient
  public boolean isSerialSP15()
  {
    return getSerial().toUpperCase().equals(MopSerialTypes.SP15.name()) ? true : false; 
  }
  
  @Transient
  public boolean isSerialHM12()
  {
    return getSerial().toUpperCase().equals(MopSerialTypes.HM12.name()) ? true : false; 
  }

  @Transient
  public boolean isSerialNM12()
  {
    return getSerial().toUpperCase().equals(MopSerialTypes.NM12.name()) ? true : false; 
  }
}
