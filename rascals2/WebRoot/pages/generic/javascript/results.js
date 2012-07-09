/**
 * Calculates the paging for the results display and writes it out to the Label with Id 'paging'
 * @param totalRecords Total number of records found
 * @param recordsDisplaying Number of records currently displaying
 * @param currentPage Current page that is on display
 */
function calculatePaging(totalRecords, recordsDisplaying, currentPage)
{
  var htmlOut = '';
  var numberOfPages = Math.ceil(totalRecords / recordsDisplaying);
  //Add the left arrow if we are not on page 1 / there is only one page
  if (numberOfPages > 1 && currentPage > 1)
  {
    htmlOut = ' <img onclick="moveToPage(' + (currentPage - 1) + ')" src="pages/generic/images/previous-page.png"/> ';
  }
  //If lots of pages, too many to list them all, list first, last, and 2 either side of current page
  if (numberOfPages > 10)
  {
    if (currentPage > 1)
      htmlOut = htmlOut + '<label class="paging" onclick="moveToPage(' + 1 + ')">' + 1 + ' </label>';
    if (currentPage > 2)
      htmlOut = htmlOut + '...';

    if (currentPage > 3)
      htmlOut = htmlOut + '<label  class="paging" onclick="moveToPage(' + (currentPage - 2) + ')">' + (currentPage - 2) + ' </label>';
    if (currentPage > 2)
      htmlOut = htmlOut + '<label  class="paging" onclick="moveToPage(' + (currentPage - 1) + ')">' + (currentPage - 1) + ' </label>';

    htmlOut = htmlOut + '<b style="font-size:12px;">'  + currentPage + ' </b>';//Current page bold and non clickable

    if (currentPage < numberOfPages - 1)
      htmlOut = htmlOut + '<label  class="paging" onclick="moveToPage(' + (currentPage + 1) + ')">' + (currentPage + 1) + ' </label>';
    if (currentPage < numberOfPages - 2)
      htmlOut = htmlOut + '<label  class="paging" onclick="moveToPage(' + (currentPage + 2) + ')">' + (currentPage + 2) + ' </label>';
    if (currentPage < numberOfPages)
      htmlOut = htmlOut + '...<label class="paging" onclick="moveToPage(' + numberOfPages + ')">' + numberOfPages + ' </label>';
  }
  //If only a few pages, list them all
  else
  {
    for (var i=1; i<=numberOfPages; i++)
    {
      if (i==currentPage)
        {
          htmlOut = htmlOut + '<b style="font-size:12px;">'  + i + ' </b>';//Current page bold and non clickable
        }
      else
      {
        htmlOut = htmlOut + '<label class="paging" onclick="moveToPage(' + i + ')">' + i + ' </label>';
      }
    }
  }
  //End Arrow
  if (numberOfPages > 1 && currentPage < numberOfPages)
  {
    htmlOut = htmlOut + ' <img onclick="moveToPage(' + (currentPage + 1) + ')" src="pages/generic/images/next-page.png"/> ';
  }
  if (document.getElementById('paging') != null)
  document.getElementById('paging').innerHTML = htmlOut;
}
//Sort table
function sortTable(mIndex, columnToSort)
{
  showLoading();

  if ($("#header" + mIndex).hasClass("desc"))
  {
    newOrder = columnToSort + ' asc';
    $("#header" + mIndex).removeClass("desc").addClass("asc");
  }
  else if ($("#header" + mIndex).hasClass("asc"))
  {
    newOrder = columnToSort + ' desc';
    $("#header" + mIndex).removeClass("asc").addClass("desc");
  }
  else
  {
    newOrder = columnToSort + ' asc';
    $("#header" + mIndex).removeClass("desc").addClass("asc");
  }

  $("#sortList").val(newOrder);
  $("#searchForm > #act").val("sort");
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#resultsTable',
    success: showResponse,
    error: function() { alert("failed sortTable()!"); }
  });
  $("#searchForm").submit();
}
//Update number of records per page
function updateRecordsPerPage()
{
  showLoading();

  $("#searchForm > #act").val("changePerPage");
  $("#recordsPerPage").val($("#recordsPerPageSelect").val());
  refreshResults();
}
//Updates the count after a filter has been completed
function updateCount()
{
  $("#count_load_img").removeClass("no_display").addClass("display");
  $("#count_display").addClass("no_display");
  $("#searchForm > #act").val("count");
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#filterDiv',
    success: bindValidation
   });
  $("#searchForm").submit();
}
//Move page
function moveToPage(pageToMoveTo)
{
  $("#searchForm > #act").val("page");
  $("#recordsPerPage").val($("#recordsPerPageSelect").val());
  $("#currentPage").val(pageToMoveTo);
  refreshResults();
}
//Clears the filter
function clearFilter()
{
  $("#sortList").val("");
  $("#searchForm > #act").val("clear");
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#filterDiv',
    success: function(){updateResults('apply');showResponse();}
  });
  $("#searchForm").submit();

