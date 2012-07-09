function calculateCurrentDate()
{
  var currentDate = getCurrentDate();
  var clock = $("#clock");
  clock.html(getDateString(currentDate) + "&nbsp;&nbsp;&nbsp;" + getTimeString(currentDate));
  setTimeout("calculateCurrentDate();", 1000);
}

function getCurrentDate()
{
  var currentDate;
  var fixedCurrentTimeMillisEl = window.document.getElementById('fixedCurrentTimeMillis');
  var fixedCurrentTimeMillis;
  var fixedCurrentTimeMillis = fixedCurrentTimeMillisEl != undefined ? new Number(fixedCurrentTimeMillisEl.value) : new Number(0);

  if (!isNaN(fixedCurrentTimeMillis))
  {
    if (fixedCurrentTimeMillis > 0)
    {
      currentDate = new Date(fixedCurrentTimeMillis);
    }
  }

  if (currentDate == undefined)
  {
    currentDate = new Date();
  }

  return currentDate;
}

function getDateString(currentDate)
{
  var day = makeTwoDigit(currentDate.getDate());
  var month = makeTwoDigit(currentDate.getMonth() + 1);
  var year = makeTwoDigit(currentDate.getFullYear());

  return day + "/" + month + "/" + year;
}

function makeTwoDigit(value)
{
  if (value <= 9)
  {
    return "0" + value;
  }
  return value;
}

function getTimeString(currentDate)
{
  var hours = currentDate.getHours();
  var minutes = currentDate.getMinutes();

  var symbol = "AM";

  if (hours >= 12)
  {
   symbol = "PM";
  }

  if (hours > 12)
  {
    hours = hours - 12;
  }

  if (hours == 0)
  {
    hours = 12;
  }

  return hours + ":" + makeTwoDigit(minutes) + " " + symbol;
}
