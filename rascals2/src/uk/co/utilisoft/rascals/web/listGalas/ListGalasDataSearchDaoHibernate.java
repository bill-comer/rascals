package uk.co.utilisoft.rascals.web.listGalas;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.genericutils.web.searchfilter.GenericResultsDaoHibernate;
import uk.co.utilisoft.rascals.domain.Gala;
/**

 * @author Philip Lau
 * @version 1.0
 */
@Repository("project.listGalasSearchDao")
public class ListGalasDataSearchDaoHibernate extends GenericResultsDaoHibernate<ListGalasDataSearchWrapper>
{
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getBaseClassName()
  {
    return "Gala";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected ListGalasDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    if (aResultToConvert.getClass().isArray())
    {
      return new ListGalasDataSearchWrapper((Gala) (((Object[]) aResultToConvert))[0]);
    }
    return new ListGalasDataSearchWrapper((Gala) aResultToConvert);
  }

}
