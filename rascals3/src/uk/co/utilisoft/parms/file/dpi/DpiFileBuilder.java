package uk.co.utilisoft.parms.file.dpi;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.parms.dao.DpiFileDao;
import uk.co.utilisoft.parms.dao.DpiFileDataDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.DpiFileData;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.FileBuilder;
import uk.co.utilisoft.parms.file.FileDataWriter;


@Service("parms.dpiFileBuilder")
public class DpiFileBuilder extends FileBuilder
{

  @Autowired(required = true)
  @Qualifier("dpiRowBuilder")
  private DpiRowBuilder mDpiRowBuilder;

  @Autowired(required = true)
  @Qualifier("parms.dpiFileDataWriter")
  private FileDataWriter mDpiFileDataWriter;

  @Autowired(required = true)
  @Qualifier("parms.dpiFileDataDao")
  private DpiFileDataDao mDpiFileDataDao;

  @Autowired(required = true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDao mDpiFileDao;


  @Transactional
  public String[] buildFileData(DpiFile aDpiFile, Supplier aSupplier)
  {
    mDpiRowBuilder.setSupplier(aSupplier);
    // create data
    List<String> fileData =  mDpiRowBuilder.buildAllRows(aDpiFile);

    // check existing dpi file has file name
    boolean fileNameAdded = populateDpiFileName(aDpiFile);

    // write data to file
    String[] saveLog = mDpiFileDataWriter.save(aDpiFile.getFileName(), formatData(fileData));

    // persist data
    DpiFileData fileDataObj = new DpiFileData(formatData(fileData), aDpiFile);

    // check if dpi file has changed
    DpiFile dfref = null;
    if (fileNameAdded)
    {
      dfref = mDpiFileDao.getById(aDpiFile.getPk());
      dfref.setFileName(aDpiFile.getFileName());
      fileDataObj.setDpiFile(dfref);
    }

    mDpiFileDataDao.makePersistent(fileDataObj);

    System.out.println("finished buildFileData " + new DateTime());
    return saveLog;
  }

  private boolean populateDpiFileName(DpiFile aDpiFile)
  {
    if (StringUtils.isBlank(aDpiFile.getFileName()))
    {
      aDpiFile.setFileName(DpiFile.createFileName(aDpiFile.getSupplier().getSupplierId(),
                                                  aDpiFile.getReportingPeriod()));
      return true;
    }
    return false;
  }

  public void setDpiFileDataDao(DpiFileDataDao aDpiFileDataDao)
  {
    mDpiFileDataDao = aDpiFileDataDao;
  }

  public void setDpiFileDataWriter(FileDataWriter aDpiFileDataWriter)
  {
    mDpiFileDataWriter = aDpiFileDataWriter;
  }
}
