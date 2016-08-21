<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link rel="stylesheet" href="../css/menu.css">
   <link rel="stylesheet" href="../css/shows/shows.css">
   <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
   <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
   <script src="../js/menu.js"></script>
   <script src="../js/shows/shows.js"></script>
   <link rel="Stylesheet" type="text/css" href="../css/shows/smoothDivScroll.css" /> 
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
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
	
	<div id="hoverPopup" class="wrapper">
      <h1 id="hoverName"> The Walking Dead </h1>
      <p id="hoverGenres"> Genres: Action, Adventure, Mystery</p>
      <p id="hoverSeasons"> Seasons: 3</p>
      <p id="hoverRuntime"> Runtime: 60 min</p>
    </div>
	<label id="no-matches"> No TV Shows with that name were found. Try browsing our selection! </label>
	<fieldset>
      <label id="search" class="text-naming"> Search For Shows: </label>
      <input type="text" id="search-text" class="text-area" placeholder='Search'>
      <button id="search-button"> Search </button>
    </fieldset>
    
    <p id="myShowsTitle" class="showsBreakup"> My Shows</p>   
    <hr id="myShowsHeader1">    
    <div id="my-content" class="makeMeScrollable"></div>
    <hr id="myShowsHeader2">
    
	<p id="topShowsTitle" class="showsBreakup"> Top Shows</p>	
	<hr>	
    <div id="top-content" class="makeMeScrollable"></div>
    <hr>
    <p id="dramaShowsTitle" class="showsBreakup"> Drama</p>  
    <hr>   
    <div id="drama-content" class="makeMeScrollable"></div>
    <hr>
    <p id="actionShowsTitle" class="showsBreakup"> Action and Adventure</p>  
    <hr>
    <div id="action-content" class="makeMeScrollable"></div>
    <hr>
    <p id="comedyShowsTitle" class="showsBreakup"> Comedy</p>  
    <hr>
    <div id="comedy-content" class="makeMeScrollable"></div>
    <hr>
    <p id="crimeShowsTitle" class="showsBreakup"> Crime </p>  
    <hr>
    <div id="crime-content" class="makeMeScrollable"></div>
    <hr>
    <p id="thrillerShowsTitle" class="showsBreakup"> Thrillers</p>  
    <hr>
    <div id="thriller-content" class="makeMeScrollable"></div>
    <hr>
    
    
    
    <script src="../js/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
    <script src="../js/jquery.mousewheel.min.js" type="text/javascript"></script>
    <script src="../js/jquery.kinetic.min.js" type="text/javascript"></script>
    <script src="../js/jquery.smoothdivscroll-1.3-min.js" type="text/javascript"></script>
    <script type="text/javascript" src="../js/shows/shows.js"></script>
    <script type="text/javascript" src="../js/menu.js"></script>
    </body>
</html>