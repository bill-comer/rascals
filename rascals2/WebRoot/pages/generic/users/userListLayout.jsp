<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<html>
<head>
<title><spring:message code="product.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<link rel="shortcut icon" href="/pages/generic/images/favicon.ico">
<link rel="icon" href="/pages/generic/images/animated_favicon.gif" type="image/gif">

<!-- JQuery -->

<script type="text/javascript" src="pages/generic/jquery/jquery.min.js"></script>
<script type="text/javascript" src="pages/generic/jquery/jquery-ui-1.8.9.custom.min.js"></script>
<script type="text/javascript" src="pages/generic/jquery/jquery.form.js"></script>
<script type="text/javascript" src="pages/generic/jquery/ui.core.js"></script>
<link href="pages/generic/jquery/jquery.ui.all.css" rel="stylesheet" type="text/css"/>


<!-- Application Javascripts -->
<script type="text/javascript" src="pages/generic/javascript/button.js"></script>
<script type="text/javascript" src="pages/generic/javascript/clock.js"></script>
<script type="text/javascript" src="pages/generic/javascript/navigation.js"></script>
<script type="text/javascript" src="pages/generic/javascript/detail.js"></script>
<script type="text/javascript" src="pages/generic/javascript/rollOver.js"></script>
<script type="text/javascript" src="pages/generic/javascript/general.js"></script>

<!-- Application Style Sheets -->
<link href="pages/generic/style/detail.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/titleBar.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/tabBar.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/tabbedFilter.css" rel="stylesheet" type="text/css" />
<link href="pages/generic/style/general.css" rel="stylesheet" type="text/css" />

<!-- Extra Javascripts -->
<script type="text/javascript" src="pages/generic/users/admin.js"></script>

<!-- Extra Style Sheets -->
<link href="pages/generic/style/admin.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
  var hovering = false;

  $(document).ready(function(){
    bindTabBarFunctions();
    });
</script>

</head>

<tiles:insert attribute="title"/>
<tiles:insert attribute="tabBar"/>

<!-- add fixed current time -->
<jsp:include page="/pages/generic/tiles/fixCurrentTime.jsp"/>

<body onload="sizeColumns();sizeWindow();calculateCurrentDate();" style="background-color: #EFEFEF;">

<div id="overall" style="width: 100%; position: absolute; top: 70px;" align="center"/>
  <div id="Window" class="window" style="width: 50%; margin-left: auto; margin-right: auto;">
    <div class="windowHeader">
      <c:set var="portalName" value="${title}" scope="session"/>
      <jsp:include page="/pages/generic/tiles/portalHeader.jsp"/>
    </div>

    <div class="windowContent" style="background-color: white;padding-top: 2px">
      <tiles:insert attribute="windowContent" ignore="true"/>
    </div>

    <div class="windowFooter">
      <jsp:include page="/pages/generic/tiles/portalFooter.jsp"/>
    </div>
  </div>
</div>

</body>
</html>
