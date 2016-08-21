

(function($) {

  $.fn.menumaker = function(options) {
      
      var cssmenu = $(this), settings = $.extend({
        title: "Menu",
        format: "dropdown",
        breakpoint: 768,
        sticky: false
      }, options);

      return this.each(function() {
        cssmenu.find('li ul').parent().addClass('has-sub');
        if (settings.format != 'select') {
          cssmenu.prepend('<div id="menu-button">' + settings.title + '</div>');
          $(this).find("#menu-button").on('click', function(){
            $(this).toggleClass('menu-opened');
            var mainmenu = $(this).next('ul');
            if (mainmenu.hasClass('open')) { 
              mainmenu.hide().removeClass('open');
            }
            else {
              mainmenu.show().addClass('open');
              if (settings.format === "dropdown") {
                mainmenu.find('ul').show();
              }
            }
          });

          multiTg = function() {
            cssmenu.find(".has-sub").prepend('<span class="submenu-button"></span>');
            cssmenu.find('.submenu-button').on('click', function() {
              $(this).toggleClass('submenu-opened');
              if ($(this).siblings('ul').hasClass('open')) {
                $(this).siblings('ul').removeClass('open').hide();
              }
              else {
                $(this).siblings('ul').addClass('open').show();
              }
            });
          };

          if (settings.format === 'multitoggle') multiTg();
          else cssmenu.addClass('dropdown');
        }

        else if (settings.format === 'select')
        {
          cssmenu.append('<select style="width: 100%"/>').addClass('select-list');
          var selectList = cssmenu.find('select');
          selectList.append('<option>' + settings.title + '</option>', {
                                                         "selected": "selected",
                                                         "value": ""});
          cssmenu.find('a').each(function() {
            var element = $(this), indentation = "";
            for (i = 1; i < element.parents('ul').length; i++)
            {
              indentation += '-';
            }
            selectList.append('<option value="' + $(this).attr('href') + '">' + indentation + element.text() + '</option');
          });
          selectList.on('change', function() {
            window.location = $(this).find("option:selected").val();
          });
        }

        if (settings.sticky === true) cssmenu.css('position', 'fixed');

        resizeFix = function() {
          if ($(window).width() > settings.breakpoint) {
            cssmenu.find('ul').show();
            cssmenu.removeClass('small-screen');
            if (settings.format === 'select') {
              cssmenu.find('select').hide();
            }
            else {
              cssmenu.find("#menu-button").removeClass("menu-opened");
            }
          }

          if ($(window).width() <= settings.breakpoint && !cssmenu.hasClass("small-screen")) {
            cssmenu.find('ul').hide().removeClass('open');
            cssmenu.addClass('small-screen');
            if (settings.format === 'select') {
              cssmenu.find('select').show();
            }
          }
        };
        resizeFix();
        return $(window).on('resize', resizeFix);

      });
  };
})(jQuery);

(function($){
$(document).ready(function(){

$(document).ready(function() {
  $("#cssmenu").menumaker({
    title: "Menu",
    format: "dropdown"
  });

  $("#cssmenu a").each(function() {
  	var linkTitle = $(this).text();
  	$(this).attr('data-title', linkTitle);
  });
});

});


})(jQuery);
$(document).ready(function(){
var ul = document.getElementById('nav');
ul.onclick = function(event) {
    var target = getEventTarget(event);
    $.post("/page/information", "", function(responseJSON) {
    	var response = JSON.parse(responseJSON);
    	var path =  getPath(target.innerHTML, response.encodedId);
    	console.log(response.page);
    	if (response.page == "friend" || response.page == "show" || response.page == "highlights") {
    		path = "../" + path;
    	}
    	location.href = path;
        if (target.innerHTML == "Logout") {
        	$.post("/logout", "", function() {
        	})
        }
    })
};
});



function getPath(name, encodedUserId) {
	if (name == "Home") {
		return "../home/" + encodedUserId;
	} else if (name == "My Shows") {
		return "../shows/" + encodedUserId;
	} else if (name == "My Groups") {
		return "../groups/" + encodedUserId;
	} else if (name == "My Schedule") {
		return "../schedule/" + encodedUserId;
	} else if (name == "Availability") {
		return "../availability/" + encodedUserId;
	} else if (name == "Friends") {
		return "../friends/" + encodedUserId;
	} else if (name == "Watch Together") {
		return "../watchTogether";
	} else if (name == "Account") {
		return "../account/" + encodedUserId;
	} else if (name == "Logout") {
		return "../tvtime";
	}
}

function getEventTarget(e) {
    e = e || window.event;
    return e.target || e.srcElement; 
}