<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<script src="pages/generic/javascript/searchFilterResults.js" type="text/javascript"></script>
<script>

function selectedRow(recordId)
{
  $("[id^=row_]").each(function() {
    $(this).removeClass("purple");
  });

  $("[id^=row_" + recordId + "]").each(function() {
    $(this).addClass("purple");
  });
}



</script>

<img id="loading_image" src="pages/generic/images/loading.gif" class="no_display" style="position:absolute;top:30%;left:40%;margin-top:-50px;margin-left:-50px;opacity:0.4;filter:alpha(opacity=40)"/>

<input type="hidden" id="resCount" value="${COUNT}"/>

  <div style="float:right; padding:0 20px 10px 0;">
    <c:if test="${COUNT > 0}">
      <label> Records Found: <img id="count_load_img" src="pages/generic/images/loading_small.gif" class="no_display" style="position:absolute;"/>  </label>
      <label id="count_display">${COUNT}</label>
    </c:if>
  </div>

  <c:choose>
    <c:when test="${COUNT == 0}">
      <div style="padding-left:40%; font-weight: bold;">No Records Found </div>
      <br>
      <br>
    </c:when>
    <c:otherwise>
      <c:if test="${fn:length(EXTRA_HEADERS) > 0}">
        <table id="extraTable" class="tablesorter" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <c:forEach items="${EXTRA_HEADERS}" var="head" varStatus="count">
                <th title="${head.title}" id="header_extra_${count.index}">
                  ${head.header}
                </th>
              </c:forEach>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${EXTRA_RESULTS}" var="res">
              <c:forEach items="${res.cells}" var="cell">
                <c:choose>
                  <c:when test="${cell.type == 'boolean'}"><td class="${cell.cellMap['cssClass']}"><input type="checkbox" disabled <c:if test="${cell.value}">checked</c:if>/></td></c:when>
                  <c:otherwise>
                    <td class="${cell.cellMap['cssClass']}">
                      <c:choose>
                        <c:when test="${empty cell.value}">&nbsp</c:when>
                        <c:otherwise>${cell.value}</c:otherwise>
                      </c:choose>
                    </td>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </c:forEach>
          </tbody>
        </table>
      </c:if>

      <c:if test="${fn:length(HEADERS) > 0}">
        <form id="redirectUrlForm" name="redirectUrlForm">
          <input type="hidden" id="id_0" name="id_0" value=""/>
          <input type="hidden" id="requestedResultsPageAction" name="requestedResultsPageAction" value=""/>

          <div id="spaced_div" style="padding-top: 5px;padding-bottom: 5px;">
            <tag:response.messages />
          </div>

          <table id="myTable" class="tablesorter" cellspacing="0" cellpadding="0">
            <thead>
              <tr>
                <!--  Prepend Extra Column Headers -->
                <jsp:include page="${extra_headers_start}"/>

                <c:forEach items="${HEADERS}" var="head" varStatus="count">
                  <th title="${head.title}"
                    <c:if test="${SORT_COLUMN == head.model}"> class="${SORT_ORDER}"</c:if>
                    id="header${count.index}"
                    <c:if test="${head.allowSort}">onclick="sortTable('${count.index}', '${head.model}')"</c:if>>
                    ${head.header}
                  </th>
                </c:forEach>
              </tr>
            </thead>
            
            <tbody>
            
						<c:forEach items="${RESULTS}" var="res" varStatus="count">
						
						    <tr id="thisId_${res.rowMap['thisId']}" class="${res.rowMap['cssClass']}" title="${res.rowMap['title']}">
                  <!-- Prepend Extra Table Column Data -->
                  <c:set var="row" value="${res}" scope="request"/>
                  <c:set var="rowCount" value="${count}" scope="request"/>

                  <c:forEach items="${res.cells}" var="cell">
                    <c:choose>
                      <c:when test="${cell.type == 'boolean'}"><td onclick="${res.rowMap['detailUrl']}" class="${cell.cellMap['cssClass']}"><input type="checkbox" disabled <c:if test="${cell.value}">checked</c:if>/></td></c:when>
                      <c:otherwise>
                        <td onclick="${res.rowMap['onClickFunction']}" class="${cell.cellMap['cssClass']}">
                          <c:choose>
                            <c:when test="${empty cell.value}">&nbsp</c:when>
                            <c:otherwise>${cell.value}</c:otherwise>
                          </c:choose>
                        </td>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </tr>

						</c:forEach>
						
            </tbody>
          </table>
        </form>
      </c:if>

      <div>
        <div style="float:right; padding-right: 20px;">
          <label>Records Per Page</label>
            <spring:bind path="commandSearch.recordsPerPage">
              <select id="recordsPerPageSelect" onchange="updateRecordsPerPage();" style="width:45px;">
                <c:forEach items="${RECORDS_PER_PAGE_LIST}" var="rpp">
                  <option <c:if test="${status.value == rpp.value}">selected</c:if> value="${rpp.value}">${rpp.label}</option>
                </c:forEach>
              </select>
            </spring:bind>
        </div>

        <jsp:include page="${table_actions}"/>

        <table align="center">
          <tr>
            <td>
              <label id="paging"></label>
            </td>
          </tr>
        </table>

        <br><br>

        
      </div>

      <script>calculatePaging(${COUNT}, ${RESULTS_PER_PAGE}, ${CURRENT_PAGE});</script>

    </c:otherwise>

  </c:choose>
  
    <jsp:include page="${bottom_buttons}"/>
