package uk.co.utilisoft.parms.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.Audit;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.auditDao")
public class AuditDaoHibernate extends ParmsGenericDao<Audit, Long> implements AuditDao
{

}
