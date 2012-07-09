package uk.co.utilisoft.afms.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static uk.co.utilisoft.afms.domain.AFMSMeterRegReading.BSC_VALIDATION_STATUS;

@Entity
@Table(name="MPAN")
@SuppressWarnings("serial")
@NamedQuery(name="getSupplierIDsForParmsMonth",
            query="SELECT distinct(supplierId) FROM AFMSMpan a WHERE  (a.regiStatus is null OR (a.regiStatus is not null AND a.regiStatus != 2) AND  (a.regiStatus is not null AND a.regiStatus != 4)) AND  a.effectiveFromDate < :end")
public class AFMSMpan extends AFMSDomainObject
{
  private Long mPk;
  private String mMpanCore;
  private DateTime mEffectiveFromDate;
  private String mGridSupplyPoint;
  private String mSupplierId;
  private String mMeasurementClassification;
  private Long mRegiStatus;
  private DateTime mEffectiveToDate;
  private DateTime mSupplierEffectiveToDate;

  private Set<AFMSAgentHistory> mAgentHistorys = new HashSet<AFMSAgentHistory>();
  private AFMSAgent mAgent;

  private Collection<AFMSMeter> mAFMSMeters = new ArrayList<AFMSMeter>();

  private Collection<AFMSAregiProcess> mAregiProcesses = new ArrayList<AFMSAregiProcess>();


  /**
   * @return the mpan primary key
   */
  @Id
  @Column(name="UNIQ_ID", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mPk;
  }

  /**
   * @param aPk the mpan primary key
   */
  public void setPk(Long aMpanPk)
  {
    mPk = aMpanPk;
  }

  /**
   * @return the mpan core
   */
  @Column(name="J0003", nullable=false)
  public String getMpanCore()
  {
    return mMpanCore;
  }

  /**
   * @param aMpancore the mpan core
   */
  public void setMpanCore(String aMpancore)
  {
    mMpanCore = aMpancore;
  }

  /**
   * @return the effective from date(J0049)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0049", nullable=true)
  public DateTime getEffectiveFromDate()
  {
    return mEffectiveFromDate;
  }

  /**
   * @param aEffectiveFromDate the effective from date(J0049)
   */
  public void setEffectiveFromDate(DateTime aEffectiveFromDate)
  {
    mEffectiveFromDate = aEffectiveFromDate;
  }

  /**
   * @return the grid supply point
   */
  @Column(name="J0066", nullable=true)
  public String getGridSupplyPoint()
  {
    return mGridSupplyPoint;
  }

  /**
   * @param aGridSupplyPoint the grid supply point
   */
  public void setGridSupplyPoint(String aGridSupplyPoint)
  {
    mGridSupplyPoint = aGridSupplyPoint;
  }

  /**
   * @return the supplier id
   */
  @Column(name="SUPPLIER_ID")
  public String getSupplierId()
  {
    return mSupplierId;
  }

  /**
   * @param aSupplierId the supplier id
   */
  public void setSupplierId(String aSupplierId)
  {
    mSupplierId = aSupplierId;
  }

  /**
   * see MeasurementClassification
   * Values of A & B indicate Non HalfHourly
   * Values of C, D & E indicate HalfHourly
   *
   * @return the measurement classification(J0082)
   */
  @Column(name="J0082")
  public String getMeasurementClassification()
  {
    return mMeasurementClassification;
  }

  /**
   * @param aMeasurementClassification the measurement classification(J0082)
   */
  public void setMeasurementClassification(String aMeasurementClassification)
  {
    mMeasurementClassification = aMeasurementClassification;
  }

  /**
   * @return the registration status(X0210)
   *
   * 1            Registration accepted by MPAS
   * 2            Registration terminated - Rejected by MPAS
   * 3            Registration objection received
   * 4            Registration terminated - Objection upheld
   * 5            Opening Read Received
   * 6            Loss notification received
   * 7            Objection against loss raised
   * 8            Loss objection upheld - Registration retained
   * 9            Registration Requested
   * 10           Registration objection Removed
   * 11           Agents Appointment requested
   * 12           Agent Appointment Rejected
   * 13           Awaiting Meter details
   * 14           Awaiting Final Flow
   * 15           Objection to loss Accepted
   * 16           Objection to loss rejected
   * 17           Request Removal Of Objection
   * 18           Objection removal rejected
   * 19           Objection removal accepted
   * 20           Loss confirmed
   * 21           Loss completed
   */
  @Column(name="X0210")
  public Long getRegiStatus()
  {
    return mRegiStatus;
  }

  /**
   * @param aRegiStatus the registration status(X0210)
   */
  public void setRegiStatus(Long aRegiStatus)
  {
    mRegiStatus = aRegiStatus;
  }

