package uk.co.utilisoft.rascals.web.Swimmers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.rascals.domain.Swimmer;

/**
 * @author Gareth Morris
 * @version 1.0
 */
@Service("project.swimmerService")
public class SwimmerServiceImpl implements SwimmerService
{
  @Autowired(required=true)
  @Qualifier("project.swimmerDao")
  private SwimmerDao mSwimmerDao;



  @Override
  public List<SwimmerListDTO> getAllSortedRecords()
  {
    List<SwimmerListDTO> listItems = new ArrayList<SwimmerListDTO>();
    
    List<Swimmer> swimmers = mSwimmerDao.getAllSwimmers();
    for (Swimmer swimmer : swimmers)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> currentObjectList = new ArrayList<Object>();
      
      currentObjectList.add(swimmer.getFirstname());
      currentObjectList.add(swimmer.getSurname());
      currentObjectList.add(swimmer.getDateOfBirth());
      currentObjectList.add(swimmer.isMale());
      
      list.add(swimmer.getPk());
      listItems.add(new SwimmerListDTO(list, currentObjectList));
    }
    
    return listItems;
  }
  
  @Override
  public void createNewSwimmer(Swimmer aSwimmer)
  {
    aSwimmer.setLastUpdated(new DateTime());
    
    mSwimmerDao.saveOrUpdate(aSwimmer);
  }

  @Override
  public Swimmer getForId(Long id)
  {
    return mSwimmerDao.getForId(id);
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
