package uk.co.utilisoft.parms.dao;

import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.GSPDefinition;


@Repository("parms.gspDefinitionDao")
public class GspDefinitionDaoHibernate extends ParmsGenericDao<GSPDefinition, Long> implements GspDefinitionDao
{

}
