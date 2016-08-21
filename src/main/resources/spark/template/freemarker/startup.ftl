<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="../css/normalize.css">
    <link rel="stylesheet" href="../css/html5bp.css">
    <link rel="stylesheet" href="../css/startup/startup.css">
    <link rel="stylesheet" href="../css/startup/startup-interaction.css">
    <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
   <script src="../js/sweetalert-master/dist/sweetalert.min.js"></script>
   <link rel="stylesheet" type="text/css" href="../js/sweetalert-master/dist/sweetalert.css">
  </head>
  <body>
  <br>
    <img id="logo" src="../images/TVT_Logo.png" alt="TVT logo" style="width:250px;height:225px">
    <button id="signup-button" class="styled-button-start" onClick="location.href='../signup'">Sign Up</button>
    <button id="login-button" value="login" class="styled-button-start">Login</button>
    <input type="password" id="password-text" class="text-area-start" placeholder="Password">
    <input type="email" id="email-text" class="text-area-start" placeholder="Email">
    <p id="title"> TVTime </p>
    <p id="incorrect-login"></p>
    <br>
    <hr id="top-line">
        <script src="../js/startup/three.min.js"></script>
		<script src="../js/startup/tween.min.js"></script>
		<script src="../js/startup/TrackballControls.js"></script>
		<script src="../js/startup/CSS3DRenderer.js"></script>

		<div id="container"></div>
		<div id="menu">
			<button id="table">TABLE</button>
			<button id="sphere">SPHERE</button>
			<button id="helix">HELIX</button>
			<button id="grid">GRID</button>
		</div>

	<script src="../js/startup/startup-interaction.js"></script>
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
    <script src="../js/jquery-2.1.1.js"></script>
    <script src="../js/startup/startup.js"></script>
  </body>
</html>
