package uk.co.utilisoft.genericutils.web.searchfilter;

import java.io.Serializable;

import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.domain.JodaLastUpdatedDomainObject;

/**
 * @author Philip Lau
 * @version 1.0
 */
public abstract class SearchWrapper implements JodaLastUpdatedDomainObject, Serializable
{
  private static final long serialVersionUID = 1L;

  /**
   * {@inheritDoc}
   * @see uk.co.formfill.hibernateutils.domain.DomainObject
   * #getLastUpdated()
   */
  public DateTime getLastUpdated()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.formfill.hibernateutils.domain.DomainObject
   * #getVersion()
   */
  public Integer getVersion()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.formfill.hibernateutils.domain.DomainObject
   * #setLastUpdated(java.lang.Object)
   */
  public void setLastUpdated(DateTime aDateTime)
  {

  }
}
