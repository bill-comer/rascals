package uk.co.utilisoft.parms.web.security;

import java.util.List;

import uk.co.utilisoft.afms.domain.UrlGroup;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
public interface UrlGroupDao extends ReadOnlyGenericDao<UrlGroup, Long>
{
  /**
   * @param aUrlGroupIds Ids of UrlGroupss that should be retrieved from the db.
   * @return All the required UrlGroups from the database
   */
  List<UrlGroup> getUrlGroups(Long[] aUrlGroupIds);

  /**
   * @param aUrlGroupIds Ids of UrlGroupss that should not be retrieved from
   *        the db.
   * @return All UrlGroups from the database, except those excluded.
   */
  UrlGroup[] getAllUrlGroupsExcept(Long[] aUrlGroupIds);

  /**
   * @return All UrlGroups which should be allowed by default when a role
   *         is created.
   */
  List<UrlGroup> getAllowedByDefaultUrlGroups();

  /**
   * @return UrlGroups which are always required.
   */
  List<UrlGroup> getAlwaysRequiredUrlGroups();
}
