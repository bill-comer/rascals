package uk.co.utilisoft.parms.web.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.web.dto.P0028ListDataSearchWrapper;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.p0028ListDataSearchDao")
public class P0028ListDataSearchDaoHibernate extends ParmsResultsDaoHibernate<P0028ListDataSearchWrapper>
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getAlias()
   */
  @Override
  protected String getAlias()
  {
    return "pf";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getBaseClassName()
   */
  @Override
  public String getBaseClassName()
  {
    return "P0028File";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected P0028ListDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    if (aResultToConvert.getClass().isArray())
    {
      return new P0028ListDataSearchWrapper((P0028File) (((Object[]) aResultToConvert))[0]);
    }
    return new P0028ListDataSearchWrapper((P0028File) aResultToConvert);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getSpecialSelectSql()
   */
  @Override
  protected String getSpecialSelectSql()
  {
    return "select distinct pf ";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#isBooleanTypeOptionDTOUsed()
   */
  @Override
  protected boolean isBooleanTypeOptionDTOUsed()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#addSupportedJodaDateTimeFormatters()
   */
  @Override
  protected List<DateTimeFormatter> addSupportedJodaDateTimeFormatters()
  {
    List<DateTimeFormatter> dtfFormatters = new ArrayList<DateTimeFormatter>();
    dtfFormatters.add(DateTimeFormat.forPattern("dd/MM/yyyy"));
    dtfFormatters.add(DateTimeFormat.forPattern("MMM yyyy"));
    return dtfFormatters;
  }
}
