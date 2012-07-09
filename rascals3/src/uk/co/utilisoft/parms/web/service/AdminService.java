package uk.co.utilisoft.parms.web.service;

import java.util.List;

import uk.co.utilisoft.parms.web.controller.AdminListDTO;

/**
 * @author Gareth Morris
 * @version 1.0
 */
public interface AdminService
{
  List<AdminListDTO> getAllSortedRecords();
}
