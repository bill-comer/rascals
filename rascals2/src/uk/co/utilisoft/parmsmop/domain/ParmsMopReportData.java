package uk.co.utilisoft.parmsmop.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;


@Entity
@AccessType(value="property")
@Table(name="PARMS_REPORT_DATA")
public class ParmsMopReportData extends ParmsMopBaseData
{
  private BigInteger aPk;

  // the parent report for this reading
  private ParmsMopReport mParmsMopReport;

  @Id
  @Column(name="SERIAL_FK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public BigInteger getPk()
  {
    return aPk;
  }
  public void setPk(BigInteger pk)
  {
    this.aPk = pk;
  }
  

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "REPORT_FK", nullable = false)
  @NotNull
  public ParmsMopReport getParmsMopReport()
  {
    return mParmsMopReport;
  }

  public void setParmsMopReport(ParmsMopReport aParmsMopReport)
  {
    this.mParmsMopReport = aParmsMopReport;
  }
  
  /**
   * copies all the base data from a ParmsMopSourceData to this
   * @param srcDSata
   */
  @Transient
  public void replicate(ParmsMopSourceData srcDSata)
  {
    setPk(srcDSata.getPk());
    setMpan(srcDSata.getMpan());
    setStartDate(srcDSata.getStartDate());
    setCompletedDate(srcDSata.getCompletedDate());
    
    setStandard1(srcDSata.getStandard1());
    setStandard2(srcDSata.getStandard2());
    setStandard3(srcDSata.getStandard3());
    setStandard4(srcDSata.getStandard4());
    setStandard5(srcDSata.getStandard5());
    setStandard6(srcDSata.getStandard6());
    setStandard7(srcDSata.getStandard7());
    setStandard8(srcDSata.getStandard8());
    
    setGenDate1(srcDSata.getGenDate1());
    setGenDate2(srcDSata.getGenDate2());
    setGenDate3(srcDSata.getGenDate3());
    setGenDate4(srcDSata.getGenDate4());
    
    setGenString1(srcDSata.getGenString1());
    setGenString2(srcDSata.getGenString2());
    setGenString3(srcDSata.getGenString3());
    setGenString4(srcDSata.getGenString4());
    
    setPendingIndicator(srcDSata.isPendingIndicator());
    setWdElapsed(srcDSata.getWdElapsed());
    setExcludeIndicator(srcDSata.isExcludeIndicator());
    setHalfHourlyIndicator(srcDSata.isHalfHourlyIndicator());
  }
  
  /**
   * replicates a List<ParmsMopSourceData>
   * @param srcData
   * @return
   */
  @Transient
  public static List<ParmsMopReportData> replicate(List<ParmsMopSourceData> srcData)
  {
    List<ParmsMopReportData> resultsData = new ArrayList<ParmsMopReportData>();
    
    for (ParmsMopSourceData parmsMopSourceData : srcData)
    {
      ParmsMopReportData result = new ParmsMopReportData();
      result.replicate(parmsMopSourceData);
      resultsData.add(result);
    }
    return resultsData;
  }

}
