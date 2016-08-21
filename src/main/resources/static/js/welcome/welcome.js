var _currUser;

$(document).ready(function(){
    $.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
    	_currUser = response.encodedId;
    })
    confirmButton();
});

function confirmButton() {
	$("#confirm").on('click', function(event){
		location.href = "/shows/" + _currUser;
	});
}

