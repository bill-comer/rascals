<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<script type="text/javascript" src="pages/javascript/dpiGeneration.js"></script>
<script type="text/javascript" src="pages/jquery/malsup-blockui-5b40212/jquery.blockUI.js"></script>
<script type="text/javascript" src="pages/javascript/forms.js"></script>

<!-- Extra Javascripts -->
<script>
  $(document).ready(function() {
    dpiGenerationDatePicker();
    $('#reportingPeriod').bind("keydown", function(e) {
      if (e.type == 'keydown' && e.which == 13) return false;
    });
  });
</script>

<style>

  .ui-datepicker-calendar {
      display: none;
   }

  .errorText {
    font-family: Verdana, Arial, Helvetica, sans-serif;
    font-size: 12px;
    color: red;
  }
</style>

<form id="mopGeneration" name="mopGeneration" method="post" action="" onsubmit="return false;">

  <input type="hidden" id="date_picker_format" name="date_picker_format" value='<spring:message code="date.picker.day.month.year.format"/>' />
  <spring:bind path="mopGenerationCommand.mopGenerationRequestedAction">
    <input type="hidden" name="${status.expression}" id="${status.expression}" value="${status.value}" />
  </spring:bind>

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

  <div style="margin-top:10px;margin-left:10px;margin-right:10px;" align="center" >

    <tag:errors name="mopGenerationCommand" />

    <table id="myTable" align="center" style="width: 600px;padding-top: 0px;margin-top:0px;font-family:arial;text-align: center;border: 0px solid black;border-collapse: collapse;font-size: 8pt;vertical-align: top;width:100%; height: 250px;;background-color:white;">
      <tbody>
        <tr>
        </tr>
        <tr>
         <td>
              Report Types
          </td>
          <td>
            <input type="checkbox" name="sp11" value="sp11" />SP11<br />
            <input type="checkbox" name="sp14" value="sp14" />SP14<br />
            <input type="checkbox" name="sp15" value="sp15" />SP15<br />
            <input type="checkbox" name="hm12" value="hm12" />HM12<br />
            <input type="checkbox" name="nm12" value="nm12" />NM12<br />
          </td>
        </tr>
      
        <tr>
          <td>
            <spring:message code="field.mop.reporting.period" />:
          </td>
          <td>
            <spring:bind path="mopGenerationCommand.reportingPeriod">
              <input type="text" name="${status.expression}" id="${status.expression}"
              <c:choose>
                <c:when test="${status.error}">value="${status.value}" class="inputError"</c:when>
                <c:otherwise>value="${status.value}" class="date"</c:otherwise>
              </c:choose> />
              <input type="hidden" id="${status.expression}_datepicker" name="${status.expression}_datepicker"/>
            </spring:bind>
          </td>
          <td>
            <a class="button_input" onclick="this.blur();submitGenerateMopFileForm('Generating MOP File ')" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);">
              <span><spring:message code="buttom.parms.admin.create.generate.report"/></span>
            </a>
          </td>
        </tr>
        </tbody>
    </table>
  </div>

</form>