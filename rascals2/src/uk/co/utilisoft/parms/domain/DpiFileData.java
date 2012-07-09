package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name="PARMS_DPI_FILE_DATA")
@SuppressWarnings("serial")
public class DpiFileData extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mData;
  private DpiFile mDpiFile;

  public DpiFileData() { }

  public DpiFileData(String aData, DpiFile aDpiFile)
  {
    mData = aData;
    mDpiFile = aDpiFile;
    setLastUpdated(new DateTime());
  }

  /**
   * @return the clob data as text
   */
  @Lob
  @Column(name="DATA",nullable=true)
  public String getData()
  {
    return mData;
  }

  /**
   * @param mData the clob data as text
   */
  public void setData(String mData)
  {
    this.mData = mData;
  }

  /**
   * @return the DPI File
   */
  @ManyToOne
  @JoinColumn(name = "DPI_FILE_FK")
  @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
  @NotNull
  public DpiFile getDpiFile()
  {
    return mDpiFile;
  }

  /**
   * @param aDpiFile the DPI File
   */
  public void setDpiFile(DpiFile aDpiFile)
  {
    this.mDpiFile = aDpiFile;
  }

  @Transient
  public String getDisplayableData()
  {
    String displayData = getData().replaceAll("\\r\\n", "<BR>");
    return displayData;
  }
}
