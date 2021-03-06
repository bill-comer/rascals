package uk.co.utilisoft.afms.domain;
// Generated 26-Nov-2010 10:40:14 by Hibernate Tools 3.1.0.beta5
// with HibernateUtils.


import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Authority generated by hbm2java.
 */
@Entity
@Table(name="AMOPS_AUTHORITY")
@SuppressWarnings("serial")
public class Authority  implements java.io.Serializable
{
  // Fields

  private Long mAuthorityPk;
  private Calendar mLastUpdated;
  private User mUser;
  private Role mRole;

  // Constructors

  /** Default constructor. */
  public Authority()
  {
  }


  /**
   * Full constructor.
   * @param aUser The required value
   * @param aRole The required value
   */
  public Authority(User aUser, Role aRole)
  {
    mUser = aUser;
    mRole = aRole;
  }

  // Property accessors and mutators

  /**
   * @return authorityPk
   */
  @Id
  @Column(name="AUTHORITY_PK", nullable=false)
  public Long getAuthorityPk()
  {
      return mAuthorityPk;
  }
  /**
   * @param aAuthorityPk The required value
   */
  public void setAuthorityPk(Long aAuthorityPk)
  {
      mAuthorityPk = aAuthorityPk;
  }

  /**
   * @return lastUpdated
   */
  @Column(name="LAST_UPD", nullable=false)
  public Calendar getLastUpdated()
  {
      return mLastUpdated;
  }
  /**
   * @param aLastUpdated The required value
   */
  public void setLastUpdated(Calendar aLastUpdated)
  {
      mLastUpdated = aLastUpdated;
  }

  /**
   * @return user
   */  
  @ManyToOne(fetch=FetchType.EAGER, optional=false)
  @JoinColumn(name="USER_FK", nullable=true)
  public User getUser()
  {
      return mUser;
  }
  /**
   * @param aUser The required value
   */
  public void setUser(User aUser)
  {
      mUser = aUser;
  }

  /**
   * @return role
   */
  @ManyToOne(fetch=FetchType.EAGER, optional=false)
  @JoinColumn(name="ROLE_FK", nullable=true)
  public Role getRole()
  {
      return mRole;
  }
  /**
   * @param aRole The required value
   */
  public void setRole(Role aRole)
  {
      mRole = aRole;
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
    buffer.append("authorityPk").append("='")
      .append(getAuthorityPk()).append("' ");
    buffer.append("lastUpdated").append("='")
      .append(getLastUpdated()).append("' ");
    buffer.append("user").append("='")
      .append(getUser()).append("' ");
    buffer.append("role").append("='")
      .append(getRole()).append("' ");
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
    if (!(aOther instanceof Authority))
    {
      return false;
    }

    Authority castOther = (Authority) aOther;

    return equality(this.getAuthorityPk(), castOther.getAuthorityPk())
      && equality(this.getLastUpdated(), castOther.getLastUpdated())
      && equality(this.getUser(), castOther.getUser())
      && equality(this.getRole(), castOther.getRole());
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

    if (getAuthorityPk() != null)
    {
      result = 37 * result + getAuthorityPk().hashCode();
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

    if (getUser() != null)
    {
      result = 37 * result + getUser().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    if (getRole() != null)
    {
      result = 37 * result + getRole().hashCode();
    }
    else
    {
      result = 37 * result;
    }

    return result;
  }

  // The following is extra code specified in the hbm.xml files

  private static final long serialVersionUID = 1L;

  // end of extra code specified in the hbm.xml files

}


