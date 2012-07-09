package uk.co.utilisoft.parms.web.security;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;

/**
 * User class used to implement Acegi authentication.
 * @author Bev Ridyard
 * @version 1.0
 */
public class SecurityUser extends User
{
  private static final long serialVersionUID = 1L;
  private String mEmailAddress;

  /**
   * Constructor.
   * @param aUsername User name
   * @param aPassword Password
   * @param aEmailAddress Email address
   * @param isEnabled Whether user is enabled
   * @param isAccountNonExpired Whether account has not expired
   * @param isCredentialsNonExpired Whether credentials have not expired
   * @param isAccountNonLocked Whether account is not locked
   * @param aAuthorities Authorities which user owns
   */
  public SecurityUser(String aUsername, String aPassword, String aEmailAddress,
                      boolean isEnabled, boolean isAccountNonExpired,
                      boolean isCredentialsNonExpired,
                      boolean isAccountNonLocked,
                      GrantedAuthority[] aAuthorities)
  {
    super(aUsername, aPassword, isEnabled, isAccountNonExpired,
        isCredentialsNonExpired, isAccountNonLocked, aAuthorities);
    setEmailAddress(aEmailAddress);
  }

  /**
   * @param aEmailAddress Email address
   */
  private void setEmailAddress(String aEmailAddress)
  {
    mEmailAddress = aEmailAddress;
  }

  /**
   * @return Email address
   */
  public String getEmailAddress()
  {
    return mEmailAddress;
  }

}
