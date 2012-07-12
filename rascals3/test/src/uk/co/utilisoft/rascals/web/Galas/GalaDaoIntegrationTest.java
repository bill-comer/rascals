package uk.co.utilisoft.rascals.web.Galas;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.rascals.domain.Gala;

public class GalaDaoIntegrationTest extends BaseDaoIntegrationTest
{


  @Autowired(required = true)
  @Qualifier("project.galaDao")
  private GalaDaoImpl galaDao;
  
  @Test
  public void testGalaDao_getGala() throws Exception
  {
    assertNotNull(galaDao);
    assertNotNull(galaDao.getAllGalas());
    
    List<Gala> galas = galaDao.getAllGalas();
    
    assertTrue(galas.size() > 0);
    
    Gala gala = galaDao.getForId(1L);
    
    //Gala gala = galas.get(0);
    assertNotNull(gala);

    assertEquals("t1", gala.getName());
    assertEquals(new Long(1), gala.getPk());
    
    assertNotNull(gala.getRaces());
    assertEquals(0, gala.getRaces().size());
  }
}
