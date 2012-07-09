package uk.co.utilisoft.parms.web.security;

import java.util.List;

import uk.co.utilisoft.afms.domain.Role;
import uk.co.utilisoft.table.exception.FrameworkException;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
public interface RoleDao
{
  /**
   * @return All authorities from the database.
   */
  List<Role> getAllRoles();

  /**
   * @param aRoleId The pk of the required Role.
   * @return The required Role.
   * @throws NoRecordFoundException If the pk does not exist.
   */
  Role getRole(String aRoleId) ;

  /**
   * @param aRoleId The pk of the required Role.
   * @return The required Role.
   * @throws NoRecordFoundException If the pk does not exist.
   * @throws FrameworkException If things go wrong.
   */
  Role getRoleIncChildren(String aRoleId)
    throws FrameworkException;

  /**
   * @param aRoleIds The pks of the required Role.
   * @return The required authorities.
   */
  Role[] getRoles(String[] aRoleIds);

  /**
   * @param aRole The Role to insert or update.
   */
  void saveOrUpdateRole(Role aRole);

  /**
   * @param aName The name of the Role to check.
   * @param aIncludeDeleted
   *        True if deleted roles should be included in the check.
   * @return True if the Role exists, false otherwise.
   */
  boolean roleExists(String aName, boolean aIncludeDeleted);
}
