<%@ tag body-content="scriptless" %>
<%@ attribute name="name" required="true" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:hasBindErrors name="${name}">
  <c:forEach items="${errors.allErrors}" var="error">
    <div class="errorText"><c:forEach items="${error.code}" var="code">
      <spring:message code="${code}" arguments="${error.arguments}" />
    </c:forEach></div>
  </c:forEach>
</spring:hasBindErrors>
