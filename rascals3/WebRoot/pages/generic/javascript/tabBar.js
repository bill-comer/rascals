function bindTabBarFunctions()
{
  $(".menu_group").hover(
    function()
    {
      $(this).css("text-decoration", "underline");
      $(this).css("cursor", "hand");
      var pos = $(this).position();
      $("#" + this.id + "Sub").css("display", "block").css( { "left": (pos.left) + "px", "top":(pos.top + 15) + "px" } );
      $("#" + this.id + "Subiframe").css("display", "block").css( { "left": (pos.left) + "px", "top":(pos.top + 15) + "px" } ).css("height", $("#" + this.id + "Sub").height());
    }
    ,
    function()
    {
      $(this).css("text-decoration", "none");
      var id = "#" + this.id + "Sub";
      setTimeout(function(){hideMe(id)}, 75);
    });

  $(".menu_div").hover(function()
    {
      hovering = true;
    },function()
    {
    $("#" + this.id).css("display", "none");
    $("#" + this.id + "iframe").css("display", "none");
    hovering = false;
    });
}
function hideMe(idToHide)
{
  if (!hovering)
  {
  $(idToHide).css("display", "none");
  $(idToHide + "iframe").css("display", "none");
  }
}