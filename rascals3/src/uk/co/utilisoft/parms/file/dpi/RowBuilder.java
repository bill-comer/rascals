package uk.co.utilisoft.parms.file.dpi;

import java.util.List;

import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.SerialConfiguration;

public interface RowBuilder
{
  /**
   * 
   * @param rows
   * @param gsp
   * @param isHH
   * @param isMOP
   * @param dpiFile
   * @param parmsReportingPeriod
   * @param aSerials TODO
   * @param isMonthT TODO
   * @param agents TODO
   */
  void addRowsForGspAndMonth(List<String> rows, GridSupplyPoint gsp, 
      boolean isHH, boolean isMOP, DpiFile dpiFile, ParmsReportingPeriod parmsReportingPeriod, 
      List<SerialConfiguration> aSerials, boolean isMonthT);
}
