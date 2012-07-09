package uk.co.utilisoft.afms.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;

/**
 * 
 */
@Entity
@Table(name="AMOPS_USER")
@DiscriminatorColumn(name = "ENABLED", discriminatorType = DiscriminatorType.INTEGER)
public class User extends AFMSDomainObject
{
  // Fields

  private Long mUserPk;
  private String mUserName;
  private String mPassword;
  private String mEmailAddress;
  private String mPhoneNumber;
  private Boolean mEnabled;
  private Set<Authority> mAuthorities = new HashSet<Authority>(0);
  private DateTime mEndDate;

  /** Default constructor. */
  public User()
  {
  }

  /**
   * Minimal constructor.
   * @param aUserName The required value
   * @param aPassword The required value
   * @param aEnabled The required value
   */
  public User(String aUserName, String aPassword, Boolean aEnabled)
  {
    mUserName = aUserName;
    mPassword = aPassword;
    mEnabled = aEnabled;
  }

  /**
   * Full constructor.
   * @param aUserName The required value
   * @param aPassword The required value
   * @param aEmailAddress The required value
   * @param aPhoneNumber The required value
   * @param aEnabled The required value
   * @param aAuthorities The required value
   * @param aEndDate The required value
   */
  public User(String aUserName, String aPassword, String aEmailAddress,
    String aPhoneNumber, Boolean aEnabled, Set<Authority> aAuthorities,
    DateTime aEndDate)
  {
    mUserName = aUserName;
    mPassword = aPassword;
    mEmailAddress = aEmailAddress;
    mPhoneNumber = aPhoneNumber;
    mEnabled = aEnabled;
    mAuthorities = aAuthorities;
    mEndDate = aEndDate;
  }

  /**
   * @return userPk
   */
  @Id
  @Column(name="USER_PK", nullable = false)
  public Long getPk()
  {
      return mUserPk;
  }
  /**
   * @param aUserPk The required value
   */
  public void setPk(Long aUserPk)
  {
      mUserPk = aUserPk;
  }

  /**
   * @return userName
   */
  @Column(name = "USERNAME", nullable = false)
  public String getUserName()
  {
      return mUserName;
  }
  /**
   * @param aUserName The required value
   */
  public void setUserName(String aUserName)
  {
      mUserName = aUserName;
  }

  /**
   * @return password
   */
  @Column(name = "PASSWORD", nullable = false)
  public String getPassword()
  {
      return mPassword;
  }
  
  /**
   * @param aPassword The required value
   */
  public void setPassword(String aPassword)
  {
      mPassword = aPassword;
  }

  /**
   * @return emailAddress
   */
  @Column(name = "EMAILADDRESS", nullable = false)
  public String getEmailAddress()
  {
      return mEmailAddress;
  }
  /**
   * @param aEmailAddress The required value
   */
  public void setEmailAddress(String aEmailAddress)
  {
      mEmailAddress = aEmailAddress;
  }

  /**
   * @return phoneNumber
   */
  @Column(name = "PHONENUMBER", nullable = false)
  public String getPhoneNumber()
  {
      return mPhoneNumber;
  }
  /**
   * @param aPhoneNumber The required value
   */
  public void setPhoneNumber(String aPhoneNumber)
  {
      mPhoneNumber = aPhoneNumber;
  }

  /**
   * @return enabled
   */
  @Column(name = "ENABLED", nullable = false)
  public Boolean isEnabled()
  {
      return mEnabled;
  }
  
  /**
   * @param aEnabled The required value
   */
  public void setEnabled(boolean aEnabled)
  {
      mEnabled = aEnabled;
  }

  /**
   * @return authorities
   */
  @OneToMany(fetch = FetchType.EAGER, mappedBy="user")
  public Set<Authority> getAuthorities()
  {
      return mAuthorities;
  }
  
  /**
   * @param aAuthorities The required value
   */
  public void setAuthorities(Set<Authority> aAuthorities)
  {
      mAuthorities = aAuthorities;
  }

  /**
   * @return endDate
   */
  @Column(name = "END_DATE", nullable = false)
  public DateTime getEndDate()
  {
      return mEndDate;
  }
  
  /**
   * @param aEndDate The required value
   */
  public void setEndDate(DateTime aEndDate)
  {
      mEndDate = aEndDate;
  }

  /**
   * @see java.lang.Object#toString()
   * @return A string representation of the object
   */
   @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(getClass().getName()).append("@")
      .append(Integer.toHexString(hashCode())).append(" [");
    buffer.append("lastUpdated").append("='")
      .append(getLastUpdated()).append("' ");
    buffer.append("userName").append("='")
      .append(getUserName()).append("' ");
    buffer.append("emailAddress").append("='")
      .append(getEmailAddress()).append("' ");
    buffer.append("phoneNumber").append("='")
      .append(getPhoneNumber()).append("' ");
    buffer.append("enabled").append("='")
      .append(isEnabled()).append("' ");
    buffer.append("endDate").append("='")
      .append(getEndDate()).append("' ");
    buffer.append("]");

    return buffer.toString();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   * @param aOther The reference object with which to compare
   * @return True if this object is the same as the aOther
   *         argument; false otherwise
   */
  @Override
  public boolean equals(Object aOther)
  {
    if (this == aOther)
    {
      return true;
    }
    if (aOther == null)
    {
      return false;
    }
    if (!(aOther instanceof User))
    {
      return false;
    }

    User castOther = (User) aOther;

    return equality(this.getPk(), castOther.getPk())
      && equality(this.getLastUpdated(), castOther.getLastUpdated())
      && equality(this.getUserName(), castOther.getUserName())
      && equality(this.getPassword(), castOther.getPassword())
      && equality(this.getEmailAddress(), castOther.getEmailAddress())
      && equality(this.getPhoneNumber(), castOther.getPhoneNumber())
      && (this.isEnabled() == castOther.isEnabled());
  }

  private boolean equality(Object aFirstObject, Object aSecondObject)
  {
    return
      (aFirstObject == null) == (aSecondObject == null)
        && (aFirstObject != null && aFirstObject.equals(aSecondObject))
          || aFirstObject == null;
  }

  /**
   * @see java.lang.Object#hashCode()
   * @return A hash code value for this object.
   */
  @Override
  public int hashCode()
  {
    int result = 17;

    if (getPk() != null)
    {
      result = 37 * result + getPk().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getLastUpdated() != null)
    {
      result = 37 * result + getLastUpdated().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getUserName() != null)
    {
      result = 37 * result + getUserName().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getPassword() != null)
    {
      result = 37 * result + getPassword().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getEmailAddress() != null)
    {
      result = 37 * result + getEmailAddress().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getPhoneNumber() != null)
    {
      result = 37 * result + getPhoneNumber().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (isEnabled())
    {
      result = result + 1;
    }

    return result;
  }

  // The following is extra code specified in the hbm.xml files

  private static final long serialVersionUID = 1L;


  // end of extra code specified in the hbm.xml files

}


