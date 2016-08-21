var _notications;
var _nameNoteMap = [];
var _showNoteMap = [];

function populateNotificationNames(notifications) {
	for (noteNum in notifications) {
		var note = notifications[noteNum];
		_nameNoteMap[note.senderName] = note;
		if (note.showName != null) {
			_showNoteMap[note.showName] = note; 
		}
	}
}


$(document).ready(function(){
    $.post("/page/information", {}, function(responseJSON) {
    	var response = JSON.parse(responseJSON);
    	var upcomingEvents = response.upcomingEvents;
    	$('#no-shows').hide();
    	if (upcomingEvents.length === 0) {
    		$('#no-shows').show();
    	} else {
        	for (ev in upcomingEvents) {
            	renderUpcomingEvents(upcomingEvents[ev], $('#upcomingEvents'));
        	}
    	}
    });
    
	toastr.options = {
			  "closeButton": true,
			  "debug": false,
			  "newestOnTop": true,
			  "progressBar": false,
			  "positionClass": "toast-near-top-right",
			  "preventDuplicates": false,
			  "showDuration": "300",
			  "hideDuration": "1000",
			  "timeOut": 0,
			  "extendedTimeOut": 0,
			  "showEasing": "swing",
			  "hideEasing": "linear",
			  "showMethod": "fadeIn",
			  "hideMethod": "fadeOut",
			  "tapToDismiss": true
			};
    $.post("/notifications", {}, function(responseJSON) {
    	var response = JSON.parse(responseJSON);
    	_notifications = response.notifications;
    	populateNotificationNames(_notifications);
    	console.log(_notifications);
    	for (notifNum in _notifications) {
    		var notif = _notifications[notifNum];
    		if (notif.isAcceptance) {
    			toastr["warning"](renderAcceptance(notif));
    		} else {
        		if (notif.isFriendReq) {
        			toastr["success"](renderFriendRequest(notif));
        			$(".toast-message").on('click', function(event) {
        				var target = getTarget(event);
        				var parent = target.parentNode;
        				var name = $(parent).text().split(" ")[0];
        				console.log($(target).text());
        				if ($(target).text() === "Accept") {
            				$.post("/friend/add", {"acceptedFriend": _nameNoteMap[name].senderID}, function(responseJSON) {
            				})
        				} else if ($(target).text() === "Deny") {
            				$.post("/friend/deny", {"deniedFriend": _nameNoteMap[name].senderID}, function(responseJSON) {
            				})
        				}
        			});
        		} else {
        			toastr["info"](renderWatchRequest(notif));
        			$(".toast-message").on('click', function(event) {
        				var target = getTarget(event);
        				var parent = target.parentNode;
        				var senderName = $(parent).text().split(" ")[0];
        				var showName = parseShowName(parent);
        				console.log(showName);
        				var postParameters = {
        						"senderId": _nameNoteMap[senderName].senderID,
        						"showId": _showNoteMap[showName].showID
        				};
        				if ($(target).text() === "Okay!") {
            				$.post("/acceptWatchRequest", postParameters, function(responseJSON) {
            					var response = JSON.parse(responseJSON);
            					if (response.warning != null) {
                					if (response.warning === "Behind") {
                						var numBehind = response.numBehind;

                						toastr["error"](renderBehindWatchRequest(notif, numBehind));
                	        			$(".toast-message").on('click', function(event) {
                	        				var target = getTarget(event);
                	        				var parent = target.parentNode;
                	        				var name = $(parent).text().split(" ")[0];
                	        				location.href = '/highlights/' + _showNoteMap[showName].showID + '/' + numBehind;
                	        			});
                					} else if (response.warning === "Ahead") {
                						var numAhead = response.numAhead;
                						toastr["error"](renderAheadWatchRequest(notif, numAhead));
                					}
            					}
            				})
        				} else if ($(target).text() === "No, thanks.") {
            				$.post("/rejectWatchRequest", postParameters, function(responseJSON) {
            					console.log(responseJSON);
            				})
        				}
        			});
        		}
    		}
    	}
    });
});

function escapeIllegalChars( myid ) {
    return myid.replace( /(:|\.|\[|\]|,)/g, "\\$1" );
}

function parseShowName(parent) {
	var notifTextSplit = $(parent).text().split(" ");
	var i = 0;
	var startIndex;
	while (i < notifTextSplit.length) {
		if (notifTextSplit[i] !== "requested") {
			i++;
		} else {
			startIndex = i + 3;
			break;
		}
	}
	var showName = "";
	while (startIndex < notifTextSplit.length) {
		if (!(notifTextSplit[startIndex] === "on:")) {
			showName += (notifTextSplit[startIndex] + " ");
			startIndex++;
		} else {
			return showName.substring(0, showName.length - 1);
		}
	}
}

/** getTarget gets the target of a click 
 * 
 * @param e the event 
 * @returns the target element
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    return target; 
}

function renderUpcomingEvents(ev, evList) {
	var evEntry = document.createElement('li');
	var timeLabel = document.createElement('time');
	$(timeLabel).addClass(' cbp_tmtime ');
	$(timeLabel).attr('datetime', '2013-04-10 18:30');
	$(timeLabel).append('<span>' + ev.date + '</span> <span>' + ev.time + '</span>');
	
	var icon = '<div class="cbp_tmicon cbp_tmicon-flower"></div>';
	var ep = ev.ep;
	var epInfo = document.createElement('div');
	$(epInfo).addClass(' cbp_tmlabel ');
	
	var showTitle = document.createElement('h2');
	$(showTitle).text(ep.showName + ': ' + ep.title);
	$(epInfo).append($(showTitle));
	
	var showDescrip = document.createElement('p');
	$(showDescrip).text(ep.description);
	$(epInfo).append($(showDescrip));
	
	$(evEntry).append($(timeLabel));
	$(evEntry).append($(icon));
	$(evEntry).append($(epInfo));
	evList.append($(evEntry));
}

function renderFriendRequest(notif) {
	var friendName = notif.senderName;
	var reqText = friendName.concat(" wants to be friends with you!<br />");  
	var acceptButton = "<button type=\"button\" class=\"reqButton accept\">Accept</button>";
	var denyButton = "<button type=\"button\" class=\"reqButton deny\">Deny</button>";
		
	return reqText.concat(acceptButton).concat(denyButton);
}

function renderWatchRequest(notif) {
	console.log(notif);
	var friendName = notif.senderName;
	var showName = notif.showName;
	var watchTime = notif.watchTime;
	var reqText = friendName.concat(" requested to watch ").concat(showName).concat(" on: ")
	.concat(watchTime).concat(notif.groupMembers + "<br />");
	var okayButton = "<button type=\"button\" class=\"reqButton okay\">Okay!</button>";
	var noButton = "<button type=\"button\" class=\"reqButton no\">No, thanks.</button>";
	return reqText.concat(okayButton). concat(noButton);
}

function renderAcceptance(notif) {
	if (notif.isFriendReq) {
		return notif.recipientName + " accepted your friend request!";
	} else {
		return notif.recipientName + " accepted your request to watch " + notif.showName + " together!";
	}
}

function renderAheadWatchRequest(notif, epsAhead) {
	return "Note that you're " + epsAhead + " episodes ahead of the group!";
}

function renderBehindWatchRequest(notif, epsBehind) {
	var reqText = "Oops! Looks like you're " + Math.abs(epsBehind) + " episodes behind the group!";
	var highlightsButton = "<button type=\"button\" class=\"reqButton highlights\">Highlights</button>";
	return reqText.concat(highlightsButton);
}
