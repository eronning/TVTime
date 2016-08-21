/* global variables */
var _currFriend;
var _commonShows;
var _commonFriends;

var _friendWrap1;
var _friendWrap2;

var _friendName;

/* setups the page */
$(document).ready(function(){
	_friendWrap1 = document.getElementById("friendWrap1");
	_friendWrap2 = document.getElementById("friendWrap2");
	
    $.post("/gather/friend/data", "", function(responseJSON) {
    	var response = JSON.parse(responseJSON);
    	_currFriend = response.friend;
    	_friendName = _currFriend.name;
    	_commonShows = response.commonShows;
    	_commonFriends = response.commonFriends;
    	populateFriendWrap(_currFriend, response.areFriends, response.requestedFriend);
    	populateShowInfoWrap(_commonShows);
    	populateFriendInfoWrap(_commonFriends);
    })
    
});

/* binds the watch together button to take the user to the watch together page with the right show */
$("#showInfo").mousedown(function(event) {
	var target = getTarget(event);
	
	var tagName = $(target).prop("tagName");
	if (tagName.toLowerCase() == "button") {
		target = target.parentNode.parentNode;
		var child = $(target).children()[1];
		var name = $(child).find(".show-name")[0].innerHTML;
		location.href = "/watchTogether/" + name;
	}
});
/** populateShowInfoWrap populates the show mutual shows section
 * 
 * @param shows are the shows to populate with
 */
function populateShowInfoWrap(shows) {
	var divToPopulate = document.getElementById('showInfo');
	for (showNum in shows) {
		var wrap = document.createElement("div");
		wrap.className = "showDiv";
		
		var col1 = document.createElement("div");
		col1.className = "showCol";
		
		var col2 = document.createElement("div");
		col2.className = "showCol";
		
		var name = document.createElement("p");
		name.innerHTML = shows[showNum].name;
		name.className = "show-name";
		
//		var description = document.createElement("p");
//		description.innerHTML = shows[showNum].description;
//		description.className = "show-description";
		
		
		var genres = document.createElement("p");
		var genresVal = shows[showNum].genres;
		genres.innerHTML = genresVal.substring(1, genresVal.length - 1);
		genres.className = "show-genres";
		
		var image = document.createElement("IMG");
		image.setAttribute("src", shows[showNum].image);
		image.setAttribute("alt", shows[showNum].name);
		image.className = "show-image";
		
		var scheduleBtn = document.createElement('button');
		$(scheduleBtn).text('Watch together!');
		
		col1.appendChild(image);
		col1.appendChild(scheduleBtn);
		wrap.appendChild(col1);
		
		col2.appendChild(name);
//		col2.appendChild(description);
		col2.appendChild(genres);
		wrap.appendChild(col2);
		
		divToPopulate.appendChild(wrap);
	}
}
/** populateFriendInfoWrap populates the mutual friends section 
 * 
 * @param friends is the friends to populate with
 */
function populateFriendInfoWrap(friends) {
	var divToPopulate = document.getElementById('friendInfo');
	for (friendNum in friends) {
		var wrap = document.createElement("div");
		
		var content = document.createElement("div");	
		content.className = "content";
		
		var name = document.createElement("p");
		name.innerHTML = friends[friendNum].name;
		name.className = "content-name";
		
		var email = document.createElement("p");
		email.innerHTML = "(" + friends[friendNum].email + ")";
		email.className = "content-email";
		
		var image = document.createElement("IMG");
		image.setAttribute("src", friends[friendNum].avatar);
		image.setAttribute("alt", friends[friendNum].name);
		content.appendChild(name);
		content.appendChild(email);
		content.appendChild(image);
		wrap.appendChild(content);
		divToPopulate.appendChild(wrap);
	}
}
/** getTarget gets the target of a event
 * 
 * @param e is the event
 * @returns the target element
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    return target; 
}

/** populateFriendWrap populates the user section
 * 
 * @param friend is the friend to populate the section with
 * @param areFriends a boolean on whether the two of you are friends
 * @param requestedFriend a boolean on whether the friend has been requested
 */
function populateFriendWrap(friend, areFriends, requestedFriend) {
	
	var friendImage = document.createElement("IMG");
	friendImage.setAttribute("src", friend.avatar);
	friendImage.setAttribute("alt", friend.name);
	friendImage.className = "friend-image";
	_friendWrap1.appendChild(friendImage);
	console.log(friend);
	var friendName = document.createElement("div");
	friendName.innerHTML = friend.name;
	friendName.className = "friend-name";
	_friendWrap2.appendChild(friendName);
	
    
	var friendEmail = document.createElement("div");
	friendEmail.innerHTML = friend.email;
	friendEmail.className = "friend-email";
	_friendWrap2.appendChild(friendEmail);
	
	var friendDescription = document.createElement("div");
	friendDescription.id = "friendDescription";
	friendDescription.innerHTML = "You aren't yet friends. Send a friend request!";
	if(areFriends) {
		friendDescription.innerHTML = "You are friends.";
	} 
	friendDescription.className = "friend-description";
	_friendWrap2.appendChild(friendDescription);
	
	
	var hasFriend = document.createElement("div");
	var friendButton = document.createElement("button");
	// in the event the two people are friends
	if (areFriends) {
		var hasShow = document.createElement("div");
		hasFriend.innerHTML = "You are friends.";
		hasFriend.className = "hasFriends";
		_friendWrap2.appendChild(hasShow);
		friendButton.innerHTML = "Unfriend";
		friendButton.id = "removeFriend-button";
		friendButton.className = "removeFriend-button";
		friendButton.onclick = function () {
			//performs the post for removing a friend
			$.post("/friend/remove", "", function(responseJSON) {
			})
			$("#friendDescription").fadeOut(300);
			$("#removeFriend-button").fadeOut(300, function() {
				hasFriend.innerHTML = _friendName + " has been removed from your list of friends.";
				hasFriend.className = "hasFriend";
			    _friendWrap2.appendChild(hasFriend);
			});
		}
		_friendWrap2.appendChild(friendButton);
    // in the event that the friend request was sent
	} else if (requestedFriend) {
		hasFriend.innerHTML = "Friend request to " + _friendName + " sent.";
		hasFriend.className = "hasFriend";
	    _friendWrap2.appendChild(hasFriend);
	// in the event that they aren't friends yet
	} else {
		friendButton.innerHTML = "Become Friends?";
		friendButton.id = "addFriend-button";
		friendButton.className = "addFriend-button";
		friendButton.onclick = function () {
			//performs the post to make friends
			$.post("/friend/request", {"friendEmail" : friend.email}, function(responseJSON) {
			})
			$("#friendDescription").fadeOut(300);
			$("#addFriend-button").fadeOut(300, function() {
				hasFriend.innerHTML = "Friend request to " + _friendName + " sent.";
				hasFriend.className = "hasFriend";
			    _friendWrap2.appendChild(hasFriend);
			});
		};
		_friendWrap2.appendChild(friendButton);
	}
}