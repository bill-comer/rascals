<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>
<%@ taglib uri="http://www.joda.org/joda/time/tags" prefix="joda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Extra Javascripts -->
<script type="text/javascript" src="pages/javascript/viewReports.js"></script>

<script>
  function disableDownloadButton()
  {
    alert("Download diabled until a report has been created.");
  }
</script>

<form id="parmsViewDpiReport" name="parmsViewDpiReport" method="post" commandName="parmsReportCommand">

   <input type="hidden" id="id_0" name="id_0" value="${parmsReportCommand.dpiFilePk}"/>
   <input type="hidden" id="requestedAction" name="requestedAction">


<c:if test="${!empty parmsReportCommand.errorMessage}">
  <table width="100%">
    <tr>
      <td class="errorText">${parmsReportCommand.errorMessage}</td>
    </tr>
  </table>
</c:if>

  <div id="spaced_div" style="padding-top: 5px;padding-bottom: 5px;">
    <tag:response.messages />
  </div>

  <table id="myTable" class="tablesorter" cellspacing="0" cellpadding="5" style="padding-bottom: 10px;border-color: #D4D4D4">
    <thead>
      <tr>
        <c:set var="numberOfDpiFileDetailColumns" value="${fn:length(columnMessageCodes)}"/>
        <c:forEach begin="1" end="${numberOfDpiFileDetailColumns}" varStatus="loopStatus">
          <input type="hidden" id="columnWidth_${loopStatus.index - 1}" value="${columnWidths[loopStatus.index - 1]}" />
        </c:forEach>
        <c:forEach begin="1" end="${numberOfDpiFileDetailColumns}" varStatus="loopStatus">
          <th id="columnTitle_${loopStatus.index - 1}"><spring:message code="${columnMessageCodes[loopStatus.index - 1]}"/></th>
        </c:forEach>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td id="supplier_val">${parmsReportCommand.dpiFile.supplier.supplierId}</td>
        <td id="reporting_end_month_val">
          <joda:format value="${parmsReportCommand.dpiFile.reportingPeriod.nextReportingPeriod.startOfFirstMonthInPeriod}" pattern="MMM yyyy"/>
        </td>
        <td id="filename_val">
          <c:choose>
            <c:when test="${not empty parmsReportCommand.dpiFile}">
              <c:out value="${parmsReportCommand.dpiFile.fileName}"/>
            </c:when>
            <c:otherwise>
              <spring:message code="field.dpi.file.not.created"/>
            </c:otherwise>
          </c:choose>
        </td>
        <td id="generated_val">
          <c:if test="${not empty parmsReportCommand.dpiFile.dateCreated}">
            <joda:format value="${parmsReportCommand.dpiFile.dateCreated}" pattern="dd/MM/yyyy HH:mm:ss"/>
          </c:if>
        </td>

        <c:if test="${displayTMinus1Data}">
          <td id="reporting_period_month_t_minus_1_val}">
            <joda:format value="${parmsReportCommand.dpiFile.reportingPeriod.startOfFirstMonthInPeriod}" pattern="MMM yyyy" />
          </td>
        </c:if>

        <c:if test="${displayTData}">
          <td id="reporting_period_month_t_val">
            <joda:format value="${parmsReportCommand.dpiFile.reportingPeriod.nextReportingPeriod.startOfFirstMonthInPeriod}" pattern="MMM yyyy" />
          </td>
        </c:if>
      </tr>
    </tbody>
  </table>