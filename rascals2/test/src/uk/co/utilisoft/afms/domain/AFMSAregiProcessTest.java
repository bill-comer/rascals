package uk.co.utilisoft.afms.domain;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.utils.Freeze;


public class AFMSAregiProcessTest
{

  @Test
  public void getLatestD0268ReceiptDate_bothSetUp_D0268RxIsLatest() throws Exception
  { 
    Freeze.freeze(new DateTime());
    DateTime now = new DateTime();
    
    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setD0268DupReceived(now);
    aregiProcess.setD0268ReceivedDate(now.plusDays(1));

    //test method
    assertEquals(now.plusDays(1), aregiProcess.getLatestD0268ReceiptDate());
  }

  @Test
  public void getLatestD0268ReceiptDate_bothSetUp_D0268DupRxIsLatest() throws Exception
  { 
    Freeze.freeze(new DateTime());
    DateTime now = new DateTime();
    
    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setD0268DupReceived(now.plusDays(2));
    aregiProcess.setD0268ReceivedDate(now);

    //test method
    assertEquals(now.plusDays(2), aregiProcess.getLatestD0268ReceiptDate());
  }
  

  @Test
  public void getLatestD0268ReceiptDate_DupNotSetup_D0268DupRxIsLatest() throws Exception
  { 
    Freeze.freeze(new DateTime());
    DateTime now = new DateTime();
    
    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setD0268ReceivedDate(now.plusDays(3));

    //test method
    assertEquals(now.plusDays(3), aregiProcess.getLatestD0268ReceiptDate());
  }
  

  @Test
  public void getLatestD0268ReceiptDate_RxNotSetup_D0268DupRxIsLatest() throws Exception
  { 
    Freeze.freeze(new DateTime());
    DateTime now = new DateTime();
    
    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    aregiProcess.setD0268DupReceived(now.plusDays(3));

    //test method
    assertEquals(now.plusDays(3), aregiProcess.getLatestD0268ReceiptDate());
  }
  

  @Test
  public void getLatestD0268ReceiptDate_NeitherSetup_getNULLBack() throws Exception
  { 
    Freeze.freeze(new DateTime());
    DateTime now = new DateTime();
    
    AFMSAregiProcess aregiProcess = new AFMSAregiProcess();
    
    //test method
    assertNull(aregiProcess.getLatestD0268ReceiptDate());
  }
}
