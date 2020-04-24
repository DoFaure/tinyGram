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
<%
    if (!userService.isUserLoggedIn()) {
        response.sendRedirect("/login");
    }

    //Get connected user informations
    Principal newUser = request.getUserPrincipal();
    Key keyNewUser = KeyFactory.createKey("User", request.getUserPrincipal().getName());
    Entity e;
    try {
        e = datastore.get(keyNewUser);
        pageContext.setAttribute("entity", e);
    } catch (EntityNotFoundException ex) {
        // TODO Auto-generated catch block
        e = new Entity("User", request.getUserPrincipal().getName());
        e.setProperty("mail", userService.getCurrentUser().getEmail());
        e.setProperty("name", userService.getCurrentUser().getNickname());
        datastore.put(e);
        e = datastore.get(e.getKey());
        pageContext.setAttribute("entity", e);
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
 	 	  		<a class="profile" href="/profile.jsp?key="><img class="icon-nav" src="/resources/img/user.png"></a>
	  		</div>
		</div>
	</nav>
	<div class="container" id="logout">
		<div class="row signout">
			<div class="col-8"></div>
			<div class="col-4 signout">
				<a href='<%=userService.createLogoutURL("/../index.jsp")%>'
					class="btn btn-danger btn-sm">SIGN OUT</a>
			</div>
		</div>	
	</div>

	<div id="script"></div>
	

<script>

Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

var Posts = {	
		list: [],
	    nextToken: "",
	    loadList: function() {
		    console.log("request")
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/receive"})
	        .then(function(result) {
	        	Posts.list=result.items;
	        	console.log("got:",result)
 	            if ('nextPageToken' in result) {
 		        	Posts.nextToken= result.nextPageToken
 	            } else {
 	            	Posts.nextToken=""
 	            }});
	    },
	    next: function() {
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/receive?next="+Posts.nextToken})
	        .then(function(result) {
	        	console.log("got:",result)
	        	result.items.map(function(item){Posts.list.push(item)})
	            if ('nextPageToken' in result) {
		        	Posts.nextToken= result.nextPageToken
	            } else {
	            	Posts.nextToken=""
	            }})
	    }
	}

var Users = {
		list: [],
	    nextToken: "",
	    loadList: function() {
		    console.log("request")
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users"})
	        .then(function(result) {
	        	Users.list=result.items;
	        	console.log("got:",result)
	    	})
	    }
}

var PostView = {
	 oninit: Posts.loadList(),
	 view: function() {
		return m('div', [
			Posts.list.map(function(item) {
				return m('div', {class: 'margin-top flex'}, [
					m('div', {class: 'card'}, [
						m('div', {class: 'card-header d-flex align-items-center'}, [
// 							Need to find a solution to get the User mame by his STRING KEY
// 							m('h5',{class: ''}, item.properties.owner), 
							m('div', {class: 'col-sm-5 ml-auto'}, [
								m('h6',{class: 'date'}, new Date(item.properties.date * 1000).toLocaleString() )
							])
						]),
						m('img',{class: 'card-img-top', 'src': item.properties.URL}),
						m('div', {class: 'card-body'}, [
							m('p', {class: ''}, item.properties.body)
						]),
						m('div', {class: 'card-footer text-muted'}, [
							m('p', {class: 'card-text'}, [
								m('a', {href: 'link_like', class: 'icon-block'}, [
									Object.size(item.properties.likes)&nbsp; , 
									m('i', {class: 'fa fa-heart', style: 'color: #FF0000'}, "" ), 
								])
							])
						])
					])
				]);
			}),
			m('div', {class: 'seeMore'}, [
				  m('button',{
				      class: 'btn btn-outline-secondary btn-sm is-link',
				      onclick: function(e) {Posts.next()}
				      },
				  "Next"),
			])
		])
	 }
}

var SuggestionView = {
		 oninit: Users.loadList(),
		 view: function() {
		  	return m('div', {class: 'card'}, [
		  			m('div', {class: 'card-header'}, [
		  				m('div', {class: 'row'}, [
		  					m('div', {class: 'col-8'}, [
		  						m('h5', {class: 'card-title'}, "Suggestion")
		  					]),
		  					m('div', {class: 'col-4 member-col'}, [
		  						m('a', {href: 'lien', class: '', role: 'button'}, "See all")
		  					])
		  				])
		  			]),
		  			m('div', {class: 'card-body'}, [
		  				m('div', {class: 'scrollable'}, [
		  					Users.list.map(function(item) {
		  						return m('div', {class: 'content'}, [
		  							m('div', {class: 'identity'}, [
		  								m('p', {class: 'card-text btn-align'}, [
		  									m('a', {class: 'profile-link', href: 'lien'}, [
		  										m('b', {}, item.properties.name)
		  									])
		  								])
		  							]),
		  							m('div', {class: 'follow'}, [
		  								m('a', {class: 'btn btn-primary btn-sm', href: 'lien'}, "Follow")
		  							])
		  						])
	  						})
		  				])
		  			])
		  		])
		 }
	}

var view = {
	view: function() {
		return m('div', {class:'container'}, [
			m('div', {class:'row'}, [
				m("div", {class: 'col-8'}, m(PostView)),
		    	m("div", {class: 'col-4'}, m(SuggestionView)),
			]) 
	    ])
	}
}

m.mount(document.getElementById("script"), view)


</script>

</body>
</html>