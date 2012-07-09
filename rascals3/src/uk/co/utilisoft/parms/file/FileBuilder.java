package uk.co.utilisoft.parms.file;

import java.util.List;

import org.springframework.util.StringUtils;

public abstract class FileBuilder
{

  public String formatData(List<String> aData)
  {
    return StringUtils.collectionToDelimitedString(aData, System.getProperty("line.separator"));
  }
}
