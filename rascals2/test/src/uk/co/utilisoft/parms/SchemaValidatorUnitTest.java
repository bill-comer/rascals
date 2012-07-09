package uk.co.utilisoft.parms;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.SerialConfiguration;
import uk.co.utilisoft.parms.domain.Supplier;

import static junit.framework.Assert.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class SchemaValidatorUnitTest
{
  /**
   * Validate hibernate domain objects manually
   */
  @Test
  public void validateDomainsHaveNoConstraintErrors001()
  {
    // parms domains
    ConfigurationParameter cp = new ConfigurationParameter(ConfigurationParameter.NAME
                                                           .PARMS_DPI_FILE_LOCATION, "c:/temp", "DPI File Location");
    assertTrue(validate(cp).isEmpty());

    Supplier supplier = new Supplier("OVOE");
    supplier.setLastUpdated(new DateTime());
    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    DataCollector agent = new DataCollector("OVOE", true, dpiFile, true);
    assertTrue(validate(agent).isEmpty());

    assertTrue(validate(dpiFile).isEmpty());

    DpiFileData dpiFileData = new DpiFileData("test dpi file data", dpiFile);
    assertTrue(validate(dpiFileData).isEmpty());

    assertTrue(validate(new GridSupplyPoint("_A", dpiFile)).isEmpty());

    MOP mop = new MOP("OVOE", true, dpiFile, true);
    assertTrue(validate(mop).isEmpty());

    SerialConfiguration serialConfig = new SerialConfiguration();
    serialConfig.setDataCollector(false);
    serialConfig.setEnabled(true);
    serialConfig.setHalfHourly(true);
    serialConfig.setMonthT(false);
    serialConfig.setMop(false);
    serialConfig.setName("SP04");
    serialConfig.setLastUpdated(new DateTime());
    assertTrue(validate(serialConfig).isEmpty());

    assertTrue(validate(supplier).isEmpty());
  }

  private Set<ConstraintViolation<Object>> validate(Object aDomainToValidate)
  {
    ValidatorFactory factory = Validation.byDefaultProvider().configure().buildValidatorFactory();
    Validator validator = factory.getValidator();
    return validator.validate(aDomainToValidate);
  }
}
