package uk.co.utilisoft.parms.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 *
 */
public class PoolChecksumTest
{
  /**
   * EBES jul 2011 checksum test 2.
   */
  public void testChecksumForSp04Data04()
  {
    String header = "ZHD|P0142001|X|EBES|Z|POOL|20110804141806";
    String rec1 = "SUB|H|X|EBES|20110731|M";
    String rec2 = "SP4|_A||||";
    String rec3 = "SP4|_B||||";
    String rec4 = "SP4|_C||||";
    String rec5 = "SP4|_D||||";
    String rec6 = "SP4|_E||||";
    String rec7 = "SP4|_F||||";
    String rec8 = "SP4|_G||||";
    String rec9 = "SP4|_H||||";
    String rec10 = "SP4|_J||||";
    String rec11 = "SP4|_K||||";
    String rec12 = "SP4|_L||||";
    String rec13 = "SP4|_M||||";
    String rec14 = "SP4|_N||||";
    String rec15 = "SP4|_P||||";

    Long expectedChecksum = 995520093L;

    PoolChecksumCalculator calc = new PoolChecksumCalculator();
    calc.addLineToCheckSum(header);
    calc.addLineToCheckSum(rec1);
    calc.addLineToCheckSum(rec2);
    calc.addLineToCheckSum(rec3);
    calc.addLineToCheckSum(rec4);
    calc.addLineToCheckSum(rec5);
    calc.addLineToCheckSum(rec6);
    calc.addLineToCheckSum(rec7);
    calc.addLineToCheckSum(rec8);
    calc.addLineToCheckSum(rec9);
    calc.addLineToCheckSum(rec10);
    calc.addLineToCheckSum(rec11);
    calc.addLineToCheckSum(rec12);
    calc.addLineToCheckSum(rec13);
    calc.addLineToCheckSum(rec14);
    calc.addLineToCheckSum(rec15);

    Long checksum = calc.getCheckSum();
    assertNotNull(checksum);
    assertEquals(expectedChecksum, checksum);
  }

  /**
   * EBES jul 2011 checksum test
   */
  @Test
  public void testChecksumForSp04Data03()
  {
    String header = "ZHD|P0142001|X|EBES|Z|POOL|20110804095850";
    String rec1 = "SUB|H|X|EBES|20110731|M";
    String rec2 = "SP4|_A||||";
    String rec3 = "SP4|_B||||";
    String rec4 = "SP4|_C||||";
    String rec5 = "SP4|_D||||";
    String rec6 = "SP4|_E||||";
    String rec7 = "SP4|_F||||";
    String rec8 = "SP4|_G||||";
    String rec9 = "SP4|_H||||";
    String rec10 = "SP4|_J||||";
    String rec11 = "SP4|_K||||";
    String rec12 = "SP4|_L||||";
    String rec13 = "SP4|_M||||";
    String rec14 = "SP4|_N||||";
    String rec15 = "SP4|_P||||";

    Long expectedChecksum = 810708569L;

    PoolChecksumCalculator calc = new PoolChecksumCalculator();
    calc.addLineToCheckSum(header);
    calc.addLineToCheckSum(rec1);
    calc.addLineToCheckSum(rec2);
    calc.addLineToCheckSum(rec3);
    calc.addLineToCheckSum(rec4);
    calc.addLineToCheckSum(rec5);
    calc.addLineToCheckSum(rec6);
    calc.addLineToCheckSum(rec7);
    calc.addLineToCheckSum(rec8);
    calc.addLineToCheckSum(rec9);
    calc.addLineToCheckSum(rec10);
    calc.addLineToCheckSum(rec11);
    calc.addLineToCheckSum(rec12);
    calc.addLineToCheckSum(rec13);
    calc.addLineToCheckSum(rec14);
    calc.addLineToCheckSum(rec15);
    Long checksum = calc.getCheckSum();

    assertNotNull(checksum);
    assertEquals(expectedChecksum, checksum);
  }

