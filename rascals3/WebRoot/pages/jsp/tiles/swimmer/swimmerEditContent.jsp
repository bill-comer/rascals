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


<form id="adminForm" name="adminForm" method="post" commandName="swimmersCommand">

  <form:hidden path="swimmersCommand.requestedAction" id="swimmersCommand.requestedAction"/>

<spring:nestedPath path="swimmersCommand">

  <table class="adminEdit" align="center" style="margin-top:10px;">


		        
    <tag:detailField type="text" title="League" className="largeTextField" validationType="string" 
        allowNull="false" maxLength="500" excludedValues="new Array()" 
        messageCode="field.admin.swimmer.firstname" path="swimmer.firstname"/>
                    
    <tag:detailField type="text" title="League" className="largeTextField" validationType="string" 
        allowNull="false" maxLength="500" excludedValues="new Array()" 
        messageCode="field.admin.swimmer.surname" path="swimmer.surname"/>

			<tr>
        <th><spring:message code="field.admin.swimmer.dob"/></th>

				<td align="left"><spring:bind path="swimmer.dateOfBirth">
						<input id="${status.expression}" class="date"
							name="${status.expression}" type="date" value="${status.value}" />
					</spring:bind></td>
			</tr>


    <tr>
        <th><spring:message code="field.admin.swimmer.male.or.female"/></th>

         <td>
              <form:radiobutton path="male" value="male"  />Male
              <br>
              <form:radiobutton path="male" value="female" />Female
          </td>
      </tr>


  </table>

  <table align="center" style="margin-top:10px;">
		<tr>
			<td>
			  <a id="cancelButton" onclick="window.location.href='listBoySwimmers.htm';" class="button_input" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.cancel"/></span></a>
			</td>
			<td>
			  <a id="saveButton" class="button_input" onclick="this.blur(); cancel();" onmouseover="buttonHover(this);" onmouseout="buttonNormal(this);"><span><spring:message code="button.save"/></span></a>
			</td>
			
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

</script>