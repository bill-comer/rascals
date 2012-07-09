package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface DpiFileDao extends UtilisoftGenericDao<DpiFile, Long>
{
  public List<DpiFile> getDpiFilesForSupplier(final Supplier aSupplier);

}
