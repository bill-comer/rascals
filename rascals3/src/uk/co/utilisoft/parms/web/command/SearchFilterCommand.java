package uk.co.utilisoft.parms.web.command;

import java.util.ArrayList;
import java.util.List;

import uk.co.utilisoft.parms.web.dto.SearchCriteriaDTO;
import uk.co.utilisoft.table.model.JodaDateTimeDTO;


/**
 * @author Daniel Winstanley
 * @version 1.0
 */
public class SearchFilterCommand extends PagingCommand
{
  private String mNewFilter;
  private JodaDateTimeDTO mDateTime;
  private boolean mSaveFilter;
  private List<SearchCriteriaDTO< ? >> mSearchCriteriaDTOs;

  /**
   *
   */
  public SearchFilterCommand()
  {
    mDateTime = new JodaDateTimeDTO();
  }

  /**
   * @return the newFilter
   */
  public String getNewFilter()
  {
    return mNewFilter;
  }

  /**
   * @param aNewFilter the newFilter to set
   */
  public void setNewFilter(String aNewFilter)
  {
    mNewFilter = aNewFilter;
  }

  /**
   * @return the dateTime
   */
  public JodaDateTimeDTO getDateTime()
  {
    return mDateTime;
  }

  /**
   * @param aDateTime the dateTime to set
   */
  public void setDateTime(JodaDateTimeDTO aDateTime)
  {
    mDateTime = aDateTime;
  }

  /**
   * @return the saveFilter
   */
  public boolean isSaveFilter()
  {
    return mSaveFilter;
  }

  /**
   * @param aSaveFilter the saveFilter to set
   */
  public void setSaveFilter(boolean aSaveFilter)
  {
    mSaveFilter = aSaveFilter;
  }

  /**
   * @return the searchCriteriaDTO
   */
  public List<SearchCriteriaDTO<?>> getSearchCriteriaDTOs()
  {
    if (mSearchCriteriaDTOs == null)
    {
      mSearchCriteriaDTOs = new ArrayList<SearchCriteriaDTO<?>>();
    }
    return mSearchCriteriaDTOs;
  }

  /**
   * @param aSearchCriteriaDTOs the searchCriteriaDTOs to set
   */
  public void setSearchCriteriaDTOs(List<SearchCriteriaDTO<?>> aSearchCriteriaDTOs)
  {
    mSearchCriteriaDTOs = aSearchCriteriaDTOs;
  }

}