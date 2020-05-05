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
    
    <!-- mithril import -->
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
	      <input type="text" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
	    </div>
	  </form>
	  <div class="nav navbar-nav navbar-right">
	 	  <a class="like" href="/followers"><img class="icon-nav" src="/resources/img/heart.png"></a>
 	 	  <a class="profile" href="/profile?key=${KeyFactory.keyToString(entity.key)}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  </div>
	</nav>
 
 <div class="container">
	<div id="script"></div>
</div>

	
	

<script>

Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

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
 	            if ('nextPageToken' in result) {
 		        	Users.nextToken= result.nextPageToken
 	            } else {
 	            	Users.nextToken=""
            }});
	    },
	    next: function() {
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users?next="+Users.nextToken})
	        .then(function(result) {
	        	console.log("got:",result)
	        	result.items.map(function(item){Users.list.push(item)})
	            if ('nextPageToken' in result) {
		        	Users.nextToken= result.nextPageToken
	            } else {
	            	Users.nextToken=""
	            }})
	    }
}

var MembersView = {
		 oninit: Users.loadList(),
		 view: function() {			 
		  	return m('div', {class: 'card'}, [
		  		m('div', {class: 'list-members'}, [
	  				Users.list.map(function(item) {
	  					return m('div', {class: 'content'}, [
	  						m('div', {class: 'identity'}, [
	  							m('p', {class: 'card-text btn-align'}, [
	  								m('a', {class: 'profile-link', href: '/profile'}, [
	  									m('b', item.properties.name),	
	  								])
	  							])
	  						]),
	  						m('div', {class: 'follow'}, [
	  							m('a', {class: 'btn btn-primary btn-sm', href: 'lien'}, "Follow")
	  						])
	  					])
	  				})
		  		]),
		  		m('div', {class: 'seeMore'}, [
		  			m('a', {
		  				class: 'btn btn-outline-secondary btn-sm is-link',
					    onclick: function(e) {Users.next()}
		  			}, "Next")
		  		])
		  	])
		 }
};

m.mount(document.getElementById("script"), MembersView);


</script>

</body>
</html>