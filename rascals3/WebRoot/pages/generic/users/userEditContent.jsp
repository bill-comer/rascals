<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<form id="adminForm" name="adminForm" method="post" commandName="usersCommand">

  <form:hidden path="usersCommand.requestedAction" id="usersCommand.adminRequestedAction"/>

  <table class="adminEdit" align="center" style="margin-top:10px;">
  
    <tag:response.messages />

    <tag:detailField type="text" title="User Name" className="largeTextField" validationType="string" allowNull="false" maxLength="200" excludedValues="new Array()" messageCode="field.admin.listusers.username" path="usersCommand.user.userName"/>
    <tag:detailField type="text" title="Email Address" className="largeTextField" validationType="string" allowNull="false" maxLength="500" excludedValues="new Array()" messageCode="field.admin.listusers.email" path="usersCommand.user.emailAddress"/>

    <tag:detailField type="passwordinput" title="Password" className="largeTextField" validationType="string" allowNull="false" maxLength="200" excludedValues="new Array()" messageCode="field.admin.listusers.password" path="usersCommand.user.password"/>
    <tag:detailField type="passwordinput" title="Password" className="largeTextField" validationType="string" allowNull="false" maxLength="200" excludedValues="new Array()" messageCode="field.admin.listusers.confirmpassword" path="usersCommand.user.confirmPassword"/>
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

