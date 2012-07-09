<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<form id="adminForm" name="adminForm" method="post" commandName="adminCommand">

  <form:hidden path="adminCommand.adminRequestedAction" id="adminCommand.adminRequestedAction"/>

  <table class="adminEdit" align="center" style="margin-top:10px;">
    <tag:detailField type="text" title="Description" className="largeTextField" validationType="string" allowNull="false" maxLength="200" excludedValues="new Array()" messageCode="field.description" path="adminCommand.configurationParameter.description"/>
    <tag:detailField type="text" title="Value" className="largeTextField" validationType="string" allowNull="false" maxLength="500" excludedValues="new Array()" messageCode="field.value" path="adminCommand.configurationParameter.value"/>
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

  <jsp:include page="/pages/jsp/tiles/admin/hoverConfirmation.jsp" />

</form>

