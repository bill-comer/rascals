package uk.co.utilisoft.rascals.web.Swimmers;

import java.util.List;

import uk.co.utilisoft.rascals.domain.Swimmer;

/**
 * @author Gareth Morris
 * @version 1.0
 */
public interface SwimmerService
{
  List<SwimmerListDTO> getAllSortedRecords();
  
  public void createNewSwimmer(Swimmer aSwimmer);

  Swimmer getForId(Long id);
}
