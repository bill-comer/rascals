package uk.co.utilisoft.rascals.web.Swimmers;

import java.util.List;

import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.rascals.domain.Swimmer;


@Repository("project.swimmerDao")
public class SwimmerDaoImpl  extends UtilisoftGenericDaoHibernate<Swimmer, Long>  implements SwimmerDao
{

  @Override
  public List<Swimmer> getAllSwimmers()
  {
    String queryString =
                "from Swimmer g order by g.surname";
    return (List<Swimmer>)getHibernateTemplate().find(queryString);
  }

  @Override
  public void saveOrUpdate(Swimmer aSwimmer)
  {
    getHibernateTemplate().saveOrUpdate(aSwimmer);
    
  }

  @Override
  public Swimmer getForId(Long id)
  {
    return getById(id);
  }

}
