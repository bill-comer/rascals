package uk.co.utilisoft.parms.web.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.web.dto.Sp04ListDataSearchWrapper;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.sp04ListDataSearchDao")
public class Sp04ListDataSearchDaoHibernate extends ParmsResultsDaoHibernate<Sp04ListDataSearchWrapper>
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getAlias()
   */
  @Override
  protected String getAlias()
  {
    return "sf";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ResultsDaoHibernate#getBaseClassName()
   */
  @Override
  public String getBaseClassName()
  {
    return "Sp04File";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected Sp04ListDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    return new Sp04ListDataSearchWrapper((Sp04File) aResultToConvert);
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
