package uk.co.utilisoft.parms.web.service.p0028;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.P0028FileDataDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Audit.TYPE;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.p0028.Sp04Validator;
import uk.co.utilisoft.parms.file.p0028.UploadStatus;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;
import uk.co.utilisoft.parms.web.dto.P0028FileUploadWarningDto;
import uk.co.utilisoft.parms.web.service.AdminService;

/**
 *
 */
@Service("parms.P0028UploadService")
public class P0028UploadService implements AdminService
{
  @Autowired(required = true)
  @Qualifier("parms.sp04Validator")
  private Sp04Validator mSp04Validator;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDataDao")
  private P0028FileDataDao mP0028FileDataDao;

  /**
   * @param aP0028UploadStatusWarningsInfo the p0028 file upload status and warnings information
   * @return true if warnings are saved, otherwise, false
   */

  /**
   * @param aP28UploadStatusWarningsInfo the P0028 File upload status for a Data Collector and the
   *        {@link Sp04FromAFMSMpan} or P0028Active warning information
   * @param aP0028FileName the P0028 File Name
   * @return true if P0028 File upload warnings were generated, otherwise, false
   */
  @ParmsAudit(auditType = TYPE.P0028_UPLOAD_GENERATE_WARNINGS)
  public boolean saveP0028FileUploadWarnings(Map<UploadStatus, List<IterableMap<String, ?>>>
      aP28UploadStatusWarningsInfo, String aP0028FileName)
  {
    List<P0028FileUploadWarningDto> warningsInfoDtos = new ArrayList<P0028FileUploadWarningDto>();

    if (aP28UploadStatusWarningsInfo != null
        && !aP28UploadStatusWarningsInfo.isEmpty())
    {
      if (!aP28UploadStatusWarningsInfo.keySet().isEmpty())
      {
        UploadStatus status = aP28UploadStatusWarningsInfo.keySet().iterator().next();
        List<IterableMap<String, ?>> warningsInfos = aP28UploadStatusWarningsInfo.get(status);

        if (warningsInfos != null && !warningsInfos.isEmpty())
        {
          if (warningsInfos != null && !warningsInfos.isEmpty())
          {
            for (IterableMap<String, ?> warningsForP28ActiveOrSp04FromAfms : warningsInfos)
            {
              // 1st from list should be p0028actives map warnings, 2nd is sp04fromafmsmpan map warnings
              if (!warningsForP28ActiveOrSp04FromAfms.isEmpty())
              {
                MapIterator<String, ?> warningsIter = warningsForP28ActiveOrSp04FromAfms.mapIterator();
                while (warningsIter.hasNext())
                {
                  String warningsMpan = warningsIter.next();
                  Object warningsMpanValue = warningsIter.getValue();

                  if (warningsMpanValue instanceof P0028Active)
                  {
                    warningsInfoDtos.add(new P0028FileUploadWarningDto<P0028Active>(warningsMpan,
                      P0028FileUploadWarningDto.WARNING.P0028_FILE_UPLOAD_MPAN, (P0028Active) warningsMpanValue));
                  }
                  else if (warningsMpanValue instanceof Sp04FromAFMSMpan)
                  {
                    warningsInfoDtos.add(new P0028FileUploadWarningDto<Sp04FromAFMSMpan>(warningsMpan,
                      P0028FileUploadWarningDto.WARNING.AFMS_MPAN, (Sp04FromAFMSMpan) warningsMpanValue));
                  }
                }
              }
            }
          }
        }
      }
    }

    if (!warningsInfoDtos.isEmpty())
    {
      // write warning message
      String warningsMsg = parseWarningData(warningsInfoDtos);
      if (warningsMsg != null)
      {
        return updateWarning(aP0028FileName, warningsMsg);
      }
    }

    return false;
  }

  /**
   * @param aP28FileName the P0028File fule name
   * @param aWarningInfoData the warning information as text
   * @return true if the P0028FileData.Warnings record has been updated
   */
  @Transactional(isolation = Isolation.SERIALIZABLE,
                 propagation = Propagation.SUPPORTS,
                 rollbackFor = Exception.class)
  boolean updateWarning(String aP28FileName, String aWarningInfoData)
  {
    if (StringUtils.isNotBlank(aP28FileName))
    {
      P0028FileData p28FileData = mP0028FileDataDao.getLatestByP0028FileName(aP28FileName);

      if (p28FileData != null
          && StringUtils.isNotBlank(aWarningInfoData))
      {
        p28FileData.setWarnings(aWarningInfoData);
        mP0028FileDataDao.makePersistent(p28FileData);
        return true;
      }
    }

    return false;
  }

