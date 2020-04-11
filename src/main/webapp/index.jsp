<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<meta name="google-signin-scope" content="profile email">
<meta name="google-signin-client_id"
     content="">
<title>Login page</title>
</head>

<body>
	<h1>Welcome to TinyGram, the little <i>Instagram</i></h1>


<h2>Before connexion - Generate fake datas</h2>

<h2>Login Google</h2>	
  <div class="g-signin2" data-onsuccess="onSignIn" data-theme="dark"></div>
<!--   <div class="g-signin2" data-onsuccess="onSignIn" data-scope="https://www.googleapis.com/auth/plus.login" data-accesstype="offline" data-redirecturi="http://localhost:8080/homepage.html"></div> -->
   <script>
      function onSignIn(googleUser) {
        // Useful data for your client-side scripts:
        var profile = googleUser.getBasicProfile();
        console.log("ID: " + profile.getId()); // Don't send this directly to your server!
        console.log('Full Name: ' + profile.getName());
        console.log('Given Name: ' + profile.getGivenName());
        console.log('Family Name: ' + profile.getFamilyName());
        console.log("Image URL: " + profile.getImageUrl());
        console.log("Email: " + profile.getEmail());

        // The ID token you need to pass to your backend:
        var id_token = googleUser.getAuthResponse().id_token;
        console.log("ID Token: " + id_token);
        
        var redirectUrl = 'login';
        //using jquery to post data dynamically
        var form = $('<form action="' + redirectUrl + '" method="post">' +
                            '<input type="text" name="id_token" value="' +
                             id_token + '" />' +
	                                                                  '</form>');
	      $('body').append(form);
	      form.submit();
      }
    </script>
</body>
</html>
