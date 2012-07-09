package uk.co.utilisoft.parmsmop.web.mopGeneration;

import static uk.co.utilisoft.parms.web.controller.WebConstants.ACTION_GENERATE_MOP_REPORT;
import static uk.co.utilisoft.parms.web.controller.WebConstants.PARMS_MOP_GEN_COMMAND;
import static uk.co.utilisoft.parms.web.controller.WebConstants.PARMS_MOP_GEN_SCREEN_TITLE;
import static uk.co.utilisoft.parms.web.controller.WebConstants.REPORT_MOP_GEN_REQUESTED_ACTION;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.util.DateUtil;
import uk.co.utilisoft.parmsmop.report.MopReportServiceImpl;
/**
 * Administration for Generating Reports.
 *
 * @author Philip Lau
 * @version 1.0
 */
@Controller("project.generateMopController")
@SessionAttributes(PARMS_MOP_GEN_COMMAND)
public class GenerateMopReportController
{

  @Autowired(required = true)
  @Qualifier("parmsmop.mopReportService")
  private MopReportServiceImpl mMopReportServiceImpl;

  @Autowired(required = true)
  @Qualifier("parms.MopGenerationValidator")
  private MopGenerationValidator mValidator;

  /**
   * @param aBinder the data binder
   * @throws Exception
   */
  @InitBinder
  public void initBinder(ServletRequestDataBinder aBinder) throws Exception
  {
    CustomNumberEditor numEditor = new CustomNumberEditor(Integer.class, true);
    aBinder.registerCustomEditor(Integer.class, numEditor);
  }

  /**
   * @param aRequest the request
   * @return the model and view
   */
  @RequestMapping(method = RequestMethod.GET)
  protected ModelAndView formBackingObject(HttpServletRequest aRequest)
  {
    MopGenerationCommand command = new MopGenerationCommand();
    command.setReportingPeriod(DateTimeFormat.forPattern("MMM yyyy").print(new DateTime().minusMonths(1)));
    return nextPage(command, null, null);
  }

  private ModelAndView nextPage(MopGenerationCommand aCommand, BindingResult aErrors, String aViewName)
  {
    ModelAndView mav = new ModelAndView();

    if (aErrors != null && aErrors.hasErrors())
    {
      mav.addAllObjects(aErrors.getModel());
    }

    // defaults to itself
    if (aViewName != null && !aViewName.equals(""))
    {
      mav.setViewName(aViewName);
    }
    else
    {
      mav.setViewName(getViewName());
    }

    mav.addObject(PARMS_MOP_GEN_COMMAND, aCommand);
    mav.addObject("title", PARMS_MOP_GEN_SCREEN_TITLE);
    return mav;
  }

  /**
   * @param aCommand the command
   * @param aResult the binding result
   * @param aRequest the request
   * @return the model and view
   * @throws Exception
   */
  @RequestMapping(method = RequestMethod.POST, params = REPORT_MOP_GEN_REQUESTED_ACTION + "=" + ACTION_GENERATE_MOP_REPORT)
  public ModelAndView generateReport(@ModelAttribute(PARMS_MOP_GEN_COMMAND) MopGenerationCommand aCommand,
                                     BindingResult aResult, HttpServletRequest aRequest) throws Exception
  {
    ModelAndView mav = null;
    mav = nextPage(aCommand, null, "redirect:/listMopReports.htm");

    getValidator().validate(aCommand, aResult);
    
    if (!aResult.hasErrors())
    {
      if (aCommand.getMopGenerationRequestedAction().equals(ACTION_GENERATE_MOP_REPORT))
      {
        DateTime parsedReportingPeriod = DateUtil.parseTextAsJodaDateTime(aCommand.getReportingPeriod(), DateTimeFormat
                    .forPattern("MMM yyyy"), DateTimeFormat.forPattern("dd/MM/yyyy"));
        
        String type = "SP11";
        
        try
        {
          mMopReportServiceImpl.createNewReport(type);
        } catch (Exception e)
        {
          e.printStackTrace();
          String errMsg = e.getMessage();
          return nextPage(new MopGenerationCommand(), aResult, null);
        }
      }
      
    }
    return mav;
    

/*    if (!aResult.hasErrors())
    {
      if (aCommand.getMopGenerationRequestedAction().equals(ACTION_GENERATE_MOP_REPORT))
      {
        DateTime parsedReportingPeriod = DateUtil.parseTextAsJodaDateTime(aCommand.getReportingPeriod(), DateTimeFormat
          .forPattern("MMM yyyy"), DateTimeFormat.forPattern("dd/MM/yyyy"));
        DateTime endReportingMonth = null;
        if (parsedReportingPeriod != null)
        {
          endReportingMonth = new ParmsReportingPeriod(parsedReportingPeriod.toDateMidnight())
            .getStartOfFirstMonthInPeriod().toDateTime();
        }

        DateTime startReportingPeriod = null;
        startReportingPeriod = endReportingMonth != null ? endReportingMonth.minusMonths(1) : null;

        if (startReportingPeriod != null)
        {
          String errMsg = mDpiGenerationService.generateParmsReport(startReportingPeriod);

          if (StringUtils.isNotBlank(errMsg))
          {
            BindException errors = getErrors(aCommand, FAILED_TO_WRITE_FILE, errMsg);
            aResult.addAllErrors(errors);
            mav = nextPage(aCommand, aResult, null);
          }
          else
          {
            mav = nextPage(aCommand, null, "redirect:/listMopReports.htm");
          }
        }
        else
        {
          BindException errors = getErrors(aCommand, FAILED_TO_WRITE_FILE, "A Reporting Period is required");
          mav = nextPage(aCommand, errors, null);
        }
      }
      else
      {
        BindException errors = getErrors(aCommand, FAILED_TO_WRITE_FILE, "Unexpected action "
                                         + aCommand.getMopGenerationRequestedAction());
        mav = nextPage(aCommand, errors, null);
      }
      return mav;
    }*/

  }

  private BindException getErrors(MopGenerationCommand aCommand, String aErrorCode, String aErrorMessageArg)
  {
    BindException errors = null;

    if (StringUtils.isNotBlank(aErrorMessageArg))
    {
      errors = new BindException(aCommand, PARMS_MOP_GEN_COMMAND);
      errors.reject(aErrorCode, new Object[] {aErrorMessageArg}, null);
    }

    return errors;
  }

  /**
   * @return the view name
   */
  public String getViewName()
  {
    return "parms.mopGeneration";
  }

  /**
   * @return a validator
   */
  protected MopGenerationValidator getValidator()
  {
    return mValidator;
  }

}
