package uk.co.utilisoft.genericutils.dtc;

import java.io.Serializable;

import org.springframework.util.Assert;

public class MPANCore implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1923472934293429L;
  private static final int size = 13;
  
  public MPANCore(String aValue)
  {
    setValue(aValue);
  }

  String mValue;

  public String getValue()
  {
    return mValue;
  }

  public void setValue(String aValue)
  {
    Assert.hasLength(aValue, "MPAN must be a non null string");
    Assert.isTrue(aValue.length() == size, "MPAN must be of length " + size + "," + aValue + " is " + aValue.length() + " chars.");
    
    Assert.isTrue(aValue.matches("[0-9]*"), "must be only digits");
    
    this.mValue = aValue;
  }
  
  public String toString()
  {
    return getValue();
  }
  

  @Override
  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MPANCore))
      return false;

    MPANCore ga = (MPANCore) o;
    return ga.getValue().equals(getValue());
  }

  @Override
  public int hashCode()
  {
    return super.hashCode();
  }
  
  
}
