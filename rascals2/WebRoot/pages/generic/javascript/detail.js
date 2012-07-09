function submitDetailForm()
{
  $("#detailRequestedAction").val('saveDetail');
  $("#detailForm").submit();
}

function applyFilter()
{
  $("#detailRequestedAction").val('apply');
  $("#detailForm").submit();
}

function submitClickedUrl(urlClicked)
{
  $("#detailRequestedAction").val('clickedUrl');
  $("#clickedUrl").val(urlClicked);
  $("#detailForm").submit();
}

function submitChangedMeter()
{
  $("#detailRequestedAction").val('changedMeter');
  $("#detailForm").submit();
}

function submitAddContact()
{
  $("#detailRequestedAction").val('addContact');
  $("#detailForm").submit();
}

function submitRemoveContact(removedIndex)
{
  $("#detailRequestedAction").val('removeContact');
  $("#removedIndex").val(removedIndex);
  $("#detailForm").submit();
}

function clearChangesContactDetailEdit()
{
  window.location.href = window.location;
}

function submitNavigation(applicationTabUrl, clickedDetailTab, changedMeter, clickedSearch)
{
  if (clickedDetailTab == 'true')
  {
	submitClickedUrl(applicationTabUrl);
  }
  else if (changedMeter == 'true')
  {
	submitChangedMeter();
  }
  else if (clickedSearch == "true")
  {
    applyFilter();
  }
  else
  {
	navigateToUrl(applicationTabUrl);
  }
}

function changedMeter()
{
  var offsetY = getYBarOffset();
  $("#scrollBarOffset").val(offsetY);

  if (allowNavigationAwayDetail())
  {
    submitChangedMeter();
  }
  else
  {
	openBeforeNavigationDialog('', 'false', 'true', 'false');
  }
}

function clickedSearchButton()
{
  if (allowNavigationAwayDetail())
  {
    applyFilter();
  }
  else
  {
	openBeforeNavigationDialog('', 'false', 'false', 'true');
  }
}

function clickedTab(urlClicked)
{
  if (allowNavigationAwayDetail())
  {
	submitNavigation(urlClicked, 'true', 'false');
  }
  else
  {
	openBeforeNavigationDialog(urlClicked, 'true', 'false', 'false');
  }
}

function fieldEdit(itemName)
{
  var newValueField = document.getElementById(itemName);
  var oldValueField = document.getElementById(itemName + '_originalValue');
  
  if (oldValueField.value == newValueField.value)
  {
	changeClassToFine(newValueField);
  }
  else
  {
    changeClassToChange(newValueField);
  }
}

function fieldFilterEdit(itemName)
{
  /*var newValueField = document.getElementById(itemName);
  var oldValueField = document.getElementById(itemName + '_originalValue');
  if (oldValueField.value == newValueField.value)
  {
	changeClassToFine(newValueField);
  }
  else
  {
    changeClassToChange(newValueField);
  }*/
}

function bookmarkedFieldEdit(item)
{
  var newValueField = item;
  var oldValueField = document.getElementById('summaryBox.bookmarked_originalValue');
  if (oldValueField.value == newValueField.checked)
  {
	changeClassToFine(newValueField);
  }
  else
  {
    var currentClassName = newValueField.className;
    if (currentClassName.indexOf("change") != -1 || currentClassName.indexOf("error") != -1)
    {
	  if (currentClassName.indexOf("_") != -1)
      {
        field.className = currentClassName.substring(0, currentClassName.indexOf("_"));
      }
      else
      {
        field.className = currentClassName;
      }
    }
  }
}

function afterFieldEdit(messageCode, typeForValidation, itemName, allowNull, excludedValues, maxPrecision, maxScale)
{
  var newValueField = document.getElementById(itemName);
  if (maxPrecision == null || maxPrecision == "")
  {
    maxPrecision = -1;
  }
  if (maxScale == null || maxScale == "")
  {
	maxScale = -1;
  }
  
  var errorMessage = jsonrpc.validationHelper.validateField(messageCode,
		                                                    typeForValidation,
		                                                    newValueField.value,
		                                                    allowNull,
		                                                    {"javaClass": "java.util.List", "list": excludedValues},
		                                                    maxPrecision,
		                                                    maxScale);

  if (errorMessage != null)
  {
	changeClassToError(newValueField);
    addError(itemName, errorMessage);
  }
  else
  {
    fieldEdit(itemName);
    removeError(itemName);
  }
}

