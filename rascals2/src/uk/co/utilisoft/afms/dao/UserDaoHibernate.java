package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.afms.domain.User;

/**
 * Implementation of UserDao using Hibernate.
 *
 * @author ridyardb
 * @author Alison Verkroost
 * @version 1.0
 */
@Repository("parms.userDao")
public class UserDaoHibernate extends AfmsGenericDao<User, Long>  implements UserDao
{
  /**
   * Returns user with specified user name.  Assumes that only one record is
   * allowed to exist with that user name.
   * @param aUserName User name
   * @return User domain object
   * @throws NoRecordFoundException if no record found with the specified ID
   */
  @SuppressWarnings("unchecked")
  public User getUser(String aUserName) 
  {
    String queryString =
      "from User u where u.userName = ? and u.endDate is null";
    List<User> users = getHibernateTemplate().find(queryString, aUserName);
    if (users.isEmpty())
    {
      throw new RuntimeException("No user found with name[" + aUserName
          + "]");
    }
    User user = users.get(0);
    return user;
  }

  /**
   * Returns user with specified username ignoring the case of the username
   * on the database and the input username.  Required for single sign on
   * because Windows Active Directory is case insensitive but returns the
   * username as stored in the directory, which can be in any case.
   * @param aUserName User name
   * @return User domain object
   * @throws NoRecordFoundException if no record found with the specified ID
   */
  public User getCaseInsensitiveUser(String aUserName)
  {
    String queryString = "from User u where upper(u.userName) = ? and u.endDate is null";
    List users = getHibernateTemplate().find(queryString,
                                             aUserName.toUpperCase());
    if (users.isEmpty())
    {
      throw new RuntimeException("No user found with name[" + aUserName
          + "]");
    }
    User user = (User) users.get(0);
    return user;
  }

  /**
   * @return Collection of all User domain objects on the database
   */
  @SuppressWarnings("unchecked")
  public List<User> getAllUsers()
  {
    String queryString =
      "from User u where u.endDate is null order by u.userName";
    return getHibernateTemplate().find(queryString);
  }

  /**
   * @param aIgnoreUsers The names of users to exclude from the search.
   * @return List of all User domain objects on the database, except for those
   *         specifically excluded.
   * @see uk.co.utilisoft.afms.dao.webprojecttemplate.data.user.UserDao
   *      #getAllUsersExcept(Collection)
   */
  @SuppressWarnings("unchecked")
  public List<User> getAllUsersExcept(final Collection<String> aIgnoreUsers)
  {
    if (aIgnoreUsers == null || aIgnoreUsers.size() == 0)
    {
      return getAllUsers();
    }

    Object results = getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback
       *      #doInHibernate(org.hibernate.Session)
       */
      public Object doInHibernate(Session aSession)
        throws HibernateException, SQLException
      {
        Criteria criteria = aSession.createCriteria(User.class);
        criteria.add(
          Restrictions.not(Restrictions.in("userName", aIgnoreUsers)));
        criteria.add(Restrictions.isNull("endDate"));
        criteria.addOrder(Order.asc("userName"));
        return criteria.list();
      }
    });

    return (List<User>) results;
  }

  /**
   * Inserts a new or updates an existing User domain object.
   * @param aUser User domain object
   */
  public void saveOrUpdateUser(User aUser)
  {
     getHibernateTemplate().saveOrUpdate(aUser);
  }

  /**
   * Retrieves all users according to the flag set.
   * @param isEnabledUsers if true, enabled users are included
   * @param isDisabledUsers if true, disabled users are included
   * @return Collection of User domain objects
   */
  @SuppressWarnings("unchecked")
  public Collection<User> getUsers(Boolean isEnabledUsers,
                                   Boolean isDisabledUsers)
  {
    Collection<User> results = new ArrayList<User>();
    if (!isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      return results;
    }

    StringBuffer query = new StringBuffer();
    query.append("from User u ");
    if (isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      query.append("where u.enabled = 1 and u.endDate is null");
    }
    else if (!isEnabledUsers.booleanValue() && isDisabledUsers.booleanValue())
    {
      query.append("where u.enabled = 0 and u.endDate is null");
    }
    else
    {
      query.append("where u.endDate is null");
    }
    results = getHibernateTemplate().find(query.toString());
    return results;
  }

  /**
   * Retrieves users according to the flags set for number of records and
   * in the order specified.
   * @param isEnabledUsers if true, enabled users are included
   * @param isDisabledUsers if true, disabled users are included
   * @param aFirstRec First record to be retrieved
   * @param aMaxRec Maximum number of records to be retrieved
   * @param aSortField Field to sort results by
   * @param aSortOrder asc or desc
   * @return Collection of User domain objects
   */
  @SuppressWarnings("unchecked")
  public Collection<User> getUsers(Boolean isEnabledUsers,
                                   Boolean isDisabledUsers,
                                   Integer aFirstRec, Integer aMaxRec,
                                   String aSortField, String aSortOrder)
  {
    Collection<User> results = new ArrayList<User>();
    if (!isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      return results;
    }

    StringBuffer queryString = new StringBuffer();
    queryString.append("from User u ");
    if (isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      queryString.append("where u.enabled = 1 and u.endDate is null ");
    }
    else if (!isEnabledUsers.booleanValue() && isDisabledUsers.booleanValue())
    {
      queryString.append("where u.enabled = 0 and u.endDate is null ");
    }
    else
    {
      queryString.append("where u.endDate is null ");
    }
    if (aSortField != null && aSortOrder != null)
    {
      queryString.append("order by u." + aSortField + " " + aSortOrder);
    }
    Query query
    = getSession().createQuery(queryString.toString())
    .setFirstResult(aFirstRec.intValue())
    .setMaxResults(aMaxRec.intValue());
    results = query.list();
    return results;
  }

  /**
   * Counts number of users meeting criteria set by flags.
   * @param isEnabledUsers if true, enabled users are included
   * @param isDisabledUsers if true, disabled users are included
   * @return Count of User domain objects meeting criteria
   */
  public Integer getUserCount(Boolean isEnabledUsers,
                              Boolean isDisabledUsers)
  {
    if (!isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      return new Integer(0);
    }

    StringBuffer query = new StringBuffer();
    query.append("select count(*) from User u ");
    if (isEnabledUsers.booleanValue() && !isDisabledUsers.booleanValue())
    {
      query.append("where u.enabled = 1 and u.endDate is null");
    }
    else if (!isEnabledUsers.booleanValue() && isDisabledUsers.booleanValue())
    {
      query.append("where u.enabled = 0 and u.endDate is null");
    }
    else
    {
      query.append("where u.endDate is null");
    }
    return (Integer) getHibernateTemplate().find(query.toString()).get(0);
  }

  /**
   * @param aUsername The username to check.
   * @param aIncludeDeleted
   *        True if deleted users should be included in the check.
   * @return True if a user with the given name exists, false otherwise.
   * @see uk.co.utilisoft.afms.dao.webprojecttemplate.data.user.UserDao
   *      #userExists(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public boolean userExists(String aUsername, boolean aIncludeDeleted)
  {
    StringBuffer query = new StringBuffer(
      "select count(distinct username) from User where userName = ?");
    if (!aIncludeDeleted)
    {
      query.append(" and endDate is null");
    }
    String queryString = query.toString();

    List<Number> results = getHibernateTemplate().find
                                                      (queryString, aUsername);
    Number count = results.get(0);

    Logger.getLogger(getClass()).debug("Number of users with name " + aUsername
                                       + ": " + count);
    return count.intValue() > 0;
  }
}
