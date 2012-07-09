<%@ tag body-content="scriptless" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:forEach items="${RESPONSE_MESSAGE_DATA}" var="responseMessageData" varStatus="status">
  <div class="responseMessageText">
    <spring:message code="${responseMessageData.key}" arguments="${responseMessageData.value}" argumentSeparator=","/>
  </div>
</c:forEach>