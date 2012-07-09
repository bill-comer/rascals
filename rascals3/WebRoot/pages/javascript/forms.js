function submit(form_id, msg)
{
  $.blockUI({ message: '<div style="font-size:11px;font-weight:bold">' + msg + '<img src="pages/images/loading_small.gif" style="vertical-align: middle;"/></div>', css: {border: '1px solid black', padding: '3px'}, overlayCSS: {backgroundColor: '#000000', opacity: '0.0'} });
  $("#" + form_id).submit();
}