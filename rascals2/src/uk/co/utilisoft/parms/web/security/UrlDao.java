package uk.co.utilisoft.parms.web.security;

import java.util.List;

import uk.co.formfill.springutils.security.DbBasedUrlDao;
import uk.co.utilisoft.afms.domain.Url;

/**
 * @author ridyardb
 * @author Alison Verkroost
 *
 */
public interface UrlDao extends DbBasedUrlDao
{
  /**
   * @param aUrlIds Ids of Urls that should be retrieved from the db.
   * @return The required Urls from the database.
   */
  Url[] getUrls(Long[] aUrlIds);

  /**
   * @param aUrlIds Ids of Urls that should not be retrieved from the db.
   * @return All Urls from the database, except those excluded.
   */
  Url[] getAllUrlsExcept(Long[] aUrlIds);

  /**
   * @return All urls which should be allowed by default when a user is created.
   */
  List<Url> getAllowedByDefaultUrls();
}
