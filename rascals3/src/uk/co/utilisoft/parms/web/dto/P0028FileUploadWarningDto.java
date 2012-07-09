package uk.co.utilisoft.parms.web.dto;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028FileUploadWarningDto<T>
{
  private String mMpan;
  private P0028FileUploadWarningDto.WARNING mWarning;
  private T mAdditionalData;

  /**
   * @param aMpan the mpan
   * @param aWarning the warning
   * @param aAdditionalData any additional data
   */
  public P0028FileUploadWarningDto(String aMpan,
                                   P0028FileUploadWarningDto.WARNING aWarning,
                                   T aAdditionalData)
  {
    mMpan = aMpan;
    mWarning = aWarning;
    mAdditionalData = aAdditionalData;
  }

  /**
   * @return the mpan
   */
  public String getMpan()
  {
    return mMpan;
  }

  /**
   * @param aMpan the mpan
   */
  public void setMpan(String aMpan)
  {
    mMpan = aMpan;
  }

  /**
   * @return the warning
   */
  public P0028FileUploadWarningDto.WARNING getWarning()
  {
    return mWarning;
  }

  /**
   * @param aWarning the warning
   */
  public void setWarning(P0028FileUploadWarningDto.WARNING aWarning)
  {
    mWarning = aWarning;
  }

  /**
   * @return the additional data
   */
  public T getAdditionalData()
  {
    return mAdditionalData;
  }

  /**
   * @param aAdditionalData the additional data
   */
  public void setAdditionalData(T aAdditionalData)
  {
    mAdditionalData = aAdditionalData;
  }

  public static enum WARNING
  {
    AFMS_MPAN("MPAN found in AFMS Database"),
    P0028_FILE_UPLOAD_MPAN("MPAN found in P0028 File Upload");

    private String mDescription;

    private WARNING(String aDescription)
    {
      mDescription = aDescription;
    }

    /**
     * @return the description of the Audit
     */
    public String getDescription()
    {
      return mDescription;
    }

    /**
     * @param aDescription the description of the Audit
     */
    public void setDescription(String aDescription)
    {
      mDescription = aDescription;
    }
  }
}