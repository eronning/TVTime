<!doctype html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
	<script src='../js/fullcalendar-2.3.1/lib/jquery.min.js'></script>
	<script src='../js/fullcalendar-2.3.1/lib/moment.min.js'></script>
	<script src='../js/fullcalendar-2.3.1/fullcalendar.js'></script>
   <link rel="stylesheet" href="../css/menu.css">
   
   <script src="../js/menu.js"></script>
   <script src="../js/availability/availability.js"></script>
   
   <link rel='stylesheet' href='../js/fullcalendar-2.3.1/fullcalendar.css' />
    
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
   <link rel="stylesheet" type="text/css" href="../css/availability/availability.css">   
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
	<!--	<h1> Weekly Availability </h1> -->
	<div id='availCal'> </div>
	<div id = 'availButtons'>
		<button id="save"> Save My Free Time</button>
		<button id="clear"> Clear Free Time</button>
	</div>
</body>
<html>