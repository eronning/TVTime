/* global variables */
var _currName;
var _currEmail;
var _currPassword;

var _currClick;

var friends;

var friendsContent = document.getElementById('container');

/* this gets the page ready on startup */
$(document).ready(function(){
    $.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _currName = response.name;
        _currEmail = response.email;
        _currPassword = response.password;
    });
    $.post("/getFriends", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
    	friends = response.myFriends;
    	
    	if (friends.length !== 0) {
    		$("#no-friends").hide();
            populate(friendsContent, friends);
    	} else {
    		$("#no-friends").show();
    	} 
    });
	$("#no-matches").hide();
    $("#addFriend").hide();
});

/** populate populates the friends on the page.
 * 
 * @param element is the element to populate
 * @param table is the table storing data to populate with
 */
function populate(element, table) {
	for (var i = 0; i < table.length; i++) {
		var wrap = document.createElement("div");
		wrap.className = "friend";
		
		var content = document.createElement("div");	
		content.className = "content";
		
		var name = document.createElement("p");
		name.innerHTML = table[i].name;
		name.className = "content-name";
		
		var email = document.createElement("p");
		email.innerHTML = "(" + table[i].email + ")";
		email.className = "content-email";
		
		var image = document.createElement("IMG");
		image.setAttribute("src", table[i].avatar);
		image.setAttribute("alt", table[i].name);
		
		content.appendChild(name);
		content.appendChild(email);
		content.appendChild(image);
		wrap.appendChild(content);
		
		wrap.onclick = function() {
			var target = getTarget(event);
			clickedFriend(target);		
		}
		
        //makes sure its not null in the loading process
		if (element !== null) {
			element.appendChild(wrap);
		}
		
	}	
}
/** getTarget gets the target of a click.
 * 
 * @param e is the event
 * 
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    if (target.parentNode) {
    	target = target.parentNode;
    }
    return target; 
}

/* this binds the search button to a click and performs the appropriate actions */
$("#search-button").bind('click', function() {
	var search = document.getElementById('search-text');
	var searchVal = search.value;
	$('#no-friends').hide();
	$("#no-matches").hide();
	if(searchVal != "") {
		var postParameters = {
			input: searchVal
		};
		
		$.post("/search", postParameters, function(responseJSON) {
			var response = JSON.parse(responseJSON);
			if (response.length == 0) {
				$("#container").empty();
				$("#no-matches").show();
			} else {
				//show matches
				$("#container").empty();
				populate(friendsContent, response);
			}
		})
	}
});
/** clickedFriend is the function that is performed when a 
 *                user is clicked on. It takes the user to
 *                that specific friend page.
 * 
 * @param clicked is the clickced element
 */
function clickedFriend(clicked) {
	var elements = $(clicked).children();
	if(elements.length == 1) {
		elements = elements.children();
	} 
	var name = $(elements[0]).text();
	var email= $(elements[1]).text();
	var postParameters = {
		friendEmail: email
	};
	$.post("/friends/nav", postParameters, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var userId = response.userId;
		var friendId = response.friendId;
		if (userId != "" && friendId != "") {
			location.href = "/friend/" + userId+ "/" + friendId;
		}
	})
}

