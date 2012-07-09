package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

/**
 *
 */
@Entity
@Table(name = "PARMS_SUPPLIER",
       uniqueConstraints = @UniqueConstraint(name = "IX_PARMS_SUPPLIER_IX1", columnNames = { "SUPPLIER_ID" })
)
@SuppressWarnings("serial")
public class Supplier extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  /**
   * char(4) derived from the AfmsMPAN.SUPPLIER_ID
   */
  private String mSupplierId;

  /**
   * Used by spring only
   */
  public Supplier()
  {
    super();
  }

  /**
   * @param aSupplierId the supplier identity
   */
  public Supplier(String aSupplierId)
  {
    super();
    mSupplierId = aSupplierId;
  }


  /**
   * @return the supplier identity
   */
  @Column(name = "SUPPLIER_ID")
  @NotNull
  @Length(max = 4)
  public String getSupplierId()
  {
    return mSupplierId;
  }

  /**
   * @param aSupplierId the supplier identity
   */
  public void setSupplierId(String aSupplierId)
  {
    mSupplierId = aSupplierId;
  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }

    if (!(o instanceof Supplier))
    {
      return false;
    }

    Supplier ga = (Supplier) o;
    return ga.getSupplierId().equals(getSupplierId());
  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    int result = 17;
    byte[] data = getSupplierId() != null ? getSupplierId().getBytes() : new byte[0];
    for (byte b : data)
    {
      result = 31 * result + b;
    }

    result = 31 * result + (null == getSupplierId() ? 0 : getSupplierId().hashCode());

    return result;
  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return "supplierId:" + getSupplierId();
  }
}
