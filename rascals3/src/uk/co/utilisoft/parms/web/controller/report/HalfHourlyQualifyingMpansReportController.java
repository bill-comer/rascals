package uk.co.utilisoft.parms.web.controller.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.web.command.report.HalfHourlyQualifyingMpansCommand;
import uk.co.utilisoft.parms.web.controller.AbstractParmsController;
import uk.co.utilisoft.parms.web.service.report.ReportService;

import static uk.co.utilisoft.parms.web.controller.WebConstants.HALF_HOURLY_QUALIFYING_MPANS_COMMAND;
import static uk.co.utilisoft.parms.web.util.FieldErrorCodes.FAILED_TO_WRITE_FILE;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Controller("parms.HalfHourlyQualifyingMpansReportController")
@SessionAttributes(HALF_HOURLY_QUALIFYING_MPANS_COMMAND)
public class HalfHourlyQualifyingMpansReportController extends AbstractParmsController
{
  private static final String mCommandName = HALF_HOURLY_QUALIFYING_MPANS_COMMAND;

  @Autowired(required = true)
  @Qualifier("parms.reportService")
  private ReportService mReportService;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.ParmsController#getCommandName()
   */
  public String getCommandName()
  {
    return mCommandName;
  }

  @RequestMapping(method = RequestMethod.GET)
  public Object formBackingObject(HttpServletRequest aRequest) throws Exception
  {
    aRequest.setAttribute("title", getObjectName());
    ModelAndView mav = new ModelAndView(getViewName());
    HalfHourlyQualifyingMpansCommand command = getNewCommand();

    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < getNumberOfIdentifiers(); i++)
    {
      list.add(null);
    }
    command.setIdentifier(list);

    command.setSuppliers(mSupplierDao.getAll());

    mav.addObject(command);
    return mav;
  }

  @RequestMapping(params = "requestedAction" + "=getHalfHourlyQualifyingMpans", method = RequestMethod.POST)
  public ModelAndView getHalfHourlyQualifyingMpans(@ModelAttribute() HalfHourlyQualifyingMpansCommand aCommand,
                                                   BindingResult aResult, HttpServletRequest aRequest,
                                                   HttpServletResponse aResponse) throws Exception
  {
    Long supplierPk = Long.valueOf(aRequest.getParameter("supplierPk"));
    String fileName = downloadHalfHourlyQualifyingMpansReport(supplierPk, aResponse);
    ModelAndView mav = null;

    if (StringUtils.isBlank(fileName))
    {
      BindException errors = getError(aCommand, HALF_HOURLY_QUALIFYING_MPANS_COMMAND, FAILED_TO_WRITE_FILE,
                                      "Problem downloading Half Hourly Qualifying MPAN's report");
      aResult.addAllErrors(errors);
    }

    mav = nextPage(aCommand, aResult);
    mav.addObject("title", getObjectName());
    return mav;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.AbstractParmsController#getNewCommand()
   */
  @Override
  public HalfHourlyQualifyingMpansCommand getNewCommand()
  {
    return new HalfHourlyQualifyingMpansCommand();
  }

  /**
   * @param aSupplierPk the supplier primary key
   * @param aResponse the HttpServletResponse
   * @return the report file name
   * @throws Exception
   */
  public String downloadHalfHourlyQualifyingMpansReport(Long aSupplierPk, HttpServletResponse aResponse) throws Exception
  {
    return mReportService.downloadHalfHourlyQualifyingMpansReport(aSupplierPk, aResponse);
  }
}
