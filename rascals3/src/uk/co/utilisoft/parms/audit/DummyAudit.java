package uk.co.utilisoft.parms.audit;


public interface DummyAudit
{

  void aspectAuditMethod();
  
  void aspectAuditMethodTwoParams(String param1, Long param2);
}