function afterFilterFieldEdit(messageCode, typeForValidation, itemName, allowNull, excludedValues, maxPrecision, maxScale)
{
  var newValueField = document.getElementById(itemName);
  if (maxPrecision == null || maxPrecision == "")
  {
	maxPrecision = -1;
  }
  if (maxScale == null || maxScale == "")
  {
    maxScale = -1;
  }

  var errorMessage = jsonrpc.validationHelper.validateField(messageCode,
          												   typeForValidation,
          												   newValueField.value,
          												   allowNull,
          												   {"javaClass": "java.util.List", "list": excludedValues},
          												   maxPrecision,
          												   maxScale);
  
  if (errorMessage != null)
  {
	changeFilterClassToError(newValueField);
  }
  else
  {
    changeFilterClassToFine(newValueField);
  }
}

function addError(itemName, errorMessage)
{
  disableButton('saveButton');

  var firstErrorsTable = document.getElementById('firstErrorsTable');
  var otherErrorsTable = document.getElementById('otherErrorsTable');
  var errorsRow = document.getElementById('errorsRow');

  // Check if field error already exists
  var index = 0;
  var current;
  var alreadyExists = false;
  if (firstErrorsTable.rows.length > 0)
  {
	current = firstErrorsTable.rows[index];
    while (current != null)
    {
      if (current.id == itemName + "_error")
      {
        alreadyExists = true;
        break;
      }

      index++;
      current = firstErrorsTable.rows[index];
    }

    if (!alreadyExists && otherErrorsTable.rows.length > 0)
    {
      current = otherErrorsTable.rows[index];
      while (current != null)
      {
        if (current.id == itemName + "_error")
        {
          alreadyExists = true;
          break;
        }
        index++;
        current = otherErrorsTable.rows[index];
      }
    }
  }

  if (!alreadyExists)
  {
    if (firstErrorsTable.style.display == 'none') // Only error
    {
      errorsRow.style.display = 'block';
      firstErrorsTable.style.display = 'block';
      addErrorRow('firstErrorsTable', itemName, errorMessage);
    }
    else
    {
      addErrorRow('otherErrorsTable', itemName, errorMessage);
      $("#errorExpandButton").css("visibility", "visible");
    }
  }

  var itemField = document.getElementById(itemName);
  itemField.title = errorMessage;
}

function addErrorRow(tableName, itemName, errorMessage)
{
  itemName = stripItemName(itemName);

  var table = document.getElementById(tableName);
  var newRow = table.insertRow();
  newRow.id = itemName + "_error";

  var newCell1 = newRow.insertCell();
  newCell1.innerHTML = "&nbsp;";
  newCell1.className = "errorText";
  newCell1.width = "5px";

  var newCell2 = newRow.insertCell();
  newCell2.innerHTML = "- &nbsp;";
  newCell2.className = "errorText";
  newCell2.width = "15px";

  var newCell3 = newRow.insertCell();
  newCell3.innerHTML = errorMessage;
  newCell3.className = "errorText";
  newCell3.width = "400px";
  newCell3.align = "left";
}

function removeError(itemName)
{
  var firstErrorsTable = document.getElementById('firstErrorsTable');
  var otherErrorsTable = document.getElementById('otherErrorsTable');
  var errorsRow = document.getElementById('errorsRow');

  if (firstErrorsTable == null || firstErrorsTable.style.display == 'none')
  {
    return; // No errors present
  }

  var currentRow = firstErrorsTable.rows[0];
  if (currentRow.id == itemName + "_error")
  {
	firstErrorsTable.deleteRow(0);
    if (otherErrorsTable.rows.length == 0)
    {
      otherErrorsTable.style.display = 'none';

      $("#errorExpandButton").css("visibility", "hidden");
      $("#errorsArrow").attr("src", "pages/images/toggle-dark-right-error.png");

      errorsRow.style.display = 'none';
      firstErrorsTable.style.display = 'none';

      checkButtons();
    }
    else
    {
      addErrorRow('firstErrorsTable', otherErrorsTable.rows[0].id, otherErrorsTable.rows[0].cells[2].innerHTML);
      otherErrorsTable.deleteRow(0);

      if (otherErrorsTable.rows.length == 0)
      {
        otherErrorsTable.style.display = 'none';
        $("#errorExpandButton").css("visibility", "hidden");
        $("#errorsArrow").attr("src", "pages/images/toggle-dark-right-error.png");
      }
    }
  }
  else
  {
	var rows = otherErrorsTable.rows;
    var index = 0;
    var currentRow = otherErrorsTable.rows[index];
    while (currentRow != null)
    {
      if (currentRow.id == itemName + "_error")
      {
        otherErrorsTable.deleteRow(index);
        break;
      }
      index++;
      currentRow = otherErrorsTable.rows[index];
    }

    if (otherErrorsTable.rows.length == 0)
    {
      otherErrorsTable.style.display = 'none';
      $("#errorExpandButton").css("visibility", "hidden");
      $("#errorsArrow").attr("src", "pages/images/toggle-dark-right-error.png");
    }
  }

  var itemField = document.getElementById(itemName);
  itemField.title = "";
}

