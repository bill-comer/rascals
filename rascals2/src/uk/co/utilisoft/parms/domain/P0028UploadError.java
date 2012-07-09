package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;

@Entity
@AccessType(value="property")
@Table(name="PARMS_P0028_UPLOAD_ERROR")
@SuppressWarnings("serial")
public class P0028UploadError extends BaseVersionedDomainObject<Long, Long, DateTime>
{

  // the parent P0028Data that this error refers to
  private P0028Data mP0028Data;

  // the upload failure reason
  private Sp04FaultReasonType mFailureReason;



  public P0028UploadError()
  {
  }

  public P0028UploadError(P0028Data aP0028Data,
      Sp04FaultReasonType aFailureReason)
  {
    this.mP0028Data = aP0028Data;
    this.mFailureReason = aFailureReason;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "P0028_DATA_FK", nullable = false, updatable = true, insertable = true)
  @NotNull
  public P0028Data getP0028Data()
  {
    return mP0028Data;
  }

  public void setP0028Data(P0028Data p0028Data)
  {
    this.mP0028Data = p0028Data;
  }

  @Column(name="FAILURE_REASON",unique=true, length=255)
  @Enumerated(EnumType.STRING)
  @NotNull
  public Sp04FaultReasonType getFailureReason()
  {
    return mFailureReason;
  }

  public void setFailureReason(Sp04FaultReasonType aFailureReason)
  {
    this.mFailureReason = aFailureReason;
  }
}
