package uk.co.utilisoft.parms.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.Sp04File;

@Repository("parms.sp04FileDao")
public class Sp04FileDaoHibernate extends ParmsGenericDao<Sp04File, Long> implements Sp04FileDao
{

}
