package uk.co.utilisoft.afms.domain;

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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


@Entity
@Table(name="AREGI_PROCESS")
@SuppressWarnings("serial")
public class AFMSAregiProcess extends AFMSDomainObject
{

  private Long mArProcessPk;
  private AFMSMpan mMpan;
  private DateTime mD0268Received;     //D0268 Flow is the install of a HalfHourly Meter. This is populated once
  private DateTime mD0268DupReceived;  //DUP is populated for every D0268 rxd there after
  private DateTime mD0150Received;




  /**
   * @return the primary key
   */
  @Id
  @Column(name="AR_PROC_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mArProcessPk;
  }

  /**
   * @param aPk the primary key
   */
  public void setPk(Long aPk)
  {
    mArProcessPk = aPk;
  }


  /**
   * @return the AFMS Mpan
   */
  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="MPAN_LNK", nullable=true)
  public AFMSMpan getMpan()
  {
    return mMpan;
  }

  /**
   * @param aMpan the AFMS Mpan
   */
  public void setMpan(AFMSMpan aMpan)
  {
    mMpan = aMpan;
  }


  /**
   * NB this is deliberately private and getLatestD0268ReceiptDate() should be used instead
   * @return the effective to date(J0928)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="D0268_RECEIVED", nullable=true)
  private DateTime getD0268ReceivedDate()
  {
    return mD0268Received;
  }

  /**
   * @param aEffectiveToDate the effective to date(J0928)
   */
  public void setD0268ReceivedDate(DateTime aD0268Received)
  {
    mD0268Received = aD0268Received;
  }


  /**
   * NB this is deliberately private and getLatestD0268ReceiptDate() should be used instead
   * @return the effective to date(J0928)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="D0268_DUP_RECEIVED", nullable=true)
  private DateTime getD0268DupReceived()
  {
    return mD0268DupReceived;
  }

  public void setD0268DupReceived(DateTime mD0268DupReceived)
  {
    this.mD0268DupReceived = mD0268DupReceived;
  }

  /**
   * @return the D0150 Received
   */
  @Type(type = "org.joda.time.DateTime")
  @Column(name = "D0150_RECEIVED", nullable = true)
  public DateTime getD0150Received()
  {
    return mD0150Received;
  }

  public void setD0150Received(DateTime aD0150Received)
  {
    mD0150Received = aD0150Received;
  }

  /**
   * return the later of mD0268Received and mD0268DupReceived
   * @return
   */
  @Transient
  public DateTime getLatestD0268ReceiptDate()
  {
    if (mD0268Received == null && mD0268DupReceived == null)
    {
      return null;
    }
    if (mD0268Received == null)
    {
      return mD0268DupReceived;
    }

    if (mD0268DupReceived == null)
    {
      return mD0268Received;
    }

    if (mD0268Received.isAfter(mD0268DupReceived))
    {
      return mD0268Received;
    }
    else
    {
      return mD0268DupReceived;
    }
  }

}
