<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>
 
 
        <table align="center">
          <tr>
            <td>
              <a class="button_input" onclick="this.blur(); redirectPlainUrl('createSwimmer.htm');" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" style="text-align: center">
                <span><spring:message code="tab.create.new.swimmer"/></span>
              </a>
            </td>
            
<c:if test="${COUNT > 0}">
            <td>
              <a class="button_input" onclick="this.blur(); redirectUrl('editSwimmer.htm', 'id_0', 'thisId_');" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" style="text-align: center">
                <span><spring:message code="tab.edit.swimmer"/></span>
              </a>
            </td>
</c:if>
            
            
          </tr>
        </table>