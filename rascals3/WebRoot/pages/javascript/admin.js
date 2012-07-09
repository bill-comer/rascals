function unselect(elementId) {
  var element = document.getElementById(elementId);
  if (element != null) {
    for (i = 0; i < element.length; i++) {
      element.options[i].selected = false;
    }
  }
}


function move(fromElementId, toElementId) {
  var from = document.getElementById(fromElementId);
  var to = document.getElementById(toElementId);

  for (i = 0; i < from.length; i++) {
    if (from.options[i].selected == true) {
      to.options[to.length] = new Option(from.options[i].text,
          from.options[i].value, false, false);
      to.options[to.length - 1].title = from.options[i].text;
    }
  }

  for (j = from.length; j > 0; j--) {
    if (from.options[j - 1].selected == true) {
      from.options[j - 1] = null;
    }
  }
}

function sizeWindow(nameParam) {
  var name = '';
  var columnWidthName = 'columnWidth_';
  if (nameParam != null) {
    columnWidthName = nameParam + "_" + columnWidthName;
    name = nameParam;
  }

  var index = 0;
  var totalWidth = 40, lastWidth;
  var currentColumnTitle, currentColumnValue;
  var currentColumnWidth = document.getElementById(columnWidthName + index);
  while (currentColumnWidth != null) {
    lastWidth = currentColumnWidth.value;
    totalWidth += parseInt(currentColumnWidth.value);
    index++;
    currentColumnWidth = document.getElementById(columnWidthName + index);
  }

  var window = document.getElementById(name + "Window");
  window.style.width = totalWidth;
}

function sizeWindows() {
  sizeWindow('adminList1');
  sizeWindow('adminList2');
}

function sizeColumns(name) {
  var columnWidthName = 'columnWidth_';
  var columnTitleName = 'columnTitle_';
  var columnValueName = 'columnValue_';
  // var tableName = 'table';
  // var tableInnerName = 'tableInner';
  // var listHeaderDivName = 'listHeaderDiv';
  var listHeaderTableName = 'myTable';
  // var listBodyDivName = 'listBodyDiv';
  // var listBodyTableName = 'listBodyTable';
  // var footerTableName = 'footerTable';

  if (name != null) {
    columnWidthName = name + "_" + columnWidthName;
    columnTitleName = name + "_" + columnTitleName;
    columnValueName = name + "_" + columnValueName;
    // tableName = name + "_" + tableName;
    // tableInnerName = name + "_" + tableInnerName;
    // listHeaderDivName = name + "_" + listHeaderDivName;
    listHeaderTableName = name + "_" + listHeaderTableName;
    // listBodyDivName = name + "_" + listBodyDivName;
    // listBodyTableName = name + "_" + listBodyTableName;
    // footerTableName = name + "_" + footerTableName;
  }

  var index = 0, innerIndex;
  var totalWidth = 0;
  var currentColumnTitle, currentColumnValue;
  var currentColumnWidth = document.getElementById(columnWidthName + index);
  var lastWidth = 0;
  while (currentColumnWidth != null) {
    lastWidth = currentColumnWidth.value;
    currentColumnTitle = document.getElementById(columnTitleName + index);

    innerIndex = 0;
    currentColumnValue = document.getElementById(columnValueName
        + innerIndex + '_' + index);
    while (currentColumnValue != null) {
      currentColumnValue.style.width = currentColumnWidth.value;
      innerIndex++;
      currentColumnValue = document.getElementById(columnValueName
          + innerIndex + '_' + index);
    }

    currentColumnTitle.style.width = currentColumnWidth.value;

    if (!isNaN(parseInt(currentColumnWidth.value)))
    {
      totalWidth += parseInt(currentColumnWidth.value);
    }

    index++;
    currentColumnWidth = document.getElementById(columnWidthName + index);
  }

  // var listHeaderDiv = document.getElementById(listHeaderDivName);
  // listHeaderDiv.style.width = totalWidth;

  var listHeaderTable = document.getElementById(listHeaderTableName);
  listHeaderTable.style.width = totalWidth;

  // var listBodyDiv = document.getElementById(listBodyDivName);
  // listBodyDiv.style.width = totalWidth + 17;

  // var listBodyTable = document.getElementById(listBodyTableName);
  // listBodyTable.style.width = totalWidth;

  // var footerTable = document.getElementById(footerTableName);
  // footerTable.style.width = totalWidth;
}

function viewItem(id) {

  var identifierIndex = 0;
  var currentIdentifierField = document.getElementById('id_' + id + '_'
      + identifierIndex);
  while (currentIdentifierField != null) {
    document.getElementById('identifier[' + identifierIndex + ']').value = currentIdentifierField.value;
    identifierIndex++;
    currentIdentifierField = document.getElementById('id_' + id + '_'
        + identifierIndex);
  }

  document.items.submit();
}

function newItem() {
  document.items.submit();
}

function sizeMultiColumns() {
  sizeColumns('adminList1');
  sizeColumns('adminList2');
}

function cancel() {
  $("#adminCommand\\.adminRequestedAction").val('cancel');
  $("#adminForm").submit();
}

function save() {
  $("#adminCommand\\.adminRequestedAction").val('save');
  $("#adminForm").submit();
}

function deleteCurrent() {
  $("#adminCommand\\.adminRequestedAction").val('delete');
  $("#adminForm").submit();
}

function loginSubmit() {
  document.loginform.submit();
}

function submitForm(myDiv, e) {
  var keycode;

  if (window.event)
    keycode = window.event.keyCode;

  else if (e)
    keycode = e.which;

  else
    return true;

  if (keycode == 13) {
    document.loginform.submit();
  }
}
