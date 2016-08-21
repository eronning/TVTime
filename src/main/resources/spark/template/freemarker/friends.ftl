<!doctype html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
   <link rel="stylesheet" href="../css/menu.css">
   <link rel="stylesheet" href="../css/friends/friends.css">
   <link rel="stylesheet" href="../css/simplegrid.css">  
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
   <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
   <script src="../js/menu.js"></script>
   <title>${title}</title>
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
    
    <fieldset>
      <label id="search" class="text-naming"> Search For Friends: </label>
      <input type="text" id="search-text" class="text-area" placeholder='Search'>
      <button id="search-button"> Search </button>
    </fieldset>
    

    <div id="container" class="grid grid-pad"></div>
    <p id="no-friends"> Find and add your friends. </p>
    <p id="no-matches"> No matches found.</p>

    <script src="../js/friends/friends.js"></script>
</body>
<html>