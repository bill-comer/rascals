package uk.co.utilisoft.parms.database.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.usertype.UserType;
import org.joda.time.DateMidnight;

import uk.co.utilisoft.parms.ParmsReportingPeriod;

public class ParmsReportingPeriodType implements UserType
{
  private Logger mLogger = Logger.getLogger(getClass());

  /**
   * Implementation taken from org.hibernate.type.MutableType via
   * org.hibernate.type.CalendarType.
   * @return true if the field is mutable, false otherwise.
   *
   * @see org.hibernate.type.Type#isMutable()
   */
  public boolean isMutable()
  {
    return false; // because ParmsReportingMonth.mStartMonth never changes for lifecycle of a ParmsReportingMonth object
  }

  /**
   * @param aRs A JDBC result set
   * @param aNames The column names
   * @param aOwner The containing entity
   * @return The retrieved value.
   * @throws HibernateException If a HibernateException occurs.
   * @throws SQLException If a SQLException occurs.
   *
   * @see org.hibernate.usertype.UserType
   *      #nullSafeGet(java.sql.ResultSet, java.lang.String[],
   *                   java.lang.Object)
   */
  public Object nullSafeGet(ResultSet aRs, String[] aNames, Object aOwner)
      throws HibernateException, SQLException
  {
    if (aRs.wasNull())
    {
      return null;
    }

    Timestamp ts = aRs.getTimestamp(aNames[0]);

    if (ts == null)
    {
      return null;
    }

    DateMidnight dateTime;
    if (Environment.jvmHasTimestampBug())
    {
      dateTime = new DateMidnight(ts.getTime() + ts.getNanos() / 1000000);
    }
    else
    {
      dateTime = new DateMidnight(ts.getTime());
    }

    return new ParmsReportingPeriod(dateTime);
  }

  /**
   * Implementation taken mainly from org.hibernate.type.NullableType.
   *
   * @param aSt A JDBC prepared statement
   * @param aValue The object to write
   * @param aIndex Statement parameter index
   * @throws HibernateException If a HibernateException occurs.
   * @throws SQLException If a SQLException occurs.
   */
  public void nullSafeSet(PreparedStatement aSt, Object aValue, int aIndex)
      throws HibernateException, SQLException
  {
    try
    {
      if (aValue == null)
      {
        if (mLogger.isDebugEnabled())
        {
          mLogger.debug("binding null to parameter: " + aIndex);
        }

        aSt.setNull(aIndex, sqlType());
      }
      else
      {
        if (mLogger.isDebugEnabled())
        {
          mLogger.debug("binding '" + toString(aValue) + "' to parameter: "
            + aIndex);
        }

        set(aSt, aValue, aIndex);
      }
    }
    catch (RuntimeException re)
    {
      mLogger.info("could not bind value '" + nullSafeToString(aValue)
        + "' to parameter: " + aIndex + "; " + re.getMessage());
      throw re;
    }
    catch (SQLException se)
    {
      mLogger.info("could not bind value '" + nullSafeToString(aValue)
        + "' to parameter: " + aIndex + "; " + se.getMessage());
      throw se;
    }
  }

  /**
   * Implementation mainly taken from org.hibernate.type.CalendarType.
   *
   * @param aSt A JDBC prepared statement
   * @param aValue The object to write
   * @param aIndex Statement parameter index
   * @throws HibernateException If a HibernateException occurs.
   * @throws SQLException If a SQLException occurs.
   */
  protected void set(PreparedStatement aSt, Object aValue, int aIndex)
      throws HibernateException, SQLException
  {
    aSt.setTimestamp(aIndex, new Timestamp(((ParmsReportingPeriod) aValue).getStartOfFirstMonthInPeriod().getMillis()));
  }

