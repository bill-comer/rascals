function allowNavigationAwayDetail()
{
  var changeCountField = document.getElementById('changeCount');
  if (changeCountField != null && changeCountField.value > 0)
  {
    return false;
  }
  return true;
}

function navigateAwayFromApplicationTabs(urlClicked)
{
  var changeCountField = document.getElementById('changeCount');
  if (changeCountField != null && changeCountField.value > 0)
  {
  openBeforeNavigationDialog(urlClicked, 'false', 'false', 'false');
  }
  else
  {
    navigateToUrl(urlClicked);
  }
}

function nagivateAwayFromDetailTabs(urlClicked)
{
  var changeCountField = document.getElementById('changeCount');
  if (changeCountField != null && changeCountField.value > 0)
  {
  openBeforeNavigationDialog(urlClicked, 'true', 'false', 'false');
  }
  else
  {
  submitNavigation(applicationTabUrl, 'true', 'false');
  }
}

function navigateToUrl(urlToNavigateTo)
{
  window.location.href = urlToNavigateTo;
}

function openWindow(urlToOpen, windowWidth, windowHeight, winAttributes)
{
  var windowLeft = (screen.width - windowWidth) / 2;
  var windowTop = (screen.height - windowHeight) / 2;

  if (windowLeft < 0)
  {
    windowWidth = screen.width;
    windowLeft = 0;
  }
  if (windowTop < 0)
  {
    windowHeight = screen.height;
    windowTop = 0;
  }

  var win = window.open(urlToOpen, '', 'width=' + windowWidth + ', height=' + windowHeight + ', left=' + windowLeft + ', top=' + windowTop + ', ' + winAttributes);
  win.resizeTo(windowWidth, windowHeight);
  win.focus();
}

