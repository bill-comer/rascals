function parseIdForJQuery(fieldId)
{
  // Remove full stops
  var result = "";
  var remainingString = fieldId;
  var currentIndex;
  while (remainingString.indexOf(".") != -1)
  {
	  currentIndex = remainingString.indexOf(".");
	  result += remainingString.substring(0, currentIndex) + "\\.";
	  remainingString = remainingString.substring(currentIndex + 1);
  }
  result += remainingString;

  // Remove open square brackets
  currentIndex = 0;
  remainingString = result;
  result = "";
  while (remainingString.indexOf("[") != -1)
  {
	  currentIndex = remainingString.indexOf("[");
	  result += remainingString.substring(0, currentIndex) + "\\[";
	  remainingString = remainingString.substring(currentIndex + 1);
  }
  result += remainingString;

  // Remove closed square brackets
  currentIndex = 0;
  remainingString = result;
  result = "";
  while (remainingString.indexOf("]") != -1)
  {
	  currentIndex = remainingString.indexOf("]");
	  result += remainingString.substring(0, currentIndex) + "\\]";
	  remainingString = remainingString.substring(currentIndex + 1);
  }
  result += remainingString;

  return "#" + result;
}

function openExternalLinkImageHover(id)
{
  if ($("#" + id).attr("src") == 'pages/images/openExternalLink.png')
  {
	$("#" + id).attr("src", "pages/images/openExternalLinkHover.png");
  }
  else
  {
	$("#" + id).attr("src", "pages/images/openExternalLink.png");
  }
}

function selectedTabHover(id)
{
  if ($("#" + id).css("color") == "#3ba763")
  {
	$("#" + id).css("color", "#4ccb7c");
  }
  else
  {
	$("#" + id).css("color", "#3ba763");
  }
}

function scrollToCorrectPosition()
{
  if ($("#scrollBarOffset").length > 0)
  {
	if ($("#scrollBarOffset").val() != "")
	{
	  scrollTo(0, $("#scrollBarOffset").val());
	}
  }
}

function getYBarOffset()
{
  var offset = 0;

  if (typeof(window.pageYOffset) == 'number')
  {
    // Netscape compliant
	offset = window.pageYOffset;
  }
  else if (document.body && (document.body.scrollLeft || document.body.scrollTop))
  {
    //DOM compliant
	offset = document.body.scrollTop;
  }
  else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop))
  {
    // IE6 standards compliant mode
	offset = document.documentElement.scrollTop;
  }
  return offset;
}
