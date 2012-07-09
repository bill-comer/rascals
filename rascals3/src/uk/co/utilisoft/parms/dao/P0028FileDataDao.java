package uk.co.utilisoft.parms.dao;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.P0028FileData;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface P0028FileDataDao extends UtilisoftGenericDao<P0028FileData, Long>
{
  /**
   * @param aP0028FileId the P0028File id
   * @return the P0028FileData record
   */
  P0028FileData getByP0028FileId(Long aP0028FileId);

  P0028FileData getLatestByP0028FileName(String aP0028FileName);
}
