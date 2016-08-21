/* global variables */
var _showInformation;
var _currFriends;

var _friendNameMap = [];
var _showNames = [];
var _showNameMap = [];

var _currShow;
var _currUser;

var _friendsGroup = [];
var _friendGroupsToPost = [];

/* initializes the page */
$(document).ready(function(){
	init();
});

/* initializes the page */
function init() {
	$("#no-show").hide();
	$("#no-friends").hide();
	$("#searched").hide();
	$.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _currUser = {
        	name: response.name,
        	email: response.email,
        	avatar: response.avatar,
        	episodeNum: 1
        };
        /* posts for the show data */
        $.post("/gather/shows/data", "", function(responseJSON) {
    		var response = JSON.parse(responseJSON);
    		_showInformation = response.shows;
    		populateShowNames(_showInformation);
    		var showName = $("#searched").text();
    		if (showName === "") {
    			showName = "Daredevil";
    		}
    		var show = _showNameMap[showName];
    		populateShow(show);
    		_currShow = show;
    		
    		var postParameters = {
    				showId : _currShow.id
    		};
    		/* posts for friends that share the show */
    		$.post("/friends/for/show", postParameters, function(responseJSON) {
    			var response = JSON.parse(responseJSON);
    			
    			_currUser.episodeNum = response.currEpisodeNum;
        		_friendsGroup[_currUser.name] = _currUser;
                populateGroup(_currUser);
    			
    			_currFriends = response.friendsForShow;
    			populateFriendNames(_currFriends);
    			populateFriendsForShow(_currFriends);
    			
    			initCalendar();
    			updateCalendar();
    		})
    	})
    })
	
}
/* binds the group to a click to remove the elements in the group on clicking */
$("#myGroup").bind('click', function(event) {
	var target = getTarget(event);
	var name = $(target).find(".content-name")[0].innerHTML;
	if (name != _currUser.name) {
		delete _friendsGroup[name];
		$(target).remove();
		$(target).removeClass("content");
		$(target).addClass("scrollContent");
		$("#friends-show-content").append($(target));
		updateCalendar();
	}
});
/* binds the friends for a show to a click in order to remove the elements and place them in the group */
$("#friends-show-content").bind('click', function(event) {
	var target = getTarget(event);
	var tagName = $(target).prop("tagName");
	if (tagName.toLowerCase() == "div") {
	  var name = $(target).find(".content-name")[0].innerHTML;
	  $(target).remove();
	  var friend = _friendNameMap[name];
	  if (friend !== undefined && _friendsGroup[friend.name] === undefined) {
		  _friendsGroup[friend.name] = friend;
	  	  populateGroup(friend);
	  	  updateCalendar();
	  }
	}
});
/** getTarget gets the target of a click 
 * 
 * @param e the event 
 * @returns the target element
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    if (target.parentNode) {
    	target = target.parentNode;
    }
    return target; 
}
/** populateFriendNames populates the name to friend map
 * 
 * @param friendInformation the information of the friends
 */
function populateFriendNames(friendInformation) {
	for (var friend in friendInformation) {
		var friendInfo = friendInformation[friend];
		_friendNameMap[friendInfo.name] = friendInfo; 
	}
}

/** populateShowNames populates the name to show map
 *                    and show names for autocorrect
 *  
 * @param showInformation the information of the shows
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
/** populateFriendsFor show populates the friends for a specific show
 * 
 * @param friends is the list of friends for the show
 */
function populateFriendsForShow(friends) {
	$("#friends-show-content").empty();
	var friendsDiv = document.getElementById('friends-show-content');
	if (friends.length == 0) {
		$("#no-friends").show();
	} else {
		$("#no-friends").hide();
	}
	
	for (friendsNum in friends) {
		var wrap = document.createElement("div");
		wrap.className = "friendScrollWrap";
		
		var content = document.createElement("div");
		content.className = "scrollContent";
		
		var name = document.createElement("p");
		name.innerHTML = friends[friendsNum].name;
		name.className = "content-name";
		
		var email = document.createElement("p");
		email.innerHTML = "(" + friends[friendsNum].email + ")";
		email.className = "content-email";
		
		var episodeNum = document.createElement("p");
		episodeNum.innerHTML = "Episode: " + friends[friendsNum].episodeNum;
		episodeNum.className = "content-num";
		
		
		var image = document.createElement("IMG");
		image.setAttribute("src", friends[friendsNum].avatar);
		image.setAttribute("alt", friends[friendsNum].name);
		content.appendChild(name);
		content.appendChild(email);
		content.appendChild(episodeNum);
		content.appendChild(image);
		wrap.appendChild(content);
		friendsDiv.appendChild(wrap);
	}
}

