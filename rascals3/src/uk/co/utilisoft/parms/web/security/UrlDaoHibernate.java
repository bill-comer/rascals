package uk.co.utilisoft.parms.web.security;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import uk.co.formfill.springutils.security.DbBasedUrl;
import uk.co.utilisoft.afms.domain.Url;
import uk.co.utilisoft.afms.domain.UrlGroup;

/**
 * @author ridyardb
 * @author Alison Verkroost
 */
@Repository("parms.urlDao")
public class UrlDaoHibernate extends HibernateDaoSupport implements UrlDao
{
  /**
   * @return Collection of URL domain objects
   * @see uk.co.formfill.springutils.security.DbBasedUrlDao#getAllUrls()
   */
  @SuppressWarnings("unchecked")
  public Collection<? extends DbBasedUrl> getAllUrls()
  {
    return (List<Url>) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback
         *      #doInHibernate(org.hibernate.Session)
         */
        public Object doInHibernate(Session aSession)
            throws HibernateException, SQLException
        {
          Criteria urlCriteria = aSession.createCriteria(Url.class);
          List<Url> urls = urlCriteria.list();
          for (Url url : urls)
          {
            getHibernateTemplate().initialize(url.getUrlGroups());
            for (UrlGroup urlGroup : url.getUrlGroups())
            {
              getHibernateTemplate().initialize(urlGroup.getRoles());
            }
          }
          return urls;
        }
      });
  }

  /**
   * @param aUrl Text version of URL
   * @return URL domain object which implements the DbBasedUrl interface
   * @see
   * uk.co.formfill.springutils.security.DbBasedUrlDao#getUrl(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public DbBasedUrl getUrl(final String aUrl)
  {
    return (Url) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback
         *      #doInHibernate(org.hibernate.Session)
         */
        public Object doInHibernate(Session aSession)
            throws HibernateException, SQLException
        {
          Criteria urlCriteria = aSession.createCriteria(Url.class);
          urlCriteria.add(Restrictions.eq("url", aUrl));
          List<Url> urls = urlCriteria.list();

          if (urls.isEmpty())
          {
            return null;
          }

          Url url = urls.get(0);
          getHibernateTemplate().initialize(url.getUrlGroups());
          for (UrlGroup urlGroup : url.getUrlGroups())
          {
            getHibernateTemplate().initialize(urlGroup.getRoles());
          }
          return url;
        }
      });
  }

  /**
   * @param aUrlIds Ids of Urls that should be retrieved from the db.
   * @return The required Urls from the database.
   * @see uk.co.utilisoft.afmsweb.data.Url.UrlDao
   *      #getAllUrlsExcept(java.lang.Long[])
   */
  @SuppressWarnings("unchecked")
  public Url[] getUrls(final Long[] aUrlIds)
  {
    if (aUrlIds != null && aUrlIds.length > 0)
    {
      final String querySql = "from Url where id in(:UrlIds)";

      return (Url[]) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback
         *      #doInHibernate(org.hibernate.Session)
         */
        public Object doInHibernate(Session aSession)
          throws HibernateException, SQLException
        {
          Query query = aSession.createQuery(querySql);
          query.setParameterList("UrlIds", aUrlIds);
          List urls = query.list();
          return urls.toArray(new Url[0]);
        }
      });
    }

    return new Url[]{};
  }


  /**
   * @param aUrlIds Ids of Urls that should not be retrieved from the db.
   * @return All Urls from the database, except those excluded.
   * @see uk.co.utilisoft.afmsweb.data.Url.UrlDao
   *      #getAllUrlsExcept(java.lang.Long[])
   */
  @SuppressWarnings("unchecked")
  public Url[] getAllUrlsExcept(final Long[] aUrlIds)
  {
    if (aUrlIds != null && aUrlIds.length > 0)
    {
      final String querySql = "from Url where id not in(:UrlIds)";

      return (Url[]) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback
         *      #doInHibernate(org.hibernate.Session)
         */
        public Object doInHibernate(Session aSession)
          throws HibernateException, SQLException
        {
          Query query = aSession.createQuery(querySql);
          query.setParameterList("UrlIds", aUrlIds);
          List urls = query.list();
          return urls.toArray(new Url[0]);
        }
      });
    }

    return getAllUrls().toArray(new Url[0]);
  }

  /**
   * @return All urls which should be allowed by default when a role is created.
   * @see uk.co.utilisoft.afmsweb.data.url.UrlDao#getAllowedByDefaultUrls()
   */
  @SuppressWarnings("unchecked")
  public List<Url> getAllowedByDefaultUrls()
  {
    Url example = new Url();
    example.setAllowByDefault(true);
    return getHibernateTemplate().findByExample(example);
  }
}
