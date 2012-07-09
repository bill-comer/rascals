package uk.co.utilisoft;

import org.joda.time.DateTime;

import uk.co.utilisoft.utils.Freeze;

public abstract class BaseTest
{
  public void freezeTime()
  {
    Freeze.freeze(new DateTime());
  }

  public void freezeTime(DateTime aTimeToFreezeAt)
  {
    Freeze.freeze(aTimeToFreezeAt);
  }
}