  /**
   * Implementation mainly taken from org.hibernate.type.NullableType. A
   * null-safe version of {@link #toString(Object)}. Specifically we are
   * worried about null safeness in regards to the incoming value parameter, not
   * the return.
   *
   * @param aValue The value to convert to a string representation; may be null.
   * @return The string representation; may be null.
   * @throws HibernateException Thrown by {@link #toString(Object)}, which this
   *           calls.
   */
  private String nullSafeToString(Object aValue) throws HibernateException
  {
    if (aValue != null)
    {
      return toString(aValue);
    }

    return null;
  }

  /**
   * @param aValue value of the correct type.
   * @return A string representation of the given value.
   * @throws HibernateException If a HibernateException occurs.
   */
  private String toString(Object aValue) throws HibernateException
  {
    return ((ParmsReportingPeriod) aValue).toString();
  }

  /**
   *
   * @return Types.DATE
   */
  private int sqlType()
  {
    return Types.TIMESTAMP;
  }

  /**
   * @return The class returned by nullSafeGet.
   * @see org.hibernate.usertype.UserType#returnedClass()
   */
  public Class<?> returnedClass()
  {
    return ParmsReportingPeriod.class;
  }

  /**
   * @param aX First object of type returned by returnedClass.
   * @param aY Second object of type returned by returnedClass.
   * @return True if the objects are equal, false otherwise.
   * @see org.hibernate.usertype.UserType
   *      #equals(java.lang.Object, java.lang.Object)
   * @throws HibernateException to conform to superclass signature
   */
  public boolean equals(Object aX, Object aY) throws HibernateException
  {
    if (aX == aY)
    {
      return true;
    }

    if (aX == null || aY == null)
    {
      return false;
    }

    return aX.equals(aY);
  }

  /**
   * @param aX Object of type returned by returnedClass.
   * @return Hashcode of given object.
   * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
   * @throws HibernateException to conform to superclass signature
   */
  public int hashCode(Object aX) throws HibernateException
  {
    if (aX == null)
    {
      return -1;
    }

    return aX.hashCode();
  }

  /**
   * @return The sql typecodes.
   * @see org.hibernate.usertype.UserType#sqlTypes()
   */
  public int[] sqlTypes()
  {
    return new int[] {sqlType()};
  }

  /**
   * Implementation taken from
   * org.springframework.orm.hibernate3.support.AbstractLobType.
   * @param aCached The object to be cached.
   * @param aOwner The owner of the cached object.
   * @return A reconstructed object from the cachable representation.
   * @throws HibernateException If a HibernateException occurs.
   *
   * @see org.hibernate.usertype.UserType
   *      #assemble(java.io.Serializable, java.lang.Object)
   */
  public Object assemble(Serializable aCached, Object aOwner)
      throws HibernateException
  {
    return aCached;
  }

  /**
   * @param aValue the object to be cloned, which may be null.
   * @return A copy of the given object.
   * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
   * @throws HibernateException to conform to superclass signature
   */
  public Object deepCopy(Object aValue) throws HibernateException
  {
    return aValue;

  }

  /**
   * Implementation taken from
   * org.springframework.orm.hibernate3.support.AbstractLobType.
   * @param aValue The object to be cached.
   * @return A cachable representation of the object.
   *
   * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
   * @throws HibernateException to conform to superclass signature
   */
  public Serializable disassemble(Object aValue) throws HibernateException
  {
    return (Serializable) aValue;
  }

  /**
   * Implementation taken from
   * org.springframework.orm.hibernate3.support.AbstractLobType.
   * @param aOriginal The value from the detached entity being merged
   * @param aTarget The value in the managed entity
   * @param aOwner The owner of the cached object.
   * @return The value to be merged
   *
   * @see org.hibernate.usertype.UserType
   *      #replace(java.lang.Object, java.lang.Object, java.lang.Object)
   * @throws HibernateException to conform to superclass signature
   */
  public Object replace(Object aOriginal, Object aTarget, Object aOwner)
      throws HibernateException
  {
    return aOriginal;
  }
}
