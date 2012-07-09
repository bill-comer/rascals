<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<form name="items" method="post" action="">

<c:forEach
  items="${adminListCommand.identifier}" varStatus="loopStatus">
  <spring:bind path="adminListCommand.identifier[${loopStatus.index}]">
    <input type="hidden" id="${status.expression}"
      name="${status.expression}" value="${status.value}" />
  </spring:bind>
  
</c:forEach>

<c:set var="numberOfItems" value="${fn:length(items)}" />

<c:set var="numberOfColumns" value="${fn:length(columnMessageCodes)}" />
  
  <c:forEach
    begin="1" end="${numberOfColumns}" varStatus="loopStatus">
    <input type="hidden" id="columnWidth_${loopStatus.index - 1}"
    value="${columnWidths[loopStatus.index - 1]}" />
  </c:forEach>

<div style="margin-top: 10px; margin-left: 10px; margin-right: 10px;"
  align="center">

<table class="tablesorter" id="myTable" cellspacing="0" cellpadding="5"
  style="padding-bottom: 10px">
  <thead>
    <tr>
      <c:forEach begin="1" end="${numberOfColumns}"
        varStatus="loopStatus">
        <th id="columnTitle_${loopStatus.index - 1}">
          <spring:message code="${columnMessageCodes[loopStatus.index - 1]}" />
        </th>
      </c:forEach>
    </tr>
  </thead>

  <c:forEach begin="1" end="${numberOfItems}" varStatus="loopStatus">
    <c:forEach items="${items[loopStatus.index - 1].identifier}"
      var="innerId" varStatus="idLoopStatus">
      <input type="hidden"
        id="id_${loopStatus.index-1}_${idLoopStatus.index}"
        name="id_${loopStatus.index-1}_${idLoopStatus.index}"
        value="${innerId}" />
    </c:forEach>


    <tbody>
      <tr id="item_${loopStatus.index - 1}"
        onclick="viewItem('${loopStatus.index -1}');"
        onMouseOver="javascript:highlightRow('item_${loopStatus.index - 1}', 'roll');"
        onMouseOut="javascript:highlightRow('item_${loopStatus.index - 1}', 'over');">
        <c:forEach begin="1" end="${numberOfColumns}"
          varStatus="innerLoopStatus">
          <td
            id="columnValue_${loopStatus.index - 1}_${innerLoopStatus.index - 1}">
                ${items[loopStatus.index - 1].values[innerLoopStatus.index - 1]}
            </td>
        </c:forEach>
      </tr>
    </tbody>
  </c:forEach>
</table>
</div>

<c:if test="${adminListCommand.canCreateNew}">
  <div class="spaced_div">
  <table align="center">
  </table>
  </div>
</c:if>

</form>