package uk.co.utilisoft.parmsmop.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;


@Entity
@AccessType(value="property")
@Table(name="PARMS_DATA")
public class ParmsMopSourceData extends ParmsMopBaseData
{
  private BigInteger aPk;
  
  @Id
  @Column(name="SERIAL_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public BigInteger getPk()
  {
    return aPk;
  }
  public void setPk(BigInteger pk)
  {
    this.aPk = pk;
  }
  
  
}
