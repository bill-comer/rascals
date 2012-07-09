package uk.co.utilisoft.parms.web.controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.view.RedirectView;

public class RedirectViewIdParametersOnly extends RedirectView
{
  private List mValidParamPattern;

  public List getValidParamPattern()
  {
    return mValidParamPattern;
  }

  private void setValidParamPattern(List validParamPattern)
  {
    this.mValidParamPattern = validParamPattern;
  }

  public RedirectViewIdParametersOnly()
  {
    super();
  }

  public RedirectViewIdParametersOnly(String url, boolean contextRelative,
      boolean http10Compatible, boolean exposeModelAttributes)
  {
    super(url, contextRelative, http10Compatible, exposeModelAttributes);
  }

  public RedirectViewIdParametersOnly(String url, boolean contextRelative,
      boolean http10Compatible)
  {
    super(url, contextRelative, http10Compatible);
  }

  public RedirectViewIdParametersOnly(String url, boolean contextRelative)
  {
    super(url, contextRelative);
  }

  public RedirectViewIdParametersOnly(String url)
  {
    super(url);
  }

  public RedirectViewIdParametersOnly(String url, List aValidParamPatterns)
  {
    super(url);
    setValidParamPattern(aValidParamPatterns);
  }

  protected Map queryProperties(Map model) {
    Map result = new LinkedHashMap();
    for (Iterator it = model.entrySet().iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      String key = entry.getKey().toString();

      if(isValueInList(key, getValidParamPattern()))
      {
        Object value = entry.getValue();
        if (isEligibleProperty(key, value)) {
          result.put(key, value);
        }
      }

    }
    return result;
  }

  public boolean isValueInList(String aValue, List<String> aItemsToCheck)
  {
    for (String item : aItemsToCheck)
    {
      if (aValue.startsWith(item))
      {
        return true;
      }
    }
    return false;
  }

}
