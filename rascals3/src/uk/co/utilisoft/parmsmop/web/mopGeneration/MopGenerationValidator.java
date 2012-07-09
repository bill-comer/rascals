package uk.co.utilisoft.parmsmop.web.mopGeneration;

import static uk.co.utilisoft.parms.web.controller.WebConstants.INVALID_REPORTING_PERIOD;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import uk.co.utilisoft.parms.web.validator.FormValidator;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Component("parms.MopGenerationValidator")
public class MopGenerationValidator extends FormValidator
{
  /**
   * {@inheritDoc}
   * @see org.springframework.validation.Validator#supports(java.lang.Class)
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean supports(Class aClazz)
  {
    return MopGenerationCommand.class.isAssignableFrom(aClazz);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.validator.SearchFilterValidator#validate(java.lang.Object,
   * org.springframework.validation.Errors)
   */
  @Override
  public void validate(Object aTarget, Errors aErrors)
  {
    MopGenerationCommand command = (MopGenerationCommand) aTarget;
    // Comment: date formats know by application and acceptable to Joda DateFormatter 'Jul 2022', 'JUL 2022', 'jul 2022'. Note: 'jUn 2022' cannot be parsed by Joda DateFormatter
    String mmmyyyyMatcher = "^((jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec) (\\d\\d\\d\\d))|((JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC) (\\d\\d\\d\\d))|((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) (\\d\\d\\d\\d))$";
    validateDateFormat(aTarget, aErrors, "reportingPeriod", command.getReportingPeriod(),
                       INVALID_REPORTING_PERIOD, new Object[] {"MMM YYYY"}, mmmyyyyMatcher);
  }
}
