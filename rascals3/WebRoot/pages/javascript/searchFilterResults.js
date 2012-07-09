var ref_current_clickedrow_field_id;

function clickedRow(recordId, recordIdPrefix, record_id_field_name)
{
  var id = recordId.substring(recordIdPrefix.length);

  var $ref_current_clickedrow_el = $('#' + ref_current_clickedrow_field_id);
  if($ref_current_clickedrow_el.selector.length > 0)
  {
    $ref_current_clickedrow_el.css('backgroundColor', '#FFFFFF');
  }

  var $ref_clickedrow_el = $('#' + recordId);

  if($ref_clickedrow_el.length != 0)
  {
    $ref_clickedrow_el.css('backgroundColor', '#EEEEEE');

    // reference to currently selected record on page
    ref_current_clickedrow_field_id = recordId;

    var $selected_id_el = $('#' + record_id_field_name);

    if($selected_id_el.length != 0)
    {
      $selected_id_el.val(id);
    }
  }
}

// redirect using a form located on the original page. defaults to using a get form submission
function redirectUrl(url, record_id_field_name, recordIdPrefix)
{
  var $ref_clickedrow_el = $('#' + ref_current_clickedrow_field_id);

  if($ref_clickedrow_el.length == 0)
  {
    alert("A row must be selected");
    return;
  }

  var $selected_record_id_el = $('#' + record_id_field_name);
  var id = ref_current_clickedrow_field_id.substring(recordIdPrefix.length);

  if ($selected_record_id_el.length != 0)
  {
    $selected_record_id_el.val(id);
  }

  var $redirectUrlForm = $('#redirectUrlForm');
  $redirectUrlForm.attr('method', 'get');
  $redirectUrlForm.attr('action', url);
  $redirectUrlForm.submit();
}

function toggleAnchorButton(buttonId, enable)
{
  if (enable != undefined)
  {
    if (enable)
    {
      $('#' + buttonId).removeClass('button_inputDisabled');
      $('#' + buttonId).addClass('button_input');
      $('#' + buttonId).mouseenter(function() {
        buttonHover(this);
      });
      $('#' + buttonId).mouseenter(function() {
        buttonHover(this);
      });
      $('#' + buttonId).mouseout(function() {
        buttonNormal(this);
      });
      $('#' + buttonId).removeAttr('disabled');
    }
    else
    {
      $('#' + buttonId).removeClass('button_input');
      $('#' + buttonId).addClass('button_inputDisabled');
      $('#' + buttonId).attr('disabled', 'disabled');
    }
  }
}

function redirectUrl2(url, record_id_field_name, recordId)
{
  window.location.href = url + '?' + record_id_field_name + '=' + recordId;
  return;

}

//p28 list screen download conflicts button function
function toggleP28DownloadConflictsButton(buttonId, checkRowId)
{
  if (buttonId != undefined)
  {
    if (checkRowId != undefined)
    {
      var $ref_check_row_id_el = $('#' + checkRowId);
      if ($ref_check_row_id_el.selector.length > 0)
      {
        var has_warnings = null;
        $('input:hidden').each(function() {
          if ($(this).attr('id') == checkRowId)
          {
            has_warnings = $(this).val() == 'true';
          }
        });

        toggleAnchorButton(buttonId, has_warnings);
      }
    }
  }
}

//p28 list screen view error report button function
function toggleP28ErrorReportButton(buttonId, checkRowId)
{
  if (buttonId != undefined)
  {
    if (checkRowId != undefined)
    {
      var $ref_check_row_id_el = $('#' + checkRowId);

      if ($ref_check_row_id_el.selector.length > 0)
      {
        var $checkboxes = $('input:checkbox');
        var is_checked = false;

        $checkboxes.each(function() {
          if ($(this).attr('id') == checkRowId)
          {
            is_checked = $(this).attr('checked');
          }
        });

        toggleAnchorButton(buttonId, is_checked);
      }
    }
  }
}