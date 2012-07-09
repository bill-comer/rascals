<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<form>
  <div id="standAloneSearch">
    <div id="searchBox">
      <div id="searchControls">
        <div class="box1TopLeft"><div class="box1TopRight"></div></div>
        <div class="box2Body">
          <div id="fragment-1">

            <div id="loginControls" style="padding:20px 0px 20px 30px;">
              <label>
                <spring:message code="errors.insufficient.authorities"/>
              </label>
            </div>
          </div>
        </div>
        <div class="box2BtmLeft"><div class="box2BtmRight"></div></div>
      </div>
    </div>
  </div>
</form>
