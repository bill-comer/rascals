package uk.co.utilisoft.parms.web.security;

import java.util.Set;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

import uk.co.utilisoft.afms.domain.Authority;

/**
 * Interface of service which provides a facade for all data access
 * operations associated with Users.
 */
public interface AuthenticationService extends UserDetailsService
{
  /**
   * Used by Acegi security framework to load UserDetails.
   * @see org.acegisecurity.userdetails.UserDetailsService
   * #loadUserByUsername(java.lang.String)
   * @param aUserName User name
   * @return UserDetails object containing authorities and user details
   */
  UserDetails loadUserByUsername(String aUserName);

  /**
   * Transforms the Authorities held for a User on the database into
   * GrantedAuthority Strings in the format ROLE_rolename.
   * @param anAuthorities Set of Authorites held for User on the database
   * @return Array of GrantedAuthority Strings representing the Authorities held
   *         on the database
   */
  GrantedAuthority[] transformAuthorities(Set<Authority> anAuthorities);
}

