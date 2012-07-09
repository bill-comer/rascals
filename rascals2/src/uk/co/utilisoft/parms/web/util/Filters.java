package uk.co.utilisoft.parms.web.util;

import java.util.ArrayList;
import java.util.List;

import uk.co.utilisoft.parms.web.dto.SearchCriteriaDTO;
import uk.co.utilisoft.table.model.LabelValue;

/**
 * @author Kirk Hawksworth
 * @version 1.0
 */
public enum Filters
{
    DATE(getDateComparators()), DATETIME(getDateComparators()),
    NUM(getNumComparators()), TEXT(getTextComparators()),
    BOOL(getBoolComparators()), RECORDS_PER_PAGE(getPerPageValues()),
    TEXT_LIKE(getTextLikeComparators());

  private List<LabelValue> mComparators;

  private Filters (List<LabelValue> list)
  {
    mComparators = list;
  }

  private static List<LabelValue> getDateComparators()
  {
    List<LabelValue> date = new ArrayList<LabelValue>();
    date.add(new LabelValue("is on", SearchCriteriaDTO.COMPARATOR_EQUALS));
    date.add(new LabelValue("is after", SearchCriteriaDTO.COMPARATOR_GREATER_THAN));
    date.add(new LabelValue("is before", SearchCriteriaDTO.COMPARATOR_LESS_THAN));
    date.add(new LabelValue("is on or after", SearchCriteriaDTO.COMPARATOR_GREATER_THAN_EQUALS));
    date.add(new LabelValue("is on or before", SearchCriteriaDTO.COMPARATOR_LESS_THAN_EQUALS));
    return date;
  }

  private static List<LabelValue> getBoolComparators()
  {
    List<LabelValue> date = new ArrayList<LabelValue>();
    date.add(new LabelValue("is true", "is true"));
    date.add(new LabelValue("is false", "is false"));
    return date;
  }

  private static List<LabelValue> getNumComparators()
  {
    List<LabelValue> nums = new ArrayList<LabelValue>();
    nums.add(new LabelValue("is equal to", SearchCriteriaDTO.COMPARATOR_EQUALS));
    nums.add(new LabelValue("is greater than", SearchCriteriaDTO.COMPARATOR_GREATER_THAN));
    nums.add(new LabelValue("is less than", SearchCriteriaDTO.COMPARATOR_LESS_THAN));
    nums.add(new LabelValue("is greater than or equal to", SearchCriteriaDTO.COMPARATOR_GREATER_THAN_EQUALS));
    nums.add(new LabelValue("is less than or equal to", SearchCriteriaDTO.COMPARATOR_LESS_THAN_EQUALS));
    return nums;
  }

  private static List<LabelValue> getTextComparators()
  {
    List<LabelValue> text = new ArrayList<LabelValue>();
    text.add(new LabelValue("is equal to", SearchCriteriaDTO.COMPARATOR_EQUALS));
    text.add(new LabelValue("does not equal", SearchCriteriaDTO.COMPARATOR_NOT_EQUALS));
    return text;
  }

  private static List<LabelValue> getPerPageValues()
  {
    List<LabelValue> text = new ArrayList<LabelValue>();
    text.add(new LabelValue("10", "10"));
    text.add(new LabelValue("25", "25"));
    text.add(new LabelValue("50", "50"));
    text.add(new LabelValue("100", "100"));
    return text;
  }

  private static List<LabelValue> getTextLikeComparators()
  {
    List<LabelValue> text = new ArrayList<LabelValue>();
    text.add(new LabelValue("is like", SearchCriteriaDTO.COMPARATOR_LIKE));
    text.add(new LabelValue("is not like", SearchCriteriaDTO.COMPARATOR_NOT_LIKE));
    return text;
  }

  /**
   * @return the comparitors.
   */
  public List<LabelValue> getComparators()
  {
    return mComparators;
  }

  /**
   * @param aComparators The comparitors to set.
   */
  public void setComparitors(List<LabelValue> aComparators)
  {
    mComparators = aComparators;
  }
}

