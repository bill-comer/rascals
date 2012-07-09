package uk.co.utilisoft.parms.web.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.web.dto.DpiFileReportDataSearchWrapper;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.dpiFileReportDataSearchDao")
public class DpiFileReportDataSearchDaoHibernate extends ParmsResultsDaoHibernate<DpiFileReportDataSearchWrapper>
{
  /**
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getAlias()
   * {@inheritDoc}
   */
  @Override
  protected String getAlias()
  {
    return "a";
  }

  /**
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getBaseClassName()
   * {@inheritDoc}
   */
  @Override
  public String getBaseClassName()
  {
    return "GenericAgent";
  }

  /**
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   * {@inheritDoc}
   */
  @Override
  protected DpiFileReportDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    return new DpiFileReportDataSearchWrapper((GenericAgent) aResultToConvert);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#getCustomSortOrder(java.lang.String)
   */
  @Override
  protected String getCustomSortOrder(String aOrderBySql)
  {
    if (StringUtils.isNotBlank(aOrderBySql))
    {
      String agentAlias = "a";
      String agentNameAlias = agentAlias + "." + "name";

      if (aOrderBySql.contains(agentNameAlias))
      {
        String order = aOrderBySql.toLowerCase().contains("desc") ? "desc" : null;
        
        if (order == null)
        {
        	order = aOrderBySql.toLowerCase().contains("asc") ? "asc" : null;
        }
        
        StringBuffer orderSql = new StringBuffer(getOrderByItem(aOrderBySql));
        orderSql.append(" ").append(order);
        orderSql.append(", ").append(agentAlias).append(".").append("mop").append(" ").append(order);
        return orderSql.toString();
      }
    }

    return aOrderBySql;
  }
}
