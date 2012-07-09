package uk.co.utilisoft.rascals.web.Galas;

import java.util.List;

import uk.co.utilisoft.rascals.domain.Gala;

/**
 * @author Gareth Morris
 * @version 1.0
 */
public interface GalaService
{
  List<GalaListDTO> getAllSortedRecords();
  
  public void createNewGala(Gala aGala);

  Gala getForId(Long id);
}
