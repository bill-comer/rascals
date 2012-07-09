package uk.co.utilisoft.genericutils.web.searchfilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Kirk Hawksworth
 * @version 1.0
 */
public class RowDTO
{
  private List<CellDTO> mCells;

  //Map to store row properties
  private Map<String, String> mRowMap = new HashMap<String, String>();

  /**
   * Default constructor.
   */
  public RowDTO()
  {  }

  /**
   * @param aCells the cells
   */
  public RowDTO(List<CellDTO> aCells)
  {
    mCells = aCells;
  }

  /**
   * @return the cells.
   */
  public List<CellDTO> getCells()
  {
    return mCells;
  }

  /**
   * @param aCells The cells to set.
   */
  public void setCells(List<CellDTO> aCells)
  {
    mCells = aCells;
  }

  /**
   * @return the rowMap.
   */
  public Map<String, String> getRowMap()
  {
    return mRowMap;
  }

  /**
   * @param aRowMap The rowMap to set.
   */
  public void setRowMap(Map<String, String> aRowMap)
  {
    mRowMap = aRowMap;
  }
}