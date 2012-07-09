package uk.co.utilisoft.parmsmop.file.sp11;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("parmsmop.sp11Builder")
public class Sp11Builder
{
  @Autowired(required = true)
  @Qualifier("parmsmop.sp11RowBuilder")
  private Sp11MopRowBuilder mSp11MopRowBuilder;

  @Autowired(required = true)
  @Qualifier("parmsmop.sp11ReportBuilder")
  private Sp11MopReportBuilder mSp11MopReportBuilder;
  
  
}
