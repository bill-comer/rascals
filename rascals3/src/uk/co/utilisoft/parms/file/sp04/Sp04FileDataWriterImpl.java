package uk.co.utilisoft.parms.file.sp04;

import static uk.co.utilisoft.parms.domain.ConfigurationParameter.NAME.PARMS_SP04_FILE_LOCATION;

import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.file.FileDataWriter;
import uk.co.utilisoft.parms.file.ReportFileWriter;


@Service(value="parms.sp04FileDataWriter")
public class Sp04FileDataWriterImpl  extends ReportFileWriter  implements FileDataWriter
{

  @Override
  public ConfigurationParameter.NAME getConfigParamName()
  {
    return PARMS_SP04_FILE_LOCATION;
  }
}
