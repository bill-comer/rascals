package uk.co.utilisoft.rascals.web.Galas;

import java.util.List;

import uk.co.utilisoft.afms.domain.User;
import uk.co.utilisoft.rascals.domain.Gala;

public interface GalaDao
{

  List<Gala> getAllGalas();
  
  void saveOrUpdate(Gala aUser);

  Gala getForId(Long id);
  

}