  /**
   * @return the effective to date(J0928)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0928", nullable=true)
  public DateTime getEffectiveToDate()
  {
    return mEffectiveToDate;
  }

  /**
   * @param aEffectiveToDate the effective to date(J0928)
   */
  public void setEffectiveToDate(DateTime aEffectiveToDate)
  {
    mEffectiveToDate = aEffectiveToDate;
  }


  /**
   * @return the Supplier effective to date(J0928)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0117", nullable=true)
  public DateTime getSupplierEffectiveToDate()
  {
    return mSupplierEffectiveToDate;
  }

  /**
   * @param aEffectiveToDate the Supplier effective to date(J0117)
   */
  public void setSupplierEffectiveToDate(DateTime aSupplierEffectiveToDate)
  {
    mSupplierEffectiveToDate = aSupplierEffectiveToDate;
  }

  /**
   * @return the AFMS Agent History records
   */
  @Transient
  public Set<AFMSAgentHistory> getAgentHistorys()
  {
    return mAgentHistorys;
  }

  /**
   * @param aAgentHistorys the AFMS Agent History records
   */
  public void setAgentHistorys(Set<AFMSAgentHistory> aAgentHistorys)
  {
    mAgentHistorys = aAgentHistorys;
  }

  /**
   * @return the AFMS Data Collector Agent
   */
  //@Transient
  @OneToOne(mappedBy="mpan")
  public AFMSAgent getAgent()
  {
    return mAgent;
  }

  /**
   * @param aAgent the AFMS Data Collector Agent
   */
  public void setAgent(AFMSAgent aAgent)
  {
    mAgent = aAgent;
  }

  @Transient
  public boolean isHalfHourly()
  {
    MeasurementClassification mc = new MeasurementClassification(getMeasurementClassification());
    return mc.isHalfHourly();
  }


  @OneToMany(mappedBy="mpan")
  @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
//  @LazyCollection(LazyCollectionOption.FALSE) // bug#5622 disable because performance issue with creating dpi file
  public Collection<AFMSMeter> getMeters()
  {
    return mAFMSMeters;
  }

  public void setMeters(Collection<AFMSMeter> aAFMSMeters)
  {
    this.mAFMSMeters = aAFMSMeters;
  }

  @OneToMany(mappedBy="mpan", fetch=FetchType.EAGER)
  @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
  public Collection<AFMSAregiProcess> getAregiProcesses()
  {
    return mAregiProcesses;
  }

  public void setAregiProcesses(Collection<AFMSAregiProcess> aAregiProcesses)
  {
    this.mAregiProcesses = aAregiProcesses;
  }

  @Transient
  public AFMSAregiProcess getLatestAregiProcess()
  {
    if (getAregiProcesses() == null || getAregiProcesses().size() == 0)
      return null;
    else
    {
      if (getAregiProcesses().size() == 1)
      {
        return getAregiProcesses().iterator().next();
      }
      else
      {
        AFMSAregiProcess latest = null;
        for (AFMSAregiProcess afmsAregiProcess : getAregiProcesses())
        {
          if (latest == null)
            latest = afmsAregiProcess;

          if (afmsAregiProcess.getLastUpdated().isAfter(latest.getLastUpdated()))
          {

            latest = afmsAregiProcess;
          }
        }
        return latest;
      }
    }
  }

  /**
   * Looks thru all the meters.
   * Returns the first created HH meter if any or more than one are found
   * @return
   */
  @Transient
  public AFMSMeter getFirstCreatedHalfHourlyMeter()
  {
    AFMSMeter meter = null;
    for (AFMSMeter afmsMeter : getMeters())
    {
      if (afmsMeter.isThisMeterActiveNow())
      {
        if (afmsMeter.isHalfHourlyMeter() )
        {
          if (meter == null)
          {
            meter = afmsMeter;
          }
          else
          {
            if (afmsMeter.getSettlementDate().isBefore(meter.getSettlementDate()))
            {
              meter = afmsMeter;
            }
          }
        }
      }
    }
    // this can be null if none found
    return meter;
  }


  /**
   * @return the active AFMS Meter
   */
  @Transient
  public AFMSMeter getActiveMeter()
  {
    if (getMeters() != null && !getMeters().isEmpty())
    {
      for (AFMSMeter afmsMeter : getMeters())
      {
        if (afmsMeter.isThisMeterActiveNow())
        {
          return afmsMeter;
        }
      }
    }

    return null;
  }

