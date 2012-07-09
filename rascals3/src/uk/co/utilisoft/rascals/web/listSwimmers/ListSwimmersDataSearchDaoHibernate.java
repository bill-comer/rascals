package uk.co.utilisoft.rascals.web.listSwimmers;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.genericutils.web.searchfilter.GenericResultsDaoHibernate;
import uk.co.utilisoft.rascals.domain.Swimmer;
/**

 * @author Philip Lau
 * @version 1.0
 */
@Repository("project.listSwimmersSearchDao")
public class ListSwimmersDataSearchDaoHibernate extends GenericResultsDaoHibernate<ListSwimmersDataSearchWrapper>
{
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getBaseClassName()
  {
    return "Swimmer";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected ListSwimmersDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    if (aResultToConvert.getClass().isArray())
    {
      return new ListSwimmersDataSearchWrapper((Swimmer) (((Object[]) aResultToConvert))[0]);
    }
    return new ListSwimmersDataSearchWrapper((Swimmer) aResultToConvert);
  }

}
