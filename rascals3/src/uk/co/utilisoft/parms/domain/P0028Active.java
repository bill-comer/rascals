package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;


@Entity
@AccessType(value="property")
@Table(name="PARMS_P0028_ACTIVE")
@SuppressWarnings("serial")
public class P0028Active extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private Supplier mSupplier;
  private DataCollector mDataCollector;
  private String mDataCollectorName;

  private MPANCore mMpanCore;
  private Long mMaxDemand;
  private DateTime mP0028ReceivedDate;
  private String mMeterSerialId;
  /**
   * The J0010 from MeterRegister
   */
  private DateTime mMeterReadingDate;
  private P0028Data mLatestP0028Data;



  public P0028Active()
  {
  }


  public P0028Active(Supplier supplier, DataCollector dc, String aDcName, P0028Data latestP0028Data,
      Long maxDemand, DateTime p0028ReceivedDate, String meterSerialId, MPANCore mpan,
      DateTime meterReadingDate)
  {
    setSupplier(supplier);
    setDataCollector(dc);
    setDataCollectorName(aDcName);
    setLatestP0028Data(latestP0028Data);
    setMaxDemand(maxDemand);
    setP0028ReceivedDate(p0028ReceivedDate);
    setMeterSerialId(meterSerialId);
    setMeterReadingDate(meterReadingDate);
    setMpanCore(mpan);
  }


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SUPPLIER_FK", nullable = false)
  @NotNull
  public Supplier getSupplier()
  {
    return mSupplier;
  }
  public void setSupplier(Supplier supplier)
  {
    this.mSupplier = supplier;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DATA_COLLECTOR_FK", nullable = false)
  //@NotNull
  public DataCollector getDataCollector()
  {
    return mDataCollector;
  }
  public void setDataCollector(DataCollector dataCollector)
  {
    this.mDataCollector = dataCollector;
  }

  @Column(name="DATA_COLLECTOR_NAME")
  @NotNull
  public String getDataCollectorName()
  {
    return mDataCollectorName;
  }
  public void setDataCollectorName(String aDataCollectorName)
  {
    this.mDataCollectorName = aDataCollectorName;
  }

  @Column(name="MPAN")
  @Type(type="uk.co.utilisoft.parms.MPANCore")
  @NotNull
  public MPANCore getMpanCore()
  {
    return mMpanCore;
  }
  public void setMpanCore(MPANCore mpanCore)
  {
    this.mMpanCore = mpanCore;
  }

  @Column(name="MAX_DEMAND")
  @NotNull
  public Long getMaxDemand()
  {
    return mMaxDemand;
  }
  public void setMaxDemand(Long maxDemand)
  {
    this.mMaxDemand = maxDemand;
  }

  @Column(name="P0028_RECEIVED_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getP0028ReceivedDate()
  {
    return mP0028ReceivedDate;
  }
  public void setP0028ReceivedDate(DateTime p0028ReceivedDate)
  {
    this.mP0028ReceivedDate = p0028ReceivedDate;
  }

  @Column(name="METER_ID")
  @NotEmpty
  @Length( max = 50 )
  public String getMeterSerialId()
  {
    return mMeterSerialId;
  }
  public void setMeterSerialId(String meterSerialId)
  {
    this.mMeterSerialId = meterSerialId;
  }

  @Column(name="METER_REGISTER_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getMeterReadingDate()
  {
    return mMeterReadingDate;
  }
  public void setMeterReadingDate(DateTime meterReadingDate)
  {
    this.mMeterReadingDate = meterReadingDate;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "P0028_DATA_FK", nullable = false)
  @NotNull
  public P0028Data getLatestP0028Data()
  {
    return mLatestP0028Data;
  }
  public void setLatestP0028Data(P0028Data aLatestP0028Data)
  {
    this.mLatestP0028Data = aLatestP0028Data;
  }

  @Transient
  public DateTime getMeterInstallationDeadline()
  {
    return getLatestP0028Data().getMeterInstallationDeadline();
  }


}
