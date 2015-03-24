/**
 * Author: Brent Barker
 */

//listener to be sure all tabs on the page show the same selected tab
$('[data-toggle="tab"]').on('shown.bs.tab', function (e) {
  var dataTarget = $(e.target).attr('data-target');
  
  //remove all active tabs
  $(".nav-tabs li").each(function (index) {
    $(this).removeClass('active');
  });
  
  //make correct tab active
  $('[data-target="' + dataTarget + '"]').each(function (index){
    $(this).parent().addClass('active');
  });
  
});