  /**
   * Get currently active meter, and expired meters potentially containing meter register readings required
   * to calculate maximum demand.
   *
   * Note: Includes meters which have been changed due to life expectancy expiration. (see bug 6375)
   *
   * @param aEfd an effective from date range
   * @param aEtd an effective to date range
   * @return collections of afms meters
   */
  @Transient
  public List<AFMSMeter> getNonHalfHourlyMeters(DateTime aEfd, DateTime aEtd)
  {
    List<AFMSMeter> mergedMeters = new ArrayList<AFMSMeter>();
    if (getMeters() != null && !getMeters().isEmpty())
    {
      for (AFMSMeter meter : getMeters())
      {
        if (meter.isNonHalfHourlyMeter())
        {
          // period range for calculating maximum demand
          if (meter.getEffectiveToDateMSID() == null
              || (meter.getEffectiveToDateMSID().isAfter(aEfd) && meter.getEffectiveToDateMSID().isBefore(aEtd)))
          {
            // identify expired NHH meters
            mergedMeters.add(meter);
          }
        }
      }
    }

    return mergedMeters;
  }

  /**
   * Get meter registers for currently active and expired meters potentially containing meter register readings required
   * to calculate maximum demand.
   *
   * Note: Includes meter registers which have been changed due to life expectancy expiration. (see bug 6375)
   *
   * @param aEfd an effective from date range
   * @param aEtd an effective to date range
   * @return collections of meter registers
   */
  @Transient
  public List<AFMSMeterRegister> getNonHalfHourlyMeterRegisters(DateTime aEfd, DateTime aEtd)
  {
    List<AFMSMeterRegister> nhhRegisters = new ArrayList<AFMSMeterRegister>();
    List<AFMSMeter> meters = getNonHalfHourlyMeters(aEfd, aEtd);

    if (!meters.isEmpty())
    {
      List<AFMSMeter> metersOrderBySettDateDesc
        = sort(meters, on(AFMSMeter.class).getSettlementDate(), Collections.reverseOrder());
      AFMSMeter activeMeter = meters.get(0).getMpan().getActiveMeter();
      DateTime meterSettDateRef = activeMeter != null ? activeMeter.getSettlementDate() : null;

      // check valid readings in register
      for (AFMSMeter meter : metersOrderBySettDateDesc)
      {
        for (AFMSMeterRegister register : meter.getMeterRegisters())
        {
          if (register.isValidMeterRegisterForSp04Report(aEfd, aEtd))
          {
            nhhRegisters.add(register);
          }
          else
          {
            // check for new_meter.j1254 = old_meter.etd_msid identifies a replacement meter due to expiry
            if (meterSettDateRef != null)
            {
              if (meter.getEffectiveToDateMSID() == null
                  || meter.getEffectiveToDateMSID().equals(meterSettDateRef))
              {
                boolean isValidReplacementRegister = register.getEffectiveFromDate() != null
                  && (register.getEffectiveToDate() == null || register.getEffectiveToDate().isAfter(aEfd))
                  && register.isMeasurementQuantityKW()
                  && register.isMeterRegisterIdMaxDemand()
                  && register.isMeterRegisterTypeMaxDemand();

                if (isValidReplacementRegister)
                {
                  nhhRegisters.add(register);
                }
              }
            }
          }
        }

        meterSettDateRef = meter.getSettlementDate();
      }
    }

    return nhhRegisters;
  }

  /**
   * @param aEfd an effective from date range
   * @param aEtd an effective to date range
   * @return collection of meter readings used in calculating the max demand identified by meter serial id(s)
   */
  @Transient
  public Map<String, List<AFMSMeterRegReading>> getNonHalfHourlyMeterRegReadings(DateTime aEfd, DateTime aEtd)
  {
    Set<String> meterSerialIds = new HashSet<String>();
    List<AFMSMeterRegReading> nhhReadings = new ArrayList<AFMSMeterRegReading>();
    List<AFMSMeterRegister> registers = getNonHalfHourlyMeterRegisters(aEfd, aEtd);

    for (AFMSMeterRegister register : registers)
    {
       List<AFMSMeterRegReading> readings = register.getMeterRegReadings();

       for (AFMSMeterRegReading reading : readings)
       {
         if (reading.getBSCValidationStatus() != null
             && reading.getBSCValidationStatus().equalsIgnoreCase((BSC_VALIDATION_STATUS.V.getValue()))
             && reading.getDateReceived().isAfter(aEfd)
             && reading.getDateReceived().isBefore(aEtd))
         {
           nhhReadings.add(reading);
           meterSerialIds.add(register.getMeter().getMeterSerialId());
         }
       }
    }

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();

    if (!meterSerialIds.isEmpty() && !nhhReadings.isEmpty())
    {
      msidReadings.put(StringUtils
                       .arrayToCommaDelimitedString(meterSerialIds.toArray(new String[]{}))
                       .replaceAll("\\s+", ""), nhhReadings);
    }

    return msidReadings;
  }

  @Transient
  public String toString()
  {
    return "mpan[" + getMpanCore() + "], supplier[" + getSupplierId() + "], GSP[" + getGridSupplyPoint() + "], measClass[" + getMeasurementClassification() + "]";
  }
}