function stripItemName(itemName)
{
  if (itemName.indexOf("_error") > 0)
  {
    return itemName.substring(0, itemName.indexOf("_error"));
  }
  return itemName;
}

function changeClassToFine(field)
{
  var currentClassName = field.className;
  if (currentClassName.indexOf("change") != -1 || currentClassName.indexOf("error") != -1)
  {
	if (currentClassName.indexOf("_") != -1)
    {
      field.className = currentClassName.substring(0, currentClassName.indexOf("_"));
    }
    else
    {
      field.className = currentClassName;
    }

    var changeCountField = document.getElementById('changeCount');
    if (changeCountField != null)
    {
      changeCountField.value = parseInt(changeCountField.value) - 1;
      checkButtons(changeCountField.value);
    }
  }
}

function changeClassToChange(field)
{
  var currentClassName = field.className;
  if (currentClassName.indexOf("change") == -1)
  {
	if (currentClassName.indexOf("_") != -1)
    {
	  field.className = currentClassName.substring(0, currentClassName.indexOf("_")) + '_change';
    }
    else
    {
      if (currentClassName.indexOf(' ') != -1)
      {
        field.className = currentClassName.replace(" ", "_change ");
      }
      else
      {
        field.className = currentClassName + '_change';
      }
    }

	var changeCountField = document.getElementById('changeCount');
	if (changeCountField != null)
    {
      changeCountField.value = parseInt(changeCountField.value) + 1;
      checkButtons(changeCountField.value);
    }
  }
}

function changeClassToError(field)
{
  var currentClassName = field.className;
  if (currentClassName.indexOf("_") != -1)
  {
    field.className = currentClassName.substring(0, currentClassName.indexOf("_")) + '_error';
  }
  else
  {
	if (currentClassName.indexOf(' ') != -1)
    {
      field.className = currentClassName.replace(" ", "_error ");
    }
    else
    {
      field.className = currentClassName + '_error';
    }
  }
}

function changeFilterClassToFine(field)
{
  var currentClassName = field.className;
  if (currentClassName.indexOf("error") != -1)
  {
    var errorCountField = document.getElementById('filterErrorCount');
	errorCountField.value = parseInt(errorCountField.value) - 1;

	if (currentClassName.indexOf("change") != -1 || currentClassName.indexOf("error") != -1)
	{
	  if (currentClassName.indexOf("_") != -1)
	  {
	    field.className = currentClassName.substring(0, currentClassName.indexOf("_"));
	  }
	  else
	  {
	    field.className = currentClassName;
	  }
	  checkFilterButtons();
	}
  }
}

function changeFilterClassToError(field)
{
  var currentClassName = field.className;
  if (currentClassName.indexOf("error") == -1)
  {
    var errorCountField = document.getElementById('filterErrorCount');
    errorCountField.value = parseInt(errorCountField.value) + 1;

    if (currentClassName.indexOf("_") != -1)
    {
    	field.className = currentClassName.substring(0, currentClassName.indexOf("_")) + '_error';
    }
    else
    {
  	  if (currentClassName.indexOf(' ') != -1)
      {
        field.className = currentClassName.replace(" ", "_error ");
      }
      else
      {
        field.className = currentClassName + '_error';
      }
    }
    checkFilterButtons();
  }
}

