

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<html>

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=8">
  <title><spring:message code="product.title" /></title>

<!-- JQuery -->

<script type="text/javascript" src="pages/generic/jquery/jquery.min.js"></script>
<script type="text/javascript" src="pages/generic/jquery/jquery-ui-1.8.9.custom.min.js"></script>
<script type="text/javascript" src="pages/generic/jquery/jquery.form.js"></script>
<script type="text/javascript" src="pages/generic/jquery/ui.core.js"></script>
<script type="text/javascript" src="pages/generic/jquery/jquery.tablesorter.min.js"></script>
<link href="pages/generic/jquery/jquery.ui.all.css" rel="stylesheet" type="text/css"/>


<!-- Application Javascripts -->
<script type="text/javascript" src="pages/generic/javascript/button.js"></script>
<script type="text/javascript" src="pages/generic/javascript/clock.js"></script>
<script type="text/javascript" src="pages/generic/javascript/navigation.js"></script>
<script type="text/javascript" src="pages/generic/javascript/detail.js"></script>
<script type="text/javascript" src="pages/generic/javascript/rollOver.js"></script>
<script type="text/javascript" src="pages/generic/javascript/general.js"></script>
<script type="text/javascript" src="pages/generic/users/admin.js"></script>
<script type="text/javascript" src="pages/generic/javascript/results.js"></script>
<script type="text/javascript" src="pages/generic/javascript/searchFilterResults.js"></script>

<!-- Application Style Sheets -->
<link href="pages/generic/style/detail.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/general.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/admin.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/tabBar.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/tabbedFilter.css" rel="stylesheet" type="text/css" />
<!-- 
<link href="pages/generic/style/tableSorter.css" rel="stylesheet" type="text/css" />
 -->
<link href="pages/generic/style/titleBar.css" rel="stylesheet" type="text/css" />

<!-- Extra Javascripts -->

</head>

<script type="text/javascript">
var hovering = false;

$(document).ready(function(){
  calculateCurrentDate();
  bindTabBarFunctions();
  });
</script>

<tiles:insert attribute="bodyHeader"/>
<tiles:insert attribute="title"/>
<tiles:insert attribute="tabBar"/>

<!-- add fixed current time -->
<jsp:include page="/pages/generic/tiles/fixCurrentTime.jsp"/>

<tiles:useAttribute name="extra_headers_start" scope="session"/>
<tiles:useAttribute name="extra_headers_end" scope="session"/>
<tiles:useAttribute name="extra_tdata_start" scope="session"/>
<tiles:useAttribute name="extra_tdata_end" scope="session"/>
<tiles:useAttribute name="nested_rows" scope="session"/>
<tiles:useAttribute name="table_actions" scope="session"/>
<tiles:useAttribute name="filter" scope="session"/>
<tiles:useAttribute name="results" scope="session"/>
<tiles:useAttribute name="table_name" scope="session"/>
<tiles:useAttribute name="bottom_buttons" scope="session"/>

 <div id="overall" style="width:100%;position:absolute;top:70px;">
  <div style="width:98%; background-color: white; margin-left:auto;margin-right:auto;"/>
    <c:set var="portalName" value="${table_name}" scope="session"/>
    <jsp:include page="/pages/generic/tiles/portalHeader.jsp"/>

    <div id="tempResultsTable" style="display:hidden"></div>

    <div id="filterDiv" style="padding-top:5px;padding-left:20px;position:relative;">
      <tiles:insert attribute="filter" />
    </div>

    <div id="resultsTable" style="padding: 0px 10px 10px 10px;position:relative;">
      <tiles:insert attribute="results"/>
    </div>


    <jsp:include page="/pages/generic/tiles/portalFooter.jsp"/>
   </div>
 </div>

</html>
