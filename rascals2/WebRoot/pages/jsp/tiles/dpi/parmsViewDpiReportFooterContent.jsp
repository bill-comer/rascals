 <%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
 <%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

  <table align="center" style="padding-top:10px;padding-bottom: 10px;">
    <br>
    <tr>
      <td>
        <a class="button_input"  style="width: 90px;"
            onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" style="text-align: center"
              <c:choose>
              <c:when test="${parmsReportCommand.dpiFileData.data == null || parmsReportCommand.dpiFileData.data == ''}">
                 disabled
                 onclick="disableDownloadButton();"
              </c:when>
              <c:otherwise>
                 onclick="this.blur();redirect2('downloadDpiFileReport.htm', 'id_0', ${param['id_0']});"
              </c:otherwise>
              </c:choose>
              >
          <span><spring:message code="button.parms.dpi.file.download.report"/></span>
        </a>
      </td>

      <td>
        <a class="button_input" style="width: 120px;"
            onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" style="text-align: center"
              <c:choose>
              <c:when test="${parmsReportCommand.dpiFileData.data == null || parmsReportCommand.dpiFileData.data == ''}">
                 disabled
                 onclick="disableDownloadButton();"
              </c:when>
              <c:otherwise>
                 onclick="this.blur();redirect2('downloadCsvDpiFileReport.htm', 'id_0', ${param['id_0']});"
              </c:otherwise>
              </c:choose>
              >
          <span><spring:message code="button.parms.dpi.file.download.csv.report"/></span>
        </a>
      </td>
      
      <td>
        <a id="viewDpiFileDataButton" onclick="redirect('dpiFileReportData.htm', 'id_0');"
          class="button_input" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);" style="width: 90px;">
          <span>
            <spring:message code="button.parms.report.view.data"/>
          </span>
        </a>
      </td>
      
      <td>
        <a id="backButton" class="button_input"  style="width: 80px;"
          onclick="this.blur(); history.back();" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);">
          <span>
            <spring:message code="button.back"/>
          </span>
        </a>
      </td>
    </tr>
  </table>
  <br>

</form>
