/* global variables */

var _myShowsContent = document.getElementById('my-content');
var _topShowsContent = document.getElementById('top-content');
var _dramaShowsContent = document.getElementById('drama-content');
var _comedyShowsContent = document.getElementById('comedy-content');
var _crimeShowsContent = document.getElementById('crime-content');
var _actionShowsContent = document.getElementById('action-content');
var _thrillerShowsContent = document.getElementById('thriller-content');

var _showInformation;

var _showNames = [];
var _showNameMap = [];

var _currName;
var _currEmail;
var _currPassword;

/* sets up the page */
$(document).ready(function () {
	init();
});

/** populates the show name map and fills the autocorrect 
 * 
 * @param showInformation is the show information
 */
function populateShowNames(showInformation) {
	var count = 0;
	for (var show in showInformation) {
		var showInfo = _showInformation[show];
		_showNames[count] = showInfo.name;
		_showNameMap[showInfo.name] = showInfo; 
		count++;
	}
	$("#search-text").autocomplete({
	    source: _showNames
	});
}

/* inializes the page for the user */
function init() {
	// posts for the current page information
	$.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _currName = response.name;
        _currEmail = response.email;
        _currPassword = response.password;
    })
	
	// posts to gather all of the show data
	$.post("/gather/shows/data", "", function(responseJSON) {
		var response = JSON.parse(responseJSON);
		_showInformation = response.shows;
		populateShowNames(_showInformation);
		//hides the users show div if they have no shows
		if (response.myShows.length == 0) {
			$("#my-content").hide();
			$("#myShowsTitle").hide();
			$("#myShowsHeader1").hide();
			$("#myShowsHeader2").hide();
		} else {
			//fills users show div with their shows
			populate(_myShowsContent, response.myShows);
			//makes the div scrollable
			$("#my-content").smoothDivScroll({});
		}
		//fills the show sliders with appropriate shows
		populate(_topShowsContent, response.topShows);
		populate(_dramaShowsContent, response.dramaShows);
		populate(_comedyShowsContent, response.comedyShows);
		populate(_crimeShowsContent, response.crimeShows);
		populate(_actionShowsContent, response.actionShows);
		populate(_thrillerShowsContent,response.thrillerShows);
		// makes all of the divs scrollable
		$("#top-content").smoothDivScroll({});
		$("#drama-content").smoothDivScroll({});
		$("#comedy-content").smoothDivScroll({});
		$("#crime-content").smoothDivScroll({});
		$("#action-content").smoothDivScroll({});
		$("#thriller-content").smoothDivScroll({});
	})
		
	$("#hoverPopup").hide();
	$("#no-matches").hide();
}
/* binds the search text to a property change */
$( "#search-text" ).bind('input propertchange', function() {
	$("#no-matches").fadeOut(200);
});
	
/* binds the search button to a click which takes the user to a show page if it exists */
$("#search-button").bind('click', function() {
	var search = document.getElementById('search-text');
	var searchVal = search.value;
	if (searchVal != "") {
	  var res = _showNameMap[searchVal];
	  // make client side cache post
	  if (res !== undefined) {
		  var postParameters = {
					userEmail: _currEmail,
			};
		  /* posts to go to the show page if it exists */
			$.post("/shows/page", postParameters, function(responseJSON) {
				var response = JSON.parse(responseJSON);
				var encodedId = response.encodedId;
				if (encodedId != "") {
					location.href = "/show/" + encodedId+ "/" + res.id;
				}
			})
	  } else {
		  //there were no matches 
		  $("#no-matches").fadeIn(200);
		  var postParameters = {
					input : searchVal	  
				  };
		          //would have made post should searching the database be desired 
//				  $.post("/show/search", postParameters, function(responseJSON) {
//					  
//				  })

	  }
	}
})

/* binds the elements in the show divs to hovering */
var _stillHovering = false;
$("div.makeMeScrollable").hover(function() {
	/* binds the shows div to movement to check if it moves across a image */
	$("div.makeMeScrollable").mousemove(function(event) {
		var target = getTarget(event);
		var tagName = $(target).prop("tagName");
		if (tagName.toLowerCase() == "img") {
			var targetShowInfo = _showInformation[$(target).prop("src")];
			/* binds the elements in the show divs to clicking */
			$("div.makeMeScrollable").bind('click', function(event) {
				var postParameters = {
						userEmail: _currEmail,
				};
				// posts to the shows page an moves to the page if it exists
				$.post("/shows/page", postParameters, function(responseJSON) {
					var response = JSON.parse(responseJSON);
					var encodedId = response.encodedId;
					if (encodedId != "") {
						location.href = "/show/" + encodedId+ "/" + targetShowInfo.id;
					}
				})
			});
			_stillHovering = true;
			//delay the hovering popup so it doesn't appear instantly
			var delay = 500;
		    setTimeout(function(){
		      if (_stillHovering) {
		    	//show the hovering popup
				var hoverName = document.getElementById("hoverName");
				hoverName.innerHTML = targetShowInfo.name;
				var hoverGenres = document.getElementById("hoverGenres");
				var hoverGenresVal = targetShowInfo.genres;
				hoverGenres.innerHTML = "Genres: " + hoverGenresVal.substring(1, hoverGenresVal.length - 1);
				var hoverRuntime = document.getElementById("hoverRuntime");
				hoverRuntime.innerHTML = "Runtime: " + targetShowInfo.runtime;
				var hoverSeasons = document.getElementById("hoverSeasons");
				hoverSeasons.innerHTML = "Seasons: " + targetShowInfo.seasons;
				var targetOffset = $(target).offset();
				var halfPage = getWidth() / 2;
				var leftOffset = targetOffset.left;
				// move the hover popup above the show
				$("#hoverPopup").offset({left:leftOffset,top:targetOffset.top - 200});
				$("#hoverPopup").fadeIn(200);
		      }
		    //your code to be executed after 1 seconds
		    },delay); 
		}
	});
}, function() {
	// hide the hover popup from sight since the hovering is no longer occuring
	$("#hoverPopup").fadeOut(200);
	_stillHovering = false;
});

/** getTarget gets the target of an event
 * 
 * @param e is the event
 * @returns the target element
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    return target; 
}

/** getWidth gets the width of the page
 * 
 * @returns the width of the page
 */
function getWidth() {
	  if (self.innerHeight) {
	    return self.innerWidth;
	  }

	  if (document.documentElement && document.documentElement.clientHeight) {
	    return document.documentElement.clientWidth;
	  }

	  if (document.body) {
	    return document.body.clientWidth;
	  }
	}

/** populate fills a show div with show images 
 *  
 * @param element is the element to fill
 * @param table the show images to fill the element with
 */
function populate(element, table) {
	for (var i = 0; i < table.length; i++) {
		if (table[i] != null) {
			var content = document.createElement( 'div' );	
			var image = document.createElement("IMG");
			image.setAttribute("src", table[i]);
			image.setAttribute("alt", table[i]);
			image.className = "content";
			content.appendChild(image);
	        //makes sure its not null in the loading process
			if (element !== null) {
				element.appendChild(content);
			}
		}
	}
	
}