//  $("#resultsFoundDiv").addClass("no_display").removeClass("display");
//  $("#resultsNotFoundDiv").addClass("display").removeClass("no_display");

}
//Show the response
function showResponse()
{
//    bindValidation();
//    dragtable.init(true);//Initialise drag tables
}

function submitFilterForm()
{
  $("#searchForm > #act").val("addSearch");
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#filterDiv',
    success: bindValidation,
    error: function() { alert("failed submitFilterForm()!"); }
  });
  $("#searchForm").submit();
}

function bindValidation()
{
  $(".date").change(function () {
    var RegEx = /^((((0?[1-9]|[12]\d|3[01])[\/](0?[13578]|1[02])[\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|[12]\d|30)[\/](0?[13456789]|1[012])[\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|1\d|2[0-8])[\/]0?2[\/]((1[6-9]|[2-9]\d)?\d{2}))|(29[\/]0?2[\/]((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))|(((0[1-9]|[12]\d|3[01])(0[13578]|1[02])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|[12]\d|30)(0[13456789]|1[012])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|1\d|2[0-8])02((1[6-9]|[2-9]\d)?\d{2}))|(2902((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00))))$/;
    if  (!($(this).val()).match(RegEx) ||  ($(this).val()).length != 10 )
    {
    $(this).addClass("inputError");
    }
    else
    {
      updateCount();
    }
     });
  $(".number").change(function () {

    if(isNaN(parseFloat($(this).val())))
    {
      $(this).addClass("inputError");
    }
    else
    {
      updateCount();
    }});
}

function updateResults(actionToDo)
{
  showLoading();

  $("#searchForm > #act").val(actionToDo);
  if ($("#recordsPerPageSelect").val() != null)
  {
    $("#recordsPerPage").val($("#recordsPerPageSelect").val());
  }

  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#tempResultsTable',
    success: function()
    {
      //updateCount();
      hasBindErrors();
    },
    error: function(request, error)
    {
      alert("failed updateResults()");
    }
  });
  $("#searchForm").submit();
}

//Action MUST be set before calling this, 'page', 'apply' etc.
function refreshResults()
{
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target:'#tempResultsTable',
    success: hasBindErrors,
    error: function(request, error)
    {
      alert("failed refreshResults()");
    }
   });
  $("#searchForm").submit();
}

function hasBindErrors()
{
  if ( $("#CONTAINS_BIND_ERRORS").val() == 'true')
  {
    //$("#tempResultsTable").hide();
    //contains errors, do something
    $("#filterDiv").html($("#tempResultsTable").html());
    $("#tempResultsTable").html("");
//    $("#tempResultsTable").addClass("no_display").removeClass("display");

//    $("#resultsFoundDiv").addClass("no_display").removeClass("display");
//    $("#resultsNotFoundDiv").addClass("display").removeClass("no_display");
  }
  else
  {
    $("#resultsTable").html($("#tempResultsTable").html());
    $("#tempResultsTable").html("");
    $(".inputError").each(function()
    {
      $(this).removeClass("inputError");
    });
//    $("#tempResultsTable").addClass("no_display").removeClass("display");

//    $("#resultsNotFoundDiv").addClass("no_display").removeClass("display");
//    $("#resultsFoundDiv").addClass("display").removeClass("no_display");

//    $("#errorDivWrap").addClass("no_display").removeClass("display");

    showResponse();
  }
  $("#CONTAINS_BIND_ERRORS").val("false");

  $("#loading_image").addClass("no_display").removeClass("display");
}

function getTargetDiv()
{
  return "#filterDiv";
}

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
    getRecordData("#expand_row" + myIndex, recordId);
  }
  else
  {
    $("#expand_row" + myIndex).css("display", "none");
    myElement.attr("src", myElement.attr("src").replace("down", "right"));
    var replaceImage = "<td id=\"imageLoad" + myIndex + "\" class=\"no_display\"  colspan=\"10\"><img src=\"pages/generic/images/loading.gif\"/></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>";
    $("#expand_row" + myIndex).html(replaceImage);
  }
}

function getRecordData(myTarget, recordId)
{
  $("#searchForm > #act").val("getRecordData");
  $("#hiddenRecordId").val(recordId);
  $('#searchForm').ajaxForm
  ({
    type: 'POST',
    target: myTarget,
    success: function (){isLoading=false}
  });
  $("#searchForm").submit();
}

function showLoading()
{
  $("#loading_image").removeClass("no_display").addClass("display");
// $("#resultsNotFoundDiv").removeClass("display").addClass("no_display");
// $("#resultsFoundDiv").removeClass("display").addClass("no_display");
}