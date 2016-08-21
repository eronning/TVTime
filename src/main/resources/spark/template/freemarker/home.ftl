<!DOCTYPE html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link rel="stylesheet" href="../css/menu.css">
   <link rel="stylesheet" href="../css/home/home.css">
   <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
   <script src="../js/menu.js"></script>
   <script src="../js/home/home.js"></script>
   <link rel="stylesheet" href="../css/normalize.css">
   <link rel="stylesheet" href="../css/html5bp.css">
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
   
   <script src="../js/home/toastr.js"></script>
   <link href="../css/home/toastr.css" rel="stylesheet"/>
   
   <title>${title}</title>
</head>
<body>
<br>
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

<div id='homeFrame'>
	<div id='upcoming' style="float:left; width:75%;">
		<h1>Upcoming This Week</h1>
			<p id="no-shows"> You aren't scheduled to watch any shows yet! </p>
			<ul class="cbp_tmtimeline" id="upcomingEvents">
			
			</ul>
	</div>
	<div id='notifications' style="float:right; width:24%;">
	
	</div>
</div>

</body>
<html>