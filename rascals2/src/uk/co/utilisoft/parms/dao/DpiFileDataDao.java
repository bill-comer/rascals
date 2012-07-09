package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DpiFileData;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface DpiFileDataDao extends UtilisoftGenericDao<DpiFileData, Long>
{
  /**
   * @param aDpiFilePk the DPI File primary key
   * @return a List of DPI File Data
   */
  List<DpiFileData> getByDpiFilePk(Long aDpiFilePk);
}
