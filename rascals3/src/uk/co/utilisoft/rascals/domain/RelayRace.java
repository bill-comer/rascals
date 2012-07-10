package uk.co.utilisoft.rascals.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;


@Entity
@AccessType(value="property")
@Table(name="RELAY_RACE")
@SuppressWarnings("serial")
public class RelayRace extends IndividualRace
{

}
