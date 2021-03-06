<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag"%>


<%--------------------------------Style--------------------------------------%>
<style>
   #inputTable{
     width: 95%;
   }

   .sectionHead{
     paddig-top: 5px;
     padding-bottom: 5px;
     background-color: #efefef;
     font-weight: bold;
   }

   .subField{
    padding-left: 50px;
   }


   .error{
    display: block;
    color: red;
    padding-top: 3px;
    padding-bottom: 3px;
   }

   .invalidInput{
    border: 2px solid red;
   }

   .v_digit, .date{
     text-align: right;
   }


   /* ----------- Styles for throbber overlay -------------*/
  .overlay {
    display:none; /* hidden */
    background-color: white;
    width:200px;      /*overlay width, height auto calculated */
    padding:15px;     /*padding to layout nested elements*/
    font-size:12px;
    height: 30px;
  }

  #loadingOverlay{
    position: absolute;
    left: 33%;
    top: 33%;
  }

  #loadingOverlay #message{
    float: left;
    position: absolute;
    left: 65px;
    top: 6px;
    font-size: 14px;
  }

  #loadingOverlay #throbber{
    float: left;
    position: absolute;
    top: 15px;
    left: 25px;
  }

  /* ----------- Styles for throbber overlay -------------*/

  #missingFlowsTable{
    width: 100%;
    border-collapse: collapse !important;
  }

  #missingFlowsTable td,
  #missingFlowsTable th
  {
    border: 1px solid gray;
    padding: 5px 5px 5px 5px;
    text-align: center;
  }

  #missingFlowsTable th
  {
    background-color: #efefef;
  }

</style>
<%--------------------------------HTML---------------------------------------%>


<form id="adminForm" name="adminForm" method="post" commandName="galasCommand">

  <form:hidden path="galasCommand.requestedAction" id="galasCommand.requestedAction"/>
    
  <form:hidden path="galasCommand.gala.pk" id="gala.pk"/>

<spring:nestedPath path="galasCommand">

  <table class="adminEdit" align="center" style="margin-top:10px;">


			<tr>
			  <th><spring:message code="field.admin.gala.name"/></th>
			
				<spring:bind path="gala.name">
					<td><input id="name" class="v_digit"
						type="text" name="${status.expression}" type="date"
						value="${status.value}" /></td>
				</spring:bind>
			</tr>

			        
    <tag:detailField type="text" title="League" className="largeTextField" validationType="string" 
        allowNull="false" maxLength="500" excludedValues="new Array()" 
        messageCode="field.admin.gala.league" path="gala.league"/>

			<tr>
        <th><spring:message code="field.admin.gala.eventDate"/></th>

				<td align="left"><spring:bind path="gala.eventDate">
						<input id="${status.expression}" class="date"
							name="${status.expression}" type="date" value="${status.value}" />
					</spring:bind></td>
			</tr>

			<tr>
        <th><spring:message code="field.admin.gala.eventDateOfBirthDate"/></th>

				<td align="left"><spring:bind path="gala.eventDateOfBirthDate">
						<input id="${status.expression}" class="date"
							name="${status.expression}" type="date" value="${status.value}" />
					</spring:bind></td>
			</tr>

      <tr>
        <th><spring:message code="field.admin.gala.postcode"/></th>

        <td align="left"><spring:bind path="gala.postcode">
            <input id="${status.expression}" class="text"
              name="${status.expression}" type="text" value="${status.value}" />

            <a href="https://maps.google.co.uk/maps?q=${status.value}" target="_blank">Test ${status.value}</a>
          </spring:bind></td>
      </tr>

    <tr>
        <th><spring:message code="field.admin.gala.home.or.away"/></th>

         <td>
              <form:radiobutton path="homeAway" value="home"  />Home
              <br>
              <form:radiobutton path="homeAway" value="away" />Away
          </td>
      </tr>


  </table>

  <table align="center" style="margin-top:10px;">
		<tr>
			<td>
			  <a id="cancelButton" onclick="window.location.href='listGalas.htm';" class="button_input" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.cancel"/></span></a>
			</td>
			<td>
			  <a id="saveButton" class="button_input" onclick="this.blur(); save();" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.save"/></span></a>
			</td>
      <td>
        <a id="addRaces" class="button_input" onclick="updateRace('listGalaRaces.htm', 'pk', 'gala.pk');" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.addRaces"/></span></a>
      </td>
      <!-- 
      <td>
        <a id="addSwimmers" class="button_input" onclick="updateRace('addSwimmersToGala.htm', 'pk', 'gala.pk');" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.addSwimmers"/></span></a>
      </td>
       -->
			
		</tr>
	</table>
	</spring:nestedPath>

  <jsp:include page="/pages/generic/tiles/hoverConfirmation.jsp" />

