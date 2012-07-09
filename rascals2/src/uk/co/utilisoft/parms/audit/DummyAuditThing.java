package uk.co.utilisoft.parms.audit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;

import static uk.co.utilisoft.parms.domain.Audit.TYPE;

@Service("parms.DummyAuditThing")
public class DummyAuditThing implements DummyAudit
{

  private Logger mlogger = Logger.getLogger(getClass());
  
  @Override
  @ParmsAudit(auditType = TYPE.ADMIN_PARAM_CHANGE)
  public void aspectAuditMethod()
  {
    mlogger.info("In method to be audited");
  }
  
  @Override
  @ParmsAudit(auditType = TYPE.DPI_DOWNLOAD_FILE)
  public void aspectAuditMethodTwoParams(String param1, Long param2)
  {
    mlogger.info("In method to be audited param1[" + param1 + "], p2[" + param2 + "]");
  }
}
