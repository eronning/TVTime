/* global variables */
var _currName;
var _currEmail;
var _currPassword;

var _showNumSeasons;
var _showEpisodes;
var _showSeasons;
var _showName;
var _showId;

var _currEpisodeNum;

var _nameToEpNumMap = [];

var _showWrap1;
var _showWrap2;
var _showInfoWrap;
/* sets up the page */
$(document).ready(function () {
	_showWrap1 = document.getElementById("showWrap1");
	_showWrap2 = document.getElementById("showWrap2");
	_showInfoWrap = document.getElementById("showInfoWrap");
	init();
});

/* initializes the page */
function init() {
	$.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _currName = response.name;
        _currEmail = response.email;
        _currPassword = response.password;
    })
    $.post("/gather/show/data", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _numSeasons = response.numSeasons;
        _showSeasons = response.seasons;
        _showEpisodes = response.episodes;
        _currEpisodeNum = response.currEpisodeNum;
        _showId = response.id;
        _showName = response.name;
        populateEpisodeNames(_showEpisodes);
        populateShowWrap(response);
        populateSelect(response);
        populateShowInfo(response.seasons, 1);
        populateCurrentEpisode(response.seasons[0][0]);
    })
}

/** getTarget gets the target of an event
 * 
 * @param e the event
 * @returns the target element
 */
function getTarget(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    return target; 
}

/* binds to the clicking of a button that updates the users show */
$("#showEpisodeInfo").mousedown(function(event) {
	var target = getTarget(event);
	
	var tagName = $(target).prop("tagName");
	if (tagName.toLowerCase() == "button") {
		var epName = $("#currentEpisode").text();
		epName = epName.substring(9);
		var epNum = _nameToEpNumMap[epName];
		var children = $("#showWrap2").children();
		var child = $(children)[3];
		console.log(_currEpisodeNum);
		console.log(epNum);
		if (epNum != _currEpisodeNum) {
			_currEpisodeNum = epNum;
			$(child).fadeOut(300, function() {
				$(child).text("You are currently on episode: " + _currEpisodeNum);
				$(child).fadeIn(300);
			}) 
			var postParameters = {
					showId: _showId,
					episodeNum: _currEpisodeNum
			};
			$.post("/update/current/episode", postParameters, function(responseJSON) {})
		}
	}
});

/** populateEpisodeNames fills the name to episode number map
 * 
 * @param episodes is the episodes to fill the map
 */
function populateEpisodeNames(episodes) {
	for (epNum in episodes) {
		_nameToEpNumMap[episodes[epNum].title] = parseInt(epNum) + 1;
	}
}

/** populateSelect populates the select element for seasons
 * 
 * @param response is data for filling the season select
 */
function populateSelect(response) {
	var selectSeasons = document.getElementById('seasonSelect');
	var numSeasons = response.seasons.length;
	for (num = 1; num <= numSeasons; num++) {
		var option = document.createElement('option');
		option.text = num;
		option.value = num;
		selectSeasons.appendChild(option);
	}
	
}
/* binds the seasons select to change and updates the show information */
$("#seasonSelect").bind('change', function() {
	$("#showInfo").empty();
	populateShowInfo(_showSeasons, $(this).val());
});

/** populateCurrentEpisode populates the current episode 
 * 
 * @param episode to populate with
 */
function populateCurrentEpisode(episode) {
	var divToPopulate = document.getElementById('showEpisodeInfo');
	$("#currentEpisode").text("Episode: " + episode.title);
	
	var episodeImage = document.createElement("IMG");
	episodeImage.setAttribute("src", episode.image);
	episodeImage.setAttribute("alt", episode.title);
	episodeImage.className = "episode-image";
	divToPopulate.appendChild(episodeImage);
	
	var episodeDescription = document.createElement("div");
	episodeDescription.innerHTML = episode.description;
	episodeDescription.className = "episode-description";
	divToPopulate.appendChild(episodeDescription);
    
	
	
	var episodeHasAired = document.createElement("div");
	episodeHasAired.innerHTML = "This episode has not yet been aired.";
	if (episode.hasAired) {
		episodeHasAired.innerHTML = "This episode was aired on:  " + episode.airDate + ".";
	}
	episodeHasAired.className = "episode-hasAired";
	divToPopulate.appendChild(episodeHasAired);
	
	var updateEpisodeBtn = document.createElement('button');
	$(updateEpisodeBtn).text('Make this your new current episode!');
	updateEpisodeBtn.className = "updateEpisode-button";
	divToPopulate.appendChild(updateEpisodeBtn);
	
	
}

