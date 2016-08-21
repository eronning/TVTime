/* global variables */
var _currName;
var _currEmail;
var _currPassword;
var _currAvatar;

/* the possible choices for avatar */
var ddData = [
              {
                  text: "Daenerys Targaryen",
                  value: 1,
                  selected: false,
                  description: "Mother of dragons",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/daenerys-targaryen.png"
              },
              {
                  text: "Tyrion Lannister",
                  value: 2,
                  selected: false,
                  description: "A angry midget",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/tyrion-lannister.png"
              },
              {
                  text: "Jamie Lannister",
                  value: 3,
                  selected: false,
                  description: "Tall dude missing a hand",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/jamie-lannister.png"
              },
              {
                  text: "Jon Snow",
                  value: 4,
                  selected: false,
                  description: "A lonely guy on a wall",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/jon-snow.png"
              },
              {
                  text: "Robb Stark",
                  value: 4,
                  selected: false,
                  description: "He's dead",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/robb-stark.png"
              },
              {
                  text: "Cersei Lannister",
                  value: 5,
                  selected: false,
                  description: "Bitch",
                  imageSrc: "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/cersei-lannister.png"
              }
          ];
/* setups up the page */
$(document).ready(function(){
	// posts for the page information
    $.post("/page/information", "", function(responseJSON) {
    	response = JSON.parse(responseJSON);
        _currName = response.name;
        _currEmail = response.email;
        _currPassword = response.password;
        _currAvatar = response.avatar;
        document.getElementById("name-text").value = _currName;
        document.getElementById("email-text").value = _currEmail;
        makeAvatarDiv();   
       
    })
    // makes the drop down for the avatar options
    $('#avatarOptions').ddslick({
    	data: ddData,
        width: 400,
        imagePosition: "right",
        selectText: "Select your avatar",
        onSelected: function (data) {
            console.log(data.selectedData.imageSrc);
            _currAvatar = data.selectedData.imageSrc;
            var avatarImage = document.getElementById("avatar-image");
            avatarImage.setAttribute("src", _currAvatar);
        }
    });
    
    $("#current-password-text").hide();
    $("#currPassName").hide();
    $("#new-password-text").hide();
    $("#newPassName").hide();
    $("#confirm-new-password-text").hide();
    $("#confirmNewPassName").hide();
    $("#avatarOptions").hide();
    $("#changeSettings-button").hide();
    $("#changeAvatar-button").hide();
    
});


/* makes the div that contains the user and their avatar */
function makeAvatarDiv() {
	var avatarDiv = document.getElementById("avatar-div");
    avatarDiv.className = "grid grid-pad";
    
    var avatarWrapper = document.createElement("div");
    avatarWrapper.className = "col-6-12 mobile-col-6-12";
    
    var avatarContent = document.createElement("div");
    avatarContent.className = "content";
    avatarContent.id = "avatarToAdjust";
    
    var name = document.createElement("p");
	name.innerHTML = _currName;
	name.className = "content-name";
	
	var email = document.createElement("p");
	email.innerHTML = "(" + _currEmail + ")";
	email.className = "content-email";
    
    var avatar = document.createElement("IMG");
    avatar.id = "avatar-image";
    avatar.setAttribute("src", _currAvatar);
    avatar.setAttribute("alt", _currName);
    
    avatarContent.appendChild(name);
    avatarContent.appendChild(email);
    avatarContent.appendChild(avatar);
    avatarWrapper.appendChild(avatarContent);
    avatarDiv.appendChild(avatarWrapper);
}
/* binds the name text to a property change */
$("#name-text").bind('input propertychange', function() {
	  var invalid = document.getElementById('invalid-signup');
	  invalid.innerHTML = "";
	  var name = document.getElementById('name-text');
	  name.style.color = "DeepSkyBlue";
	  name.style.borderBottom = "DeepSkyBlue";
	  name.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
	  if (name.value == "") {
	    invalid.innerHTML = "Please enter a name.";
	    name.style.color = "red";
	    name.style.borderBottom = "red";
	    name.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	  }
});
/* binds the confirming a password to a change */
$("#confirm-new-password-text").bind('input propertychange', function() {
	  var invalid = document.getElementById('invalid-signup');
	  invalid.innerHTML = "";
	  var password = document.getElementById('new-password-text');
	  var confirm =  document.getElementById('confirm-new-password-text');
	  password.style.color = "DeepSkyBlue";
	  password.style.borderBottom = "DeepSkyBlue";
	  password.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
	  confirm.style.color = "DeepSkyBlue";
	  confirm.style.borderBottom = "DeepSkyBlue";
	  confirm.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
	  if (password.value !== confirm.value) {
	    invalid.innerHTML = "The passwords must match.";
	    password.style.color = "red";
	    password.style.borderBottom = "red";
	    password.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	    confirm.style.color = "red";
	    confirm.style.borderBottom = "red";
	    confirm.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	  }
});
/* binds the update settings button to a click */
$("#updateSettings-button").bind('click', function() { 
	$("#name").hide();
	$("#name-text").hide();
	$("#email").hide();
	$("#email-text").hide();
	$("#updateSettings-button").hide();
	//shows all relevant information for updating the users settings
	$("#name").fadeIn(500);
	$("#name-text").fadeIn(500);
	$("#email").fadeIn(500);
	$("#email-text").fadeIn(500);
	$("#currPassName").fadeIn(500);
    $("#current-password-text").fadeIn(500);
    $("#newPassName").fadeIn(500);
    $("#new-password-text").fadeIn(500);
    $("#confirmNewPassName").fadeIn(500);
    $("#confirm-new-password-text").fadeIn(500);
    $("#changeSettings-button").fadeIn(500);
    $("#invalid-signup").fadeIn(500);
});
/* binds the update avatar button to a click */
$("#updateAvatar-button").bind('click', function() { 
	$("#updateAvatar-button").hide();
	//shows information for updating the users avatar 
	$("#avatarOptions").fadeIn(500);
	$("#changeAvatar-button").fadeIn(500);
	var div = document.getElementById("avatarToAdjust");
	div.style.marginTop = "-29.8%";
	
  
});
/* binds the change avatar button to a click where it updates the users avatar */
$("#changeAvatar-button").bind('click', function() { 
	$("#avatarOptions").hide();
	$("#changeAvatar-button").hide();
	$("#updateAvatar-button").fadeIn(500);
	var div = document.getElementById("avatarToAdjust");
	div.style.marginTop = "-50%";
	var postParameters = {
		name: _currName,
		email: _currEmail,
		avatar: _currAvatar
	};
	// posts to update the users avatar
	$.post("/account/avatar", postParameters, function(responseJSON) {
	})
});

