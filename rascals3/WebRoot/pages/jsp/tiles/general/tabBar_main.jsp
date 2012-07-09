<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<script type="text/javascript">
function bindTabBarFunctions()
{
  $(".menu_group").hover(
    function()
    {
      $(this).css("text-decoration", "underline");
      $(this).css("cursor", "hand");
      var pos = $(this).position();
      $("#" + this.id + "Sub").css("display", "block").css( { "left": (pos.left) + "px", "top":(pos.top + 15) + "px" } );
      $("#" + this.id + "Subiframe").css("display", "block").css( { "left": (pos.left) + "px", "top":(pos.top + 15) + "px" } ).css("height", $("#" + this.id + "Sub").height());
    }
    ,
    function()
    {
      $(this).css("text-decoration", "none");
      var id = "#" + this.id + "Sub";
      setTimeout(function(){hideMe(id)}, 75);
    });

  $(".menu_div").hover(function()
    {
      hovering = true;
    },function()
    {
    $("#" + this.id).css("display", "none");
    $("#" + this.id + "iframe").css("display", "none");
    hovering = false;
    });
}
function hideMe(idToHide)
{
  if (!hovering)
  {
  $(idToHide).css("display", "none");
  $(idToHide + "iframe").css("display", "none");
  }
}
</script>


<style type="text/css">
.menu_div {
  position: absolute;
  border-style: solid;
  border-width: 1px;
  border-color: black;
  background-color: white;
  padding: 5px 0 5px 5px;
}

.menu_header {
  font-weight: bold;
  color: #333333;
  padding-bottom: 10px;
}

.menu_link {
  font-weight: normal;
  color: #333333;
}

.menu_link:HOVER {
  font-weight: normal;
  color: #3BA763;
  cursor: hand;
}

.menu_item {
  border-top: 1px solid #CDCDCD;
  text-indent: 10px;
  padding-top: 4px;
  padding-bottom: 4px;
}

.menu_item_last {
  border-bottom: 1px solid #CDCDCD;
}

.menu_pipe {
  color: gray;
  font-size: 10px;
}

.menu_group {
  font-size: 10px;
  color: #333333;
  padding: 0 5px 0 5px;
}

.menu_group_no_access {
  font-size: 10px;
  color: grey;
  padding: 0 5px 0 5px;
}

.menu_group_no_hover {
  font-size: 10px;
  color: #333333;
  padding: 0 5px 0 5px;
}

.menu_container {
  border-top: solid;
  border-width: 1px;
  border-color: #343434;
  padding-top: 0px;
  background-image: url('pages/images/degrade.gif')
}

.menu_iframe {
  position: absolute;
  border: none;
  z-index: 0;
}
</style>

<div class="menu_container tabBarContainer">

<table style="float: right;">
  <tr>
    <td class="menu_group_no_hover">
      <a id="clock" style="color: #3BA763;"></a>
    </td>
  </tr>
</table>

<table style="float: left;">
  <tr>
    <td class="menu_pipe">|</td>
    <td class="menu_group" id="galaMenu"> <spring:message code="tab.gala"/> </td><td class="menu_pipe">|</td>
    <td class="menu_group" id="swimmerMenu"> <spring:message code="tab.swimmer"/> </td><td class="menu_pipe">|</td>
    <td class="menu_group"><a onclick="navigateAwayFromApplicationTabs('logout.htm');"><spring:message code="tab.logout" /> </a></td>
    <td class="menu_pipe">|</td>
  </tr>
</table>


   <iframe id="galaMenuSubiframe" class="no_display menu_iframe" scrolling="no" frameborder="0" src="javascript:'&lt;html&gt;&lt;/html&gt;';" style="width:200px;"></iframe>

   <div id="galaMenuSub" class="menu_div no_display" style="width: 200px;">
     <table style="width:100%;">
      <tr><th class="menu_header" align="left"><img src="pages/images/rightarrowBlack.png"/> <spring:message code="tab.gala.web.application"/> </th></tr>

      <tr>
         <td class="menu_item">
           <img src="pages/images/rightarrow35.gif"/>
               <a class="menu_link" onclick="navigateAwayFromApplicationTabs('createGala.htm');"> <spring:message code="tab.create.new.gala"/> </a>
        </td>
       </tr>

       <tr>
         <td class="menu_item">
           <img src="pages/images/rightarrow35.gif"/>
           <a class="menu_link" onclick="navigateAwayFromApplicationTabs('listGalas.htm');"> <spring:message code="tab.list.gala"/> </a>
         </td>
       </tr>

      </table>
   </div>
   
   
   <iframe id="swimmerMenuSubiframe" class="no_display menu_iframe" scrolling="no" frameborder="0" src="javascript:'&lt;html&gt;&lt;/html&gt;';" style="width:200px;"></iframe>

   <div id="swimmerMenuSub" class="menu_div no_display" style="width: 200px;">
     <table style="width:100%;">
      <tr><th class="menu_header" align="left"><img src="pages/images/rightarrowBlack.png"/> <spring:message code="tab.swimmer.web.application"/> </th></tr>

      <tr>
         <td class="menu_item">
           <img src="pages/images/rightarrow35.gif"/>
               <a class="menu_link" onclick="navigateAwayFromApplicationTabs('createSwimmer.htm');"> <spring:message code="tab.create.new.swimmer"/> </a>
        </td>
       </tr>

       <tr>
         <td class="menu_item">
           <img src="pages/images/rightarrow35.gif"/>
           <a class="menu_link" onclick="navigateAwayFromApplicationTabs('listBoySwimmers.htm');"> <spring:message code="tab.list.boy.swimmer"/> </a>
         </td>
       </tr>

       <tr>
         <td class="menu_item">
           <img src="pages/images/rightarrow35.gif"/>
           <a class="menu_link" onclick="navigateAwayFromApplicationTabs('listGirlSwimmers.htm');"> <spring:message code="tab.list.girl.swimmer"/> </a>
         </td>
       </tr>
       

      </table>
   </div>



  




</div>