function clearChanges()
{
  var fieldSets = document.getElementsByTagName('fieldset');
  var index = 0;
  var currentFieldSet = fieldSets[index], currentTable, currentNumberOfRows, currentCells, currentNumberOfCells, originalValue;
  while (currentFieldSet != null)
  {
	currentTable = currentFieldSet.childNodes[1];
	currentNumberOfRows = currentTable.rows.length;
	for (var rowIndex = 0; rowIndex < currentNumberOfRows; rowIndex++)
	{
	  currentCells = currentTable.rows[rowIndex].cells;
	  currentNumberOfCells = currentCells.length;
	  for (var cellIndex = 0; cellIndex < currentNumberOfCells; cellIndex++)
	  {
		if (currentCells[cellIndex].tagName == 'TD')
		{
		  if (currentCells[cellIndex].childNodes[0] != null)
	      {
			if (currentCells[cellIndex].childNodes[0].tagName == 'TABLE')
	        {
	    	  // Date
			  originalValue = currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[1].value;
	    	  if (currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[2].tagName == 'INPUT')
	    	  {
	    	    currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[2].value = originalValue;
	    	    changeClassToFine(currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[2]);
	    	  }
	    	  else
	    	  {
	    	    currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[3].value = originalValue;
	    	    changeClassToFine(currentCells[cellIndex].childNodes[0].rows[0].cells[0].childNodes[3]);
	    	  }

		      // Time
		      originalValue = currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[0].value;
		      if (currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[2].tagName == 'INPUT')
	    	  {
		        currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[2].value = originalValue;
			    changeClassToFine(currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[2]);
	    	  }
	    	  else
	    	  {
	    	    currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[3].value = originalValue;
			    changeClassToFine(currentCells[cellIndex].childNodes[0].rows[0].cells[1].childNodes[3]);
	    	  }
		      break;
	        }
	        else if (currentCells[cellIndex].childNodes[0].tagName == 'INPUT')
	        {
	    	  originalValue = currentCells[cellIndex].childNodes[0].value;
		      currentCells[cellIndex].childNodes[2].value = originalValue;
		      changeClassToFine(currentCells[cellIndex].childNodes[2]);
		      break;
	        }
	        else if (currentCells[cellIndex].childNodes.length > 1
	    		     && currentCells[cellIndex].childNodes[1].tagName == 'INPUT') // New jQuery date contain a script @childNodes[0]
	        {
	    	  originalValue = currentCells[cellIndex].childNodes[1].value;
	    	  currentCells[cellIndex].childNodes[3].value = originalValue;
		      changeClassToFine(currentCells[cellIndex].childNodes[3]);
		      break;
	        }
	        else if (currentCells[cellIndex].childNodes[0].tagName == 'LABEL')
	        {
	    	  currentCells[cellIndex].childNodes[0].innerHTML = "";
	        }
		  }
		}
	  }
	}
	index++;
	currentFieldSet = fieldSets[index];
  }

  var changeCountField = document.getElementById('changeCount');
  if (changeCountField != null)
  {
    changeCountField.value = 0;
    checkButtons(changeCountField.value);
  }
  clearErrorsDiv();
}

function clearErrorsDiv()
{
  var firstErrorsTable = document.getElementById('firstErrorsTable');
  var errorsRow = document.getElementById('errorsRow');
  if (firstErrorsTable.style.display == 'none')
  {
    return;
  }

  firstErrorsTable.deleteRow(0);
  errorsRow.style.display = 'none';
  firstErrorsTable.style.display = 'none';

  $("#errorExpandButton").css("visibility", "hidden");
  $("#errorsArrow").attr("src", "pages/images/toggle-dark-right-error.png");

  var otherErrorsTable = document.getElementById('otherErrorsTable');
  if (otherErrorsTable.style.display == 'none')
  {
    return;
  }

  var length = otherErrorsTable.rows.length;
  for (var i = 0; i < length; i++)
  {
	otherErrorsTable.deleteRow(0);
  }

  otherErrorsTable.style.display = 'none';
}

function checkButtons()
{
  var changeCountField = document.getElementById('changeCount');
  if (changeCountField.value == 0) // Disable Buttons
  {
	disableButton('saveButton');
    disableButton('clearChangesButton');
    enableAddContactButton();
    enableRemoveContactButtons();
  }
  else  // Enable Buttons
  {
	var firstErrorsTable = document.getElementById('firstErrorsTable');
	if (firstErrorsTable.style.display == 'none' && changeCountField.value > 0)
	{
      enableSaveButton();
	  enableClearChangesButton();
	  disableAddContactButton();
	  disableRemoveContactButtons();
	}
  }
}

function checkFilterButtons()
{
  var errorCountField = document.getElementById('filterErrorCount');
  if (errorCountField.value == 0)
  {
	enableApplyInternalButton('applyInternalButton');
  }
  else
  {
	disableButton('applyInternalButton');
  }
}

function enableSaveButton()
{
  var button = $("#saveButton");
  if (button != null)
  {
	button.unbind('click');
	button.click(function() {this.blur();openSaveChangesDialog();});
	button.unbind('mouseover');
	button.mouseover(function() {buttonHover(this);});
	button.unbind('mouseout');
	button.mouseout(function() {buttonNormal(this);});
	button.addClass("button_input").removeClass("button_inputDisabled");
  }
}

