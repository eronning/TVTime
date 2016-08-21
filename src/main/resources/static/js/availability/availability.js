var eventList;
var eventNum = 0;

$(document).ready(function(){
    initCalendar();
    makeEventList();
    saveButton();
    clearSchedule();
});

function initCalendar() {
	$('#availCal').fullCalendar({
	    header: {
	    	right: 'prev,next today',
	    	center: 'title',
	    	left: 'none'
	    },
	    defaultView: 'agendaWeek',
		height: 700,
		selectable: true,
		allDaySlot: false,
		theme: false,
		selectHelper: true,
		fixedWeekCount: false,
		timezone: "local",
		events: eventList,
		select: function(start, end) {
			var eventData = {
					start: start,
					end: end,
					id: eventNum,
			};
			eventNum++;
			$('#availCal').fullCalendar('unselect');
			$('#availCal').fullCalendar('renderEvent', eventData, true); // stick? = false
		},
		selectOverlap: false,
		eventOverlap: false,
		editable: true,
	    eventDragStart: function(event){
	    	event.color = 'default';
	    },
	    eventResizeStart: function(event){
	    	event.color = 'default';
	    },
		eventClick: function(calEvent, jsEvent, view ) {
			swal({
				title: "Are you sure you want to remove this block?",
				showCancelButton: true,
				confirmButtonColor: "#80E6B2",
				confirmButtonText: "Yup! I'm no longer free at this time.",
				closeOnConfirm: true,
				closeOnCancel: true
			},
			function(isConfirm){
				if (isConfirm) {
		   			$('#availCal').fullCalendar('removeEvents', [calEvent.id]);
				}
			});
	    }
	})
}

function clearSchedule(){
	$("#clear").on('click', function(event){
		removeAll();
		eventNum = 0;
	})
}

function saveButton() {
	$("#save").on('click', function(event){
		var eArray = $('#availCal').fullCalendar('clientEvents');
		var events = new Array(eArray.length);
		var numEvents = 0;
		for(i in eArray){ 
			var startM = eArray[i].start;
			var endM = eArray[i].end;
			var e = [startM.day(), startM.get('hours'), startM.get('minutes'), 
			         endM.day(), endM.get('hours'), endM.get('minutes')];
			events[i] = e;
			numEvents++;
			setFree(eArray[i]);
		}
		var toSend = JSON.stringify(events);
		var postParameters = {events: toSend, numEvents: numEvents};
		$.post("/save", postParameters, function(responseJSON){
		});
		removeAll();
		makeEventList();
	});
}

function setFree(event) {
	event.color = "#007A29";
}

function makeEventList() {
	$.post("/timeblocks", "", function(responseJSON) {
		eventNum = 0;
    	response = JSON.parse(responseJSON);
    	var availBlocks = response.availBlocks;
    	for (blockInd in availBlocks) {
    		var currBlock = availBlocks[blockInd];
    		var startIndex = currBlock.startIndex;
    		var endIndex = currBlock.endIndex;
    		makeBlock(startIndex, endIndex, "#007A29");
    	}
    })
}

function makeBlock(startIndex, endIndex, color){
	var startMoment = getStartMoment(startIndex);
	var endMoment = getEndMoment(startMoment, startIndex, endIndex);
	var event = {
			title : "FREE",
			start : startMoment,
			end : endMoment,
			id: eventNum,
			color: color
	}
	eventNum++;
	$('#availCal').fullCalendar('renderEvent', event, true);
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

function removeAll(){
	$('#availCal').fullCalendar('removeEvents');
}