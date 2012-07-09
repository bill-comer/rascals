<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>

  <head>
    <title><spring:message code="product.title"/></title>

    <script type="text/javascript" src="pages/generic/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="pages/generic/jquery/jquery-ui-1.8.9.custom.min.js"></script>
    <script type="text/javascript" src="pages/generic/jquery/jquery.form.js"></script>
    <script type="text/javascript" src="pages/generic/jquery/ui.core.js"></script>

    <script type="text/javascript" src="pages/generic/javascript/button.js"></script>
    <script type="text/javascript" src="pages/generic/javascript/clock.js"></script>
    <script type="text/javascript" src="pages/generic/javascript/navigation.js"></script>

    <link href="pages/generic/style/titleBar.css" rel="stylesheet" type="text/css"/>
    <link href="pages/generic/style/tabBar.css" rel="stylesheet" type="text/css"/>
    <link href="pages/generic/style/detail.css" rel="stylesheet" type="text/css"/>
        <link href="pages/generic/style/general.css" rel="stylesheet" type="text/css"/>

  </head>

  <script type="text/javascript">
    $(document).ready(function(){
      calculateCurrentDate();
    });
  </script>

  <tiles:insert attribute="bodyHeader"/>
  <tiles:insert attribute="title"/>
  <tiles:insert attribute="tabBar"/>
  <tiles:useAttribute name="portal_name" scope="session"/>

  <div id="overall" style="width:100%;position:absolute;top:70px;" align="center">

    <div style="width:600px;background-color:white;margin-left:auto;margin-right:auto;">

      <c:set var="portalName" value="${portal_name}" scope="session"/>
      <jsp:include page="/pages/generic/tiles/portalHeader.jsp"/>

      <tiles:insert attribute="windowContent"/>

      <jsp:include page="/pages/generic/tiles/portalFooter.jsp"/>

    </div>

  </div>

</html>
