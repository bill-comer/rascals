package uk.co.utilisoft.parms.web.service.p0028;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.HashedMap;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.dao.P0028FileDataDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.file.p0028.Sp04Validator;
import uk.co.utilisoft.parms.file.p0028.UploadStatus;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028UploadServiceUnitTest
{
  /**
   * Check P0028 Upload contains warnings for mpans only in P0028Active and Sp04FromAFMSMpan records.
   */
  @Test
  public void getP28FileUploadWarningsWithWarnings()
  {
    Sp04Validator sp04ValidatorMock = createMock(Sp04Validator.class);

    Long supplierPk = 28L;
    String dcName = "BART";
    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "5555555555555";

    Set<String> expMpansInOnlyP28Actives = new HashSet<String>();
    expMpansInOnlyP28Actives.add(mpan1);
    expMpansInOnlyP28Actives.add(mpan2);

    Set<String> expMpansInOnlySp04FromAfms = new HashSet<String>();
    expMpansInOnlySp04FromAfms.add(mpan3);

    P0028FileData p28FileData = new P0028FileData();
    p28FileData.setPk(300L);

    P0028File p28File = new P0028File();
    p28File.setPk(1L);
    p28File.setDateImported(new DateTime());
    p28File.setDcAgentName("BART");
    p28File.setErrored(false);
    p28File.setFilename("testp28filename001.csv");
    p28File.setReceiptDate(new DateTime());
    p28File.setVersion(0L);

    p28FileData.setP0028File(p28File);

    P0028Data p28Data1 = new P0028Data(102L, "101010", new MPANCore(mpan1), new DateTime(), p28File);
    P0028Active p28Active1 = new P0028Active();
    p28Active1.setDataCollector(new DataCollector("BART", false, null, true));
    p28Active1.setMpanCore(new MPANCore(mpan1));
    p28Active1.setDataCollectorName("BART");
    p28Active1.setLatestP0028Data(p28Data1);

    P0028Data p28Data2 = new P0028Data(99L, "333333", new MPANCore(mpan2), new DateTime(), p28File);
    P0028Active p28Active2 = new P0028Active();
    p28Active2.setDataCollector(new DataCollector("BART", false, null, true));
    p28Active2.setMpanCore(new MPANCore(mpan2));
    p28Active2.setDataCollectorName("BART");
    p28Active2.setLatestP0028Data(p28Data2);

    IterableMap<String, P0028Active> p28ActivesAndMpans = new HashedMap<String, P0028Active>();
    p28ActivesAndMpans.put(mpan1, p28Active1);
    p28ActivesAndMpans.put(mpan2, p28Active2);

    Map<String, IterableMap<String, P0028Active>> p28ActivesMap
      = new HashMap<String, IterableMap<String, P0028Active>>();
    p28ActivesMap.put(dcName, p28ActivesAndMpans);

    P0028UploadService p28UploadService = new P0028UploadService();
    p28UploadService.setSp04Validator(sp04ValidatorMock);

    IterableMap<String, P0028Active> mpansInOnlyP28ActivesMap = new HashedMap<String, P0028Active>();
    mpansInOnlyP28ActivesMap.put(mpan1, p28Active1);
    mpansInOnlyP28ActivesMap.put(mpan2, p28Active2);

    Sp04FromAFMSMpan sp04FromAfmsMpan1 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan1.setDataCollector("BART");
    sp04FromAfmsMpan1.setMpan(new MPANCore(mpan3));
    sp04FromAfmsMpan1.setPk(1L);

    IterableMap<String, Sp04FromAFMSMpan> mpansInOnlySp04FromAfmsMap = new HashedMap<String, Sp04FromAFMSMpan>();
    mpansInOnlySp04FromAfmsMap.put(mpan3, sp04FromAfmsMpan1);

    List<Object> expP28ActivesAndSp04FromAfmsMpansMaps = new ArrayList<Object>();
    expP28ActivesAndSp04FromAfmsMpansMaps.add(mpansInOnlyP28ActivesMap);
    expP28ActivesAndSp04FromAfmsMpansMaps.add(mpansInOnlySp04FromAfmsMap);

    expect(sp04ValidatorMock.validate(supplierPk, dcName, p28ActivesAndMpans))
      .andReturn(expP28ActivesAndSp04FromAfmsMpansMaps).once();

    replay(sp04ValidatorMock);

    // test call
    Map<UploadStatus, List<IterableMap<String, ?>>> uploadWarnings = p28UploadService
      .getP28FileUploadWarnings(supplierPk, p28ActivesMap, UploadStatus.UPLOADED_WITH_ROW_ERRORS);

    verify(sp04ValidatorMock);

    assertNotNull(uploadWarnings);
    assertFalse(uploadWarnings.isEmpty());

    Iterator<UploadStatus> uploadWarningsIter = uploadWarnings.keySet().iterator();
    while (uploadWarningsIter.hasNext())
    {
      UploadStatus status = uploadWarningsIter.next();
      assertFalse("expected UploadStatus other than " + UploadStatus.FAILED_STRUCTURAL_VALIDATION.name(),
                  status.equals(UploadStatus.FAILED_STRUCTURAL_VALIDATION));
      List<IterableMap<String, ?>> warnings = uploadWarnings.get(status);
      assertNotNull(warnings);
      assertFalse(warnings.isEmpty());

      // check contains warnings for mpans only in p0028actives and sp04fromafmsmpans
      for (int i = 0; i < warnings.size(); i++)
      {
        IterableMap<String, ?> warning = warnings.get(i);
        assertNotNull(warning);
        assertFalse(warning.isEmpty());

        MapIterator<String, ?> warningIter = warning.mapIterator();
        assertTrue(warningIter.hasNext());

        while (warningIter.hasNext())
        {
          String warningMpan = warningIter.next();
          assertNotNull(warningMpan);
          Object warningValue = warningIter.getValue();
          assertNotNull(warningValue);

          // first mpan-object map should contain p0028active records
          if (i == 0)
          {
            assertTrue(warningValue instanceof P0028Active);
            assertTrue(expMpansInOnlyP28Actives.contains(((P0028Active) warningValue).getMpanCore().getValue()));
          }
          else
          {
            assertTrue(warningValue instanceof Sp04FromAFMSMpan);
            assertTrue(expMpansInOnlySp04FromAfms.contains(((Sp04FromAFMSMpan) warningValue).getMpan().getValue()));
          }
        }
      }
    }
  }

  /**
   * Save with warnings in message.
   */
  @Test
  public void saveP28FileUploadWarningsWithWarnings()
  {
    // test data 1 start
    Sp04Validator sp04ValidatorMock = createMock(Sp04Validator.class);

    Long supplierPk = 28L;
    String dcName = "BART";
    String mpan1 = "1111111111111";
    String mpan2 = "2222222222222";
    String mpan3 = "5555555555555";

    Set<String> expMpansInOnlyP28Actives = new HashSet<String>();
    expMpansInOnlyP28Actives.add(mpan1);
    expMpansInOnlyP28Actives.add(mpan2);

    Set<String> expMpansInOnlySp04FromAfms = new HashSet<String>();
    expMpansInOnlySp04FromAfms.add(mpan3);

    P0028FileData p28FileDataA = new P0028FileData();
    p28FileDataA.setPk(300L);

    P0028File p28FileA = new P0028File();
    p28FileA.setPk(1L);
    p28FileA.setDateImported(new DateTime());
    p28FileA.setDcAgentName("BART");
    p28FileA.setErrored(false);
    p28FileA.setFilename("testp28filename001.csv");
    p28FileA.setReceiptDate(new DateTime());
    p28FileA.setVersion(0L);

    p28FileDataA.setP0028File(p28FileA);

    P0028Data p28Data1 = new P0028Data(102L, "101010", new MPANCore(mpan1), new DateTime(), p28FileA);
    P0028Active p28Active1 = new P0028Active();
    p28Active1.setDataCollector(new DataCollector("BART", false, null, true));
    p28Active1.setMpanCore(new MPANCore(mpan1));
    p28Active1.setDataCollectorName("BART");
    p28Active1.setLatestP0028Data(p28Data1);

    P0028Data p28Data2 = new P0028Data(99L, "333333", new MPANCore(mpan2), new DateTime(), p28FileA);
    P0028Active p28Active2 = new P0028Active();
    p28Active2.setDataCollector(new DataCollector("BART", false, null, true));
    p28Active2.setMpanCore(new MPANCore(mpan2));
    p28Active2.setDataCollectorName("BART");
    p28Active2.setLatestP0028Data(p28Data2);

    IterableMap<String, P0028Active> p28ActivesAndMpans = new HashedMap<String, P0028Active>();
    p28ActivesAndMpans.put(mpan1, p28Active1);
    p28ActivesAndMpans.put(mpan2, p28Active2);

    Map<String, IterableMap<String, P0028Active>> p28ActivesMap
      = new HashMap<String, IterableMap<String, P0028Active>>();
    p28ActivesMap.put(dcName, p28ActivesAndMpans);

    P0028UploadService p28UploadService = new P0028UploadService();
    p28UploadService.setSp04Validator(sp04ValidatorMock);

    IterableMap<String, P0028Active> mpansInOnlyP28ActivesMap = new HashedMap<String, P0028Active>();
    mpansInOnlyP28ActivesMap.put(mpan1, p28Active1);
    mpansInOnlyP28ActivesMap.put(mpan2, p28Active2);

    Sp04FromAFMSMpan sp04FromAfmsMpan1 = new Sp04FromAFMSMpan();
    sp04FromAfmsMpan1.setDataCollector("BART");
    sp04FromAfmsMpan1.setMpan(new MPANCore(mpan3));
    sp04FromAfmsMpan1.setPk(1L);

    IterableMap<String, Sp04FromAFMSMpan> mpansInOnlySp04FromAfmsMap = new HashedMap<String, Sp04FromAFMSMpan>();
    mpansInOnlySp04FromAfmsMap.put(mpan3, sp04FromAfmsMpan1);

    List<Object> expP28ActivesAndSp04FromAfmsMpansMaps = new ArrayList<Object>();
    expP28ActivesAndSp04FromAfmsMpansMaps.add(mpansInOnlyP28ActivesMap);
    expP28ActivesAndSp04FromAfmsMpansMaps.add(mpansInOnlySp04FromAfmsMap);

    expect(sp04ValidatorMock.validate(supplierPk, dcName, p28ActivesAndMpans))
      .andReturn(expP28ActivesAndSp04FromAfmsMpansMaps).once();

    replay(sp04ValidatorMock);

    Map<UploadStatus, List<IterableMap<String, ?>>> uploadWarnings = p28UploadService
      .getP28FileUploadWarnings(supplierPk, p28ActivesMap, UploadStatus.UPLOADED_WITH_ROW_ERRORS);

    verify(sp04ValidatorMock);

    assertNotNull(uploadWarnings);
    assertFalse(uploadWarnings.isEmpty());

    // test data 1 end


    // next test
    P0028FileDataDao p28FileDataDaoMock = createMock(P0028FileDataDao.class);
    p28UploadService.setP0028FileDataDao(p28FileDataDaoMock);

    P0028FileData expP28FileData = new P0028FileData();
    expP28FileData.setPk(300L);

    P0028File p28File = new P0028File();
    p28File.setPk(1L);
    p28File.setDateImported(new DateTime());
    p28File.setDcAgentName("BART");
    p28File.setErrored(false);
    p28File.setFilename("testp28filename001.csv");
    p28File.setReceiptDate(new DateTime());
    p28File.setVersion(0L);

    expP28FileData.setP0028File(p28File);

    expect(p28FileDataDaoMock.getLatestByP0028FileName("testp28filename001.csv")).andReturn(expP28FileData).once();
    expect(p28FileDataDaoMock.makePersistent(expP28FileData)).andReturn(expP28FileData).once();

    replay(p28FileDataDaoMock);

    Map<UploadStatus, List<IterableMap<String, ?>>> p28UploadStatusWarningsInfo = uploadWarnings;

    assertNotNull(p28UploadStatusWarningsInfo);
    assertFalse(p28UploadStatusWarningsInfo.isEmpty());

    // test call
    Boolean savedWarnings = p28UploadService.saveP0028FileUploadWarnings(p28UploadStatusWarningsInfo,
                                                                         "testp28filename001.csv");

    verify(p28FileDataDaoMock);

    assertEquals(Boolean.TRUE, savedWarnings);
  }

  /**
   * Save without warnings in message.
   */
  @Test
  public void saveP28FileUploadWarningsWithoutWarnings()
  {
    P0028FileDataDao p28FileDataDaoMock = createMock(P0028FileDataDao.class);
    P0028UploadService p28UploadService = new P0028UploadService();
    p28UploadService.setP0028FileDataDao(p28FileDataDaoMock);

    Map<UploadStatus, List<IterableMap<String, ?>>> p28UploadStatusWarningsInfo
      = new HashMap<UploadStatus, List<IterableMap<String, ?>>>();
    List<IterableMap<String, ?>> p0028ActivesSp04FromAfmsMpansMaps
      = new ArrayList<IterableMap<String, ?>>();
    p28UploadStatusWarningsInfo.put(UploadStatus.UPLOAD_OK, p0028ActivesSp04FromAfmsMpansMaps);

    assertNotNull(p28UploadStatusWarningsInfo);
    assertFalse(p28UploadStatusWarningsInfo.isEmpty());

    // test call
    Boolean savedWarnings = p28UploadService.saveP0028FileUploadWarnings(p28UploadStatusWarningsInfo,
                                                                         "dummyP0028FileName");

    assertEquals(Boolean.FALSE, savedWarnings);
  }
}