function enableClearChangesButton()
{
  var button = $("#clearChangesButton");
  if (button != null)
  {
	var location = "" + window.location;
    if (location.indexOf("contactDetailsEdit.htm") >= 0)
    {
      button.unbind('click');
      button.click(function() {clearChangesContactDetailEdit();});
    }
    else
    {
      button.unbind('click');
  	  button.click(function() {this.blur();clearChanges();});
    }

	button.unbind('mouseover');
	button.mouseover(function() {buttonHover(this);});
	button.unbind('mouseout');
	button.mouseout(function() {buttonNormal(this);});
	button.addClass("button_input").removeClass("button_inputDisabled").removeClass("button_inputHover");
  }
}

function enableApplyInternalButton()
{
  var button = $("#applyInternalButton");
  if (button != null)
  {
    button.unbind('click');
    button.click = function() {this.blur();applyInternalFilter();};
	button.unbind('mouseover');
	button.mouseover(function() {buttonHover(this);});
	button.unbind('mouseout');
	button.mouseout(function() {buttonNormal(this);});
	button.addClass("button_input").removeClass("button_inputDisabled").removeClass("button_inputHover");
  }

  var indexDrop = document.getElementById('indexDrop');
  if (indexDrop != null)
  {
    indexDrop.disabled = false;
  }
}

function enableAddContactButton()
{
  var button = $("#addContactMethodButton");
  if (button != null)
  {
    button.unbind('click');
	button.click = function() {this.blur();submitAddContact();};
	button.unbind('mouseover');
	button.mouseover(function() {buttonHover(this);});
	button.unbind('mouseout');
	button.mouseout(function() {buttonNormal(this);});
	button.addClass("button_input").removeClass("button_inputDisabled").removeClass("button_inputHover");
  }
}

function enableRemoveContactButtons()
{
  var index = 0;
  $("[id^=removeContactMethodButton_]").each(function() {
	$(this).unbind('click');
	$(this).click(function() {this.blur();submitRemoveContact(index);});
	$(this).unbind('mouseover');
	$(this).mouseover(function() {buttonHover(this);});
	$(this).unbind('mouseout');
	$(this).mouseout(function() {buttonNormal(this);});
	$(this).addClass("button_input").removeClass("button_inputDisabled").removeClass("button_inputHover");
	index++;
  });
}

function disableButton(name)
{
  var button = $("#" + name);
  if (button != null)
  {
    button.unbind('click');
	button.unbind('mouseover');
	button.unbind('mouseout');
	button.addClass("button_inputDisabled").removeClass("button_input"); 
  }

  var indexDrop = document.getElementById('indexDrop');
  if (indexDrop != null)
  {
    indexDrop.disabled = true;
  }
}

function disableAddContactButton()
{
  var button = $("#addContactMethodButton");
  if (button != null)
  {
	button.unbind('click');
	button.unbind('mouseover');
	button.unbind('mouseout');
	button.addClass("button_inputDisabled").removeClass("button_input");
  }
}

function disableRemoveContactButtons()
{
  $("[id^=removeContactMethodButton_]").each(function() {
	  $(this).unbind("click");
	  $(this).unbind("mouseover");
	  $(this).unbind("mouseout");
	  $(this).addClass("button_inputDisabled").removeClass("button_input");
  });
}

function textAreaCounter(textAreaElement, maxLength)
{
  var label = document.getElementById(textAreaElement.id + "_remainingCharacters");
  if (textAreaElement.value.length > maxLength)
  {
	textAreaElement.value = textAreaElement.value.substring(0, maxLength);
  }
  else
  {
    label.innerHTML = '(' + (maxLength - textAreaElement.value.length) + ' characters available)';
  }
}

function saveMeterViewerChanges(k0544MessageCode, k0998MessageCode, k0999MessageCode, k1001MessageCode)
{
  afterFieldEdit(k0544MessageCode, 'string', 'meter.k0544', 'false', new Array());
  afterFieldEdit(k0998MessageCode, 'string', 'meter.k0998', 'false', new Array());
  afterFieldEdit(k0999MessageCode, 'string', 'meter.k0999', 'false', new Array());
  afterFieldEdit(k1001MessageCode, 'string', 'meter.k1001', 'false', new Array());
  setTimeout('submitThePage()', 500);
}

function saveNoteViewerChanges(summaryMessageCode, statusMessageCode, detailMessageCode)
{
  afterFieldEdit(summaryMessageCode, 'string', 'notes.summary', 'false', new Array());
  afterFieldEdit(statusMessageCode, 'string', 'notes.status', 'false', new Array());
  afterFieldEdit(detailMessageCode, 'string', 'notes.notes', 'false', new Array());
  setTimeout('submitThePage()', 500);
}

function submitThePage()
{
  var firstErrorsTable = document.getElementById('firstErrorsTable');
  if (firstErrorsTable.style.display == 'none')
  {
	document.viewerForm.submit();
  }
}