  /**
   * @param aWarningsDtos the warning information dtos
   * @return the warning information as text
   */
  String parseWarningData(List<P0028FileUploadWarningDto> aWarningsDtos)
  {
    StringBuffer warningsMsg = new StringBuffer();

    if (aWarningsDtos == null || aWarningsDtos.isEmpty())
    {
      return null;
    }

    warningsMsg.append("MPAN, P0028 Upload Warning Description" + "\r\n");

    for (P0028FileUploadWarningDto<?> warningDto : aWarningsDtos)
    {
      warningsMsg.append(warningDto.getMpan() + ", " + warningDto.getWarning().getDescription() + "\r\n");
    }

    return warningsMsg.toString();
  }

  /**
   * Verify P0028Active and Sp04FromAFMSMpan mpans and
   *
   * @param aSupplierPk the supplier primary key
   * @param aP28ActivesMap the P0028Active records
   * @param aP28FileUploadStatus the p0028 file upload status
   * @return the P0028 File upload status for a Data Collector
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Map<UploadStatus, List<IterableMap<String, ?>>> getP28FileUploadWarnings(Long aSupplierPk,
      Map<String, IterableMap<String, P0028Active>> aP28ActivesMap, UploadStatus aP28FileUploadStatus)
  {
    Map<UploadStatus, List<IterableMap<String, ?>>> statusWarningsInfo
      = new HashMap<UploadStatus, List<IterableMap<String, ?>>>();

    // only generate warnings if the P0028 File is successfully uploaded with/without failures
    if (!aP28FileUploadStatus.equals(UploadStatus.FAILED_STRUCTURAL_VALIDATION))
    {
      if (!aP28ActivesMap.isEmpty())
      {
        String dcName = aP28ActivesMap.keySet().iterator().next();

        List<Object> warningsInfos = mSp04Validator.validate(aSupplierPk, dcName, aP28ActivesMap.get(dcName));

        if (!warningsInfos.isEmpty())
        {
          List<IterableMap<String, ?>> p28ActivesOrSp04FromAFMSMpansMaps = new ArrayList<IterableMap<String, ?>>();

          for (Object warningInfo : warningsInfos)
          {
            if (warningInfo instanceof IterableMap)
            {
              p28ActivesOrSp04FromAFMSMpansMaps.add((IterableMap) warningInfo);
            }
          }

          statusWarningsInfo.put(aP28FileUploadStatus, p28ActivesOrSp04FromAFMSMpansMaps);
        }
      }
    }

    // defaults to returning just the UploadStatus with no warning informations
    if (!statusWarningsInfo.containsKey(aP28FileUploadStatus))
    {
      statusWarningsInfo.put(aP28FileUploadStatus, null);
    }

    return statusWarningsInfo;
  }

  /**
   * get list of suppliers for Upload page
   * @return the dtos
   */
  @Override
  public List<AdminListDTO> getAllSortedRecords()
  {
    List<AdminListDTO> listItems = new ArrayList<AdminListDTO>();

    List<Supplier> suppliers = mSupplierDao.getAll();
    for (Supplier supplier : suppliers)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> currentObjectList = new ArrayList<Object>();

      currentObjectList.add(supplier.getSupplierId());

      list.add(supplier.getPk());
      listItems.add(new AdminListDTO(list, currentObjectList));
    }

    return listItems;
  }

  /**
   * @param aSupplierId the supplier id
   * @return the supplier
   */
  public Supplier getSupplier(Long aSupplierId)
  {
    return mSupplierDao.getById(aSupplierId);
  }

  /**
   * @param aP0028FileDataDao the P0028FileDataDao
   */
  public void setP0028FileDataDao(P0028FileDataDao aP0028FileDataDao)
  {
    mP0028FileDataDao = aP0028FileDataDao;
  }

  /**
   * @param aSp04Validator the sp04 validator
   */
  public void setSp04Validator(Sp04Validator aSp04Validator)
  {
    mSp04Validator = aSp04Validator;
  }
}