/** populateGroup adds a friend to the group
 *  
 * @param friend to add
 */
function populateGroup(friend) {
	var groupDiv = document.getElementById('myGroup');
	
	var wrap = document.createElement("div");
	wrap.className = "group";
	
	var content = document.createElement("div");	
	content.className = "content";
	
	var name = document.createElement("p");
	name.innerHTML = friend.name;
	name.className = "content-name";
	
	var email = document.createElement("p");
	email.innerHTML = "(" + friend.email + ")";
	email.className = "content-email";
	
	var episodeNum = document.createElement("p");
	episodeNum.innerHTML = "Episode: " + friend.episodeNum;
	console.log(friend);
	episodeNum.className = "content-num";
	
	var image = document.createElement("IMG");
	image.setAttribute("src", friend.avatar);
	image.setAttribute("alt", friend.name);
	content.appendChild(name);
	content.appendChild(email);
	content.appendChild(episodeNum);
	content.appendChild(image);
	wrap.appendChild(content);
	groupDiv.appendChild(wrap);
}

/** populateShow populates the show information section of the page
 * 
 * @param show is the show to populate with
 */
function populateShow(show) {
	_currShow = show;
	$("#friendsThatWatchTitle").text("Your Friends that watch: " + show.name);
	var wrap = document.getElementById('show');
	
	var wrapped = document.createElement("div");
	wrapped.className = "wrapped";
	
	var name = document.createElement("p");
	name.innerHTML = show.name;
	name.className = "show-name";
	
	
	var genres = document.createElement("p");
	var genresVal = show.genres;
	genres.innerHTML = genresVal.substring(1, genresVal.length - 1);
	genres.className = "show-genres";
	
	var image = document.createElement("IMG");
	image.setAttribute("src", show.image);
	image.setAttribute("alt", show.name);
	image.className = "show-image";
	
	wrapped.appendChild(name);
	wrapped.appendChild(genres);
	wrapped.appendChild(image);Strings = [];
	wrap.appendChild(wrapped);
}

/* binds the search button to a click and performs the appropriate action */
$("#search-button").bind('click', function() {
	var text = $("#search-text").val();
	if (text != "") {
		var show = _showNameMap[text];
		if (show !== undefined) {
			$(".wrapped").remove();
			if (show.id != _currShow.id) {
				$(".group").remove();
				_friendsGroup = [];
				_friendsGroup[_currUser.name] = _currUser;
				var postParameters = {showID : show.id};
				$.post("/currEpisode", postParameters, function(responseJSON) {
					var response = JSON.parse(responseJSON);
					console.log(response.epNum);
					_currUser.episodeNum = response.epNum;
					populateGroup(_currUser);
				})
				
			}
			populateShow(show);Strings = [];
			var postParameters = {
				showId : show.id	
			};
			$.post("/friends/for/show", postParameters, function(responseJSON) {
				var response = JSON.parse(responseJSON);
				_currFriends = response.friendsForShow;
				populateFriendNames(_currFriends);
				populateFriendsForShow(_currFriends);
			})
		} else {
			$("#no-matches").fadeIn(300);
		}
	}
});

