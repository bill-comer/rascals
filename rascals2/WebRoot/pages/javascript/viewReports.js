function getDateNow()
{
  return new Date();
}

function roundNumber(num, dec) {
  var result = Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
  return result;
}

function getNumMonthsFrom(fromDate, currentTimeMillis)
{
    var splitdate = fromDate.split("/");
    var nowMillis = new Number(currentTimeMillis);
    var now;
    if (isNaN(nowMillis) || nowMillis == undefined)
    {
      now = getDateNow();
    }
    else
    {
      if (nowMillis > 0)
      {
        now = new Date(nowMillis);
      }
    }

    var selected = new Date(splitdate[2], splitdate[1] - 1, splitdate[0]);
    var diff_months = (now.getTime() - selected.getTime()) /(1000 * 60 * 60 * 24 * 30);

    return roundNumber(diff_months, 2)
}

function viewDpiFileData() {
  $("#parmsReportCommand\\.requestedAction").val('viewDpiFileData');
  $("#parmsViewDpiReport").submit();
}

function viewReportForId(type) {

  document.getElementById('requestedAction').value = type;
    $("#parmsViewDpiReport").submit();
}

function requestReplication()
{
  document.getElementById('replicateRequest').value = true;
}

function viewReport(type) {
  if (selectedRow == -1)
  {
      alert("You must select a row first");
      return;
  }

  var id = selectedRow;
  var identifierIndex = 0;
  var currentIdentifierField = document.getElementById('id_' + id + '_'
      + identifierIndex);
  while (currentIdentifierField != null) {
     document.getElementById('identifier[' + identifierIndex + ']').value = currentIdentifierField.value;
     identifierIndex++;
     currentIdentifierField = document.getElementById('id_' + id + '_'
    + identifierIndex);
     }


  document.getElementById('requestedAction').value = type;
  $("#parmsViewDpiReport").submit();
}

var selectedRow = -1;
var selectedId = -1;
var oldId = -1;

function selectRow(id)
{

  if (oldId != -1)
  {
    document.getElementById('item_' + oldId).style.backgroundColor = "white";
  }

  oldId = id;

  var identifierIndex = 0;
  var currentIdentifierField = document.getElementById('id_' + id + '_' + identifierIndex);

  selectedId = currentIdentifierField.value;
  selectedRow = id;

  document.getElementById('item_' + id).style.backgroundColor = "#EEEEEE";
}

function redirect(url, record_id_field_name)
{
  var $selected_record_id_el = $('#' + record_id_field_name);

  if ($selected_record_id_el.val() == '' || $selected_record_id_el.val() == '0')
  {
    alert("Invalid row id: " + $selected_record_id_el.val());
    return;
  }

  var id = $selected_record_id_el.val();
  window.location.href = url + '?' + record_id_field_name + '=' + id;
  return;
}

function redirect2(url, record_id_field_name, recordId)
{
  window.location.href = url + '?' + record_id_field_name + '=' + recordId;
  return;
}