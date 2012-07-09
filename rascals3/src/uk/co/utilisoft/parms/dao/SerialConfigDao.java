package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.SerialConfiguration;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface SerialConfigDao extends UtilisoftGenericDao<SerialConfiguration, Long>
{

  /**
   * Gets all the enabled supplier serials for MonthT
   * @param isForMonthT
   * @return
   */
  List<SerialConfiguration> getAllSupplierSerials();


  List<SerialConfiguration> getAllHHMopSerials(boolean isForMonthT);


  List<SerialConfiguration> getAllNonHHMopSerials(boolean isForMonthT);

  List<SerialConfiguration> getAllHHDCSerials(boolean isForMonthT);


  List<SerialConfiguration> getAllNonHHDCSerials(boolean isForMonthT);
}
