<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="../css/normalize.css">
    <link rel="stylesheet" href="../css/html5bp.css">
    <link rel="stylesheet" href="../css/signup/signup.css">
    <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
  </head>
  <body>
  
    <div id="wrap" class="wrapper">
      <h1>Register For An Account</h1>
      <p>To sign-up for a free basic account please provide us with some basic information using
        the contact form below. Please use valid credentials.
      </p>
      <input type="text" id="name-text" class="text-area" placeholder='Name'>
      <input type="email" id="email-text" class="text-area" placeholder='Email'>
      <input type="password" id="password-text" class="text-area" placeholder='Password'>
      <input type="password" id="confirm-password-text" class="text-area" placeholder='Confirm Password'>
      <p id="invalid-signup"></p>
      <button id="register-button"> Register </button>
    </div>
    <p class="optimize">
      Optimized for Chrome & Firefox!
    </p>
  </body>
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
  <script src="../js/jquery-2.1.1.js"></script>
  <script src="../js/signup/signup.js"></script>
</html>