package uk.co.utilisoft.genericutils.web.searchfilter;

import uk.co.utilisoft.genericutils.web.util.controllers.CrudCommand;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class PagingCommand extends CrudCommand
{
  private Integer mCurrentPage;
  private Integer mRecordsPerPage;
  private String mSortList;

  /**
   * @return the currentPage
   */
  public Integer getCurrentPage()
  {
    return mCurrentPage;
  }

  /**
   * @param aCurrentPage the currentPage to set
   */
  public void setCurrentPage(Integer aCurrentPage)
  {
    mCurrentPage = aCurrentPage;
  }

  /**
   * @return the recordsPerPage
   */
  public Integer getRecordsPerPage()
  {
    return mRecordsPerPage;
  }

  /**
   * @param aRecordsPerPage the recordsPerPage to set
   */
  public void setRecordsPerPage(Integer aRecordsPerPage)
  {
    mRecordsPerPage = aRecordsPerPage;
  }

  /**
   * @return the sortList
   */
  public String getSortList()
  {
    return mSortList;
  }

  /**
   * @param aSortList the sortList to set
   */
  public void setSortList(String aSortList)
  {
    mSortList = aSortList;
  }
}
