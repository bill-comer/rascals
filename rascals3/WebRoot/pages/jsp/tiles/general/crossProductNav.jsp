<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<link href="pages/style/cross_product.css"
      rel="stylesheet" type="text/css"/>

<c:set var="open_comment" value="<!--"/>
<c:set var="close_comment" value="-->"/>

<jsp:useBean
  id="crossProductHelper"
  class="uk.co.formfill.springutils.web.util.CrossProductPropertyHelper" />
<%
  int noOfProducts = crossProductHelper.getNoOfProducts();
  if (noOfProducts > 0)
  {
    int width = 72 + ((noOfProducts - 1) * 30);
    pageContext.setAttribute("middle_size", width + "px",
                         PageContext.REQUEST_SCOPE);
  }
  pageContext.setAttribute("no_of_products", noOfProducts,
                       PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("products_details",
                           crossProductHelper.getCrossProductDetails(),
                       PageContext.REQUEST_SCOPE);
%>

<div class="cross_proj_nav_div">
  <div class="cross_proj_nav_div_layout">
    <div class="middle" style="width: ${middle_size}">
      <div class="cross_proj_nav_div_layout_left"></div>
      <div class="right"></div>
    </div>
  </div>

  <div class="product_logos">

   <!-- The forEach tag here is very nastily formatted but this is required to
   achieve no spacing between the images. Please do not rearrange or remove the
   comment variables. Any questions see AV. -->

   <c:forEach var="detail" items="${products_details}" varStatus="status">
      <c:if test="${status.index > 0}">
        ${close_comment}<img src="pages/images/cross_product/icons_join.gif"
        ></c:if
        ><a href="${detail.url}"
            <c:if test="${detail.newWindow}">target="_blank"</c:if>
        ><img src="${detail.image}" alt="${detail.name}"
              title="${detail.name}"></a>${open_comment}
    </c:forEach>
    <c:if test="${no_of_products != null && no_of_products > 0}">
    ${close_comment}
    </c:if>

    <img alt="www.utilisoft.co.uk" title="www.utilisoft.co.uk" src="pages/images/utilisoft_logo_icon.png" style="cursor:pointer;"
                    onclick="window.location='http://www.utilisoft.co.uk'" width="25" height="25"/>
  </div>
</div>