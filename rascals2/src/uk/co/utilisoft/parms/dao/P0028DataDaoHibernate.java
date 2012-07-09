package uk.co.utilisoft.parms.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.P0028Data;

@Repository("parms.p0028DataDao")
public class P0028DataDaoHibernate extends ParmsGenericDao<P0028Data, Long> implements P0028DataDao
{

}