/* binds the request button to a click and performs the appropriate action */
$("#request-button").bind('click', function() {
	var events = $('#togetherCal').fullCalendar('clientEvents');
	var toPost;
	for(var i in events){
		if(events[i].rendering == 'none'){
			toPost = events[i];
			break;
		}
	}
	if(toPost !== undefined){
		// gets the time block information
		var startM = toPost.start;
		var endM = toPost.end;
		var e = [startM.day(), startM.get('hours'), startM.get('minutes'), 
		         endM.day(), endM.get('hours'), endM.get('minutes')];
		var toSend = JSON.stringify(e);
		//Get the group 
		var group = [];
		var num = 0;
		for(var i in _friendsGroup){
			var friend = _friendsGroup[i];
			if (friend.name != _currUser.name) {
				group[num] = _friendsGroup[i].email;
				num++;
			}
		}
		var groupToReturn;
		if (_friendGroupsToPost.length == 0) {
			groupToReturn = JSON.stringify(group);
		} else {
			toSend = "";
			groupToReturn = JSON.stringify(_friendGroupsToPost);
		}
//		friendGroups = JSON.stringify(_friendGroupsToPost);
		var postParameters = {
		    showId: _currShow.id,
		    friendGroups: groupToReturn,
		    time: toSend
		};
		// make the post to send the request
		$.post("/send/watch/together/requests", postParameters, function(responseJSON) {
			swal({
				title: "You're now scheduled. Invitations sent to friends!",
				confirmButtonColor: "#389738",
				confirmButtonText: "Ok",
				closeOnConfirm: true,
			});
		})
	}else{
		// in the event that the user hasn't scheduled an event yet
		swal({
			title: "You didn't schedule an event. Would you like to?",
			showCancelButton: true,
			confirmButtonColor: "#389738",
			confirmButtonText: "Yes!",
			closeOnConfirm: true,
			closeOnCancel: true
		},
		function(isConfirm){
			if (isConfirm) {
				setTimeout(function() {
					scheduleGroup();
				}, 100);
			}
		});
	}
	
});
/* binds the clear button to a click and performs the appropriate action */
$("#clear-button").bind('click', function() {
	var length = $(".group").children().length;
	if (length > 1) {
		$(".group").remove();
		populateFriendsForShow(_currFriends);
		_friendsGroup = [];
		_friendsGroup[_currUser.name] = _currUser;
		populateGroup(_currUser);
	}
	updateCalendar();
});

function updateCalendar(){
	$('#togetherCal').fullCalendar('removeEvents');
	toPost = [];
	var num = 0;
	for(var i in _friendsGroup){
		toPost[num] = _friendsGroup[i].email;
		num++;
	}
	var numUsers = toPost.length;
	toPost = JSON.stringify(toPost);
	var postParameters = {users:toPost};
	$.post("/updateCal", postParameters, function(responseJSON){
		response = JSON.parse(responseJSON);
		var commonTime = response.commonTime;
		var colorNum = 1;
		for (var map in commonTime){
			var color = getColor(colorNum, numUsers);
			colorNum += 1;
			var currMap = commonTime[map];
			var blockId = 0;
			for(var i in currMap){
				var currBlock = currMap[i];
				var startIndex = currBlock.startIndex;
				var endIndex = currBlock.endIndex;
				makeBlock(startIndex, endIndex, "FREE", color, 'background', blockId, false);
				blockId++;
			}
		}
	})
}
/* binds the schedule button to a click */
$("#schedule-button").bind('click', function() {
	scheduleGroup();
});

