package uk.co.utilisoft.parms.web.command.p0028;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import uk.co.utilisoft.parms.web.command.ParmsCommand;


public class P0028UploadCommand extends ParmsCommand
{
  private MultipartFile  p0028file;

  private String mP0028Received;

  private List<String> mUploadErrorData;

  public List<String> getUploadErrorData()
  {
    return mUploadErrorData;
  }

  public void setUploadErrorData(List<String> uploadErrorData)
  {
    this.mUploadErrorData = uploadErrorData;
  }

  public void setP0028file(MultipartFile  file) {
      this.p0028file = file;
  }

  public MultipartFile  getP0028file() {
      return p0028file;
  }

  public String getP0028Received()
  {
    return mP0028Received;
  }

  public void setP0028Received(String p0028Received)
  {
    this.mP0028Received = p0028Received;
  }



}