  /**
   * OVO jul 2011 checksum test
   */
  @Test
  public void testChecksumForSp04Data02()
  {
    String header = "ZHD|P0142001|X|VOLT|Z|POOL|20110803162013";
    String rec1 = "SUB|H|X|VOLT|20110731|M";
    String rec2 = "SP4|_A||||";
    String rec3 = "SP4|_B||||";
    String rec4 = "SP4|_C||||";
    String rec5 = "SP4|_D||||";
    String rec6 = "SP4|_E||||";
    String rec7 = "SP4|_F||||";
    String rec8 = "SP4|_G||||";
    String rec9 = "SP4|_H||||";
    String rec10 = "SP4|_J||||";
    String rec11 = "SP4|_K||||";
    String rec12 = "SP4|_L||||";
    String rec13 = "SP4|_M||||";
    String rec14 = "SP4|_N||||";
    String rec15 = "SP4|_P||||";
    Long expectedChecksum = 575762248L;
    PoolChecksumCalculator calc = new PoolChecksumCalculator();
    calc.addLineToCheckSum(header);
    calc.addLineToCheckSum(rec1);
    calc.addLineToCheckSum(rec2);
    calc.addLineToCheckSum(rec3);
    calc.addLineToCheckSum(rec4);
    calc.addLineToCheckSum(rec5);
    calc.addLineToCheckSum(rec6);
    calc.addLineToCheckSum(rec7);
    calc.addLineToCheckSum(rec8);
    calc.addLineToCheckSum(rec9);
    calc.addLineToCheckSum(rec10);
    calc.addLineToCheckSum(rec11);
    calc.addLineToCheckSum(rec12);
    calc.addLineToCheckSum(rec13);
    calc.addLineToCheckSum(rec14);
    calc.addLineToCheckSum(rec15);

    Long checksum = calc.getCheckSum();

    assertNotNull(checksum);
    assertEquals(expectedChecksum, checksum);
  }

  /**
   * Test a manually generated checksum against the calculated version.
   */
  @Test
  public void testChecksumForSp04Data()
  {
    String sp04DataRow2 = "ZHD|P0142001|X|EBES|Z|POOL|20110721130400";
    String sp04DataRow3 = "SUB|H|X|EBES|20110630|M";
    String sp04DataRow4 = "SP4|_A||||";
    String sp04DataRow5 = "SP4|_B||||";
    String sp04DataRow6 = "SP4|_C||||";
    String sp04DataRow7 = "SP4|_D||||";
    String sp04DataRow8 = "SP4|_E||||";
    String sp04DataRow9 = "SP4|_F||||";
    String sp04DataRow10 = "SP4|_G||||";
    String sp04DataRow11 = "SP4|_H||||";
    String sp04DataRow12 = "SP4|_J||||";
    String sp04DataRow13 = "SP4|_K||||";
    String sp04DataRow14 = "SP4|_L||||";
    String sp04DataRow15 = "SP4|_M||||";
    String sp04DataRow16 = "SP4|_N||||";
    String sp04DataRow17 = "SP4|_P||||";
    PoolChecksumCalculator calc = new PoolChecksumCalculator();
    calc.addLineToCheckSum(sp04DataRow2);
    calc.addLineToCheckSum(sp04DataRow3);
    calc.addLineToCheckSum(sp04DataRow4);
    calc.addLineToCheckSum(sp04DataRow5);
    calc.addLineToCheckSum(sp04DataRow6);
    calc.addLineToCheckSum(sp04DataRow7);
    calc.addLineToCheckSum(sp04DataRow8);
    calc.addLineToCheckSum(sp04DataRow9);
    calc.addLineToCheckSum(sp04DataRow10);
    calc.addLineToCheckSum(sp04DataRow11);
    calc.addLineToCheckSum(sp04DataRow12);
    calc.addLineToCheckSum(sp04DataRow13);
    calc.addLineToCheckSum(sp04DataRow14);
    calc.addLineToCheckSum(sp04DataRow15);
    calc.addLineToCheckSum(sp04DataRow16);
    calc.addLineToCheckSum(sp04DataRow17);

    Long expectedChecksum = 878011997L;
    Long checksum = calc.getCheckSum();

    assertNotNull(checksum);
    assertEquals(expectedChecksum, checksum);
  }

  /**
   * @throws Exception
   */
  @Test
  public void testCalcChecksumOfSp04() throws Exception
  {
    assertEquals(692812869L, getChecksumForFile("OVOE1429.692812869.checksum"));
    assertEquals("deliberately run same file twice", 692812869L, getChecksumForFile("OVOE1429.692812869.checksum"));
    assertEquals(1769165066L, getChecksumForFile("OVOE1359.1769165066.checksum"));
  }

  
  private long getChecksumForFile(String aFileName) throws Exception
  {
    PoolChecksumCalculator checksum = new PoolChecksumCalculator();
    BufferedReader in = new BufferedReader(new InputStreamReader(getClass()
        .getClassLoader().getResourceAsStream(
            "uk/co/utilisoft/parms/file/" + aFileName)));

    String str;
    while ((str = in.readLine()) != null)
    {
      checksum.addLineToCheckSum(str);
    }
    in.close();

    return checksum.getCheckSum();
  }

}

