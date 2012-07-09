package uk.co.utilisoft.afms.dao;

import java.util.Collection;
import java.util.List;

import uk.co.utilisoft.afms.domain.User;

/**
 * Data Access Object for an application User.
 *
 * @author ridyardb
 * @author Alison Verkroost
 * @version 1.0
 */
public interface UserDao
{
  /**
   * Returns user with specified ID.
   * @param aUserName User name
   * @return User domain object
   * @throws NoRecordFoundException if no record found with the specified ID
   */
  User getUser(String aUserName) ;

  /**
   * Returns user with specified username ignoring the case of the username
   * on the database and the input username.  Required for single sign on
   * because Windows Active Directory is case insensitive but returns the
   * username as stored in the directory, which can be in any case.
   * @param aUserName User name
   * @return User domain object
   * @throws NoRecordFoundException if no record found with the specified ID
   */
  User getCaseInsensitiveUser(String aUserName) ;

  /**
   * @return Collection of all User domain objects on the database
   */
  List<User> getAllUsers();

  /**
   * @param aIgnoreUsers The names of users to exclude from the search.
   * @return List of all User domain objects on the database, except for those
   *         specifically excluded.
   */
  List<User> getAllUsersExcept(Collection<String> aIgnoreUsers);

  /**
   * Retrieves all users according to the flags set.
   * @param isEnabledUsers if true, enabled users are included
   * @param isDisabledUsers if true, disabled users are included
   * @return Collection of User domain objects
   */
  Collection<User> getUsers(Boolean isEnabledUsers, Boolean isDisabledUsers);

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
  Collection<User> getUsers(Boolean isEnabledUsers, Boolean isDisabledUsers,
                            Integer aFirstRec, Integer aMaxRec,
                            String aSortField, String aSortOrder);

  /**
   * Counts number of users meeting criteria set by flags.
   * @param isEnabledUsers if true, enabled users are included
   * @param isDisabledUsers if true, disabled users are included
   * @return Count of User domain objects meeting criteria
   */
  Integer getUserCount(Boolean isEnabledUsers,
                       Boolean isDisabledUsers);

  /**
   * Inserts a new or updates an existing User domain object.
   * @param aUser User domain object
   */
  void saveOrUpdateUser(User aUser);

  /**
   * @param aUsername The username to check.
   * @param aIncludeDeleted
   *        True if deleted users should be included in the check.
   * @return True if a user with the given name exists, false otherwise.
   */
  boolean userExists(String aUsername, boolean aIncludeDeleted);
}
