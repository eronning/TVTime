var _epNum = 0;
//var _loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";



$(document).ready(function(responseJSON){
	$.post("/getRecentlyWatched", "", function(responseJSON) {
		var response = JSON.parse(responseJSON);
	    var recentlyWatched = response.recentlyWatched;
	    for (epNum in recentlyWatched) {
		    addEp(recentlyWatched[epNum], $("#epsToConfirm"));
	    }
	});
});

function addEp(ep, epList) {
	var epEntry = document.createElement('div');
	$(epEntry).addClass(' epEntry ');
	
	var topGroup = document.createElement('div');
	$(topGroup).addClass(' topGroup ');
	
	var headerDiv = document.createElement('div');
	var header = document.createElement('p');
	$(header).addClass(' showAndEp ');
	$(header).text("Show Title: Episode Title");
	$(headerDiv).append($(header));
	
	var btnGroup = document.createElement('div');
	$(btnGroup).addClass(' radioBtns ');
	var radioBtnGrpName = "missedChoice" + _epNum;
	_epNum++;
	var watchedCheck = '<input type="radio" name=' + radioBtnGrpName + ' value=\"yes\">Watched';
	var deferCheck = '<input type="radio" name=' + radioBtnGrpName + ' value="defer">Defer';
	var catchupCheck = '<input type="radio" name=' + radioBtnGrpName + ' value="catchup">Catch Up';
	$(btnGroup).append(watchedCheck);
	$(btnGroup).append(deferCheck);
	$(btnGroup).append(catchupCheck);
	$(headerDiv).append($(btnGroup));
	
	$(topGroup).append($(headerDiv));
	
	var btmGroup = document.createElement('div');
	var synopsis = document.createElement('p');
	console.log(ep);
	$(synopsis).text('Show/Hide Synopsis!');
	$(btmGroup).append($(synopsis));
	
	$(epEntry).append($(topGroup));
	$(epEntry).append($(btmGroup));
	epList.append($(epEntry));
}