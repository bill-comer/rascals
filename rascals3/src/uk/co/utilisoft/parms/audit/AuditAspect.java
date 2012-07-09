package uk.co.utilisoft.parms.audit;


import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.context.SecurityContextHolder;

import uk.co.utilisoft.afms.dao.UserDao;
import uk.co.utilisoft.afms.domain.User;
import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.AuditDao;
import uk.co.utilisoft.parms.domain.Audit;

@Aspect
@Configurable(autowire = Autowire.BY_NAME, dependencyCheck = true)
public class AuditAspect implements AuditAspectInterface
{
  private Logger mlogger = Logger.getLogger(getClass());
  
  @Autowired(required = true)
  @Qualifier("parms.userDao")
  private UserDao mUserDao;
  
  @Autowired(required = true)
  @Qualifier("parms.auditDao")
  private AuditDao mAuditDao;
  
  @Override
  @AfterThrowing(value="execution(@uk.co.utilisoft.parms.audit.annotation.ParmsAudit * *(..)) && @annotation(parmsAudit)", argNames="parmsAudit, ex", throwing = "ex")
  public void logTheErrorAuditActivity(JoinPoint aPoint, ParmsAudit parmsAudit, Exception ex)
  {
    String userName = getUserName();
    mlogger.info("Parms Auditing User Name: " + userName);
    mlogger.info("auditType:" + parmsAudit.auditType().getDescription());
    doTheAudit(parmsAudit.auditType(), ex.getMessage(), userName);
  }
  
  @Override
  @AfterReturning(value="execution(@uk.co.utilisoft.parms.audit.annotation.ParmsAudit * *(..)) && @annotation(parmsAudit)", argNames="parmsAudit")
  public void logTheAuditActivity(JoinPoint aPoint, ParmsAudit parmsAudit)
  {
    String userName = getUserName();
    mlogger.info("Parms Auditing User Name: " + userName);
    mlogger.info("auditType:" + parmsAudit.auditType().getDescription());
    
    String arguments = getArgs(aPoint.getArgs());
    if (arguments.length() > 0)
    {
      mlogger.info("args-" + arguments);
    }
    
    doTheAudit(parmsAudit.auditType(), arguments, userName);
  }
  
  /**
   * Gets the args as one String
   * @param args
   * @return
   */
  private String getArgs(Object[] args)
  {
    String arguments = "";
    int argCount = 0;
    for (Object object : args)
    {
      if (argCount > 0)
      {
        arguments += ", ";
      }
      arguments += "arg[" + ++argCount + "]=" + "[" + object + "]";
    }
    
    return arguments;
  }

  private String getUserName()
  {
    try
    {
      return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    catch (NullPointerException npe)
    {
      return "Unknown User";
    }
  }
  
  private void doTheAudit(Audit.TYPE aAuditType, String aAuditDetails, String aUserName)

  {
    Audit audit = new Audit(aAuditType);
    audit.setDetails(aAuditDetails);
    User user = null;
    try
    {
      user = mUserDao.getUser(aUserName);
    }
    catch (RuntimeException rte)
    {
      mlogger.error("Unknown User - Using Pk[-99] " + aAuditType.getDescription() + " for details " + aAuditDetails + " Exception: "
                    + rte.getMessage());
      user = new User();
      user.setUserName("Unknown User");
      user.setPk(-99L);
    }

    try
    {
      audit.setUserFk(user.getPk());
      mAuditDao.makePersistent(audit);
    }
    catch (ConstraintViolationException cve)
    {
      mlogger.error("Failed to Audit " + aAuditType.getDescription() + " for user: " + user.getUserName() 
                    + ", details " + aAuditDetails + " Exception: " + cve.getMessage());
      throw cve;
    }
    catch (RuntimeException rte)
    {
      mlogger.error("Failed to Audit " + aAuditType.getDescription() + " for details " + aAuditDetails + " Exception: "
                    + rte.getMessage());
      throw rte;
    }
  }
}
