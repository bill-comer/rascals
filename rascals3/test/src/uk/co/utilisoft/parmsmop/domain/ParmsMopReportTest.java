package uk.co.utilisoft.parmsmop.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParmsMopReportTest
{

  @Test
  public void test_isSerialSP11_true() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP11");
    
    assertTrue(report.isSerialSP11());

    report.setSerial("sp11");
    
    assertTrue(report.isSerialSP11());
  }

  @Test
  public void test_isSerialSP11_false() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP14");
    
    assertFalse(report.isSerialSP11());
  }
  

  @Test
  public void test_isSerialSP14_true() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP14");
    
    assertTrue(report.isSerialSP14());
    report.setSerial("sp14");
    
    assertTrue(report.isSerialSP14());
  }

  @Test
  public void test_isSerialSP14_false() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP11");
    
    assertFalse(report.isSerialSP14());
  }
  


  @Test
  public void test_isSerialSP15_true() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();

    report.setSerial("SP15");
    assertTrue(report.isSerialSP15());

    report.setSerial("sP15");
    assertTrue(report.isSerialSP15());
  }

  @Test
  public void test_isSerialSP15_false() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP11");
    
    assertFalse(report.isSerialSP15());
  }
  


  @Test
  public void test_isSerialnm12_true() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();

    report.setSerial("NM12");
    assertTrue(report.isSerialNM12());

    report.setSerial("Nm12");
    assertTrue(report.isSerialNM12());
  }

  @Test
  public void test_isSerialNM12_false() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();

    report.setSerial("SP11");
    assertFalse(report.isSerialNM12());
  }
  


  @Test
  public void test_isSerialHM12_true() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();

    report.setSerial("HM12");
    assertTrue(report.isSerialHM12());

    report.setSerial("hm12");
    assertTrue(report.isSerialHM12());
  }

  @Test
  public void test_isSerialHM12_false() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial("SP11");
    
    assertFalse(report.isSerialHM12());
  }
}
