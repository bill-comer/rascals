package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;

public interface GridSupplyPointDao extends UtilisoftGenericDao<GridSupplyPoint, Long>
{
  /**
   * Gets a distinct list of all suppliers associated with a DPI file and therefore a TimePeriod
   * @param aDpiFile
   * @return
   */
  List<GridSupplyPoint> getAllGSPsDpi(DpiFile aDpiFile);
  
}
