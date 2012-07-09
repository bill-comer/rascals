package uk.co.utilisoft.genericutils.web.util;

import java.beans.PropertyEditorSupport;

import uk.co.utilisoft.genericutils.dtc.MPANCore;

public class MpanCorePropertyEditor extends PropertyEditorSupport
{
  /**
   * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
   */
  @Override
  public void setAsText(String aText) throws IllegalArgumentException
  {
    if (aText == null || aText.equals(""))
    {
      setValue(null);
    }
    else
    {
      MPANCore mpan = new MPANCore(aText);
      setValue(mpan);
    }
  }
  

  /**
   * @see java.beans.PropertyEditorSupport#getAsText()
   */
  @Override
  public String getAsText()
  {
    if (getValue() == null)
      return null;
    MPANCore mpan = (MPANCore) getValue();
    return mpan.getValue();
  }
}
