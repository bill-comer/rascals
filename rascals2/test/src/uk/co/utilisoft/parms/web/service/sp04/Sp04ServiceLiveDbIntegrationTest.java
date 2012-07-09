package uk.co.utilisoft.parms.web.service.sp04;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.dao.AFMSMeterDaoHibernate;
import uk.co.utilisoft.afms.dao.AFMSMpanDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDaoHibernate;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.P0028DataDaoHibernate;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04Calculator;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=Exception.class)
@SuppressWarnings("rawtypes")
public class Sp04ServiceLiveDbIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  private static final String SUPPLIER_ID = "EBES";

  @Autowired(required = true)
  @Qualifier("parms.sp04Service")
  private Sp04ServiceImpl mSp04Service;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDaoHibernate mAFMSMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDaoHibernate mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.sp04Calculator")
  private Sp04Calculator mSp04Calculator;

  @Autowired(required = true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate mAgentDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028DataDao")
  private P0028DataDaoHibernate mP0028DataDao;

  private Sp04FromAFMSMpanDao mMockSp04FromAFMSMpanDao = createMock(Sp04FromAFMSMpanDao.class);
  private P0028ActiveDao mMockP0028ActiveDao = createMock(P0028ActiveDao.class);

  /**
   *  ??? mpans are expected on eligible mpan screen, and sp04 report
   *  for June 2011 reporting month.
   */
  @Test
  public void testMpansWithMetersInstalledLateInReportMonthJune2011()
  {
    // simulate generation of sp04 report
    Freeze.freeze(28, 7, 2011);
    Supplier supplier = mSupplierDao.getSupplier(SUPPLIER_ID);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    DateTime now = new DateMidnight().toDateTime();

    String expSp04Mpan1 = "2000027423000";
    String expSp04Mpan2 = "1800035280644";
    String expSp04Mpan4 = "1900013209339";
    String expSp04Mpan5 = "1200052005286";
    String expSp04Mpan6 = "2000027443655";
    String expSp04Mpan7 = "2366510081011";
    String expSp04Mpan8 = "2000027327211";
    String expSp04Mpan9 = "1012823176204";

    String expP28Mpan1 = "2000027443655";
    String expP28Mpan2 = "2000011102940";

    Set<String> expMpans = new HashSet<String>();
    expMpans.add(expSp04Mpan1);
    expMpans.add(expSp04Mpan2);
    expMpans.add(expSp04Mpan4);
    expMpans.add(expSp04Mpan5);
    expMpans.add(expSp04Mpan6);
    expMpans.add(expSp04Mpan7);
    expMpans.add(expSp04Mpan8);
    expMpans.add(expSp04Mpan9);
    expMpans.add(expP28Mpan1);
    expMpans.add(expP28Mpan2);

    // afms mpans
    List<Sp04FromAFMSMpan> expSp04FromAFMSMpans = new ArrayList<Sp04FromAFMSMpan>();

    Sp04FromAFMSMpan sp04Mpan1 = createSp04FromAFMSMpan(expSp04Mpan1, 1L, 177926L, 3L,
      new DateMidnight(2011, 5, 9).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 177859L,
      "E10BG06081", new DateMidnight(2011, 2, 9).toDateTime(), new DateMidnight(2011, 1, 9).toDateTime(),
      new DateMidnight(2010, 12, 9).toDateTime(), 213927L, 155F, 165F, 155F, 21L, 21L, 100F,
      new DateMidnight(2010, 4, 27).toDateTime(), 158.33332824707F);

    Sp04FromAFMSMpan sp04Mpan2 = createSp04FromAFMSMpan(expSp04Mpan2, 2L, 187589L, 3L,
      new DateMidnight(2011, 6, 10).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 187519L,
      "E10BG18068", new DateMidnight(2011, 3, 10).toDateTime(), new DateMidnight(2011, 2, 9).toDateTime(),
      new DateMidnight(2011, 1, 10).toDateTime(), 221892L, 130F, 120F, 120F, 20L, 20L, 100F,
      new DateMidnight(2010, 11, 15).toDateTime(), 128.33332824707F);

    Sp04FromAFMSMpan sp04Mpan4 = createSp04FromAFMSMpan(expSp04Mpan4, 4L, 183857L, 3L,
      new DateMidnight(2011, 3, 9).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 183787L,
      "E10BG27700", new DateMidnight(2010, 12, 9).toDateTime(), new DateMidnight(2010, 11, 9).toDateTime(),
      new DateMidnight(2010, 10, 14).toDateTime(), 220716L, 104F, 103F, 100F, 30L, 30L, 100F,
      new DateMidnight(2010, 9, 10).toDateTime(), 102.333335876465F);

    Sp04FromAFMSMpan sp04Mpan5 = createSp04FromAFMSMpan(expSp04Mpan5, 5L, 186126L, 3L,
      new DateMidnight(2011, 4, 5).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 186056L,
      "E10BG07331", new DateMidnight(2011, 1, 5).toDateTime(), new DateMidnight(2011, 1, 5).toDateTime(),
      new DateMidnight(2010, 12, 10).toDateTime(), 223069L, 104F, 103F, 101F, 25L, 25L, 100F,
      new DateMidnight(2010, 10, 21).toDateTime(), 102.666664123535F);

    Sp04FromAFMSMpan sp04Mpan6 = createSp04FromAFMSMpan(expSp04Mpan6, 6L, 177384L, 3L,
      new DateMidnight(2010, 12, 9).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 177317L,
      "E10BG26799", new DateMidnight(2010, 9, 9).toDateTime(), new DateMidnight(2010, 8, 9).toDateTime(),
      new DateMidnight(2010, 7, 15).toDateTime(), 210022L, 145F, 145F, 140F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 1).toDateTime(), 143.333328247F);

    Sp04FromAFMSMpan sp04Mpan7 = createSp04FromAFMSMpan(expSp04Mpan7, 7L, 177882L, 3L,
      new DateMidnight(2011, 1, 11).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 177815L,
      "E10BG28521", new DateMidnight(2010, 10, 11).toDateTime(), new DateMidnight(2010, 9, 9).toDateTime(),
      new DateMidnight(2010, 8, 16).toDateTime(), 214619L, 100F, 100F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 23).toDateTime(), 100.333335876465F);

    Sp04FromAFMSMpan sp04Mpan8 = createSp04FromAFMSMpan(expSp04Mpan8, 8L, 178554L, 3L,
      new DateMidnight(2011, 2, 9).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 178487L,
      "E07BG05064", new DateMidnight(2010, 11, 9).toDateTime(), new DateMidnight(2010, 6, 3).toDateTime(),
      new DateMidnight(2010, 6, 3).toDateTime(), 207216L, 101F, 101F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 5, 19).toDateTime(), 101F);

    Sp04FromAFMSMpan sp04Mpan9 = createSp04FromAFMSMpan(expSp04Mpan9, 9L, 179306L, 3L,
      new DateMidnight(2011, 1, 11).toDateTime(), new DateMidnight(2011, 7, 28).toDateTime(), "BMET", 179239L,
      "E10BG05396", new DateMidnight(2010, 10, 11).toDateTime(), new DateMidnight(2010, 9, 9).toDateTime(),
      new DateMidnight(2010, 7, 27).toDateTime(), 212761L, 101F, 101F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 6, 9).toDateTime(), 101F);

    expSp04FromAFMSMpans.add(sp04Mpan1);
    expSp04FromAFMSMpans.add(sp04Mpan2);
    expSp04FromAFMSMpans.add(sp04Mpan4);
    expSp04FromAFMSMpans.add(sp04Mpan5);
    expSp04FromAFMSMpans.add(sp04Mpan6);
    expSp04FromAFMSMpans.add(sp04Mpan7);
    expSp04FromAFMSMpans.add(sp04Mpan8);
    expSp04FromAFMSMpans.add(sp04Mpan9);

    // p28 active mpans
    IterableMap<String, P0028Active> expP0028ActiveMpans = new HashedMap<String, P0028Active>();
    P0028Active p28Active1 = createP28Active(1L, 3L, null, "BMET", expP28Mpan1, 141L,
      new DateMidnight(2010, 10, 18).toDateTime(), "E10BG26799", new DateMidnight(2010, 9, 6).toDateTime(), 5L);
    P0028Active p28Active2 = createP28Active(2L, 3L, null, "BMET", expP28Mpan2, 103L,
      new DateMidnight(2011, 3, 21).toDateTime(), "K96C08740", new DateMidnight(2011, 1, 9).toDateTime(), 16L);

    expP0028ActiveMpans.put(p28Active1.getMpanCore().getValue(), p28Active1);
    expP0028ActiveMpans.put(p28Active2.getMpanCore().getValue(), p28Active2);

    expect(mMockSp04FromAFMSMpanDao.get(supplier.getPk(), now)).andReturn(expSp04FromAFMSMpans).once();
    expect(mMockP0028ActiveDao.get(supplier, now)).andReturn(expP0028ActiveMpans).once();

    replay(mMockSp04FromAFMSMpanDao, mMockP0028ActiveDao);

    MultiHashMap<String, Sp04FromAFMSMpan> afmsMpanDatas
      = mSp04Service.getEligibleMpansForSp04FromAfmsMpans(supplier.getPk(), now);
    MultiHashMap<String, P0028Active> p28MpanDatas
      = mSp04Service.getEligibleMpansForP0028Active(supplier.getPk(), lastMonthPrp, now);
    IterableMap<String, Object> p28AndAfmsMapToUse
      = mSp04Service.combineP0028AndAfmsMpans(afmsMpanDatas, p28MpanDatas);

    verify(mMockSp04FromAFMSMpanDao, mMockP0028ActiveDao);

    // TODO asserts
    fail();

    Freeze.thaw();
  }

  /**
   * Simulate generating sp04 data for eligible mpan screen and sp04 report.
   */
  @Test
  public void generateSp04ReportOn20110728Simulation()
  {
    // simulate generation of sp04 report
    Freeze.freeze(28, 7, 2011);
    Supplier supplier = mSupplierDao.getSupplier(SUPPLIER_ID);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    DateTime now = new DateMidnight().toDateTime();

    String expMpan1= "2000027423000";
    String expMpan2 = "1900013209339";
    String expMpan3 = "1200052005286";
    String expMpan4 = "2000027443655";
    String expMpan5 = "2366510081011";
    String expMpan6 = "1012823176204";
    Set<String> expMpans = new HashSet<String>();
    expMpans.add(expMpan1);
    expMpans.add(expMpan2);
    expMpans.add(expMpan3);
    expMpans.add(expMpan4);
    expMpans.add(expMpan5);
    expMpans.add(expMpan6);

    // afms mpans
    List<Sp04FromAFMSMpan> expSp04FromAFMSMpans = new ArrayList<Sp04FromAFMSMpan>();

    Sp04FromAFMSMpan sp04Mpan1 = createSp04FromAFMSMpan(expMpan3,1L, 186126L, 3L,
      new DateMidnight(2011, 4, 5).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 186056L,
      "E10BG07331", new DateMidnight(2011, 1, 5).toDateTime(), new DateMidnight(2011, 1, 5).toDateTime(),
      new DateMidnight(2010, 12, 10).toDateTime(), 223069L, 104F, 103F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 10, 21).toDateTime(), 102.666664124F);

    Sp04FromAFMSMpan sp04Mpan2 = createSp04FromAFMSMpan("1100025513718", 2L, 177251L, 3L,
      new DateMidnight(2011, 4, 12).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177184L,
      "E10BG02362", new DateMidnight(2011, 1, 12).toDateTime(), new DateMidnight(2010, 12, 13).toDateTime(),
      new DateMidnight(2010, 11, 10).toDateTime(), 220626L, 100F, 100F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 3, 18).toDateTime(), 100.333335876F);

    Sp04FromAFMSMpan sp04Mpan3 = createSp04FromAFMSMpan(expMpan4, 3L, 177384L, 3L,
      new DateMidnight(2010, 12, 9).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177317L,
      "E10BG26799", new DateMidnight(2010, 9, 9).toDateTime(), new DateMidnight(2010, 8, 9).toDateTime(),
      new DateMidnight(2010, 7, 15).toDateTime(), 210022L, 145F, 145F, 140F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 1).toDateTime(), 143.333328247F);

    Sp04FromAFMSMpan sp04Mpan4 = createSp04FromAFMSMpan("1200052522015", 4L, 177523L, 3L,
      new DateMidnight(2011, 5, 7).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177456L,
      "E10BG26672", new DateMidnight(2011, 2, 7).toDateTime(), new DateMidnight(2010, 8, 9).toDateTime(),
      new DateMidnight(2010, 7, 15).toDateTime(), 209816L, 150F, 145F, 145F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 12).toDateTime(), 146.666671753F);

    Sp04FromAFMSMpan sp04Mpan5 = createSp04FromAFMSMpan("1100039362147", 5L, 177849L, 3L,
      new DateMidnight(2010, 10, 15).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177782L,
      "E10BG05312", new DateMidnight(2010, 7, 15).toDateTime(), new DateMidnight(2010, 7, 9).toDateTime(),
      new DateMidnight(2010, 7, 15).toDateTime(), 210366L, 101F, 101F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 21).toDateTime(), 101F);

    Sp04FromAFMSMpan sp04Mpan6 = createSp04FromAFMSMpan(expMpan1, 6L, 177926L, 3L,
      new DateMidnight(2011, 5, 9).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177859L,
      "E10BG06081", new DateMidnight(2011, 2, 9).toDateTime(), new DateMidnight(2011, 1, 9).toDateTime(),
      new DateMidnight(2010, 12, 9).toDateTime(), 213927L, 155F, 165F, 155F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 27).toDateTime(), 158.333328247F);

    Sp04FromAFMSMpan sp04Mpan7 = createSp04FromAFMSMpan(expMpan6, 7L, 179306L, 3L,
      new DateMidnight(2011, 1, 1).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 179239L,
      "E10BG05396", new DateMidnight(2011, 10, 11).toDateTime(), new DateMidnight(2010, 9, 9).toDateTime(),
      new DateMidnight(2010, 7, 27).toDateTime(), 212761L, 101F, 101F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 6, 9).toDateTime(), 101F);

    Sp04FromAFMSMpan sp04Mpan8 = createSp04FromAFMSMpan("2000007370940", 8L, 181109L, 3L,
      new DateMidnight(2011, 5, 9).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 181042L,
      "E07BG33273", new DateMidnight(2011, 2, 9).toDateTime(), new DateMidnight(2011, 1, 9).toDateTime(),
      new DateMidnight(2010, 12, 9).toDateTime(), 210734L, 102F, 104F, 105F, 30L, 30L,  100F,
      new DateMidnight(2010, 7, 26).toDateTime(), 103.666664124F);

    Sp04FromAFMSMpan sp04Mpan9 = createSp04FromAFMSMpan(expMpan2, 9L, 183857L, 3L,
      new DateMidnight(2011, 3, 9).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 183787L,
      "E10BG27700", new DateMidnight(2010, 12, 9).toDateTime(), new DateMidnight(2010, 11, 9).toDateTime(),
      new DateMidnight(2010, 10, 14).toDateTime(), 220716L, 104F, 103F, 100F, 30L, 30L, 100F,
      new DateMidnight(2010, 9, 10).toDateTime(), 102.333335876F);

    Sp04FromAFMSMpan sp04Mpan10 = createSp04FromAFMSMpan(expMpan5, 10L, 177882L, 3L,
      new DateMidnight(2011, 1, 11).toDateTime(), new DateMidnight(2011, 10, 28).toDateTime(), "BMET", 177815L,
      "E10BG28521", new DateMidnight(2010, 10, 11).toDateTime(), new DateMidnight(2010, 9, 9).toDateTime(),
      new DateMidnight(2010, 8, 16).toDateTime(), 214619L, 100F, 100F, 101F, 30L, 30L, 100F,
      new DateMidnight(2010, 4, 23).toDateTime(), 100.333335876F);

    expSp04FromAFMSMpans.add(sp04Mpan1);
    expSp04FromAFMSMpans.add(sp04Mpan2);
    expSp04FromAFMSMpans.add(sp04Mpan3);
    expSp04FromAFMSMpans.add(sp04Mpan4);
    expSp04FromAFMSMpans.add(sp04Mpan5);
    expSp04FromAFMSMpans.add(sp04Mpan6);
    expSp04FromAFMSMpans.add(sp04Mpan7);
    expSp04FromAFMSMpans.add(sp04Mpan8);
    expSp04FromAFMSMpans.add(sp04Mpan9);
    expSp04FromAFMSMpans.add(sp04Mpan10);

    // p0028 active mpans
    IterableMap<String, P0028Active> expP0028ActiveMpans = new HashedMap<String, P0028Active>();

    // note only mpan 2000027443655 is included when running application. Try without filtering here to see if it is
    // removed on eligible screen, and sp04 file generation
    P0028Active p28Active1 = createP28Active(1L, 3L, null, "BMET", "2000027443655", 141L,
      new DateMidnight(2010, 10, 18).toDateTime(), "E10BG26799", new DateMidnight(2010, 9, 6).toDateTime(), 5L);
    P0028Active p28Active2 = createP28Active(2L, 3L, null, "BMET", "1200052522015", 103L,
      new DateMidnight(2010, 10, 18).toDateTime(), "E10BG26672", new DateMidnight(2010, 9, 6).toDateTime(), 4L);
    P0028Active p28Active3 = createP28Active(3L, 3L, null, "lbsl", "1421466100003", 121L,
      new DateMidnight(2010, 12, 21).toDateTime(), "209166113", new DateMidnight(2012, 1, 20).toDateTime(), 7L);
    P0028Active p28Active4 = createP28Active(4L, 3L, null, "BMET", "1620001096204", 100L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG26753", new DateMidnight(2011, 1, 20).toDateTime(), 8L);
    P0028Active p28Active5 = createP28Active(5L, 3L, null, "BMET", "1200028648209", 111L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG39020", new DateMidnight(2011, 1, 20).toDateTime(), 9L);
    P0028Active p28Active6 = createP28Active(6L, 3L, null, "BMET", "1013029210667", 102L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG36033", new DateMidnight(2011, 1, 20).toDateTime(), 10L);
    P0028Active p28Active7 = createP28Active(7L, 3L, null, "BMET", "1100018417970", 101L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG29394", new DateMidnight(2011, 1, 20).toDateTime(), 11L);
    P0028Active p28Active8 = createP28Active(8L, 3L, null, "BMET", "1300007616730", 103L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG30106", new DateMidnight(2011, 1, 20).toDateTime(), 12L);
    P0028Active p28Active9 = createP28Active(9L, 3L, null, "BMET", "2000000073714", 104L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG31510", new DateMidnight(2011, 1, 20).toDateTime(), 13L);
    P0028Active p28Active10 = createP28Active(10L, 3L, null, "BMET", "2199990070984", 106L,
      new DateMidnight(2011, 2, 21).toDateTime(), "E10BG47088", new DateMidnight(2011, 1, 20).toDateTime(), 14L);
    P0028Active p28Active11 = createP28Active(11L, 3L, null, "BMET", "1630000722977", 108L,
      new DateMidnight(2011, 2, 21).toDateTime(), "K08M00817", new DateMidnight(2011, 1, 20).toDateTime(), 15L);
    P0028Active p28Active12 = createP28Active(12L, 3L, null, "BMET", "2000011102940", 103L,
      new DateMidnight(2011, 3, 21).toDateTime(), "K96C08740", new DateMidnight(2011, 1, 9).toDateTime(), 16L);

    expP0028ActiveMpans.put(p28Active1.getMpanCore().getValue(), p28Active1);
    expP0028ActiveMpans.put(p28Active2.getMpanCore().getValue(), p28Active2);
    expP0028ActiveMpans.put(p28Active3.getMpanCore().getValue(), p28Active3);
    expP0028ActiveMpans.put(p28Active4.getMpanCore().getValue(), p28Active4);
    expP0028ActiveMpans.put(p28Active5.getMpanCore().getValue(), p28Active5);
    expP0028ActiveMpans.put(p28Active6.getMpanCore().getValue(), p28Active6);
    expP0028ActiveMpans.put(p28Active7.getMpanCore().getValue(), p28Active7);
    expP0028ActiveMpans.put(p28Active8.getMpanCore().getValue(), p28Active8);
    expP0028ActiveMpans.put(p28Active9.getMpanCore().getValue(), p28Active9);
    expP0028ActiveMpans.put(p28Active10.getMpanCore().getValue(), p28Active10);
    expP0028ActiveMpans.put(p28Active11.getMpanCore().getValue(), p28Active11);
    expP0028ActiveMpans.put(p28Active12.getMpanCore().getValue(), p28Active12);

    expect(mMockSp04FromAFMSMpanDao.get(supplier.getPk(), now)).andReturn(expSp04FromAFMSMpans).once();
    expect(mMockP0028ActiveDao.get(supplier, now)).andReturn(expP0028ActiveMpans).once();

    replay(mMockSp04FromAFMSMpanDao, mMockP0028ActiveDao);

    MultiHashMap<String, Sp04FromAFMSMpan> afmsMpanDatas
      = mSp04Service.getEligibleMpansForSp04FromAfmsMpans(supplier.getPk(), now);
    MultiHashMap<String, P0028Active> p28MpanDatas
      = mSp04Service.getEligibleMpansForP0028Active(supplier.getPk(), lastMonthPrp, now);
    IterableMap<String, Object> p28AndAfmsMapToUse
      = mSp04Service.combineP0028AndAfmsMpans(afmsMpanDatas, p28MpanDatas);

    verify(mMockSp04FromAFMSMpanDao, mMockP0028ActiveDao);

    MapIterator<String, Object> p28AndAfmsMapToUseIter = p28AndAfmsMapToUse.mapIterator();
    while (p28AndAfmsMapToUseIter.hasNext())
    {
      String expMpan = p28AndAfmsMapToUseIter.next();
      assertTrue("Combination of P28 Active and Afms mpan records for Sp04 reporting is missing mpan: " + expMpan,
                 expMpans.contains(expMpan));

      // expect a P0028Active record, because it has priority over matching afms mpans found in the database
      if (expMpan.equals(expMpan4))
      {
        assertTrue("expected P0028Active with mpan: " + expMpan,
                   p28AndAfmsMapToUseIter.getValue() instanceof P0028Active);
      }
    }

    Freeze.thaw();
  }

  @BeforeTransaction
  public void init()
  {
    assertNotNull(mSupplierDao);
    String supplierId = SUPPLIER_ID;
    assertNotNull(mSupplierDao.getSupplier(supplierId));

    assertNotNull(mAFMSMpanDao);

    Number afmsMpanCount = (Number) mAFMSMpanDao.getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMpan.class).setProjection(Projections.rowCount()).uniqueResult();
      }
    });

    assertNotNull(afmsMpanCount);

    if (afmsMpanCount.longValue() < 1)
    {
      fail("Failed to find any AFMSMpan records in the live database. Check this test class is configured to run against a sample live AFMS database");
    }
    else
    {
      assertNotNull(mSp04Service);

      // NOTE: Tests should not write to the live sample database.
      // Need to create mockServices and mockDaos inside mSp04FromAfmsMpanBuilder
      mSp04Service.setAFMSMpanDao(mAFMSMpanDao);
      mSp04Service.setSupplierDao(mSupplierDao);
      mSp04Service.setSp04FromAFMSMpanDao(mMockSp04FromAFMSMpanDao);

      assertNotNull(mAFMSMeterDao);
      mSp04Service.setAFMSMeterDao(mAFMSMeterDao);

      assertNotNull(mMockP0028ActiveDao);
      mSp04Service.setP0028ActiveDao(mMockP0028ActiveDao);

      assertNotNull(mSp04Calculator);
      mSp04Service.setSp04Calculator(mSp04Calculator);
    }

    assertNotNull(mAgentDao);
    assertNotNull(mP0028DataDao);
  }

  @After
  public void complete()
  {
    cleanUpAfterTest();
  }

  private Sp04FromAFMSMpan createSp04FromAFMSMpan(String aMpan, long aPk, long aMpanFk, long aSupplierFk,
      DateTime aCalcMID, DateTime aD0268SettDate, String aDc, long aDcFk, String aMeterId, DateTime aMeterReadDate1,
      DateTime aMeterReadDate2, DateTime aMeterReadDate3, Long aMeterRegFk, float aReading1, float aReading2,
      float aReading3, long aStandard1, long aStandard2, float aStandard3, DateTime aEfd, float aMaxDemand)
  {
    Sp04FromAFMSMpan sp04Mpan1 = new Sp04FromAFMSMpan();
    sp04Mpan1.setMpan(new MPANCore(aMpan));
    sp04Mpan1.setPk(aPk);
    sp04Mpan1.setMpanFk(aMpanFk);
    sp04Mpan1.setSupplierFk(aSupplierFk);
    sp04Mpan1.setCalculatedMeterInstallationDeadline(aCalcMID);
    sp04Mpan1.setD0268SettlementDate(aD0268SettDate);
    sp04Mpan1.setDataCollector(aDc);
    sp04Mpan1.setDataCollectorFk(aDcFk);
    sp04Mpan1.setMeterId(aMeterId);
    sp04Mpan1.setMeterReadingDate1(aMeterReadDate1);
    sp04Mpan1.setMeterReadingDate2(aMeterReadDate2);
    sp04Mpan1.setMeterReadingDate3(aMeterReadDate3);

    if (aMeterRegFk != null)
    {
      sp04Mpan1.setMeterRegisterFks(aMeterRegFk.toString());
    }

    sp04Mpan1.setMeterRegisterReading1(aReading1);
    sp04Mpan1.setMeterRegisterReading2(aReading2);
    sp04Mpan1.setMeterRegisterReading3(aReading3);
    sp04Mpan1.setCalculatedStandard1(aStandard1);
    sp04Mpan1.setCalculatedStandard2(aStandard2);
    sp04Mpan1.setCalculatedStandard3(aStandard3);
    sp04Mpan1.setEffectiveFromDate(aEfd);
    sp04Mpan1.setMaxDemand(aMaxDemand);

    return sp04Mpan1;
  }

  private P0028Active createP28Active(Long aPk, Long aSupplierPk, Long aDcFk, String aDcName, String aMpan,
                                      Long aMaxDemand, DateTime aP28ReceivedDate, String aMeterId,
                                      DateTime aMeterRegDate, Long aP28DataPk)
  {
    P0028Active p28Active = new P0028Active();
    p28Active.setPk(aPk);
    p28Active.setSupplier(mSupplierDao.getById(aSupplierPk));

    GenericAgent agent = mAgentDao.getAgentById(aDcFk);
    if (agent instanceof DataCollector)
    {
      p28Active.setDataCollector((DataCollector) agent);
    }

    p28Active.setDataCollectorName(aDcName);
    p28Active.setMpanCore(new MPANCore(aMpan));
    p28Active.setMaxDemand(aMaxDemand);
    p28Active.setP0028ReceivedDate(aP28ReceivedDate);
    p28Active.setMeterSerialId(aMeterId);
    p28Active.setMeterReadingDate(aMeterRegDate);
    p28Active.setLatestP0028Data(mP0028DataDao.getById(aP28DataPk));

    return p28Active;
  }
}