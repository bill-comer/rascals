package uk.co.utilisoft.rascals.web.Galas;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.rascals.domain.Gala;

/**
 * @author Gareth Morris
 * @version 1.0
 */
@Service("project.galaService")
public class GalaServiceImpl implements GalaService
{
  @Autowired(required=true)
  @Qualifier("project.galaDao")
  private GalaDao mGalaDao;



  @Override
  public List<GalaListDTO> getAllSortedRecords()
  {
    List<GalaListDTO> listItems = new ArrayList<GalaListDTO>();
    
    List<Gala> users = mGalaDao.getAllGalas();
    for (Gala gala : users)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> currentObjectList = new ArrayList<Object>();
      
      currentObjectList.add(gala.getName());
      currentObjectList.add(gala.getLeague());
      currentObjectList.add(gala.getEventDate());
      currentObjectList.add(gala.getEventDateOfBirthDate());
      currentObjectList.add(gala.isAtHome());
      currentObjectList.add(gala.getPostcode());
      
      list.add(gala.getPk());
      listItems.add(new GalaListDTO(list, currentObjectList));
    }
    
    return listItems;
  }
  
  @Override
  public void createNewGala(Gala aGala)
  {
    aGala.setLastUpdated(new DateTime());
    
    mGalaDao.saveOrUpdate(aGala);
  }

  @Override
  public Gala getForId(Long id)
  {
    return mGalaDao.getForId(id);
  }

  /**
   * 
   * @param aUser
   */
  /*void checkNewUserIsUnique(User aUser) 
  {
    User user = null;
    try {
      user = mUserDao.getCaseInsensitiveUser(aUser.getUserName());
    } catch (Exception e) {}

    Assert.isNull(user, "User already exists with the name [" + aUser.getUserName() + "].");

    user = mUserDao.getUserByEmail(aUser.getUserName());
    Assert.isNull(user, "User already exists with the email address [" + aUser.getEmailAddress() + "].");
  }*/
}
