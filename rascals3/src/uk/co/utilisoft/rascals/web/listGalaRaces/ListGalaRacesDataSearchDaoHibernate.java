package uk.co.utilisoft.rascals.web.listGalaRaces;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.genericutils.web.searchfilter.GenericResultsDaoHibernate;
import uk.co.utilisoft.rascals.domain.Race;

@Repository("project.listGalaRacesSearchDao")
public class ListGalaRacesDataSearchDaoHibernate extends GenericResultsDaoHibernate<ListGalaRacesDataSearchWrapper>
{
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getBaseClassName()
  {
    return "Race";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.dao.ParmsResultsDaoHibernate#createConvertedObject(java.lang.Object)
   */
  @Override
  protected ListGalaRacesDataSearchWrapper createConvertedObject(Object aResultToConvert)
  {
    if (aResultToConvert.getClass().isArray())
    {
      return new ListGalaRacesDataSearchWrapper((Race) (((Object[]) aResultToConvert))[0]);
    }
    return new ListGalaRacesDataSearchWrapper((Race) aResultToConvert);
  }

}