/* binds the change settings button to a click where it changes the users settings */
$("#changeSettings-button").bind('click', function(){
	  var name = document.getElementById('name-text');
	  var email = document.getElementById('email-text');
	  var currPass = document.getElementById('current-password-text');
	  var newPass = document.getElementById('new-password-text');
	  var confirmNewPass = document.getElementById('confirm-new-password-text');

	  var nameVal = name.value;
	  var emailVal = email.value;
	  var currPassVal = currPass.value;
	  var newPassVal = newPass.value;
	  var newConfirmVal = confirmNewPass.value;

	  var postParameters = {
	    oldName : _currName,
	    newName : nameVal,
	    oldEmail : _currEmail,
	    newEmail : emailVal,
	    newPassword : newPassVal
	 };
	  //makes sure that the appropriate requirements for an account are met
	  var passwordMatch = (newPassVal == newConfirmVal);
	  var emailValid = ValidateEmail(emailVal);
	  if (nameVal !== "" && emailVal !== "" && currPassVal !== "" && newPassVal !== "" && newConfirmVal !== "") {
		if(currPassVal === _currPassword) {
		  if(passwordMatch) {
	        if (emailValid) {
	        	//posts to update the users settings
	          $.post("/account/settings", postParameters, function(responseJSON) {  
	        	  var responseObject = JSON.parse(responseJSON);
	        	  var response = responseObject.response;
	        	  if (response == false) {
	        		  document.getElementById('invalid-signup').innerHTML = "The email entered already has an account.";
	        	  } else {
	        		  _currName = nameVal;
	        		  _currEmail = emailVal;
	        		  _currPassword = newPassVal;
	        		  
	        		  document.getElementById("name-text").value = nameVal;
		              document.getElementById("email-text").value = emailVal;
		              $("#name").fadeOut(500);
		              $("#name-text").fadeOut(500);
		              $("#email").fadeOut(500);
		              $("#email-text").fadeOut(500);
		              $("#currPassName").fadeOut(500);
		              $("#current-password-text").fadeOut(500);
		              $("#newPassName").fadeOut(500);
		              $("#new-password-text").fadeOut(500);
		              $("#confirmNewPassName").fadeOut(500);
		              $("#confirm-new-password-text").fadeOut(500);
		              $("#invalid-signup").innerHTML = "";
		              $("#invalid-signup").fadeOut(500);
		              $("#changeSettings-button").fadeOut(500, function() {
		            	  $("#name").fadeIn(500);
			              $("#name-text").fadeIn(500);
			              $("#email").fadeIn(500);
			              $("#email-text").fadeIn(500);
			              $("#updateSettings-button").fadeIn(1000);
		              }); 
	        	  } 
	          })
	        }
	        else {
	          document.getElementById('invalid-signup').innerHTML = "The email entered is invalid.";
	        }
	      } else {
	        document.getElementById('invalid-signup').innerHTML = "The passwords entered don't match.";
	      }
	    } else {
	    	 document.getElementById('invalid-signup').innerHTML = "The current password entered is invalid.";
	    	 currPass.style.color = "red";
		     currPass.style.borderBottom = "red";
		     currPass.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)"; 
	    }
	  } else {
		//highlights all of the invalid sections of the form
	    document.getElementById('invalid-signup').innerHTML = "You must fill out all fields";
	    if (currPassVal == "") {
	      currPass.style.color = "red";
	      currPass.style.borderBottom = "red";
	      currPass.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	    }
	    if (nameVal == "") {
	      name.style.color = "red";
	      name.style.borderBottom = "red";
	      name.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	    }
	    if (emailVal == "") {
	      email.style.color = "red";
	      email.style.borderBottom = "red";
	      email.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	    }
	    if (newPassVal == "") {
	      newPass.style.color = "red";
	      newPass.style.borderBottom = "red";
	      newPass.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
	    }
	    if (newConfirmVal == "") {
	      confirmNewPass.style.color = "red";
	      confirmNewPass.style.borderBottom = "red";
	      confirmNewPass.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
		}
	  }   

});

/** ValidateEmail makes sure that a email is valid
 * 
 * @param the email to validate
 */
function ValidateEmail(email) {  
	  var mailFormat = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/; 
	  if(mailFormat.test(document.getElementById('email-text').value)) {    
	    return true;  
	  }  
	  else {  
	    return false;  
	  }  
	}


