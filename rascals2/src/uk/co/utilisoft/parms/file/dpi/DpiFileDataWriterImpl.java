package uk.co.utilisoft.parms.file.dpi;

import static uk.co.utilisoft.parms.domain.ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION;

import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.file.FileDataWriter;
import uk.co.utilisoft.parms.file.ReportFileWriter;


/**
 * @author Philip Lau
 * @version 1.0
 */
@Service(value="parms.dpiFileDataWriter")
public class DpiFileDataWriterImpl extends ReportFileWriter  implements FileDataWriter
{
  @Override
  public ConfigurationParameter.NAME getConfigParamName()
  {
    return PARMS_DPI_FILE_LOCATION;
  }
}
