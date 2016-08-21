/** ValidateEmail makes sure that email is valid
 * 
 * @param email is the email to check
 * @returns whether or not the email is valid
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

/* binds the name text  of the form to a property change */
$("#name-text").bind('input propertychange', function() {
  var invalid = document.getElementById('invalid-signup');
  invalid.innerHTML = "";
  var name = document.getElementById('name-text');
  name.style.color = "DeepSkyBlue";
  name.style.borderBottom = "DeepSkyBlue";
  name.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
  // highlights the text field if it is empty (as in backspaced to empty)
  if (name.value == "") {
    invalid.innerHTML = "Please enter a name.";
    name.style.color = "red";
    name.style.borderBottom = "red";
    name.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
  }
});
/* binds the confirm password text  of the form to a property change */
$("#confirm-password-text").bind('input propertychange', function() {
  var invalid = document.getElementById('invalid-signup');
  invalid.innerHTML = "";
  var password = document.getElementById('password-text');
  var confirm =  document.getElementById('confirm-password-text');
  password.style.color = "DeepSkyBlue";
  password.style.borderBottom = "DeepSkyBlue";
  password.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
  confirm.style.color = "DeepSkyBlue";
  confirm.style.borderBottom = "DeepSkyBlue";
  confirm.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
  // highlights the password text fields if the passwords don't match
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

/* binds the email text  of the form to a property change */
$("#email-text").bind('input propertychange', function() {
  var invalid = document.getElementById('invalid-signup');
  invalid.innerHTML = "";
  var email = document.getElementById('email-text');
  email.style.color = "DeepSkyBlue";
  email.style.borderBottom = "DeepSkyBlue";
  email.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, DeepSkyBlue 4%)";
  var emailValid = ValidateEmail(email.value); 
  //highlights the email text in the event that it is invalid 
  if (!emailValid) {
    invalid.innerHTML = "Please enter a valid email.";
    email.style.color = "red";
    email.style.borderBottom = "red";
    email.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
  }
});
/* binds the register button to a click */
$("#register-button").bind('click', function(){
  var name = document.getElementById('name-text');
  var email = document.getElementById('email-text');
  var password = document.getElementById('password-text');
  var confirm = document.getElementById('confirm-password-text');

  var nameVal = name.value;
  var emailVal = email.value;
  var passwordVal = password.value;
  var confirmVal = confirm.value;

  var postParameters = {
    name: nameVal,
    email: emailVal,
    password: passwordVal,
  };
  // makes sure that the form standards are met
  var passwordMatch = (passwordVal == confirmVal);
  var emailValid = ValidateEmail(emailVal);
  if (nameVal !== "" && emailVal !== "" && passwordVal !== "" && confirmVal !== "") {
    if(passwordMatch) {
      if (emailValid) {
    	//posts to the server to sign the user up
        $.post("/signup/results", postParameters, function(responseJSON) {     
          responseObject = JSON.parse(responseJSON);
          var response = responseObject.response;
          //highlights the email because it already exists
          if (response == false) {
            document.getElementById('invalid-signup').innerHTML = "The email entered already has an account.";
            email.style.color = "red";
            email.style.borderBottom = "red";
            email.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
          } else {
        	  //navigates to the welcome page
        	 location.href="../welcome/" + responseObject.encoded;
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
	//highlights the sections of the form that are invalid upon user attempt to register
    document.getElementById('invalid-signup').innerHTML = "You must fill out all fields";
    if (confirmVal == "") {
      confirm.style.color = "red";
      confirm.style.borderBottom = "red";
      confirm.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
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
    if (passwordVal == "") {
      password.style.color = "red";
      password.style.borderBottom = "red";
      password.style.background = "linear-gradient(to bottom, rgba(255,255,255,0) 96%, red 4%)";
    }

  }   

});
