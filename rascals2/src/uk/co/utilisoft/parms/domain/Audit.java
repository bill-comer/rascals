package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name = "PARMS_AUDIT")
@SuppressWarnings("serial")
public class Audit extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private Long mUserFk;
  private Audit.TYPE mType;
  private String mDetails;

  /**
   * Default Constructor
   */
  public Audit()
  {
  }

  /**
   * @param aAuditType the Audit type
   */
  public Audit(Audit.TYPE aAuditType)
  {
    mType = aAuditType;
    setLastUpdated(new DateTime());
  }

  /**
   * @return the user primary key
   */
  @Column(name = "USER_FK")
  @NotNull
  public Long getUserFk()
  {
    return mUserFk;
  }

  /**
   * @param aUserFk the user primary key
   */
  public void setUserFk(Long aUserFk)
  {
    mUserFk = aUserFk;
  }

  /**
   * @return the Audit type
   */
  @Column(name = "ACTION_AUDIT_CODE", length = 255)
  @Enumerated(EnumType.STRING)
  @NotNull
  public Audit.TYPE getType()
  {
    return mType;
  }

  /**
   * @param aAuditType the Audit type
   */
  public void setType(Audit.TYPE aAuditType)
  {
    mType = aAuditType;
  }

  /**
   * @return the audit details
   */
  @Lob
  @Column(name = "DETAILS", nullable = true)
  @Size(max = 8000, message = "Maximum length of Audit Details cannot exceed 8000 characters")
  public String getDetails()
  {
    return mDetails;
  }

  /**
   * @param aDetails the audit details
   */
  public void setDetails(String aDetails)
  {
    mDetails = aDetails;
  }

  public static enum TYPE
  {
    DPI_GENERATE_REPORT("DPI Generate Report"),
    DPI_DOWNLOAD_FILE("DPI Download File"),
    DPI_REPLICATE("DPI Replicate"),
    DPI_EDIT_REPLICATED_FILE("DPI Edit Of Replicated File"),
    P0028_UPLOAD("P0028 Upload"),
    P0028_DOWNLOAD_ERROR_REPORT("P0028 Download Error Report"),
    P0028_UPLOAD_GENERATE_WARNINGS("P0028 Upload Generate Warnings"),
    P0028_UPLOAD_DOWNLOAD_WARNINGS("P0028 Upload Download Warnings"),
    SP04_GENERATE("SP04 Generate"),
    SP04_GENERATE_EXCLUDE_MPANS("SP04 Generate Exclude MPANs"),
    SP04_DOWNLOAD_REPORTS("SP04 Download Reports"),
    ADMIN_PARAM_CHANGE("Admin Parameter Changes"),
    SP04_AFMS_DATA_RUN("Get Elligable AFMS Mpans for Sp04 Inclusion"),
    SP04_AFMS_DATA_REMOVE_MPAN("MPAN(s) Eligible for SP04 Reporting"),
    SP04_AFMS_DATA_ADD_MPAN("Add MPAN to SP03Afms List");

    private String mDescription;

    private TYPE(String aDescription)
    {
      mDescription = aDescription;
    }

    /**
     * @return the description of the Audit
     */
    public String getDescription()
    {
      return mDescription;
    }

    /**
     * @param aDescription the description of the Audit
     */
    public void setDescription(String aDescription)
    {
      mDescription = aDescription;
    }
  }
}