<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.security.Principal"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page import="com.google.appengine.api.users.UserService"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%!UserService userService = UserServiceFactory.getUserService();%>

<%if(!userService.isUserLoggedIn()){ 
	response.sendRedirect("/login"); 
}else{
	DatastoreService datastore  = DatastoreServiceFactory.getDatastoreService();
	Entity e;
	Key userKey = KeyFactory.createKey("User", request.getUserPrincipal().getName());
	try {
		e = datastore.get(userKey);
		pageContext.setAttribute("entity", e);
	} catch (EntityNotFoundException ex) {
		// TODO Auto-generated catch block
		ex.printStackTrace();
	}
	
}
%>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>TinyGram</title>
    <link rel="stylesheet" href="resources/style/style.css">
    <link rel="shortcut icon" href="resources/img/logo.png" type="image/x-icon">
    <link type="text/css" rel="stylesheet" href="/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

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
	 	  <a class="like" href=""><img class="icon-nav" src="/resources/img/heart.png"></a>
 	 	  <a class="profile" href=""><img class="icon-nav" src="/resources/img/user.png"></a>
	  </div>
	</nav>
 
 <div class="row">
  <div class="col-8">
  <c:forEach items="${friends}" var="friend">
        <div class="margin-top flex">
            <div class="card">
				<img class="card-img-top" src="" >
				<div class="card-body">
				 	   <p class="card-text"><c:out value="${friend}" /></p>
				</div>
			</div>
        </div>
</c:forEach>

        <div class="margin-top flex">
            <div class="card">
				<img class="card-img-top" src="" >
				<div class="card-body">
				   <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
				</div>
			</div>
        </div>
  </div>
  <div class="col-4">
  	<div class="card ">
	  <div class="card-body">
	    <h5 class="card-title">Members</h5>
	    <div class="scrollable">
	     <c:forEach items="${users}" var="u">
	     	<div class="content">
		     	<div class="identity">
		     		<p class="card-text btn-align"><c:out value="${u.properties.firstName} ${u.properties.lastName}"/></p>
		     	</div>
		     	<div class="follow">
		     	<c:choose>
				    <c:when test="${entity.properties.friends != null }">
			     			<c:choose>
							    <c:when test="${friends.contains(u)}">
							       <a href="/unfollow?key=${KeyFactory.keyToString(u.key)}" class="btn btn-danger btn-sm">Unfollow</a>	
							    </c:when>    
							    <c:otherwise>
				       			   <a href="/follow?key=${KeyFactory.keyToString(u.key)}" class="btn btn-primary btn-sm">Follow</a>
							    </c:otherwise>
							</c:choose>	
				    </c:when>    
				    <c:otherwise>
	       			   <a href="/follow?key=${KeyFactory.keyToString(u.key)}" class="btn btn-primary btn-sm">Follow</a>	
				    </c:otherwise>
				</c:choose>	
			     				     		
		     	</div>
	     	</div>
		</c:forEach>
	  </div>
	</div>
  </div>
</div>

</body>
</html>