package uk.co.utilisoft.parms.web.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/test-parms.xml"})
public class ConfigurationParameterServiceIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.configurationParameterService")
  private ConfigurationParameterServiceImpl mConfigurationParameterServiceImpl;

  @Test
  public void autoWiring() throws Exception
  {
    assertNotNull("ConfigurationParameterServiceImpl should have been injected", mConfigurationParameterServiceImpl);
  }
}
