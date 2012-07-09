<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<div id="standAloneSearch">
  <div id="loginBox">
    <form name="loginform" id="loginform" method="post" action="j_spring_security_check" >
      <div id="errortext" class="errors">
        <c:if test="${not empty sessionScope.ACEGI_SECURITY_LAST_EXCEPTION.message}">
          <c:set var="message" value="${sessionScope.ACEGI_SECURITY_LAST_EXCEPTION.message}" />
          <ul>
            <li style="padding:10px;font-size:12px;">
              <c:choose>
                <c:when test="${message == 'Bad credentials'}">
                  <spring:message code="errors.login.invalid" />
                </c:when>
                <c:otherwise>
                  <c:out value="${sessionScope.ACEGI_SECURITY_LAST_EXCEPTION.message}" />
                </c:otherwise>
              </c:choose>
            </li>
          </ul>
        </c:if>
        <c:if test="${empty sessionScope.ACEGI_SECURITY_LAST_EXCEPTION.message}">
        &nbsp;
        </c:if>
      </div>

      <div id="searchControls">
        <div class="box1TopLeft"><div class="box1TopRight"></div>
      </div>
      <div class="box2Body">
        <div id="fragment-1">
          <div id="loginControls" style="padding:20px 0px 20px 30px;">
          <p>Your session has timed out</p>
          <p><a href="roleSelect.htm">Start new session</a></p>
          <p><a href="javascript: self.close()">Close Avant Session</a></p>
          </div>
        </div>
      </div>
    </form>
  </div>
</div>

<div class="box2BtmLeft"><div class="box2BtmRight"></div></div>
</div>