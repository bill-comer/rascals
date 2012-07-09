package uk.co.utilisoft.parms.web.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class RedirectViewIdParametersOnlyUnitTest
{

  @Test
  public void checkIfStringInList() throws Exception
  { 
    List<String> itemsToCheck = new ArrayList<String>();
    itemsToCheck.add("item1");
    
    RedirectViewIdParametersOnly sut = new RedirectViewIdParametersOnly("", itemsToCheck);
    
    assertTrue("item sgould be in List", sut.isValueInList("item1", itemsToCheck));
  }
  

  @Test
  public void checkIfStringInListMorThanOneItem() throws Exception
  { 
    List<String> itemsToCheck = new ArrayList<String>();
    itemsToCheck.add("item2");
    itemsToCheck.add("item1");
    itemsToCheck.add("item3");
    
    RedirectViewIdParametersOnly sut = new RedirectViewIdParametersOnly("", itemsToCheck);
    
    assertTrue("item sgould be in List", sut.isValueInList("item1", itemsToCheck));
  }
  
  @Test
  public void checkIfStringNotInList() throws Exception
  { 
    List<String> itemsToCheck = new ArrayList<String>();
    itemsToCheck.add("item2");
    
    RedirectViewIdParametersOnly sut = new RedirectViewIdParametersOnly("", itemsToCheck);
    
    assertFalse("item should NOT be in List", sut.isValueInList("item1", itemsToCheck));
  }
  

  @Test
  public void checkIfStringNotInListMoreThanOneItem() throws Exception
  { 
    List<String> itemsToCheck = new ArrayList<String>();
    itemsToCheck.add("item2");
    itemsToCheck.add("item4");
    itemsToCheck.add("item6");
    
    RedirectViewIdParametersOnly sut = new RedirectViewIdParametersOnly("", itemsToCheck);
    
    assertFalse("item should NOT be in List", sut.isValueInList("item1", itemsToCheck));
  }
}
