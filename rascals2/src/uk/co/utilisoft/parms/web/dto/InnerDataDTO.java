package uk.co.utilisoft.parms.web.dto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InnerDataDTO<T extends Object>
{
  List<String> mHeaders = new LinkedList<String>();
  List<T> mInnerRowData = new ArrayList<T>();

  public List<String> getHeaders()
  {
    return mHeaders;
  }

  public void setHeaders(List<String> aHeaders)
  {
    this.mHeaders = aHeaders;
  }


  public List<T> getInnerRowData()
  {
    return mInnerRowData;
  }

  public void setInnerRowData(List<T> aInnerRowData)
  {
    this.mInnerRowData = aInnerRowData;
  }
}

