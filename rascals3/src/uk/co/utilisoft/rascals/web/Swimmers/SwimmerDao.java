package uk.co.utilisoft.rascals.web.Swimmers;

import java.util.List;

import uk.co.utilisoft.rascals.domain.Swimmer;

public interface SwimmerDao
{

  List<Swimmer> getAllSwimmers();
  
  void saveOrUpdate(Swimmer aSwimmer);

  Swimmer getForId(Long id);
  

}
