<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


  
<div id="titlebar" style="width:100%;height:35px;">
   <div class="icon" style="width:100%;height:35px;">
      <table width="100%" height="35px">
         <tr>
            <td><img style="cursor: default;" src="pages/generic/images/AFMS_button.jpg" width="25" height="25"/></td>
            <td width="100%"><span class="subtitletext">&nbsp; <spring:message code="product.title"/> (Version:${sessionScope.version}) </span></td>
            <td width="2%" align="right"><img alt="www.utilisoft.co.uk" src="pages/generic/images/utilisoft_logo_icon.png" style="cursor:pointer;"
                    onclick="window.location='http://www.utilisoft.co.uk'" width="25" height="25"/></td>
         </tr>
      </table>
   </div>
</div>