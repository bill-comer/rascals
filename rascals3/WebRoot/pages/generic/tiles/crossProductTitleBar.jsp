<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean
  id="crossProductHelper"
  class="uk.co.formfill.springutils.web.util.CrossProductPropertyHelper" />

<div id="titlebar" style="width:100%;height:35px;">
   <div class="icon" style="width:100%;height:35px;">
      <table width="100%" height="35px;">
         <tr>
            <td width="1%"><img style="cursor: default;" src="pages/generic/images/AFMS_button.jpg" width="25" height="25"/></td>
            <td align="left"><span class="subtitletext">&nbsp; <spring:message code="product.title"/> (${sessionScope.version}) </span></td>

            <c:choose>
              <c:when test="${crossProductHelper.noOfProducts > 0}">
                <td>
                  <jsp:include page="crossProductNav.jsp"></jsp:include>
                </td>
              </c:when>
              <c:otherwise>
                <td width="2%" align="right"><img alt="www.utilisoft.co.uk" src="pages/generic/generic/images/utilisoft_logo_icon.png" style="cursor:pointer;"
                    onclick="window.location='http://www.utilisoft.co.uk'" width="25" height="25"/></td>
              </c:otherwise>
            </c:choose>

         </tr>
      </table>
   </div>
</div>