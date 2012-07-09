package uk.co.utilisoft.parms.web.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Philip Lau
 * @version 1.0
 */
public abstract class FormValidator implements Validator
{
  /**
   * Validate date type field values.
   *
   * @param aTarget the command object
   * @param aErrors the errors
   * @param aFormatMatcher the field format matcher as a regular expression
   * @param aFieldName the field name to validate
   * @param aValueToValidate the value of the field to validate
   * @param aErrorCode the spring error code
   * @param aErrorCodeArguments the spring error code arguments
   */
  protected void validateDateFormat(Object aTarget, Errors aErrors, String aFieldName, String aValueToValidate,
                                    String aErrorCode, Object[] aErrorCodeArguments, String aFormatMatcher)
  {
    if (StringUtils.isNotBlank(aValueToValidate))
    {
      if (!aValueToValidate.matches(aFormatMatcher))
      {
        aErrors.rejectValue(aFieldName, aErrorCode, aErrorCodeArguments, "");
      }
    }
  }
}
