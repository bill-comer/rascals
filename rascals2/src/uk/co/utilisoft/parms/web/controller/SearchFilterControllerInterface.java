package uk.co.utilisoft.parms.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.web.command.SearchFilterCommand;

/**
 * @author Philip Lau
 * @version 1.0
 *
 * @param <SearchFilterCommandType> a subclass of SearchFilterCommand
 */
public interface SearchFilterControllerInterface<SearchFilterCommandType extends SearchFilterCommand>
{
  /**
   * @param aRequest the request
   * @param aRecordId the record identifier
   * @param aRecordIdModelName the record Id Model Name
   * @return the ModelAndView
   * @throws Exception
   */
  ModelAndView formBackingObject(HttpServletRequest aRequest, String aRecordId, String aRecordIdModelName)
      throws Exception;

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView sort(HttpServletRequest aRequest, SearchFilterCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @return the ModelAndView
   */
  ModelAndView clearFilter(HttpServletRequest aRequest, SearchFilterCommandType aCommand);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView updateCount(HttpServletRequest aRequest, SearchFilterCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView doPaging(HttpServletRequest aRequest, SearchFilterCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView changeRecordsPerPage(HttpServletRequest aRequest, SearchFilterCommandType aCommand, Errors aErrors);

  /**
   * @param aRequest the request
   * @param aCommand the command
   * @param aErrors the errors
   * @return the ModelAndView
   */
  ModelAndView applyFilter(HttpServletRequest aRequest, SearchFilterCommandType aCommand, Errors aErrors);
}
