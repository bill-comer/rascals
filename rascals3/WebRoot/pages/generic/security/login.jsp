<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<script type="text/javascript">
  function login()
  {
    document.forms["loginform"].submit();
  }
</script>

<div>
  <table>
    <thead>
      <tr>
        <c:set var="numberOfColumns" value="3"/>
        <c:forEach begin="1" end="${numberOfColumns}" varStatus="loopStatus">
          <input type="hidden" id="columnWidth_${loopStatus.index - 1}" value="160" />
        </c:forEach>
        <c:forEach begin="1" end="${numberOfColumns}" varStatus="loopStatus">
          <th id="columnTitle_${loopStatus.index - 1}"></th>
        </c:forEach>
      </tr>
    </thead>
  </table>
</div>

<div id="standAloneSearch">
  <div id="loginBox">
    <form name="loginform" id="loginform" method="post" action="j_spring_security_check">
      <div id="errortext" class="errorText">
        <c:if test="${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
          <c:set var="message" value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}" />
          <c:choose>
            <c:when test="${message == 'Bad credentials'}">
              <spring:message code="errors.login.invalid" />
            </c:when>
            <c:otherwise>
              <c:out value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}" />
            </c:otherwise>
          </c:choose>
        </c:if>
        <c:if test="${empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
          &nbsp;
        </c:if>
      </div>
      <div id="searchControls"></div>
      <div class="box1TopLeft">
        <div class="box1TopRight"></div>
      </div>
      <div class="box2Body"></div>
      <div id="fragment-1"></div>

      <table id="myTable">
        <tbody>
        <tr>
        <td align="right" style="padding-left: 35px; padding-right: 10px; min-height: 35px;font-size: 12px;">
          <spring:message code="field.username" />
        </td>
        <td align="left">
        <input id="username" type="text" name="j_username" style="width: 200px;"/>
        &nbsp;*
        </td>
        </tr>
        <br/><br/>
        <tr>
        <td align="right" style="padding-left: 35px; padding-right: 10px; min-height: 35px;font-size: 12px;">
          <spring:message code="field.password" />
        </td>
        <td align="left">
        <input id="password" type="password" name="j_password" style="width: 200px;"/>
        &nbsp;*
        </td>
        </tr>
        <tr>
        <td colspan="2" style="padding-top: 15px; padding-bottom: 15px; padding-left: 160px; min-height: 50px;" align="left">
        <a class="button_input" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" onclick="this.blur();login();" style="text-align: center">
          <span><spring:message code="button.login"/></span>
        </a>
        </td>
        </tr>
      </tbody>
      </table>
    </form>
  </div>
</div>
<div class="box2BtmLeft">
  <div class="box2BtmRight"></div>
</div>