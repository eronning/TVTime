$("#email-text").hide(0);
$("#password-text").hide(0);

var emailVal;
var passwordVal;

/* binds the login button to a click */
$("#login-button").bind('click', function(){
  var button = document.getElementById("login-button");
  
  var email = document.getElementById('email-text');
  var password = document.getElementById('password-text');
 

  $("#email-text").slideDown("slow");
  $("#password-text").slideDown("slow");
  
  emailVal = email.value;
  passwordVal = password.value;

  var postParameters = {
    email: emailVal,
    password: passwordVal,
  };
  // makes sure that the button is sign in when clicked 
  if (button.value == "signin") {
	// makes sure the user has entered in both fields 
    if (emailVal !== "" && passwordVal !== "") {
      // posts to the server to attempt to login
      $.post("/login", postParameters, function(responseJSON) {
        responseObject = JSON.parse(responseJSON);
        var response = responseObject.response;
        // the user login was invalid
        if (response !== "true") {
          if (response == "email") {
            document.getElementById("incorrect-login").innerHTML = "Invalid login.";
          }
          if (response == "password") {
            document.getElementById("incorrect-login").innerHTML = "Incorrect password.";
          }
          //highlights the sign up button
          var signup = document.getElementById("signup-button");
          signup.style.color = "yellow";
          signup.style.background = "black";
          signup.style.transform = "translateY(-5px)";
        } else {
        	// the login was valid and takes the user to their home page
        	    $.post("/getRecentlyWatched", "", function(responseJSON) {
        	    	var response = JSON.parse(responseJSON);
        	    	if (response.recentlyWatched.length === 0) {
        	    		location.href = "/home/" + emailVal;
        	    	} else {
        	    		var recentEpsString = stringifyRecentEps(response.recentlyWatched);
        	        	swal({
        	    			title: "Welcome back!",
        	    			text: "Here are the episodes you should've watched before your last login:\n " +
        	    					recentEpsString,
        	    			showCancelButton: true,
        	    			confirmButtonColor: "DeepSkyBlue",
        	    			cancelButtonText: "No! Let me make changes.",
        	    			confirmButtonText: "Yes, I'm all caught up!",
        	    			closeOnConfirm: true,
        	    			closeOnCancel: false
        	    			},
        	    			function(isConfirm){
        	    				if (!isConfirm) {
        	    					location.href = "/changeWatched";
        	    				} else {
        	    					location.href = "/home/" + emailVal;
        	    				}
        	    			}
        	    		);
        	    	}
        	    });
        }
      })
      password.value = "";
    }
  }
  button.value = "signin";
  button.innerHTML = "Sign In";

});

function stringifyRecentEps(recentEps) {
	var epsString = "\n ";
	for (epNum in recentEps) {
		var ep = recentEps[epNum];
		var epString = ep.showName + " (S" + ep.seasonNum + "E" + ep.epNum + "): " + ep.title;
		epsString = epsString + epString + "\n";
	}
	
	return epsString;
}