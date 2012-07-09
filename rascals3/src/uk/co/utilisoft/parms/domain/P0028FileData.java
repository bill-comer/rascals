package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;
import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name = "PARMS_P0028_FILE_DATA")
@SuppressWarnings("serial")
public class P0028FileData extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mData;
  private P0028File mP0028File;
  private String mWarnings;

  /**
   * @return the P0028File lob warnings
   */
  @Lob
  @Column(name = "WARNINGS", nullable = true)
  @Size(max = 8000, message = "Maximum length of P0028 Upload Warnings cannot exceed 8000 characters")
  public String getWarnings()
  {
    return mWarnings;
  }

  /**
   * @param aWarnings the P0028File lob warnings
   */
  public void setWarnings(String aWarnings)
  {
    this.mWarnings = aWarnings;
  }

  /**
   * @return the P0028File lob data
   */
  @Lob
  @Column(name = "DATA", nullable = false)
  @Size(max = 8000, message = "Maximum length of data cannot exceed 8000 characters")
  @NotNull
  public String getData()
  {
    return mData;
  }

  /**
   * @param aData the P0028File lob data
   */
  public void setData(String aData)
  {
    this.mData = aData;
  }

  /**
   * @return the P0028File
   */
  @ManyToOne
  @JoinColumn(name = "P0028_FILE_FK")
  @NotNull
  @ForeignKey(name = "FK_P0028_FILE_DATA_001")
  public P0028File getP0028File()
  {
    return mP0028File;
  }

  /**
   * @param aP0028File the P0028File
   */
  public void setP0028File(P0028File aP0028File)
  {
    this.mP0028File = aP0028File;
  }
}
