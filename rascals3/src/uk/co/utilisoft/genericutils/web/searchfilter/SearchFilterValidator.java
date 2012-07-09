package uk.co.utilisoft.genericutils.web.searchfilter;

import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import uk.co.utilisoft.genericutils.dtc.MPANCore;
import config.GenericWebConstants;


/**
 * @author Daniel Winstanley/Bill Comer
 * @version 1.0
 */

@Component("generic.SearchFilterValidator")
public class SearchFilterValidator implements Validator, InitializingBean
{
  private javax.validation.Validator mValidator;

  /**
   * @see org.springframework.validation.Validator
   * #supports(java.lang.Class)
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public boolean supports(Class aClass)
  {
    return SearchFilterCommand.class.isAssignableFrom(aClass);
  }

  /**
   * @see org.springframework.validation.Validator
   * #validate(java.lang.Object, org.springframework.validation.Errors)
   * {@inheritDoc}
   */
  public void validate(Object aTarget, Errors aErrors)
  {
    validateMpan(aTarget, aErrors);

    validateConstraints(aTarget, aErrors);
  }

  private void validateConstraints(Object aTarget, Errors aErrors)
  {
    Set<ConstraintViolation<Object>> constraintViolations = mValidator.validate(aTarget);
    for (ConstraintViolation<Object> constraintViolation : constraintViolations)
    {
      String propertyPath = constraintViolation.getPropertyPath().toString();
      String message = constraintViolation.getMessage();
      aErrors.rejectValue(propertyPath, "", message);
    }
  }
  

  private void validateMpan(Object aTarget, Errors aErrors)
  {
    SearchFilterCommand command = (SearchFilterCommand) aTarget;
    for (SearchCriteriaDTO<?> criteria : command.getSearchCriteriaDTOs())
    {
      if (criteria.getClassType().equals(MPANCore.class))
      {
        SearchCriteriaDTO<String> typedFilterCriteria = (SearchCriteriaDTO<String>) criteria;
        if (criteria.getDisplayName().equals("MPAN"))
        {
          if (typedFilterCriteria.getSearchValue() != null)
          {
            String searchValue = typedFilterCriteria.getSearchValue().trim();

            if (searchValue.length() > 0)
            {
              try
              {
                new MPANCore(searchValue).setValue(searchValue);
              }
              catch (RuntimeException re)
              {
                aErrors.rejectValue("searchCriteriaDTOs[0].searchValue", GenericWebConstants.INVALID_MPAN, new Object[] {re.getMessage()}, "");
              }
            }
          }
        }
      }
    }
  }

  /**
   * Set up JPA Validator
   * @throws Exception
   */
  public void afterPropertiesSet() throws Exception
  {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    mValidator = validatorFactory.usingContext().getValidator();
  }

  /**
   * @return the Validator.
   */
  public javax.validation.Validator getValidator()
  {
    return mValidator;
  }

  /**
   * @param aValidator The Validator to set.
   */
  public void setValidator(javax.validation.Validator aValidator)
  {
    mValidator = aValidator;
  }

  /**
   * Validate date type field values.
   *
   * @param aTarget the command object
   * @param aErrors the errors
   * @param aCriteriaClassType the search criteria class type
   * @param aDisplayName the display name of the filter field
   * @param aFormatMatcher the field format matcher as a regular expression
   * @param aFieldName the field name to validate
   * @param aErrorCode the spring error code
   * @param aErrorCodeArguments the spring error code arguments
   */
  @SuppressWarnings("unchecked")
  protected void validateFilterDateFormat(Object aTarget, Errors aErrors, Class<?> aCriteriaClassType,
                                          String aDisplayName, String aFieldName, String aErrorCode,
                                          Object[] aErrorCodeArguments, String aFormatMatcher)
  {
    SearchFilterCommand command = (SearchFilterCommand) aTarget;
    for (SearchCriteriaDTO<?> criteria : command.getSearchCriteriaDTOs())
    {
      if (criteria.getClassType().equals(aCriteriaClassType))
      {
        SearchCriteriaDTO<String> typedFilterCriteria = (SearchCriteriaDTO<String>) criteria;
        if (typedFilterCriteria.getSearchValue() != null)
        {
          if (criteria.getDisplayName().equals(aDisplayName))
          {
            String searchValue = typedFilterCriteria.getSearchValue().trim();
            boolean alreadyFailed = false;

            if (searchValue != null && !searchValue.trim().equals(""))
            {
              if (!searchValue.matches(aFormatMatcher))
              {
                aErrors.rejectValue(aFieldName, aErrorCode, aErrorCodeArguments, "");
                alreadyFailed = true;
              }
              if (!alreadyFailed)
              {
                isThisARealDate(aErrors, aFieldName, aErrorCode, aErrorCodeArguments, searchValue);
              }
            }
          }
        }
      }
    }
  }

  private void isThisARealDate(Errors aErrors, String aFieldName, String aErrorCode, Object[] aErrorCodeArguments,
      String searchValue) {
    String[] data = Arrays.asList(aErrorCodeArguments).toArray(new String[aErrorCodeArguments.length]);
    try
    {
      DateTimeFormatter fmt = DateTimeFormat.forPattern(data[0]);
      fmt.parseDateTime(searchValue);
    }
    catch (Exception e)
    {
      data[0] = data[0].toLowerCase();
      aErrors.rejectValue(aFieldName, aErrorCode, data, ""); 
    }
  }

}

