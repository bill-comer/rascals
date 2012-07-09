package uk.co.utilisoft.parms.web.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.web.dto.P0028FileReportDataSearchWrapper;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.p0028FileReportDataSearchDao")
public class P0028FileReportDataSearchDaoHibernate extends ParmsResultsDaoHibernate<P0028FileReportDataSearchWrapper>
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getAlias()
   */
  @Override
  protected String getAlias()
  {
    return "pfd";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getBaseClassName()
   */
  public String getBaseClassName()
  {
    return "P0028Data";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected P0028FileReportDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    return new P0028FileReportDataSearchWrapper((P0028Data) aResultToConvert);
  }
}