function scheduleGroup() {
	_friendGroupsToPost = [];
	updateCalendar();
	var runtime = _currShow.runtime;
	toPost = [];
	var num = 0;
	for(var i in _friendsGroup){
		toPost[num] = _friendsGroup[i].email;
		num++;
	}
	var numUsers = toPost.length;
	toPost = JSON.stringify(toPost);
	var showId = _currShow.id;
	postParameters = {users: toPost, runtime: runtime};
	$.post("/scheduleGroup", postParameters, function(responseJSON){
		response = JSON.parse(responseJSON);
		if(response.status === '1'){
			swal({
				title: "There wasn't a common time for your whole group, but here are some options:",
				showCancelButton: true,
				confirmButtonColor: "#389738",
				confirmButtonText: "Change my group",
				cancelButtonText: "Display some options",
				closeOnConfirm: true,
				closeOnCancel: true
			},
			function(isConfirm){
				if (!isConfirm) {
		   			var partitions = response.partitions;
		   			var optionStrings = [];
		   			for(var i in partitions){
		   				var optionString = optionStrings[i];
   						if (optionString === undefined) {
   							optionStrings[i] = "";
   						}
   						var missing = false;
		   				var groupOption = [];
		   				for(var j in partitions[i]){
		   					var groupMember = [];
		   					var pairing = partitions[i][j];
		   				    if (pairing.length > 1) {
		   				    	for (var k in pairing){
			   						var tb = partitions[i][j][k];
			   						var startTime = getStartMoment(tb.sIndex);
			   						var start = startTime.format("dddd h:mmA");
			   						var endTime = getEndMoment(startTime, tb.sIndex, tb.eIndex);
			   						var end = endTime.format("dddd h:mmA");
			   						var option = groupOption[i];
			   					    groupMember[k] = tb.name;
			   					}
			   					var memberString = " on " + start + "<br> to " + end + "<br> Group: ";
			   					for (memberNum in groupMember) {
			   						memberString += " " + groupMember[memberNum] + ",";
			   					}
			   					groupOption[j] = memberString;
		   				    } else {
		   				    	groupOption[j] = "Missing: " + pairing[0].name;
		   				    	missing = true;
		   				    }
		   					
		   				}
		   				var groupString = optionStrings[i];
		   				var length = groupOption.length;
		   				for (groupNum in groupOption) {
		   					var groupedString = groupOption[groupNum]
		   					if (missing != true) {
		   						groupedString = groupedString.substring(0, groupedString.length - 1);
		   					}
		   					if (groupNum < length - 1) {
		   						groupString += groupedString + ".<br><br>";
		   					} else {
		   						groupString += groupedString + ".";
		   					}		   					
		   				}
		   				optionStrings[i] = groupString;
		   			}
		   			var box = bootbox.dialog({
		   			  message: "Pick a time to watch " + _currShow.name + ":",
		   			  title: "Here are some options:",
		   			  buttons: {
		   			    first: {
		   			      label: optionStrings[0],
		   			      className: "btn-one",
		   			      callback: function() {
		   			    	  displayOptionBlock(partitions, 0);
		   			      }
		   			    },
		   			    second: {
		   			      label: optionStrings[1],
		   			      className: "btn-two",
		   			      callback: function() {
		   			    	displayOptionBlock(partitions, 1);
		   			      }
		   			    },
		   			    third: {
		   			      label: optionStrings[2],
		   			      className: "btn-three",
		   			      callback: function() {
		   			    	displayOptionBlock(partitions, 2);
		   			      }
		   			    },
		   			   avail: {
		   			      label: "It looks like there are no times <br> which you can schedule. <br><br> Try increasing you're availibility!",
		   			      className: "btn-avail",
		   			      callback: function() {
		   			        
		   			      }
		   			    },
		   			    no: {
		   			      label: "Go back",
		   			      className: "btn-back",
		   			      callback: function() {
		   			        
		   			      }
		   			    }
		   			  }
		   			});
		   			box.find('.modal-content').css(
		   					{'background': 'url("http://marshall-design.com/codepen/abs6.jpg")',
		   					 'font-weight' : 'bold',
		   					  'color': 'white',
		   					  'font-size': '20px',
		   					  'font-weight' : 'bold'}
		   			);
		   			var cssHidden = {'visibility': 'hidden'};
		   			var cssGeneric = {'width' : '90%', 
		   					 'background-color' : 'DeepSkyBlue',
		   					 'padding' : 'auto',
		   					 'color' : 'white',
		   					 'margin-left' : '5%',
		   					 'margin-right' : '5%',
		   					 'margin-top' : '10px',
		   					 'font-size' : '18px',
		   					 'font-color' : 'black',
		   					 'font-weight' : 'bold',
		   					 'overflow'  : 'auto'};
		   			var buttonOneCss = cssGeneric;
		   			var buttonTwoCss = cssGeneric;
		   			var buttonThreeCss = cssGeneric;
		   			var buttonNoOptionsCss = cssHidden;
		   			var buttonBackCss = cssGeneric
		   			if (optionStrings[0] === undefined && optionStrings[1] === undefined && optionStrings[2] === undefined) {
		   				buttonNoOptionsCss = cssGeneric;
		   				buttonBackCss = cssHidden;
		   			}
		   			if (optionStrings[0] === undefined) {
		   				buttonOneCss = cssHidden;
		   			}
		   			if (optionStrings[1] == undefined) {
		   				buttonTwoCss = cssHidden;
		   			}
		   			if (optionStrings[2] === undefined) {
		   				buttonThreeCss = cssHidden;
		   			}
		   			box.find(".btn-one").css(
		   					buttonOneCss
		   		   );
		   			box.find(".btn-two").css(
		   					buttonTwoCss
		   		   );
		   			box.find(".btn-three").css(
		   					buttonThreeCss
		   		   );
		   			box.find(".btn-avail").css(
		   					buttonNoOptionsCss
		   		   );
		   			box.find(".btn-back").css(
		   					buttonBackCss
		   		   );
				}
			});
		}else{
			var tb = response.tb;
			var startIndex = tb.startIndex;
			var endIndex = tb.endIndex;
			makeBlock(startIndex, endIndex, _currShow.name, "blue", 'none', _currShow.id, true);
		}
	})
}



