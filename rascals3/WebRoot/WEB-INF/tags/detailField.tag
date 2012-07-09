<%@ tag body-content="scriptless"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<%@ attribute name="path" required="true"%>
<%@ attribute name="type" required="true" %>
<%@ attribute name="title" required="true"%>
<%@ attribute name="messageCode" required="true"%>
<%@ attribute name="allowNull" required="true"%>
<%@ attribute name="excludedValues" required="true"%>
<%@ attribute name="className" required="false"%>
<%@ attribute name="validationType" required="false"%>
<%@ attribute name="maxLength" required="false"%>
<%@ attribute name="dateTimeIndex" required="false"%>
<%@ attribute name="maxPrecision" required="false"%>
<%@ attribute name="maxScale" required="false"%>

<tr title="${title}">
  <th><spring:message code="${messageCode}"/></th>
  <td>

    <c:choose>

    <c:when test="${type == 'text'}">
      <spring:bind path="${path}">
          <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
          <input type="text"
                 id="${status.expression}"
                 name="${status.expression}"
             		 value="${status.value}"
                 class="${className}"
                 maxlength="${maxLength}"
                 onkeyup="fieldEdit('${status.expression}');"
                 onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', '${validationType}', '${status.expression}', '${allowNull}', ${excludedValues}, '${maxPrecision}', '${maxScale}');"/><c:if test="${allowNull == 'false'}">&nbsp;*</c:if>
      </spring:bind>
    </c:when>

    <c:when test="${type == 'password'}">
      <spring:bind path="${path}">
          <input type="password"
                 id="${status.expression}"
             	 name="${status.expression}"
             	 value="*****"
                 class="${className}"
                 disabled="disabled"
                 maxlength="${maxLength}"
                 onkeyup="fieldEdit('${status.expression}');"/>
      </spring:bind>
    </c:when>

    <c:when test="${type == 'date'}">
      <spring:bind path="${path}">
          <script type="text/javascript">
	          $(function() {
		          $.fx.speeds._default = 10;
		          $(parseIdForJQuery('${status.expression}')).datepicker({
			            showOn: 'button',
			            buttonImage: 'pages/images/calendar.gif',
			            buttonImageOnly: true,
			            showAnim: 'fadeIn',
			            duration: 'fast',
			            dateFormat: 'dd/mm/yy'
		          });
	          });
          </script>
          <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
          <input type="text"
                 id="${status.expression}"
                 name="${status.expression}"
                 value="${status.value}"
                 maxlength="10"
                 class="dateTextField"
                 onkeyup="fieldEdit('${status.expression}');"
                 onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', 'date', '${status.expression}', '${allowNull}', ${excludedValues});"/>
          <c:if test="${allowNull == 'false'}">&nbsp;*</c:if>
       
      </spring:bind>
    </c:when>

    <c:when test="${type == 'textArea'}">
      <spring:bind path="${path}">
          <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
          <textarea id="${status.expression}"
								    name="${status.expression}"
								    rows="5"
								    cols="30"
								    class="textAreaField"
									  onkeydown="textAreaCounter(this, '${maxLength}');"
									  onkeyup="fieldEdit('${status.expression}');textAreaCounter(this, '${maxLength}');"
									  onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', 'string', '${status.expression}', '${allowNull}', ${excludedValues});">${status.value}</textarea>
          <c:if test="${allowNull == 'false'}">&nbsp;*</c:if>
        
			  </td>
			  </tr>
			  <tr>
				  <td>&nbsp;</td>
				  <td><label id="${status.expression}_remainingCharacters"></label>
		  </spring:bind>
	  </c:when>

		<c:when test="${type == 'dateTime'}">
      <table style="border-style:none;border-collapse:collapse;border-spacing:0px;">
        <tr>
          <td>
        	  <spring:bind path="${path}">
        		    <script type="text/javascript">
	                $(function() {
		                $.fx.speeds._default = 10;
		                $(parseIdForJQuery('${status.expression}')).datepicker({
			                 showOn: 'button',
			                 buttonImage: 'pages/images/calendar.gif',
			      		       buttonImageOnly: true,
			      		       showAnim: 'fadeIn',
			      		       duration: 'fast',
			      		       dateFormat: 'dd/mm/yy'
		      		      });
	      		      });
      			    </script>
      			    <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
      			    <input type="text"
             			     id="${status.expression}"
             			     name="${status.expression}"
             			     value="${status.value}"
             			     maxlength="10"
             			     class="dateTextField"
             			     onkeyup="fieldEdit('${status.expression}');"
             			     onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', 'date', '${status.expression}', '${allowNull}', ${excludedValues});"/>
        		  
        		</spring:bind>
          </td>
        	<td style="padding-left:10px;">
        	  <spring:bind path="${path}">
        		    <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
        		    <input type="text"
               		     id="${status.expression}"
              		     name="${status.expression}"
              		     value="${status.value}"
              		     maxlength="8"
              		     class="dateTextField"
              		     onkeyup="fieldEdit('${status.expression}');"
              		     onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', 'time', '${status.expression}', '${allowNull}', ${excludedValues});"/>
        		    <c:if test="${allowNull == 'false'}">&nbsp;*</c:if>
        		 
        		</spring:bind>
        	</td>
        </tr>
      </table>
		</c:when>
		
		
    <c:when test="${type == 'dateOnly'}">
      <table style="border-style:none;border-collapse:collapse;border-spacing:0px;">
        <tr>
          <td>
            <spring:bind path="${path}">
                <script type="text/javascript">
                  $(function() {
                    $.fx.speeds._default = 10;
                    $(parseIdForJQuery('${status.expression}')).datepicker({
                       showOn: 'button',
                       buttonImage: 'pages/images/calendar.gif',
                       buttonImageOnly: true,
                       showAnim: 'fadeIn',
                       duration: 'fast',
                       dateFormat: 'dd/mm/yy',
                       altFormat: 'dd/mm/yy'
                    });
                  });
                </script>
                <input type="hidden" id="${status.expression}_originalValue" name="${status.expression}_originalValue" value="${status.value}"/>
                <input type="text"
                       id="${status.expression}"
                       name="${status.expression}"
                       value="${status.value}"
                       maxlength="10"
                       class="dateTextField"
                       onkeyup="fieldEdit('${status.expression}');"
                       onchange="afterFieldEdit('<spring:message code="${messageCode}"/>', 'date', '${status.expression}', '${allowNull}', ${excludedValues});"/>
              
            </spring:bind>
          </td>

        </tr>
      </table>
    </c:when>


    <c:otherwise></c:otherwise>
    </c:choose>

  </td>
</tr>
