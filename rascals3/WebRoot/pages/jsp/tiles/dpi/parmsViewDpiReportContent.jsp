<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

  <div id="report_data" style="padding-left: 30px;" align="left">
   <kbd>
    <c:if test="${!empty parmsReportCommand.dpiFileData}">
      ${parmsReportCommand.dpiFileData.displayableData}
    </c:if>
   </kbd>
  </div>