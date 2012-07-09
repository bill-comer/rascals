package uk.co.utilisoft.parms.web.security;

import java.util.List;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.afms.domain.UrlGroup;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
@Repository("parms.urlGroupDao")
public class UrlGroupDaoHibernate
  extends ReadOnlyGenericDaoHibernate<UrlGroup, Long> implements UrlGroupDao
{
  /**
   * Constructor.
   */
  public UrlGroupDaoHibernate()
  {
    super(UrlGroup.class);
  }

  /**
   * @param aUrlGroupIds Ids of UrlGroupss that should be retrieved from the db.
   * @return All the required UrlGroups from the database
   */
  @SuppressWarnings("unchecked")
  public List<UrlGroup> getUrlGroups(Long[] aUrlGroupIds)
  {
    return getHibernateTemplate().findByNamedQueryAndNamedParam(
      "getUrlGroups", "urlGroupIds", aUrlGroupIds);
  }

  /**
   * @param aUrlGroupIds Ids of UrlGroupss that should not be retrieved from
   *        the db.
   * @return All UrlGroups from the database, except those excluded.
   */
  @SuppressWarnings("unchecked")
  public UrlGroup[] getAllUrlGroupsExcept(Long[] aUrlGroupIds)
  {
    if (aUrlGroupIds.length == 0)
    {
      return getAll().toArray(new UrlGroup[0]);
    }

    return (UrlGroup[]) getHibernateTemplate().findByNamedQueryAndNamedParam(
      "getAllUrlGroupsExcept", "urlGroupIds", aUrlGroupIds)
      .toArray(new UrlGroup[0]);
  }

  /**
   * @return All UrlGroups which should be allowed by default when a role
   *         is created.
   */
  @SuppressWarnings("unchecked")
  public List<UrlGroup> getAllowedByDefaultUrlGroups()
  {
    return
      getHibernateTemplate().findByNamedQuery("getAllowedByDefaultUrlGroups");
  }

  /**
   * @return UrlGroups which are always required.
   */
  @SuppressWarnings("unchecked")
  public List<UrlGroup> getAlwaysRequiredUrlGroups()
  {
    return
      getHibernateTemplate().findByNamedQuery("getAlwaysRequiredUrlGroups");
  }

}