</form>

<%--------------------------------JS-----------------------------------------%>
<script src="pages/generic/jquery/jquery.tools.min.js"></script>
<script type="text/javascript">

<%-- Required for validation inside PageController--%>
<jsp:include page="/pages/javascript/Validator.js" />

<%-- Required for  jsonPrintF functionality--%>
<jsp:include page="/pages/javascript/Utility.js" />

PageController = {
  mErrorTemplate : "<label class='error'>{message}</label>",

  /**
  * Validate Page fields
  */
  validate: function(){
    var thisClass = this; //preserve this reference
    thisClass.resetValidationErrors();
    var isValid = true;

      //validate required fields are not empty
      $(".v_required").each(function(){
          var value = $(this).val();
          if(Validator.isEmpty(value)){
            $(this).after(thisClass.mErrorTemplate.jsonPrintf({message: "Required"}));
            $(this).addClass("invalidInput");
            isValid = false;
          }
      });
      //validate integer fields
      $(".v_digit").each(function(){
          var value = $(this).val();
          //only validate non empty fields
          if(!Validator.isEmpty(value)){
            if(!Validator.isDigit(value) && value.toLowerCase()!="n/a"){
              $(this).after(thisClass.mErrorTemplate.jsonPrintf({message: "Is not an integer"}));
              $(this).addClass("invalidInput");
              isValid = false;
            }
          }
      });

      //validate integer fields
      $(".date").each(function(){
          var value = $(this).val();
          //only validate non empty fields
          if(!Validator.isEmpty(value)){
            if(!Validator.isValidDate(value)){
              $(this).after(thisClass.mErrorTemplate.jsonPrintf({message: "Invalid date"}));
              $(this).addClass("invalidInput");
              isValid = false;
            }
          }
      });
      //validate integer fields
      $(".v_range").each(function(){
          var value = $(this).val();
          var min = parseFloat($(this).attr("v_min"));
          var max = parseFloat($(this).attr("v_max"));

          //only validate non empty fields
          if(!Validator.isEmpty(value)){
            if(Validator.isDigit(value) && !value.toLowerCase()!="n/a"){
              if(!Validator.inRange(value, min, max)){
                $(this).after(thisClass.mErrorTemplate.jsonPrintf({
                message: "Invalid value must be between "+ min +" and "  + max }));
                $(this).addClass("invalidInput");
                isValid = false;
              }
            }
          }
      });

     //validate flow names
      $(".flowName").each(function(){
          var value = $(this).val();
          //only validate non empty fields
          if(!Validator.isEmpty(value)){
            if(value.length>10){
              $(this).after(thisClass.mErrorTemplate.jsonPrintf({
                message: "Flow name can not be longer 10 chars"}));
                $(this).addClass("invalidInput");
                isValid = false;
            }
          }
      });

    return isValid;
  },

  resetValidationErrors: function(){
    $(".error").remove();
    $(".invalidInput").removeClass("invalidInput");
  },

  /** Submit form to server  **/
  submit: function(){
    if(this.validate()){
      $("#saveManualData").attr("action", "saveManualData.htm").submit();
      $("#loadingOverlay #message").text("Saving Data");
      $("#loadingOverlay").overlay().load();     //show throbber
    }
    else{
      alert("Form contains errors");
    }
  },

  /**
  * Initiatise controller
  */
  init: function(){
    //add date picker to date fields
    $("input[type='date']").datepicker({dateFormat: "dd/mm/yy" });

    //register button styles
    $(".button_input")
      .mouseover(function(){buttonHover(this);})
      .mouseout(function(){buttonNormal(this);})
      .css("text-align", "center");

      //register overlay
      $("#loadingOverlay").overlay({
        mask: {color: "black", loadSpeed: 200,  opacity: 0.5},
        top: "40%",
        load: false,
        closeOnClick: false
      });

    this.connectDom();
  },

  /*
  * Connect page elements to this controller
  */
  connectDom: function(){
    var thisClass = this; //preserve reference to class

    $("#addManualDataButton").click(function(){
      thisClass.submit();
    });
  },

};


$(function(){
  PageController.init();
});

function updateRace(url, record_id_field_name, recordId)
{
	galaPk = adminForm.elements[recordId].value;

  window.location.href = url + '?' + record_id_field_name + '=' + galaPk;
	return;
}

</script>