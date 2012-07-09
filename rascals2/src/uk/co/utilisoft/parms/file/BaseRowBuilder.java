package uk.co.utilisoft.parms.file;

import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.parms.dao.GspDefinitionDao;
import uk.co.utilisoft.parms.domain.GSPDefinition;

public class BaseRowBuilder
{
  
  @Autowired(required=true)
  @Qualifier("parms.gspDefinitionDao")
  private GspDefinitionDao mGspDefinitionDao;
  public void setGspDefinitionDao(
      GspDefinitionDao aGspDefinitionDao)
  {
    this.mGspDefinitionDao = aGspDefinitionDao;
  }
  
  public List<GSPDefinition> getAllGspDefinitions()
  {
    return mGspDefinitionDao.getAll();
  }
  
  public IterableMap<String, Boolean> getMapAllGspDefinitions()
  {
    //IterableMap<String, P0028Active> activeMap = new HashedMap<String, P0028Active>();
    IterableMap<String, Boolean> gspMap = new HashedMap<String, Boolean>();
    
    List<GSPDefinition> gsps =  getAllGspDefinitions();
    for (GSPDefinition gsp : gsps)
    {
      gspMap.put(gsp.getName(), false);
    }
    
    return gspMap;
  }
  
  public String getFooter(ChecksumCalculator aChecksumCalculator)
  {
    PoolFooter footer = new PoolFooter();
    return footer.createFooter(aChecksumCalculator.getCheckSum(), aChecksumCalculator.getRecordCount());
  }
  
  protected  PoolChecksumCalculator getChecksumCalculator()
  {
    return new PoolChecksumCalculator();
  }
}
