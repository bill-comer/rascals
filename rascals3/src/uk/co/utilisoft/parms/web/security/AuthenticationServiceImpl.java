package uk.co.utilisoft.parms.web.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

import uk.co.utilisoft.afms.dao.UserDao;
import uk.co.utilisoft.afms.domain.Authority;
import uk.co.utilisoft.afms.domain.Role;
import uk.co.utilisoft.afms.domain.Url;
import uk.co.utilisoft.afms.domain.UrlGroup;
import uk.co.utilisoft.afms.domain.User;
import uk.co.utilisoft.table.exception.FrameworkException;

/**
 * Implementaion of AuthenticationService interface.
 */
public class AuthenticationServiceImpl implements AuthenticationService
{
  /** Security. */
  /** Abbreviation used for System Administrator role. */
  public static final String SYS_ADMIN = "SYSADMIN";
  public static final String ROLE_PREFIX = "ROLE_";
  
  private UserDao mUserDao;
  private RoleDao mRoleDao;
  private UrlGroupDao mUrlGroupDao;

  /**
   * true if single sign on is enabled
   */
  private Boolean mUseSingleSignon = false;

  /**
   * @param aUserDao User Data Access Object
   * @param aRoleDao Role Data Access Object
   */
  public AuthenticationServiceImpl(UserDao aUserDao, RoleDao aRoleDao,
                                   UrlGroupDao aUrlGroupDao)
  {
    mUserDao = aUserDao;
    mRoleDao = aRoleDao;
    mUrlGroupDao = aUrlGroupDao;
  }

  /**
   * gets the single sign on flag
   * @return value of mUseSingleSignon
   */
  public Boolean getUseSingleSignon()
  {
    return mUseSingleSignon;
  }

  /**
   * sets the single sign on flag
   * @param aUseSingleSignon true means the controller will use single sign on
   */
  public void setUseSingleSignon(Boolean aUseSingleSignon)
  {
    this.mUseSingleSignon = aUseSingleSignon;
  }

  /**
   * Private default constructor.
   */
  @SuppressWarnings("unused")
  private AuthenticationServiceImpl()
  {
  }

  /**
   * Loads UserDetails required by Acegi security framework.
   * @see org.acegisecurity.userdetails.UserDetailsService
   *      #loadUserByUsername(java.lang.String)
   * @param aUserName User name
   * @return Acegi UserDetails object
   */
  public UserDetails loadUserByUsername(String aUserName)
  {
    UserDetails userDetails = null;
    User daoUser = null;
    try
    {
      if (getUseSingleSignon())
      {
    	daoUser = mUserDao.getCaseInsensitiveUser(aUserName);
      }
      else
      {
        daoUser = mUserDao.getUser(aUserName);
      }
      userDetails = transformUser(daoUser);
    }
    catch (RuntimeException nrfe)
    {
      throw new UsernameNotFoundException(nrfe.getMessage());
    }
    return userDetails;
  }

  /**
   * Transforms DFWeb User domain object to UserDetails object required by Acegi
   * security framework.
   * @param aUser User domain object
   * @return Acegi UserDetails object
   */
  private UserDetails transformUser(User aUser)
  {
    Set<Authority> authorities = aUser.getAuthorities();
    GrantedAuthority[] roles = transformAuthorities(authorities);
    UserDetails userDetails = new SecurityUser(aUser.getUserName(), aUser
        .getPassword(), aUser.getEmailAddress(), aUser.isEnabled(), true, true,
                                               true, roles);
    return userDetails;
  }

  /**
   * Transforms the Authorities held for a User on the database into
   * GrantedAuthority Strings in the format ROLE_rolename.
   * @param anAuthorities Set of Authorites held for User on the database
   * @return Array of GrantedAuthority Strings representing the Authorities held
   *         on the database
   */
  public GrantedAuthority[] transformAuthorities(Set<Authority> anAuthorities)
  {
    List<GrantedAuthority> grantedAuthorities =
      new ArrayList<GrantedAuthority>();
    Iterator<Authority> iterator = anAuthorities.iterator();
    while (iterator.hasNext())
    {
      Authority authority = iterator.next();
      String roleName = createRoleName(authority);
      if (!grantedAuthorities.contains(roleName))
      {
        grantedAuthorities.add(new GrantedAuthorityImpl(roleName));
      }

      try
      {
        Set<UrlGroup> urlGroups = null;
        if (authority.getRole().getShortRoleName()
            .equals(SYS_ADMIN))
        {
          urlGroups = new HashSet<UrlGroup>(mUrlGroupDao.getAll());
        }
        else
        {
          Role role = mRoleDao.getRoleIncChildren(authority.getRole().getShortRoleName());
          urlGroups = role.getPages();
        }

        Set<String> allowed = new HashSet<String>();
        for (UrlGroup urlGroup : urlGroups)
        {
          allowed.add(urlGroup.getCode());
          for (Url url : urlGroup.getUrls())
          {
            allowed.add(url.getUrl());
          }
        }
        for (String permission : allowed)
        {
          grantedAuthorities.add(new GrantedAuthorityImpl(permission));
        }
      }
      catch (RuntimeException nrfe)
      {
        throw new AuthenticationServiceException("Role no longer in db", nrfe);
      }
      catch (FrameworkException fe)
      {
        throw new AuthenticationServiceException("Error getting role", fe);
      }

    }
    return grantedAuthorities.toArray(new GrantedAuthorityImpl[] {});
  }

  /**
   * Returns String which contains ROLE_rolename.
   * @param anAuthority Authority domain object
   * @return Role as a String prefixed by ROLE_
   */
  private String createRoleName(Authority anAuthority)
  {
    StringBuffer roleName = new StringBuffer();
    roleName.append(ROLE_PREFIX);
    roleName.append(anAuthority.getRole().getShortRoleName());
    return roleName.toString();
  }
}
