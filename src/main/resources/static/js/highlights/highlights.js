$(document).ready(function(){
	$.post("/getHighlights", {}, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		console.log(response);
		for (epNum in response.watch) {
			populateCurrentEpisode('watch', response.watch[epNum]);
		}
		for (epNum in response.skip) {
			populateCurrentEpisode('skip', response.skip[epNum]);
		}
	})	
});

function populateCurrentEpisode(listId, episode) {
	console.log(episode);
	var divToPopulate = document.getElementById(listId);
	
	var wrap = document.createElement("div");
	wrap.className = "wrapper";
	
	var episodeTitle = document.createElement("div");
	episodeTitle.innerHTML = "<h3>Ep: " + episode.epNum + " -- "+ episode.title + " (Rated: " + episode.rating + ")" + "</h3>";
	episodeTitle.className = "episode-title";
	
	var episodeImage = document.createElement("IMG");
	episodeImage.setAttribute("src", episode.imgURL);
	episodeImage.setAttribute("alt", episode.title);
	episodeImage.className = "episode-image";
	
	var episodeDescription = document.createElement("div");
	episodeDescription.innerHTML = episode.description;
	episodeDescription.className = "episode-description";
	
	wrap.appendChild(episodeTitle);
	wrap.appendChild(episodeImage);
	wrap.appendChild(episodeDescription);
	divToPopulate.appendChild(wrap);
    
}