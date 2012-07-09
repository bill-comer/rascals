package uk.co.utilisoft.parms;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
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

  @Transient
  public boolean isValid()
  {
    if (StringUtils.isNotBlank(mValue))
    {
      if (mValue.length() == size)
      {
        if (mValue.matches("[0-9]*"))
        {
          return true;
        }
      }
    }

    return false;
  }

  @Transient
  public void assertValue()
  {
    Assert.hasLength(mValue, "MPAN must be a non null string");
    Assert.isTrue(mValue.length() == size, "MPAN must be of length " + size + "," + mValue + " is " + mValue.length() + " chars.");
    Assert.isTrue(mValue.matches("[0-9]*"), "MPAN must contain only digits");
  }
}