function displayOptionBlock(partitions, num) {
	var count = 0;
	for(var i in partitions[num]){
 		  var name = "";
 		  var sIndex = -1;
 		  var eIndex = -1;
 		  var pairing = partitions[num][i];
 		  if (pairing.length > 1) {
 			 var friendGroup = [];
 			 for(var j in pairing){
 	 			  tb = partitions[num][i][j];
 	 			  sIndex = tb.sIndex;
 	 			  eIndex = tb.eIndex;
 	 			  name += tb.name + " ";
 	 			  friendGroup[j] = tb;	
 	 		  }
 			 if (friendGroup.length > 0) {
 				 _friendGroupsToPost[count] = friendGroup;
 	 			 count += 1; 
 			 }
 	 		  makeBlock(sIndex, eIndex, name , 'blue','none', _currShow.id, false);  
 		  }
 	  } 
}

function getColor(i, numUsers){
	var expression = i / numUsers;
	return "rgba(75,255,61," + expression + ")";
	
}

$( "#search-text" ).bind('input propertchange', function() {
	$("#no-show").fadeOut(200);
});

function isValidMove(event){
	var array = $('#togetherCal').fullCalendar('clientEvents');
	for(i in array){
		if(array[i].id != event.id && 
				array[i].rendering == 'background' && 
				array[i].color === "rgba(75,255,61,1)"){
			if(event.start < array[i].start || event.end > array[i].end){
				continue;
			} 
			if (event.end > array[i].start && event.start < array[i].end){
	        	return true;
	    	}
		}
	}
	return false;
}


function initCalendar() {
	$('#togetherCal').fullCalendar({
		header: {
			left: '',
			center: '',
			right: ''
		},
		defaultView: 'agendaWeek',
		height: 700,
		allDaySlot: false,
		theme: false,
		fixedWeekCount: false,
		timezone: "local",
		eventStartEditable: true,
		eventDrop: function(event, delta, revertFunc, jsEvent, ui, view ){
			if(!isValidMove(event)){
				revertFunc();
			}
		},
		eventOverlap: function(stillEvent, movingEvent){
			return stillEvent.rendering === 'background';
		},
	})
}


function makeBlock(startIndex, endIndex, name, color, rendering, id, editable){
	var startMoment = getStartMoment(startIndex);
	var endMoment = getEndMoment(startMoment, startIndex, endIndex);
	var event = {
			title : name,
			start : startMoment,
			end : endMoment,
			id: id,
			color: color,
			rendering: rendering,
			startEditable: editable
	}
	$('#togetherCal').fullCalendar('renderEvent', event, true);
}

function getStartMoment(startIndex){
	return moment().day('Sunday')
	.hours(startIndex / 2.0)
	.minute((startIndex % 2) * 30)
	.seconds(0)
	.milliseconds(0);
}

function getEndMoment(startMoment, startIndex, endIndex){
	var startclone = moment(startMoment);
	return startclone.add((endIndex - startIndex + 1) *30, 'minutes');
}