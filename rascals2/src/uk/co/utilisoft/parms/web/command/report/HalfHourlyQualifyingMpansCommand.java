package uk.co.utilisoft.parms.web.command.report;

import java.util.List;

import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.web.command.ParmsCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class HalfHourlyQualifyingMpansCommand extends ParmsCommand
{
  List<Supplier> mSuppliers;

  /**
   * @return the suppliers
   */
  public List<Supplier> getSuppliers()
  {
    return mSuppliers;
  }

  /**
   * @param aSuppliers the suppliers
   */
  public void setSuppliers(List<Supplier> aSuppliers)
  {
    mSuppliers = aSuppliers;
  }

}
