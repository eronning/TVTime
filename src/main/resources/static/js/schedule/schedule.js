$(document).ready(function(){
	initCalendar();
    makeEventList();
//    scheduleButton();
//    saveScheduleButton();
//    clearScheduleButton();
});

function initCalendar() {
	$('#scheduleCal').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'agendaDay,agendaWeek,month'
		},
		defaultView: 'agendaWeek',
		height: 700,
		allDaySlot: false,
		theme: false,
		fixedWeekCount: false,
		timezone: "local",
		editable: false,
		eventOverlap: function(stillEvent, movingEvent){
			return stillEvent.rendering === 'background';
		},
		eventClick: function(calEvent, jsEvent, view) {
			swal({
				title: "Are you sure you want to remove this block?",
				showCancelButton: true,
				confirmButtonColor: "#80E6B2",
				confirmButtonText: "Yup! Remove this event",
				closeOnConfirm: true,
				closeOnCancel: true
			},
			function(isConfirm){
				if (isConfirm) {
		   			$('#scheduleCal').fullCalendar('removeEvents', [calEvent.id]);
		   			var allEvents = $('#scheduleCal').fullCalendar('clientEvents');
		   			var toPost = [];
		   			var numEvents = 0;
		   			for(i in allEvents){
		   				if(allEvents[i].rendering === 'none'){
		   					var startM = allEvents[i].start;
		   					var endM = allEvents[i].end;
		   					var e = [allEvents[i].id, startM.day(), startM.get('hours'), startM.get('minutes'), 
		   					         endM.day(), endM.get('hours'), endM.get('minutes')];
		   					toPost[numEvents] = e;
		   					numEvents++;
		   				}
		   			}
		   			toPost = JSON.stringify(toPost);
		   			var postParameters = {events: toPost};
		   			console.log(postParameters);
		   			$.post("/saveSchedule", postParameters , function(responseJSON){
		   				
		   			})
				}
			});
	    }
	})
}

function makeEventList() {
	$.post("/timeblocks", "", function(responseJSON) {
		response = JSON.parse(responseJSON);
		var availBlocks = response.availBlocks;
		
		var eventNum = 0;
		
		for (blockInd in availBlocks) {
			var currBlock = availBlocks[blockInd];
			var startIndex = currBlock.startIndex;
			var endIndex = currBlock.endIndex;
			makeBlock(startIndex, endIndex, "FREE", "#007A29", 'background', eventNum, 0);
			eventNum++;
		}
		
		var schedule = response.schedule;
		for(i in schedule){
			var name = schedule[i];
			console.log(name);
			var sIndex = schedule[i].startIndex;
			var eIndex = schedule[i].endIndex;
			console.log(id);
			var id = schedule[i].id;
			var numTimes = schedule[i].recurrences;
			makeBlock(sIndex, eIndex, i, "blue", 'none', id, numTimes);
		}
	})
}

function makeBlock(startIndex, endIndex, name, color, rendering, id, numTimes){
	var startMoment = getStartMoment(startIndex);
	//if before now, add a week
	if (startMoment.isBefore(moment())) {
		startMoment.add(7, 'days');
	}
	var endMoment = getEndMoment(startMoment, startIndex, endIndex);
	for (i = 0; i < Math.min(numTimes, 10); i++) { 		
		var event = {
				title : name,
				start : startMoment,
				end : endMoment,
				id: id,
				color: color,
				rendering: rendering
		}
		console.log("Repeating")
		$('#scheduleCal').fullCalendar('renderEvent', event, true);
		startMoment.add(7, 'days');
		endMoment.add(7, 'days');
	}
	
	// for (i = 0; i < numTimes; i++) {
	// 	$('#scheduleCal').fullCalendar('renderEvent', event, true);
	// }
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

//function scheduleButton(){
//	$('#schedule').on('click', function(event){
//		$.post("/schedule", "", function(responseJSON){
//			response = JSON.parse(responseJSON);
//			if(response.size == "0"){
//				alert("Nothing Scheduled! Add shows or add more available time!");
//			}else{
//				removeAll();
//				makeEventList();
//			}
//		})
//	})
//}
//
//function removeAll(){
//	$('#scheduleCal').fullCalendar('removeEvents');
//}
//
//function clearScheduleButton(){
//	$('#clearSchedule').on('click', function(event){
//		var allEvents = $('#scheduleCal').fullCalendar('clientEvents');
//		for(i in allEvents){
//			if(allEvents[i].rendering === 'none'){
//				console.log(allEvents[i]);
//				$('#scheduleCal').fullCalendar('removeEvents', allEvents[i].id);
//			}
//		}
//	})
//}
//
//function saveScheduleButton(){
//	$('#saveSchedule').on('click', function(event){
//		var allEvents = $('#scheduleCal').fullCalendar('clientEvents');
//		var toPost = [];
//		var numEvents = 0;
//		for(i in allEvents){
//			if(allEvents[i].rendering === 'none'){
//				var startM = allEvents[i].start;
//				var endM = allEvents[i].end;
//				var e = [allEvents[i].id, startM.day(), startM.get('hours'), startM.get('minutes'), 
//				         endM.day(), endM.get('hours'), endM.get('minutes')];
//				toPost[numEvents] = e;
//				numEvents++;
//			}
//		}
//		toPost = JSON.stringify(toPost);
//		var postParameters = {events: toPost};
//		console.log(postParameters);
//		$.post("/saveSchedule", postParameters , function(responseJSON){
//			
//		})
//	})
//}

