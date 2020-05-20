<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%!UserService userService = UserServiceFactory.getUserService();%>

<%if(userService.isUserLoggedIn()){
	response.sendRedirect("/homepage");
}%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<meta http-equiv="content-type"
	content="application/xhtml+xml; charset=UTF-8" />     
<title>Login page</title>
</head>


<body>
	<h1>Welcome to TinyGram, the little <i>Instagram</i></h1>
	
	<h2>Generate fake datas <b>before connexion</b> -<a href='/generatedatas'> Populate User and Post table (10sc)</a></h2>
	
	<h2>Then, connect to the application -<a href='login'> h e r e </a></h2>
</body>
</html>
