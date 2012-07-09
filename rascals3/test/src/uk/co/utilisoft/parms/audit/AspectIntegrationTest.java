package uk.co.utilisoft.parms.audit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/test-parms.xml"})
public class AspectIntegrationTest 
{

  @Autowired(required=true)
  @Qualifier("parms.DummyAuditThing")
  private DummyAuditThing dummyAuditThing;

  @Test
  public void TestAspectCallNoArgs() throws Exception
  { 
    dummyAuditThing.aspectAuditMethod();
  }
  
  @Test
  public void TestAspectCallTwoArgs() throws Exception
  { 
    dummyAuditThing.aspectAuditMethodTwoParams("p_1", 99L);
  }
  
  
  
}
