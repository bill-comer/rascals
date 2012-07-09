


function highlightRow(selection, action)
{
  var id = document.getElementById(selection);
  var currentClass = id.className;
  if (action == "roll")
  {
    if (currentClass != "select")
    {
      id.className = action;
    }
  }
  if (action == "over")
  {
    if (currentClass != "select")
    {
      id.className = action;
    }
  }
}

function clickedRow(selection, type)
{
  var selectedElement = document.getElementById(selection);
  selectedElement.className="select";

  var creationLoc = type + 'Creation.htm?' + type + 'Type=';
  location.href = creationLoc + selection;
}

function goToMarketUserAdmin()
{
  location.href = 'marketUserList.htm';
}

function goToMarketResourceAdmin()
{
  location.href = 'marketResourceList.htm';
}

function goToParticipantAdmin()
{
  location.href = 'participantList.htm';
}
