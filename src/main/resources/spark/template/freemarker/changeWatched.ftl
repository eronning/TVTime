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
   <script src="../js/changeWatched/changeWatched.js"></script>
   <link rel="stylesheet" href="../css/normalize.css">
   <link rel="stylesheet" href="../css/html5bp.css">
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
   
   <script src="../js/home/toastr.js"></script>
   <link href="../css/home/toastr.css" rel="stylesheet"/>
   
   <link rel="stylesheet" href="../css/style.css">
   <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
   <link rel="stylesheet" href="../css/changeWatched/changeWatched.css">
   
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

	<form>
		<h1> Missed a show since you last logged in?</h1>
		<h2> (No worries, no spoilers.)</h2>
		<p> If you missed an episode
		this week, you can choose to defer it to next week or catch up before
		your regularly scheduled time next week. (Alternatively, read the detailed
		synopsis of the episode and check watched to skip this episode
		altogether!)</p>
		<div id="epsToConfirm"></div>
		<input type="submit" value="Confirm" class="styledButton medButton" style="display: block; margin: 0 auto;">
	</form>
</body>
<html>