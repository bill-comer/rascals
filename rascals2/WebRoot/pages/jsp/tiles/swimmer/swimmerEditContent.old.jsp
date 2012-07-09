<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<form id="adminForm" name="adminForm" method="post" commandName="swimmersCommand">

  <form:hidden path="swimmersCommand.requestedAction" id="swimmersCommand.requestedAction"/>

  <table class="adminEdit" align="center" style="margin-top:10px;">
  
    <tag:response.messages />

    <tag:detailField type="text" title="Gala Name" className="largeTextField" validationType="string" 
        allowNull="false" maxLength="200" excludedValues="new Array()" 
        messageCode="field.admin.swimmer.firstname" path="swimmersCommand.swimmer.firstname"/>

    <tag:detailField type="text" title="Gala Name" className="largeTextField" validationType="string" 
        allowNull="false" maxLength="200" excludedValues="new Array()" 
        messageCode="field.admin.swimmer.surname" path="swimmersCommand.swimmer.surname"/>
        


  </table>

  <table align="center" style="margin-top:10px;">
		<tr>
			<td>
			  <a id="saveButton" onclick="openSaveChangesDialog();" class="button_input" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.save"/></span></a>
			</td>
			<td>
			  <a id="cancelButton" class="button_input" onclick="this.blur(); cancel();" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.cancel"/></span></a>
			</td>
			
		</tr>
	</table>

  <jsp:include page="/pages/generic/tiles/hoverConfirmation.jsp" />

</form>

