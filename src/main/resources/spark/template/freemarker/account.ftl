<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
    <link rel="stylesheet" href="../css/menu.css">
    <script src="../js/menu.js"></script>
    <link rel="stylesheet" href="../css/normalize.css">
    <link rel="stylesheet" href="../css/html5bp.css">
    <link rel="stylesheet" href="../css/account/account.css">
    <link rel="stylesheet" href="../css/simplegrid.css">  
    <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
    <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
  </head>
  <body>
  
   <div id='cssmenu'>
    <ul id='nav'>
       <li><a href='#'>Home</a></li>
       <li><a href='#'>My Shows</a></li>
	   <!-- <li><a href='#'>My Groups</a></li> -->
	   <li><a href='#'>My Schedule</a></li>
	   <li><a href='#'>Watch Together</a></li>
       <li><a href='#'>Availability</a></li>
       <li><a href='#'>Friends</a></li>
       <li><a href='#'>Account</a></li>
       <li><a href='#'>Logout</a></li>
    </ul>
</div>
    
    <p id="title">Your Account</p>
    <hr>
    
    <div id="wrap1" class="wrapper">
      <p id="info"> This is your current account information. If you wish to update this information just fill out the correct portions of the form. </p>
      <p id="name" class="text-naming"> Name: </p>
      <input type="text" id="name-text" class="text-area" placeholder='Name'>
      <p id="email" class="text-naming"> Email: </p>
      <input type="email" id="email-text" class="text-area" placeholder='Email'>
      <p id="currPassName" class="text-naming"> Current Password: </p>
      <input type="password" id="current-password-text" class="text-area" placeholder='Current Password'>
      <p id="newPassName" class="text-naming"> New Password: </p>
      <input type="password" id="new-password-text" class="text-area" placeholder='New Password'>
      <p id="confirmNewPassName" class="text-naming"> Confirm New Password: </p>
      <input type="password" id="confirm-new-password-text" class="text-area" placeholder='Confirm New Password'>
      <p id="invalid-signup"></p>
      <button id="updateSettings-button"> Update Settings </button>
      <button id="changeSettings-button"> Change Your Settings </button>
    </div>
    
    <div id="wrap2" class="wrapper">
      <p id="info"> This is your current avatar information. If you wish to update this information just fill out the correct portions of the form. </p>
      <p id="name" class="text-naming"> Current Avatar: </p>
      <div id="avatar-div"><div>
      <select id="avatarOptions"></select>
      <button id="updateAvatar-button"> Update your Avatar </button>
      <button id="changeAvatar-button"> Change your Avatar </button>
    </div>
    
    
    
  </body>
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
  <script src="../js/jquery-2.1.1.js"></script>
  <script src="../js/ddslick.js"></script>
  <script src="../js/account/account.js"></script>
</html>