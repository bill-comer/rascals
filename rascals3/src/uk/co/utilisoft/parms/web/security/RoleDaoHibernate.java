package uk.co.utilisoft.parms.web.security;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.afms.domain.Role;
import uk.co.utilisoft.table.exception.FrameworkException;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
@Repository("parms.roleDao")
public class RoleDaoHibernate extends HibernateDaoSupport
  implements RoleDao
{

  /**
   * @return All authorities from the database.
   * @see uk.co.utilisoft.webprojecttemplate.data.role.RoleDao
   *      #getAllRoles()
   */
  @SuppressWarnings("unchecked")
  public List<Role> getAllRoles()
  {
    String queryString =
      "from Role r where r.endDate is null order by r.shortRoleName";
    return getHibernateTemplate().find(queryString);
  }

  /**
   * @param aRoleId The pk of the required Role.
   * @return The required Role.
   * @throws NoRecordFoundException If the pk does not exist.
   * @see uk.co.utilisoft.webprojecttemplate.data.role.RoleDao
   *      #getRole(java.lang.Long)
   */
  @SuppressWarnings("unchecked")
  public Role getRole(String aRoleId) 
  {
    List<Role> roles = getHibernateTemplate().find(
      "from Role where endDate is null and shortRoleName = ?", aRoleId);
    if (roles.size() < 1)
    {
      throw new RuntimeException("No Role found with name[" + aRoleId + "]");
    }

    return roles.get(0);
  }

  /**
   * @param aRoleId The pk of the required Role.
   * @return The required Role.
   * @throws NoRecordFoundException If the pk does not exist.
   * @throws FrameworkException If things go wrong.
   */
  public Role getRoleIncChildren(final String aRoleId)
    throws  FrameworkException
  {
    Object result = getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback
       *      #doInHibernate(org.hibernate.Session)
       */
      public Object doInHibernate(Session aSession)
        throws HibernateException, SQLException
      {
        Criteria criteria = aSession.createCriteria(Role.class);
        criteria.add(Restrictions.eq("shortRoleName", aRoleId));
        criteria.add(Restrictions.isNull("endDate"));

        Role role = (Role) criteria.uniqueResult();

        if (role != null)
        {
          getHibernateTemplate().initialize(role.getUsers());
          getHibernateTemplate().initialize(role.getPages());
          return role;
        }

        return new RuntimeException("No Role found with name[" + aRoleId + "]");
      }
    });

    if (result instanceof Role)
    {
      return (Role) result;
    }
    else
    {
      throw new FrameworkException("Unexpected return type: "
                                   + result.getClass());
    }
  }

  /**
   * @param aRoleIds The pks of the required Role.
   * @return The required authorities.
   * @see uk.co.utilisoft.webprojecttemplate.data.role.RoleDao
   *      #getRoles(String[])
   */
  @SuppressWarnings("unchecked")
  public Role[] getRoles(final String[] aRoleIds)
  {
    if (aRoleIds.length < 1)
    {
      return new Role[0];
    }

    final String querySql =
      "from Role where shortRoleName in(:RoleIds) and endDate is null";

    return (Role[]) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback
       *      #doInHibernate(org.hibernate.Session)
       */
      public Object doInHibernate(Session aSession)
        throws HibernateException, SQLException
      {
        Query query = aSession.createQuery(querySql);
        query.setParameterList("RoleIds", aRoleIds);
        List<Role> authorities = query.list();
        return authorities.toArray(new Role[0]);
      }
    });
  }

  /**
   * @param aRole The Role to insert or update.
   * @see uk.co.utilisoft.webprojecttemplate.data.role.RoleDao
   *      #saveOrUpdateRole(uk.co.formfill.etsettra.domain.Role)
   */
  public void saveOrUpdateRole(Role aRole)
  {
    getHibernateTemplate().saveOrUpdate(aRole);
  }

  /**
   * @param aName The name of the Role to check.
   * @param aIncludeDeleted
   *        True if deleted roles should be included in the check.
   * @return True if the Role exists, false otherwise.
   * @see uk.co.utilisoft.webprojecttemplate.data.role.RoleDao
   *      #RoleExists(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public boolean roleExists(String aName, boolean aIncludeDeleted)
  {
    StringBuffer query = new StringBuffer(
     "select count(distinct shortRoleName) from Role where shortRoleName = ?");
    if (!aIncludeDeleted)
    {
      query.append(" and endDate is null");
    }
    String queryString = query.toString();

    List<Number> results = getHibernateTemplate().find(queryString, aName);
    Number count = results.get(0);
    return count.intValue() > 0;
  }

}
