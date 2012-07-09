<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>

<% pageContext.setAttribute("DATE_FORMAT", uk.co.utilisoft.table.TableConstants.SCREEN_DATE_FORMAT); %>

<script type="text/javascript">

</script>

<style>
  .hideDates {
      display: none;
  }

  .ui-datepicker-trigger {
    margin-left:4px;
  }
</style>

  <spring:hasBindErrors name="commandSearch">
    <input type="hidden" id="CONTAINS_BIND_ERRORS" value="true"/>
  </spring:hasBindErrors>

  <form:form id="searchForm" method="post" onsubmit="return false;" commandName="commandSearch">
  <form:hidden path="requestedAction" id="act"/>
  <form:hidden path="sortList" id="sortList"/>
  <form:hidden path="recordsPerPage" id="recordsPerPage"/>
  <form:hidden path="currentPage" id="currentPage"/>
  <form:hidden path="saveFilter" id="saveFilter"/>

  <input type="hidden" id="selectedRecordId" name="selectedRecordId" value=""/>
  <input type="hidden" value="" name="hiddenRecordId" id="hiddenRecordId"/>
  <input type="hidden" id="filter_date_format" value="${DATE_FORMAT}"/>

  <div style="padding-left: 150px;padding-right: 150px;width: 400px;text-align: justify;min-height:15px;">
    <tag:errors name="commandSearch"/>
  </div>

  <div>

    <div style="float:left; width:400px;">

     <br>

    <a class="submit" onclick="updateResults('apply')" href="#"><img src="pages/generic/images/tick.png"></img><label>Apply</label></a>
    <a class="submit" onclick="clearFilter()" href="#"><img height="20px" width="15px" src="pages/generic/images/ref.png"></img><label>Clear</label></a>

    </div>

      <div id="searchCriteriaDiv" style=" padding-left: 150px;">
      <table>
        <c:forEach items="${commandSearch.searchCriteriaDTOs}" var="options" varStatus="count">
          <tr>
          <td>
            <form:checkbox path="searchCriteriaDTOs[${count.index}].enabled" />
          </td>

          <spring:bind path="commandSearch.searchCriteriaDTOs[${count.index}].displayName">
            <td align="right" style="font-size: 8pt;">${status.value}</td>
            <input type="hidden" name="${status.expression}" value="${status.value}"/>
          </spring:bind>

          <spring:bind path="commandSearch.searchCriteriaDTOs[${count.index}].type">
            <input type="hidden" name="${status.expression}" value="${status.value}"/>

          <c:choose>
            <c:when test="${status.value == 'num' || status.value == 'bigDecimal'}">
              <td>
                <form:select path="searchCriteriaDTOs[${count.index}].comparator">
                  <c:forEach items="${NUM_COMPARATOR}" var="com">
                    <form:option value="${com.value}">${com.label}</form:option>
                  </c:forEach>
                </form:select>
              </td>
              <td>
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue" cssClass="number" cssErrorClass="inputError"/>
              </td>
            </c:when>

            <c:when test="${status.value == 'date' || status.value == 'parmsreportingperiod'}">
              <td><form:select path="searchCriteriaDTOs[${count.index}].comparator">
                <c:forEach items="${DATE_COMPARATOR}" var="com">
                  <form:option value="${com.value}">${com.label}</form:option>
                </c:forEach>
              </form:select></td>
              <td>
                <!-- searchCriteriaDTOs[${count.index}].searchValue.dateTime is causing null pointer -->
                <spring:bind path="searchCriteriaDTOs[${count.index}].searchValue" >
                  <input type="text" name="${status.expression}" id="${status.expression}"
                  <c:choose>
                    <c:when test="${status.error}">value="${status.value}" class="inputError date"</c:when>
                    <c:otherwise> value="${status.value}" class="date"</c:otherwise>
                  </c:choose> />


                  <!--   <bcs:calendarSelector inputId="${status.expression}" />-->
                  <script type="text/javascript">
                    $(function() {
                      $.fx.speeds._default = 10;
                      var dateFieldId = parseIdForJQuery('${status.expression}');
                      $(parseIdForJQuery('${status.expression}')).datepicker({
                        showOn: 'button',
                        buttonImage: 'pages/generic/images/calendar.gif',
                        buttonImageOnly: true,
                        buttonText: 'Select a Reporting Month',
                        showAnim: 'fadeIn',
                        buttonText: 'Select a Reporting Period',
                        duration: 'fast',
                        dateFormat: 'M yy',
                        altField: dateFieldId,
                        altFormat: 'M yy',
                        changeMonth: true,
                        changeYear: true,
                        showButtonPanel: true,
                        onClose: function(dateText, inst) {
                          var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
                          var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
                          $(this).datepicker('setDate', new Date(year, month, 1));
                          $(parseIdForJQuery('${status.expression}')).focus();
                        },
                        beforeShow : function(input, inst) {
                          if ((datestr = $(this).val()).length > 0) {
                            $(this).datepicker('option', 'monthNames', ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']);
                            year = datestr.substring(datestr.length-4, datestr.length);
                            month = jQuery.inArray(datestr.substring(0, datestr.length-5), $(this).datepicker('option', 'monthNames'));
                            $(this).datepicker('option', 'defaultDate', new Date(year, month, 1));
                            $(this).datepicker('setDate', new Date(year, month, 1));
                          }
                        }
                      });
                      $(parseIdForJQuery('${status.expression}')).focus(function () {
                        $('.ui-datepicker-calendar').addClass('hideDates');
                      });
                    });

               </script>
                </spring:bind>
              </td>
            </c:when>


            <c:when test="${status.value == 'boolean'}">
              <td>
              <form:hidden  path="searchCriteriaDTOs[${count.index}].comparator" />
                <form:select onchange="" path="searchCriteriaDTOs[${count.index}].searchValue">
                    <form:option  value="" ></form:option>
                  <c:forEach items="${BOOL_COMPARATOR}" var="com">
                    <form:option  value="${com.value}" >${com.label}</form:option>
                  </c:forEach>
                </form:select>
             </td>
            </c:when>

            <c:when test="${status.value == 'text'}">
              <td>
                <form:select path="searchCriteriaDTOs[${count.index}].comparator">
                  <c:forEach items="${TEXT_COMPARATOR}" var="com">
                    <form:option  value="${com.value}" >${com.label}</form:option>
                  </c:forEach>
                </form:select>
             </td>
              <td>
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue" onblur=""/>
              </td>
            </c:when>

            <c:when test="${status.value == 'mpancore'}">
              <td>
                <form:select path="searchCriteriaDTOs[${count.index}].comparator">
                  <c:forEach items="${TEXT_COMPARATOR}" var="com">
                    <form:option  value="${com.value}" >${com.label}</form:option>
                  </c:forEach>
                </form:select>
             </td>
              <td>
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue" onblur=""/>
              </td>
            </c:when>

            <c:when test="${status.value == 'datetime'}">
              <td>
                <form:select path="searchCriteriaDTOs[${count.index}].comparator">
                  <c:forEach items="${DATE_COMPARATOR}" var="com">
                    <form:option value="${com.value}">${com.label}</form:option>
                  </c:forEach>
                </form:select>
              </td>
              <td>
                <!-- disable form fields for statuses of datetime
                <form:input  path="searchCriteriaDTOs[${count.index}].searchValue.dateTime" id="tradeDate${count.index}"/>
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue.hours" cssStyle="width:20px;" />:
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue.minutes" cssStyle="width:20px;" />:
                <form:input path="searchCriteriaDTOs[${count.index}].searchValue.seconds" cssStyle="width:20px;" />
                -->
               <!--   <bcs:calendarSelector inputId="tradeDate${count.index}"/> -->


               </td>
              <!-- </td> -->
            </c:when>

            <c:when test="${status.value == 'enum'}">
              <td>
                <form:select path="searchCriteriaDTOs[${count.index}].comparator">
                  <c:forEach items="${TEXT_COMPARATOR}" var="com">
                    <form:option value="${com.value}">${com.label}</form:option>
                  </c:forEach>
                </form:select>
              </td>
              <td>
                <form:select cssClass="extraLargeComboField" path="searchCriteriaDTOs[${count.index}].searchValue">
                  <c:forEach items="${options.filterValues}" var="com">
                    <form:option value="${com.value}">${com.name}</form:option>
                  </c:forEach>
                </form:select>
              </td>
            </c:when>

            <c:when test="${status.value == 'customselect'}">
              <td>
                <form:select cssClass="mediumComboField" path="searchCriteriaDTOs[${count.index}].searchValue">
                  <c:forEach items="${options.filterValues}" var="com">
                    <form:option value="${com.value}">${com.name}</form:option>
                  </c:forEach>
                </form:select>
              </td>
              <td>
                <spring:bind path="commandSearch.searchCriteriaDTOs[${count.index}].comparator">
                  <input type="hidden" name="${status.expression}" value="="/>
                </spring:bind>
              </td>
            </c:when>

            <c:otherwise></c:otherwise>
          </c:choose>

           </spring:bind>
         </tr>
         <form:hidden path="searchCriteriaDTOs[${count.index}].modelName"/>
       </c:forEach>
   </table>
   </div>
 </div>

<div style="clear:both;"></div>




</form:form>