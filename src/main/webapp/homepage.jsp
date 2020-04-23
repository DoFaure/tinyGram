<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.security.Principal"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page
	import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page import="com.google.appengine.api.users.UserService"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%!UserService userService = UserServiceFactory.getUserService();%>
<%!DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();%>
<%!SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");%>
<%
	if (!userService.isUserLoggedIn()) {
		response.sendRedirect("/login");
	} else {
		//Get connected user informations
		Entity e;
		try {
			e = datastore.get(KeyFactory.createKey("User", request.getUserPrincipal().getName()));
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
<link rel="shortcut icon" href="resources/img/logo.png"
	type="image/x-icon">
<link type="text/css" rel="stylesheet"
	href="/bootstrap/css/bootstrap.css" rel="stylesheet">
<link type="text/css" rel="stylesheet"
	href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="/font-awesome/css/font-awesome.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<!-- mithril import -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css">
<script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>
<script src="https://unpkg.com/mithril/mithril.js"></script>
<!-- -------------- -->

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
				<input type="text" class="form-control" placeholder="Username"
					aria-label="Username" aria-describedby="basic-addon1">
			</div>
		</form>
		<div class="nav navbar-nav navbar-right justify-content-end">
			<div class="nav navbar-nav navbar-right">
	 	  		<a class="like" href="/followers.jsp"><img class="icon-nav" src="/resources/img/heart.png"></a>
 	 	  		<a class="profile" href="/profile.jsp?key=${KeyFactory.keyToString(entity.key)}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  		</div>
		</div>
	</nav>

	<!--  all friend's posts ordered by date -->
	<div class="container">
		<div class="row">
			<div class="col-8" id="test">

<script>

var me = "${KeyFactory.keyToString(entity.key)}";

var Posts = {
		list: [],
	    nextToken: "",
	    loadList: function() {
		    console.log("resquest")
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/users/"+me+"/receive"})
	        .then(function(result) {
	        	Posts.list=result.items
	        	console.log("got:",result)
// 	            if ('nextPageToken' in result) {
// 		        	MyPost.nextToken= result.nextPageToken
// 	            } else {
// 	            	MyPost.nextToken=""
	            })
	    }
	}

var PostView = {
		  oninit: Posts.loadList,
		  view: function() {
		   	return m('div', [
			  m('div',{class:'subtitle'},"My Posts"),
			  m('table', {class:'table is-striped',"table":"is-striped"},[
			    m('tr', [
				  m('th', {width:"50px"}, "like"),
				  m('th', {width:"50px"}, "del"),
			      m('th', {width:"50px"}, "Bodys"),
			      m('th', {width:"50px"}, "Urls"),
			      m('th', {width:"50px"}, "Like"),
			    ]),
			    Posts.list.map(function(item) {
				    console.log("map")
			      return m("tr", [
		            m("td", m("button", {onclick: function(e) {
						console.log("like:"+item.key.id)
		                 }},"like")),
		                 m("td", m("button", {onclick: function(e) {
		     				console.log("del:"+item.key.id)
		                 }},"del")),
			        m('td', m('label', item.properties.body)),
			        m('td', m('img', {class: 'is-rounded', 'src': item.properties.url})),
			        m('td', m('label', item.properties.likes)),
			      ])
			    }),
			   ])
			 ])
		  }
		}

var Hello = {
		   view: function() {
			   console.log("hi")
		   	return m('div', {class:'container'}, [
		           m("h1", {class: 'title'}, 'The TinyGram Post'),
		           m('div',{class: 'tile is-ancestor'},[
		        	   m("div", {class: 'tile'}, m('div',{class:'tile is-child box'},m(PostView))),
		           ])
		       ])
		   }
		}


m.mount(document.getElementById("test"), Hello)	
</script>
<%-- 				<c:forEach items="${posts}" var="post"> --%>
<!-- <!-- 				store useful values  --> 
<%-- 					<c:set var="postUser" value="${post.properties.user}" --%>
<%-- 						scope="request" /> --%>
<%-- 					<c:set var="postDate" value="${post.properties.date}" --%>
<%-- 						scope="request" /> --%>
<%-- 					<c:set var="postLikes" value="${post.properties.likes}" --%>
<%-- 						scope="request" /> --%>
<!-- <!-- ---------------------------------- --> 
<!-- 					<div class="margin-top flex"> -->
<!-- 						<div class="card"> -->
<!-- 							<div class="card-header d-flex align-items-center"> -->
<!-- 								<h5> -->
<%-- 									<%=datastore.get(KeyFactory.stringToKey((String) request.getAttribute("postUser"))) --%>
<%-- 						.getProperty("firstName")%> --%>
<%-- 									<%=datastore.get(KeyFactory.stringToKey((String) request.getAttribute("postUser"))) --%>
<%-- 						.getProperty("lastName")%> --%>
<!-- 								</h5> -->
<!-- 								<div class="col-sm-5 ml-auto"> -->
<!-- 									<h6 class="date"> -->
<%-- 										<%=formatDate.format(request.getAttribute("postDate"))%> --%>
<!-- 									</h6> -->
<!-- 								</div> -->
<!-- 							</div> -->
<%-- 							<img class="card-img-top" src="${post.properties.URL}" --%>
<%-- 								alt="url-image-post : <c:out value=" ${post.properties.URL}"/>"> --%>
<!-- 							<div class="card-body"> -->
<!-- 								<p class="card-text"> -->
<%-- 									<c:out value="${post.properties.body}" /> --%>
<!-- 								</p> -->
<!-- 							</div> -->
<!-- 							<div class="card-footer text-muted"> -->
<%-- 								<% --%>
<!--  									ArrayList<String> likedBy = (ArrayList<String>) request.getAttribute("postLikes"); -->
<%-- 								%> --%>
<!-- 								<p class="card-text"> -->
<%-- 									<% --%>
<!--  										if (likedBy.contains(request.getUserPrincipal().getName())) { -->
<%-- 									%> --%>
<%-- 									<a href="/unlike?key=${KeyFactory.keyToString(post.key)}&url=${request.getRequestURI()}" --%>
<!-- 										class="icon-block"> <i class="fa fa-heart" -->
<!-- 										style="color: #FF0000"></i> -->
<!-- 									</a> -->
<%-- 									<% --%>
<!--  										} else { -->
<%-- 									%> --%>
<%-- 									<a href="/like?key=${KeyFactory.keyToString(post.key)}&url=${request.getRequestURI()}" --%>
<!-- 										class="icon-block"> <i class="fa fa-heart-o" -->
<!-- 										style="color: #FF0000"></i> -->
<!-- 									</a> -->
<%-- 									<% --%>
<!--  										} -->
<%-- 									%> --%>
<%-- 									<%=likedBy.size()%> --%>
<!-- 									likes -->

<!-- 								</p> -->
<!-- 							</div> -->
<!-- 						</div> -->
<!-- 					</div> -->
<%-- 				</c:forEach> --%>

<!-- 					<div class="seeMore"> -->
<%-- 						<a href="/homepage?last=${last}" class="btn btn-outline-secondary btn-sm">See more</a> --%>
<!-- 					</div> -->
				
			</div>

			<!--  members card / to follow and unfollow -->
			<div class="col-4">
				<div class="card ">
					<div class="card-header">
						<div class="row">
							<div class="col-8"><h5 class="card-title">Members</h5></div>
							<div class="col-8 member-col"><a href="/members" class="members" role="button">See all <i class="fa fa-plus-square"></i>
							</a></div>
						</div>
 					
						
  				 	</div>
					<div class="card-body">
						<div class="scrollable">
							<!-- 	 For each users, we display Firstname and Lastname  -->
							<c:forEach items="${users}" var="u">
								<div class="content">
									<div class="identity">
										<p class="card-text btn-align">
											<a class="profile-link" href="/profile?key=${KeyFactory.keyToString(u.key)}">
												<b><c:out value="${u.properties.firstName} ${u.properties.lastName}" /></b>
											</a>
										</p>
									</div>
									<div class="follow">
										<c:choose>
											<%-- 		     	if user has friends  --%>
											<c:when test="${entity.properties.friends != null }">
												<c:choose>
													<%-- 			     			If he is friend with that person --%>
													<c:when test="${friends.contains(u)}">
														<a href="/unfollow?key=${KeyFactory.keyToString(u.key)}&url=/homepage&cursor=${currentLast}"
															class="btn btn-danger btn-sm">Unfollow</a>
													</c:when>
													<%-- 							    else    --%>
													<c:otherwise>
														<a href="/follow?key=${KeyFactory.keyToString(u.key)}&url=/homepage&cursor=${currentLast}"
															class="btn btn-primary btn-sm">Follow</a>
													</c:otherwise>
												</c:choose>
											</c:when>
											<%-- 				    else     --%>
											<c:otherwise>
												<a href="/follow?key=${KeyFactory.keyToString(u.key)}&url=/homepage&cursor=${currentLast}"
													class="btn btn-primary btn-sm">Follow</a>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="signout">
					<a href='<%=userService.createLogoutURL("/../index.jsp")%>'
						class="btn btn-danger btn-md">SIGN OUT</a>
				</div>
			</div>
		</div>
	</div>

</body>
</html>