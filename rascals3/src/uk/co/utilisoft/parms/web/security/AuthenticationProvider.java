package uk.co.utilisoft.parms.web.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

/**
 * @author comerb
 */
public class AuthenticationProvider extends
    AbstractUserDetailsAuthenticationProvider
{
  private UserDetailsService mUserDetailsService;

  @Override
  protected UserDetails retrieveUser(String aUsername,
      UsernamePasswordAuthenticationToken aAuthentication)
      throws AuthenticationException
  {
    UserDetails loadedUser;
    try
    {
      loadedUser = this.getUserDetailsService().loadUserByUsername(aUsername);
    }
    catch (DataAccessException repositoryProblem)
    {
      throw new AuthenticationServiceException(repositoryProblem.getMessage(),
          repositoryProblem);
    }
    if (loadedUser == null)
    {
      throw new AuthenticationServiceException("User cannot be null");
    }
    return loadedUser;
  }


  public UserDetailsService getUserDetailsService()
  {
    return mUserDetailsService;
  }

  /**
   * saxasa
   * @param userDetailsService safd
   */
  public void setUserDetailsService(UserDetailsService userDetailsService)
  {
    this.mUserDetailsService = userDetailsService;
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException
  {
  }
}
