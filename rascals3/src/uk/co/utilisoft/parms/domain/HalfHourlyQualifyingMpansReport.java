package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name = "PARMS_HH_QULFY_MPANS_REPORT")
@SuppressWarnings("serial")
public class HalfHourlyQualifyingMpansReport extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  public static final String[] HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER
    = new String[] {"NHH DC ID", "MPAN", "Meter Serial ID", "Meter Installation Deadline", "Maximum Demand",
    "Meter Reading Date 1", "Meter Reading 1", "Meter Reading Date 2", "Meter Reading 2", "Meter Reading Date 3",
    "Meter Reading 3"};

  public static final String[] P0028_HALF_HOURLY_QUALIFYING_MPANS_REPORT_HEADER
    = new String[] {"NHH DC ID", "MPAN", "Meter Serial ID", "Meter Installation Deadline", "Maximum Demand",
    "Meter Reading Date"};

  private String mData;
  private String mFileName;
  private DateTime mDateCreated;

  /**
   * @return the report data
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
   * @param aData the report data
   */
  public void setData(String aData)
  {
    mData = aData;
  }

  /**
   * @return the report file name
   */
  @Column(name="FILENAME")
  @NotEmpty
  @Length( max = 100 )
  public String getFileName()
  {
    return mFileName;
  }

  /**
   * @param aFileName the report file name
   */
  public void setFileName(String aFileName)
  {
    mFileName = aFileName;
  }

  /**
   * @return the date when the report was created
   */
  @Column(name="DATE_CREATED")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getDateCreated()
  {
    return mDateCreated;
  }

  /**
   * @param aDateCreated the date when the report was created
   */
  public void setDateCreated(DateTime aDateCreated)
  {
    mDateCreated = aDateCreated;
  }

}
