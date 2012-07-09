package uk.co.utilisoft.parms.web.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/test-parms.xml"})
public class AdminListControllerIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.adminListController")
  private AdminListController mAdminListController;
  
  
  @Test
  public void autoWiring() throws Exception
  { 
    assertNotNull("AdminListController should have been injected", mAdminListController);
    
    assertNotNull("AdminService was not injected into AdminListController", mAdminListController.getAdminService());
  }
}
