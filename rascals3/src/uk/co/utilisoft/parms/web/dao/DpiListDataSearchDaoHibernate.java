package uk.co.utilisoft.parms.web.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.web.dto.DpiListDataSearchWrapper;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.dpiListDataSearchDao")
public class DpiListDataSearchDaoHibernate extends ParmsResultsDaoHibernate<DpiListDataSearchWrapper>
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getAlias()
   */
  @Override
  protected String getAlias()
  {
    return "df";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getBaseClassName()
   */
  @Override
  public String getBaseClassName()
  {
    return "DpiFile";
  }



  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected DpiListDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    return new DpiListDataSearchWrapper((DpiFile) aResultToConvert);
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
