package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.junit.Test;


public class MultiHashMapTest
{

  @Test
  public void testMultiHashMap() throws Exception
  { 
    
    MultiHashMap<String, String> map = new MultiHashMap<String, String>();
    
    map.put("aa", "aa1");
    map.put("aa", "aa1");
    map.put("bb", "bb1");
    map.put("bb", "bb2");
    map.put("bb", "bb3");
    assertEquals(2, map.size());
    
    Set<String> keys = map.keySet();
    assertEquals(2, keys.size());
    assertTrue(keys.contains("aa"));
    assertTrue(keys.contains("bb"));
    
    Collection<String> dataAA = map.getCollection("aa");
    assertEquals(2, dataAA.size());
    
    Collection<String> dataBB = map.getCollection("bb");
    assertEquals(3, dataBB.size());
    
    boolean bb1 = false, bb2 = false, bb3 = false;
    for (String val : dataBB)
    {
      if (val.equals("bb1"))
      {
        bb1 = true;
      }
      if (val.equals("bb2"))
      {
        bb2 = true;
      }
      if (val.equals("bb3"))
      {
        bb3 = true;
      }
    }
    assertTrue(bb1);
    assertTrue(bb2);
    assertTrue(bb3);
    
    
    
  }
}
