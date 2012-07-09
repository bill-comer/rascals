package uk.co.utilisoft.rascals.web.Galas;

import java.util.List;

import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.rascals.domain.Gala;


@Repository("project.galaDao")
public class GalaDaoImpl  extends UtilisoftGenericDaoHibernate<Gala, Long>  implements GalaDao
{

  @Override
  public List<Gala> getAllGalas()
  {
    String queryString =
                "from Gala g order by g.eventDate";
    return (List<Gala>)getHibernateTemplate().find(queryString);
  }

  @Override
  public void saveOrUpdate(Gala aGala)
  {
    getHibernateTemplate().saveOrUpdate(aGala);
    
  }

  @Override
  public Gala getForId(Long id)
  {
    return getById(id);
  }

}
