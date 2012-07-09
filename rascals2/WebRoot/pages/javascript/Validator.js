Validator = {

    isEmpty : function(aValue){
      return (aValue==undefined || aValue==null || aValue=="");
    },

    isValidDate: function(aValue){
      //matches dd/mm/yyyy
      return aValue=="" || aValue.match(/^\d{2}\/\d{2}\/\d{4}$/);
    },

    remoteLookUp : function(aUrl, aData){
      var exists = false;

      $.ajax({
        timeout: 1000,
        url: aUrl,
        async: false,
        type: "GET",
        dataType: "text",
        data: aData,
        success: function(aResponse){ exists = (aResponse == "true") },
        error: function(aJqXHR, aTextStatus, aErrorThrown){
          alert("Error: "+ aTextStatus + ", remote lookup failed.");
        }
      });

      return exists;
    },

    hasUnsavedChanges: function(){
      if(typeof PAGE_EDITABLE == "undefined")
      {
        //ignore unsaved changes is user does not have permission to change page
        return false;
      }
      else
      {
        if(typeof PAGE_CHANGED==="undefined")
        {
          return false;
        }
        else
        {
          window.alert("Page contains unsaved changes. \nSave or Cancel changes before continuing.");
          return true;
        }
      }
   },

   isDigit: function(aValue){
     var re = /^\d*$/;  //matches digits only
     return re.test(aValue); //match against value and return result
   },

   inRange: function(aValue, aMin, aMax)
   {
     var number = parseFloat(aValue);
     return number<=aMax && number >= aMin;
   }

}