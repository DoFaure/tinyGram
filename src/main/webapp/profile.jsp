<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.security.Principal"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page import="com.google.appengine.api.users.UserService"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%!UserService userService = UserServiceFactory.getUserService();%>
<%!DatastoreService datastore  = DatastoreServiceFactory.getDatastoreService(); %>
<%!SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); %>
<%if(!userService.isUserLoggedIn()){ 
	response.sendRedirect("/login"); 
}else{
	//Get connected user informations
	Entity e;
	try {
		e = datastore.get( KeyFactory.createKey("User", request.getUserPrincipal().getName()));
		pageContext.setAttribute("entity", e);
	} catch (EntityNotFoundException ex) {
		// TODO Auto-generated catch block
		ex.printStackTrace();
	}
}

%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>TinyGram</title>
    <link rel="stylesheet" href="resources/style/style.css">
    <link rel="shortcut icon" href="resources/img/logo.png" type="image/x-icon">
    <link type="text/css" rel="stylesheet" href="/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/font-awesome/css/font-awesome.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <script src="/bootstrap/js/bootstrap.js"></script>
    
</head>
<body>
<!-- 	navbar / menu -->
	<nav class="navbar navbar-light bg-light align-items-center">
	  <a class="nav navbar-nav navbar-left navbar-brand" href="/homepage">
	    <img class="icon-nav" src="resources/img/logo.png" alt="">TinyGram
	  </a>
	  <form class="form-inline nav navbar-nav navbar-center">
	    <div class="input-group">
	      <div class="input-group-prepend">
	        <span class="input-group-text" id="basic-addon1">@</span>
	      </div>
	      <input type="text" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
	    </div>
	  </form>
	  <div class="nav navbar-nav navbar-right">
	 	  <a class="like" href="/followers"><img class="icon-nav" src="/resources/img/heart.png"></a>
 	 	  <a class="profile" href="/profile?key=${KeyFactory.keyToString(entity.key)}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  </div>
	</nav>
 
 <div class="container">
	 <div class="media profile-information">
		  <img src="/resources/img/user.png" class="align-self-center mr-3 profile-logo" alt="">
			  <div class="media-body profile-information-body">
			    <h5 class="mt-0"><c:out value="${user.properties.mail}" /></h5>
			    <div class="row">
				    <div class="col">
				      <b><c:out value="${numberOfPosts}"/></b> posts
				    </div>
				    <div class="col">
				      <b><c:out value="${followers}" /></b> followers
				    </div>
				    <div class="col">
				     <b><c:out value="${numberOfFollow}" /></b> follows
				    </div>
			  </div>
			  <div class="row">
			  		<div class="col">
			  		  <b><c:out value="${user.properties.firstName}"/> <c:out value="${user.properties.lastName}"/></b>
			  		</div>
			  </div>
		  	</div>
	</div>
	
	<hr class="solid">
	<!--  see all user posts -->
	 <div class="row">
		<div class="col-12">
		 	<c:forEach items="${posts}" var="post">
					<c:set var="postUser" value="${post.properties.user}" scope="request" />
					<c:set var="postDate" value="${post.properties.date}" scope="request" />
					<c:set var="postLikes" value="${post.properties.likes}" scope="request" />
					<div class="margin-top flex">
						<div class="card">
							<div class="card-header d-flex align-items-center">
								<h5>
									<%=datastore.get(KeyFactory.stringToKey((String) request.getAttribute("postUser")))
						.getProperty("firstName")%>
									<%=datastore.get(KeyFactory.stringToKey((String) request.getAttribute("postUser")))
						.getProperty("lastName")%>
								</h5>
								<div class="col-sm-5 ml-auto">
									<h6 class="date">
										<%=formatDate.format(request.getAttribute("postDate"))%>
									</h6>
								</div>
							</div>
							<img class="card-img-top" src="${post.properties.URL}"
								alt="url-image-post : <c:out value=" ${post.properties.URL}"/>">
							<div class="card-body">
								<p class="card-text">
									<c:out value="${post.properties.body}" />
								</p>
							</div>
							<div class="card-footer text-muted">
								<%
									ArrayList<String> likedBy = (ArrayList<String>) request.getAttribute("postLikes");
								%>
								<p class="card-text">
									<%
										if (likedBy.contains(request.getUserPrincipal().getName())) {
									%>
									<a href="/unlike?key=${KeyFactory.keyToString(post.key)}"
										class="icon-block"> <i class="fa fa-heart"
										style="color: #FF0000"></i>
									</a>
									<%
										} else {
									%>
									<a href="/like?key=${KeyFactory.keyToString(post.key)}"
										class="icon-block"> <i class="fa fa-heart-o"
										style="color: #FF0000"></i>
									</a>
									<%
										}
									%>
									<%=likedBy.size()%>
									likes

								</p>
							</div>
						</div>
					</div>
				</c:forEach>
		</div>
	
	</div>
</div>

</body>
</html>