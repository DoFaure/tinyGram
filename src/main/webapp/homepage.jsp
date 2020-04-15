<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%!UserService userService = UserServiceFactory.getUserService();%>

<!DOCTYPE html>
<html lang="en">
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script src="https://apis.google.com/js/platform.js" async defer></script>
  </head>
  <body>
 	<h2>Hello fuckin' world - Username : ${userName}</h2>
 	
	<%if(userService.isUserLoggedIn()){ %>
	<p><a href="<%=userService.createLogoutURL("/../index.jsp") %>">Sign out</a></p>
	<%}else{
		response.sendRedirect("/login");
	}%>
	
 	<h2>Generate fake datas</h2>	
	<li><a href='/generateDatas'>Populate the Friend table (long)</a>
 	
  </body>
</html>
