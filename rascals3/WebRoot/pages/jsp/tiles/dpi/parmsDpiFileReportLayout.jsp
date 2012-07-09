<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<html>
<head>
<title><spring:message code="product.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<link rel="shortcut icon" href="/pages/images/favicon.ico">
<link rel="icon" href="/pages/images/animated_favicon.gif" type="image/gif">

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
<script type="text/javascript" src="pages/javascript/rollOver.js"></script>
<script type="text/javascript" src="pages/javascript/general.js"></script>

<!-- Application Style Sheets -->
<link href="pages/style/detail.css" rel="stylesheet" type="text/css" />
<link href="pages/style/titleBar.css" rel="stylesheet" type="text/css" />
<link href="pages/style/tabBar.css" rel="stylesheet" type="text/css" />
<link href="pages/style/tabbedFilter.css" rel="stylesheet" type="text/css" />
<link href="pages/style/tableSorter.css" rel="stylesheet" type="text/css" />

<!-- Extra Javascripts -->
<script type="text/javascript" src="pages/javascript/admin.js"></script>

<!-- Extra Style Sheets -->
<link href="pages/style/admin.css" rel="stylesheet" type="text/css" />
<link href="pages/style/parms.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
  var hovering = false;

  $(document).ready(function(){
    bindTabBarFunctions();
    resizeWindowContent();
  });

  function resizeWindowContent()
  {
    var winContentTop = $('#windowContent').position().top;
    var winFooterTop = $('#windowFooter').position().top;
    var windowContentHeight = winContentTop - winFooterTop;
    if (windowContentHeight < 0)
    {
      windowContentHeight = windowContentHeight * -1;
    }
    $('#windowContent').css('height', windowContentHeight + 'px');
  }

  $(window).resize(function() {
    var winContentTop = $('#windowContent').position().top;
    var winFooterTop = $('#windowFooter').position().top;
    var windowContentHeight = winContentTop - winFooterTop;
    if (windowContentHeight < 0)
    {
      windowContentHeight = windowContentHeight * -1;
    }
    $('#windowContent').css('height', windowContentHeight + 'px');
  });


</script>

</head>

<tiles:insert attribute="title"/>
<tiles:insert attribute="tabBar"/>

<!-- add fixed current time -->
<jsp:include page="/pages/jsp/tiles/general/fixCurrentTime.jsp"/>

<body onload="sizeColumns();sizeWindow();calculateCurrentDate();" style="background-color: #EFEFEF;">

<div id="overall" style="width: 100%; position: absolute; top: 70px;
                         padding: 0;
                         margin: 0;
                         overflow: hidden;
                         height: 100%;"/>
  <div id="Window" class="window" style="width: 50%; margin-left: auto; margin-right: auto;
                                         margin-top: 0;
                                         margin-bottom: 0;
                                         padding: 0;
                                         height: 100%;
                                         position: relative;
                                         overflow:hidden;" align="center">

    <div id="windowContentOuter" style="padding-top: 145px;
                                        position: absolute;
                                        height: 100%;
                                        width: 100%;
                                        top: 0px;
                                        -ms-box-sizing: border-box;
                                        -moz-box-sizing: border-box;
                                        -webkit-box-sizing: border-box;">

    <div id="windowContent" class="windowContent" style="overflow: auto;height: 100%;
                                      background-color: white;">
      <tiles:insert attribute="content" ignore="true" />
    </div>
    </div>

    <div class="windowHeader" style="background-color: white;padding-bottom: 5px;
                                     top: 0px;
                                     position: absolute;
                                     height: 140px;">
      <c:set var="portalName" value="${title}" scope="session"/>
      <jsp:include page="/pages/jsp/tiles/general/portalHeader.jsp"/>

      <tiles:insert attribute="headerContent" ignore="true" />
    </div>

    <div id="windowFooter" class="windowFooter" style="background-color: white;padding-top: 0px;
                                     position: absolute;
                                     bottom: 75px;
                                     width: 100%;
                                     height: -200px;">
      <tiles:insert attribute="footerContent" ignore="true" />
      <jsp:include page="/pages/jsp/tiles/general/portalFooter.jsp"/>
    </div>
  </div>
</div>

</body>
</html>