package uk.co.utilisoft.parmsmop.dao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;


@Repository("parms.mopReportSummaryDao")
public class ParmsReportSummaryDaoImpl extends MOPGenericDao<ParmsReportSummary, BigInteger> implements ParmsReportSummaryDao<ParmsReportSummary>
{

  @Override
  public List<ParmsReportSummary> getSp11DataForStandard1(final ParmsMopReport aReport)
  {
    return (List<ParmsReportSummary>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<ParmsReportSummary> doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        String sql = "SELECT cast(GEN_STR_1 as varchar(30)) AS 'participantId', cast(GEN_STR_4 as varchar(30)) as 'reportString2', cast(Count(STANDARD_1) AS varchar(30)) AS 'reportString3'," 
                    + " cast(Sum(STANDARD_2) AS varchar(30)) AS 'reportString4',"
                    + " cast(Sum(STANDARD_3) as varchar(30)) AS 'reportString5', cast(Sum(STANDARD_4) as varchar(30)) AS 'reportString6'," 
                    + " cast(Sum(STANDARD_5) as varchar(30)) AS 'reportString7'," 
                    + " cast(Sum(STANDARD_6) as varchar(30)) AS 'reportString8', cast(Sum(STANDARD_7) as varchar(30)) AS 'reportString9'," 
                    + " cast(Sum(STANDARD_8) as varchar(30)) AS 'reportString10', cast(NHH_HH_IND as varchar(1)) as 'halfHourlyIndicator'" 
                    + " FROM [AMOPS].[dbo].[PARMS_Report_Data]"
                    + " WHERE REPORT_FK = 188"
                    + " and STANDARD_1 = :standard1Value"
                    + " GROUP BY GEN_STR_1, GEN_STR_4, NHH_HH_IND";
        
        SQLQuery query = aSession.createSQLQuery(sql);
        query.setFloat("standard1Value", new Float(1.0));
        query.setResultTransformer(Transformers.aliasToBean(ParmsReportSummary.class));

        List<ParmsReportSummary> results = query.list();
        aSession.flush();
        aSession.clear();

        for (ParmsReportSummary d0067Received : results)
        {
          d0067Received.setParmsMopReport(aReport);
        }
        return results;

      }
    });
  }

  @Override
  public List<ParmsReportSummary> getSp11ActiveSuppliers(final ParmsMopReport aReport)
  {
    return (List<ParmsReportSummary>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<ParmsReportSummary> doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        String sql = "SELECT cast(Supplier_Id as varchar(30)) AS 'participantId', " 
                    + "cast(GSP_Group as varchar(30)) as 'reportString2', "
                    + " cast('0' AS varchar(30)) AS 'reportString3'," 
                    + " cast('0' AS varchar(30)) AS 'reportString4',"
                    + " cast('0' as varchar(30)) AS 'reportString5', " 
                    + " cast('0' as varchar(30)) AS 'reportString6'," 
                    + " cast('0' as varchar(30)) AS 'reportString7'," 
                    + " cast('0' as varchar(30)) AS 'reportString8', " 
                    + " cast('0' as varchar(30)) AS 'reportString9'," 
                    + " cast('0' as varchar(30)) AS 'reportString10', " 
                    + " cast(NHH_HH_IND as varchar(1)) as 'halfHourlyIndicator'" 
                    + " FROM [AMOPS].[dbo].[Active_Suppliers]"
                    //+ " where supplier_id + GSP_Group + NHH_HH_IND not in" 
                    //+ " (select Rep_Str_1 + Rep_Str_2 + NHH_HH_IND from PARMS_Report_Summary where report_fk = :reportPk  ) and NHH_HH_IND in ('N','H')" 
                    + " group by Supplier_Id, GSP_Group, NHH_HH_IND";
        
        SQLQuery query = aSession.createSQLQuery(sql);
        //query.setFloat("reportPk", aReport.getPk());
        query.setResultTransformer(Transformers.aliasToBean(ParmsReportSummary.class));

        List<ParmsReportSummary> results = query.list();
        aSession.flush();
        aSession.clear();

        for (ParmsReportSummary d0067Received : results)
        {
          d0067Received.setParmsMopReport(aReport);
        }
        return results;

      }
    });
  }

  @Override
  public List<ParmsReportSummary> getSp11MissingActiveSuppliersForNHH(final ParmsMopReport aReport)
  {
    return (List<ParmsReportSummary>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<ParmsReportSummary> doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        String sql = "SELECT cast(Supplier_Id as varchar(30)) AS 'participantId', " 
                    + "cast(GSP_Group as varchar(30)) as 'reportString2', "
                    + " cast('0' AS varchar(30)) AS 'reportString3'," 
                    + " cast('0' AS varchar(30)) AS 'reportString4',"
                    + " cast('0' as varchar(30)) AS 'reportString5', " 
                    + " cast('0' as varchar(30)) AS 'reportString6'," 
                    + " cast('0' as varchar(30)) AS 'reportString7'," 
                    + " cast('0' as varchar(30)) AS 'reportString8', " 
                    + " cast('0' as varchar(30)) AS 'reportString9'," 
                    + " cast('0' as varchar(30)) AS 'reportString10', " 
                    + " cast('N' as varchar(1)) as 'halfHourlyIndicator'" 
                    + " FROM [AMOPS].[dbo].[Active_Suppliers]"
                    //+ " where supplier_id + GSP_Group + NHH_HH_IND not in" 
                    //+ " (select Rep_Str_1 + Rep_Str_2 + NHH_HH_IND from PARMS_Report_Summary where report_fk = :reportPk  ) and NHH_HH_IND in ('B')" 
                    + " group by Supplier_Id, GSP_Group, NHH_HH_IND";
        
        SQLQuery query = aSession.createSQLQuery(sql);
       // query.setFloat("reportPk", aReport.getPk());
        query.setResultTransformer(Transformers.aliasToBean(ParmsReportSummary.class));

        List<ParmsReportSummary> results = query.list();
        aSession.flush();
        aSession.clear();

        for (ParmsReportSummary d0067Received : results)
        {
          d0067Received.setParmsMopReport(aReport);
        }
        return results;

      }
    });
  }

  @Override
  public List<ParmsReportSummary> getSp11MissingActiveSuppliersForHH(final ParmsMopReport aReport)
  {
    return (List<ParmsReportSummary>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<ParmsReportSummary> doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        String sql = "SELECT cast(Supplier_Id as varchar(30)) AS 'participantId', " 
                    + "cast(GSP_Group as varchar(30)) as 'reportString2', "
                    + " cast('0' AS varchar(30)) AS 'reportString3'," 
                    + " cast('0' AS varchar(30)) AS 'reportString4',"
                    + " cast('0' as varchar(30)) AS 'reportString5', " 
                    + " cast('0' as varchar(30)) AS 'reportString6'," 
                    + " cast('0' as varchar(30)) AS 'reportString7'," 
                    + " cast('0' as varchar(30)) AS 'reportString8', " 
                    + " cast('0' as varchar(30)) AS 'reportString9'," 
                    + " cast('0' as varchar(30)) AS 'reportString10', " 
                    + " cast('H' as varchar(1)) as 'halfHourlyIndicator'" 
                    + " FROM [AMOPS].[dbo].[Active_Suppliers]"
                   // + " where supplier_id + GSP_Group + NHH_HH_IND not in" 
                   // + " (select Rep_Str_1 + Rep_Str_2 + NHH_HH_IND from PARMS_Report_Summary where report_fk = :reportPk  ) and NHH_HH_IND in ('B')" 
                    + " group by Supplier_Id, GSP_Group, NHH_HH_IND";
        
        SQLQuery query = aSession.createSQLQuery(sql);
       // query.setFloat("reportPk", aReport.getPk());
        query.setResultTransformer(Transformers.aliasToBean(ParmsReportSummary.class));

        List<ParmsReportSummary> results = query.list();
        aSession.flush();
        aSession.clear();

        for (ParmsReportSummary d0067Received : results)
        {
          d0067Received.setParmsMopReport(aReport);
        }
        return results;

      }
    });
  }




}