/** populateShowInfo populates the show information (aka the episodes per season
 * 
 * @param seasons is the seasons in a show
 * @param seasonNum is the number of the season to populate
 */
function populateShowInfo(seasons, seasonNum) {
	var divToPopulate = document.getElementById('showInfo');
	_currSeasonNum = seasonNum - 1;
	var season = seasons[seasonNum - 1];
	for (episodeNum in season) {
		var episode = season[episodeNum];
		var episodeDiv = document.createElement("div");
		episodeDiv.className = "episodeDiv";
		
		var episodeTitleDiv = document.createElement("div");
		episodeTitleDiv.className = "episodeTitle";
		episodeTitleDiv.innerHTML = "Episode " + episode.episodeNum +  " : " + episode.title;
		episodeDiv.appendChild(episodeTitleDiv);
		episodeDiv.onclick = function (event) {
			var episodeInfo = getTarget(event).innerHTML;
			if (episodeInfo.charAt(0) == 'E') {
				$("#showEpisodeInfo").empty();
				var startCutOff = 7;
				var endCutOff = episodeInfo.search(":");
				var episodeNum = episodeInfo.substring(startCutOff, endCutOff - 1);
				populateCurrentEpisode(_showEpisodes[parseInt(episodeNum) - 1]);
			}
		}
		divToPopulate.appendChild(episodeDiv);
	}
}

/** populateShowWrap populates the wrap for the show 
 * 
 * @param response contains all the show information
 */
function populateShowWrap(response) {
	
	var showImage = document.createElement("IMG");
	showImage.setAttribute("src", response.image);
	showImage.setAttribute("alt", response.name);
	showImage.className = "show-image";
	_showWrap1.appendChild(showImage);
	
	var showTitle = document.createElement("div");
	showTitle.innerHTML = response.name;
	showTitle.className = "show-title";
	_showWrap2.appendChild(showTitle);
	
	var showDescription = document.createElement("div");
	showDescription.innerHTML = response.showDescription;
	showDescription.className = "show-description";
	_showWrap2.appendChild(showDescription);
	
	var showEpisode = document.createElement("div");
	showEpisode.innerHTML = "You are currently on episode: " + response.currEpisodeNum;
	showEpisode.className = "show-episode";
	_showWrap2.appendChild(showEpisode);
    
	var showRuntime = document.createElement("div");
	showRuntime.innerHTML = "Episodes for this show last: " + response.runtime + " minutes";
	showRuntime.className = "show-runtime";
	_showWrap2.appendChild(showRuntime);
	
	var showIsEnded = document.createElement("div");
	showIsEnded.innerHTML = "This show is still being aired.";
	if (response.isEnded) {
		showIsEnded.innerHTML = "This show is no longer being aired.";
	}
	showIsEnded.className = "show-isEnded";
	_showWrap2.appendChild(showIsEnded);
	
	
	var hasShow = document.createElement("div");
	var showButton = document.createElement("button");
	
	//in the event that the user has the show in their list
	if (response.hasShow) {
		var hasShow = document.createElement("div");
		hasShow.innerHTML = "This show is in your list of favorites.";
		hasShow.className = "hasShow";
		_showWrap2.appendChild(hasShow);
		showButton.innerHTML = "Remove this show";
		showButton.id = "removeShow-button";
		showButton.className = "removeShow-button";
		showButton.onclick = function () {
			//posts to remove the show from the users list
			$.post("/show/remove", "", function(responseJSON) {
			})
			$("#removeShow-button").fadeOut(300, function() {
				hasShow.innerHTML = _showName + " has been removed from your list of shows.";
				hasShow.className = "hasShow";
			    _showWrap2.appendChild(hasShow);
			});
		}	
	// in the event that the user doesn't have the show in their list
	} else {
		showButton.innerHTML = "Add this show";
		showButton.id = "addShow-button";
		showButton.className = "addShow-button";
		showButton.onclick = function () {
			// posts to add the show to the users list
			$.post("/show/add", "", function(responseJSON) {
			})
			$("#addShow-button").fadeOut(300, function() {
				hasShow.innerHTML = _showName + " has been added to your list of shows.";
				hasShow.className = "hasShow";
			    _showWrap2.appendChild(hasShow);
			});
		}
	}
	_showWrap2.appendChild(showButton);
}