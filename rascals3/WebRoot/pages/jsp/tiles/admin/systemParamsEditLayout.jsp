<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>



<html>

<head>
    <title><spring:message code="product.title"/></title>

    <!-- JQuery -->

    <script type="text/javascript" src="pages/jquery/jquery-1.5.0.min.js"></script>
    <script type="text/javascript" src="pages/jquery/jquery-ui-1.8.9.custom.min.js"></script>
    <script type="text/javascript" src="pages/jquery/jquery.form.js"></script>
    <script type="text/javascript" src="pages/jquery/ui.core.js"></script>
    <link href="pages/jquery/jquery.ui.all.css" rel="stylesheet" type="text/css"/>

    <!-- Application Javascripts -->
    <script type="text/javascript" src="pages/javascript/button.js"></script>
    <script type="text/javascript" src="pages/javascript/clock.js"></script>
    <script type="text/javascript" src="pages/javascript/navigation.js"></script>
    <script type="text/javascript" src="pages/javascript/detail.js"></script>
    <script type="text/javascript" src="pages/javascript/admin.js"></script>
    <script type="text/javascript" src="pages/javascript/general.js"></script>

    <!-- Application Style Sheets -->
    <link href="pages/style/detail.css" rel="stylesheet" type="text/css"/>
    <link href="pages/style/parms.css" rel="stylesheet" type="text/css"/>
    <link href="pages/style/admin.css" rel="stylesheet" type="text/css" />
    <link href="pages/style/tabBar.css" rel="stylesheet" type="text/css"/>
    <link href="pages/style/tabbedFilter.css" rel="stylesheet" type="text/css"/>
    <link href="pages/style/tableSorter.css" rel="stylesheet" type="text/css"/>
    <link href="pages/style/titleBar.css" rel="stylesheet" type="text/css"/>

</head>

<script type="text/javascript">
var hovering = false;

$(document).ready(function(){
  calculateCurrentDate();
  bindTabBarFunctions();


  $.fx.speeds._default = 200;
  $("#dialogSaveChanges").dialog({
      modal: true,
      draggable: false,
      resizable: false,
      title:'<img src="pages/images/downrightarrow.gif"/> &nbsp; Save Changes Confirmation',
      disabled:true,
      closeOnEscape: false,
      autoOpen: false,
      show:"blind",
      width:300,
      height:150,
    open: function(event, ui) {$(this).parent().children().children('.ui-dialog-titlebar-close').hide();},
    buttons: {"Confirm": function() {$(this).dialog('close'); save();},
              "Cancel": function() {$(this).dialog('close');}}
    });

  });

function openSaveChangesDialog()
{
  $('#dialogSaveChanges').dialog('open');
}


  </script>

<jsp:include page="/pages/jsp/tiles/general/bodyHeaderStandard.jsp"/>

<tiles:insert attribute="title" />
<tiles:insert attribute="tabBar" />

<!-- add fixed current time -->
<jsp:include page="/pages/jsp/tiles/general/fixCurrentTime.jsp"/>

<tiles:useAttribute name="portal_name" scope="session" />

<div id="overall" style="width: 100%; position: absolute; top: 70px;" align="center">

  <div style="width: 40%; background-color: white; margin-left: auto; margin-right: auto;"/>

  <c:set var="portalName" value="System Configuration Edit" scope="session"/>
  <jsp:include page="/pages/jsp/tiles/general/portalHeader.jsp"/>
  <tiles:insert attribute="content"/>
  <jsp:include page="/pages/jsp/tiles/general/portalFooter.jsp" /></div>

</div>

</body>
</html>
