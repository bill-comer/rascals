<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<%
  pageContext.setAttribute("fixedCurrentTimeMillis", new org.joda.time.DateTime().getMillis());
%>

<div>
  <input type="hidden" name="fixedCurrentTimeMillis" id="fixedCurrentTimeMillis" value="${fixedCurrentTimeMillis}"/>
</div>