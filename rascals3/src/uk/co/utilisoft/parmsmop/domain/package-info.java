@TypeDefs(
{
  @TypeDef(name = "org.joda.time.DateTime", typeClass = JodaDateTimeType.class),
  @TypeDef(name = "uk.co.utilisoft.parms.MPANCore", typeClass = MPANCoreType.class)
  })

package uk.co.utilisoft.parmsmop.domain;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import uk.co.formfill.hibernateutils.type.JodaDateTimeType;
import uk.co.utilisoft.parms.database.type.MPANCoreType;

