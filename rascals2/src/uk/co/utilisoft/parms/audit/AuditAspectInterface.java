package uk.co.utilisoft.parms.audit;

import org.aspectj.lang.JoinPoint;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AuditAspectInterface
{
  /**
   * Audit errored parms activity.
   * 
   * @param aPoint the aspect join point
   * @param parmsAudit the audit data
   * @param ex the exception thrown
   */
  public void logTheErrorAuditActivity(JoinPoint aPoint, ParmsAudit parmsAudit, Exception ex);
  
  /**
   * Audit parms activity.
   * 
   * @param aPoint the aspect join point
   * @param parmsAudit the audit data
   */
  void logTheAuditActivity(JoinPoint aPoint, ParmsAudit parmsAudit);
}
