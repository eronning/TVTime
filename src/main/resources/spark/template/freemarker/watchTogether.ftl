<!doctype html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
  
	<script src='../js/fullcalendar-2.3.1/lib/jquery.min.js'></script>
    <script src='../js/fullcalendar-2.3.1/lib/moment.min.js'></script>
    <script src='../js/fullcalendar-2.3.1/fullcalendar.js'></script>
     <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />
   <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
   <link rel="stylesheet" href="../css/menu.css">
   <script src="../js/menu.js"></script>
   <link rel='stylesheet' href='../js/fullcalendar-2.3.1/fullcalendar.css' />
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
   <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
   <link rel="stylesheet" type="text/css" href="../css/watchTogether/watchTogether.css">
      
   
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
<br>
   <div class="wrapper">
	<div id='togetherCal' class="togetherCol"></div>
	<div id='sidebar' class="sideBarCol">
		<div id='show' class="wrapping"> 
          <fieldset id="searchField">
            <label id="searchShowTitle" class="text-naming"> Show Search: </label>
            <input type="text" id="search-text" class="text-area" placeholder="Search">
            <button id="search-button"> Search </button>
          </fieldset>
          <p id="no-show" class="no-matches"> No show by that name could be found. </p>
          <p id="searched">${showToWatch}</p>
        </div>
		<div id='friends' class="wrapping"> 
		  <button id="request-button"> Send Request </button>
		  <button id="schedule-button"> Schedule Group </button>
		  <button id="clear-button"> Clear Group </button>
		  <label id="myGroupTitle" class="text-naming"> My Group: </label>
		  <div id="myGroup"></div>
		</div>
   </div>
  </div>
  <label id="friendsThatWatchTitle" class="text-naming"> Your Friends that watch </label>
  <div id="friends-show-content"></div>
  <p id="no-friends" class="no-matches"> None of your friends watch this show. </p> 
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
   <script src="../js/bootbox.min.js"></script>
  <script src="../js/watchTogether/watchTogether.js"></script>
</body>
<html>