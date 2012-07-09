package uk.co.utilisoft.parms.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.web.command.PagingCommand;

/**
 * @author Philip Lau
 * @version 1.0
 *
 * @param <PagingCommandType> a subclass of PagingCommand
 */
public interface PagingControllerInterface<PagingCommandType extends PagingCommand>
{
  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView doPaging(HttpServletRequest aRequest, PagingCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView changeRecordsPerPage(HttpServletRequest aRequest, PagingCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView sort(HttpServletRequest aRequest, PagingCommandType aCommand, Errors aErrors);
}
