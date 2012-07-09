Utility = {
    showThrobber: function(aMessage){
      $("#loadingOverlay #message").text(aMessage);
      $("#loadingOverlay").overlay().load();
      return this;
    },



    hideThrobber: function(){
      $('#throbber').remove();
      return this;
    },

    setPageChanged: function(){
      PAGE_CHANGED = true;
      return this;
    },

    unsetPageChanged: function(){
      delete PAGE_CHANGED;
      return this;
    },

    init: function(){
      this.registerPrototypes();
      return this;
    },

    registerPrototypes: function(){
      /**Add json printf functionality to string objects */
      String.prototype.jsonPrintf = function(obj) {
        var formatted = new String(this);
        for(key in obj) {
          formatted = formatted.split("{" + key + "}").join(obj[key]);
        }
        return formatted;
      };

      String.prototype.contains = function(aVal) {
        return this.indexOf(aVal) != -1;
      };

      return this;
    }
}.init(); //call constructor