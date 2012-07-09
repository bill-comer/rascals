package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.domain.SerialConfiguration;

@SuppressWarnings("unchecked")
public class SerialConfigDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.serialConfigDao")
  private SerialConfigDaoHibernate mSerialConfigDao;

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testSerialConfigurationNameIsNullConstraintsValidation()
  {
    SerialConfiguration sc = new SerialConfiguration();
    mSerialConfigDao.makePersistent(sc);
  }

  @Test
  public void testGetSupplierSerials() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllSupplierSerials();
    assertNotNull(configs);
    assertEquals(1, configs.size());
    assertEquals("SP04", configs.get(0).getName());
  }
/*
  @Test
  public void testGetHH_MOP_SerialsMonthT() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllHHMopSerials(true);
    assertNotNull(configs);
    assertEquals(2, configs.size());

    boolean foundSP05 = false;
    boolean foundSP06 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("SP05")){
        foundSP05 = true;
      }else  if (serialConfiguration.getName().equals("SP06")){
        foundSP06 = true;
      } else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundSP05);
    assertTrue(foundSP06);
  }

  @Test
  public void testGetHH_MOP_SerialsMonthT_1() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllHHMopSerials(false);
    assertNotNull(configs);
    assertEquals(2, configs.size());

    boolean foundHM04 = false;
    boolean foundHM05 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("HM04")){
        foundHM04 = true;
      }else  if (serialConfiguration.getName().equals("HM05")){
        foundHM05 = true;
      } else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundHM04);
    assertTrue(foundHM05);
  }

  @Test
  public void testGetNonHH_MOP_SerialsMonthT() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllNonHHMopSerials(true);
    assertNotNull(configs);
    assertEquals(2, configs.size());

    boolean foundSP05 = false;
    boolean foundSP06 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("SP05")){
        foundSP05 = true;
      }else  if (serialConfiguration.getName().equals("SP06")){
        foundSP06 = true;
      } else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundSP05);
    assertTrue(foundSP06);
  }

  @Test
  public void testGetNonHH_MOP_SerialsMonthT_1() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllNonHHMopSerials(false);
    assertNotNull(configs);
    assertEquals(2, configs.size());

    boolean foundNM03 = false;
    boolean foundNM04 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("NM03")){
        foundNM03 = true;
      }else  if (serialConfiguration.getName().equals("NM04")){
        foundNM04 = true;
      } else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundNM03);
    assertTrue(foundNM04);
  }

  @Test
  public void testGetHH_DC_SerialsMonthT() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllHHDCSerials(true);
    assertNotNull(configs);
    assertEquals(5, configs.size());

    boolean foundSP05 = false;
    boolean foundSP06 = false;
    boolean foundHC01 = false;
    boolean foundHM02 = false;
    boolean foundHM06 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("SP05")){
        foundSP05 = true;
      } else  if (serialConfiguration.getName().equals("SP06")){
        foundSP06 = true;
      } else  if (serialConfiguration.getName().equals("HC01")){
        foundHC01 = true;
      }else  if (serialConfiguration.getName().equals("HM02")){
        foundHM02 = true;
      }else  if (serialConfiguration.getName().equals("HM06")){
        foundHM06 = true;
      }else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundSP05);
    assertTrue(foundSP06);
    assertTrue(foundHC01);
    assertTrue(foundHM02);
    assertTrue(foundHM06);
  }

  @Test
  public void testGetHH_DC_SerialsMonthT_1() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllHHDCSerials(false);
    assertNotNull(configs);
    assertEquals(3, configs.size());

    boolean foundHC02 = false;
    boolean foundHM01 = false;
    boolean foundHM03 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("HC02")){
        foundHC02 = true;
      } else  if (serialConfiguration.getName().equals("HM01")){
        foundHM01 = true;
      } else  if (serialConfiguration.getName().equals("HM03")){
        foundHM03 = true;
      }else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundHC02);
    assertTrue(foundHM01);
    assertTrue(foundHM03);
  }

  @Test
  public void testGetNonHH_DC_SerialsMonthT() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllNonHHDCSerials(true);
    assertNotNull(configs);
    assertEquals(3, configs.size());

    boolean foundSP05 = false;
    boolean foundSP06 = false;
    boolean foundNM02 = false;
    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("SP05")){
        foundSP05 = true;
      } else  if (serialConfiguration.getName().equals("SP06")){
        foundSP06 = true;
      } else  if (serialConfiguration.getName().equals("NM02")){
        foundNM02 = true;
      }else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }
    assertTrue(foundSP05);
    assertTrue(foundSP06);
    assertTrue(foundNM02);
  }

  @Test
  public void testGetNonHH_DC_SerialsMonthT_1() throws Exception
  {
    assertNotNull("mSerialConfigDao not injected", mSerialConfigDao);
    List<SerialConfiguration> configs =   mSerialConfigDao.getAllNonHHDCSerials(false);
    assertNotNull(configs);
    assertEquals(3, configs.size());

    boolean foundNC02 = false;
    boolean foundNC03 = false;
    boolean foundNM01 = false;

    for (SerialConfiguration serialConfiguration : configs)
    {
      if (serialConfiguration.getName().equals("NC02")){
        foundNC02 = true;
      } else  if (serialConfiguration.getName().equals("NC03")){
        foundNC03 = true;
      } else  if (serialConfiguration.getName().equals("NM01")){
        foundNM01 = true;
      }else {
        fail("should not have found any others name[" + serialConfiguration.getName() + "]");
      }
    }

    assertTrue(foundNC02);
    assertTrue(foundNC03);
    assertTrue(foundNM01);
  }*/
}