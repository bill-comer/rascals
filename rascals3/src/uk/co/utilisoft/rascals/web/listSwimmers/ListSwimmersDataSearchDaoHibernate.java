package uk.co.utilisoft.rascals.web.listSwimmers;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.genericutils.web.searchfilter.GenericResultsDaoHibernate;
import uk.co.utilisoft.genericutils.web.searchfilter.QueryStringBindValueCombo;
import uk.co.utilisoft.rascals.domain.Swimmer;
/**

 * @author Philip Lau
 * @version 1.0
 */
@Repository("project.listSwimmersSearchDao")
public class ListSwimmersDataSearchDaoHibernate extends GenericResultsDaoHibernate<ListSwimmersDataSearchWrapper>
{
  private boolean male = true;
  public boolean isMale()
  {
    return male;
  }
  public void setMale(boolean male)
  {
    this.male = male;
  }



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
  
  /**
   * Add fixed criteria to the middle of the query.
   * @return the fixed criteria
   */
  @Override
  protected QueryStringBindValueCombo addFixedCriteria(int aCurrentParamIndex)
  {
    String sexValue = "0";
    if (isMale())
    {
      sexValue = "1";
    }
    
    QueryStringBindValueCombo isMaleCombo = new QueryStringBindValueCombo(" stuff.male = " + sexValue + " ");
    return isMaleCombo;
  }

}
