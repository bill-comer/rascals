function submitGenerateMopFileForm(msg)
{
  $('#mopGenerationRequestedAction').val('generateParmsMopReport');
  var $form = $('#mopGeneration');
  $form.attr('onsubmit', '');
  submit("mopGeneration", msg);
}

function submitGenerateDpiFileForm(msg)
{
  $('#dpiGenerationRequestedAction').val('generateParmsDpiFile');
  var $form = $('#dpiGeneration');
  $form.attr('onsubmit', '');
  submit("dpiGeneration", msg);
}

function dpiGenerationDatePicker() {
  $("#reportingPeriod" + "_datepicker").datepicker({
    showOn: 'button',
    buttonImage: 'pages/images/calendar.gif',
    buttonImageOnly: true,
    showAnim: 'fadeIn',
    duration: 'fast',
    dateFormat: 'M yy',
    altFormat: 'M yy',
    altField: '#reportingPeriod',
    maxDate: '+0',
    buttonText: 'Select a DPI Report Date',
    changeMonth: true,
    changeYear: true,
    showButtonPanel: true,
    onClose: function(dateText, inst) {
        var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
        var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
        $(this).datepicker('setDate', new Date(year, month, 1));
        $("#reportingPeriod" + "_datepicker").focus();
    }
  });
}