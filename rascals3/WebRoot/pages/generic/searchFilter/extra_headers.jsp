<script type="text/javascript">
var isLoading = false;

function expand(myIndex, recordId)
{
  if (isLoading) return;
  var myElement =  $("#expander" + myIndex);
  if (myElement.attr("src").match(/right.png$/))
  {
    $("#expand_row" + myIndex).css("display", "inline");
    $("#imageLoad" + myIndex).removeClass("no_display").addClass("display");

    var source =  myElement.attr("src").replace("right", "down");
    myElement.attr("src", source);

    //Submit a form and go and get the
    isLoading = true;
    getRecordInnerData("#expand_row" + myIndex, recordId);
  }
  else
  {
    $("#expand_row" + myIndex).css("display", "none");
    myElement.attr("src", myElement.attr("src").replace("down", "right"));
    var replaceImage = "<td id=\"imageLoad" + myIndex + "\" class=\"no_display\"  colspan=\"10\"><img src=\"pages/images/loading.gif\"/></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>";
    $("#expand_row" + myIndex).html(replaceImage);
  }
}

function getRecordInnerData(myTarget, recordId)
{
  $("#act").val("getRecordInnerData");
  $("#hiddenRecordId").val(recordId);
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target: myTarget,
    success: function (){isLoading = false}
  });
  $("#searchForm").submit();
}
</script>

<th>&nbsp;</th>