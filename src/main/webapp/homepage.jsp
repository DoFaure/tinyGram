<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
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
<link rel="shortcut icon" href="resources/img/logo.png"
	type="image/x-icon">
<link type="text/css" rel="stylesheet"
	href="/bootstrap/css/bootstrap.css" rel="stylesheet">
<link type="text/css" rel="stylesheet"
	href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="/font-awesome/css/font-awesome.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

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
				<input type="text" class="form-control" placeholder="Username"
					aria-label="Username" aria-describedby="basic-addon1">
			</div>
		</form>
		<div class="nav navbar-nav navbar-right justify-content-end">
			<div class="nav navbar-nav navbar-right">
	 	  		<a class="like" href="/followers.jsp"><img class="icon-nav" src="/resources/img/heart.png"></a>
 	 	  		<a class="profile" href="/profile?key=${KeyFactory.keyToString(entity.key)}"><img class="icon-nav" src="/resources/img/user.png"></a>
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
	
	<div id="help" hidden="true">"${entity.key}"</div>
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

let uKey = document.getElementById("help").innerHTML.split("\"")[2];

var PostView = {
	 oninit: Posts.loadList(),
	 view: function() {
		 var class_array = [];
		 
		 Posts.list.map(function(item) {
			 if (item.properties.likes.includes(uKey)) {
				 class_array[item] = 'fa fa-heart';
			 } else {
				 class_array[item] = 'fa fa-heart-o';
			 }
		 });
		 
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
								m('a', {class: 'icon-block'}, [
									m('i', {class: class_array[item], style: 'color: #FF0000', id: item.properties.date, onclick: function() {updateLike(item.properties.date)}, onload: function() {updateLike(item.properties.date)}}, '')
								]),
								m('p', {class: 'card-text'}, Object.size(item.properties.likes)+  " likes")
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
			 var my_friends = "${entity.properties.friends}";
			 var class_array = [];
			 var text = [] ;
			 
			 Users.list.map(function(item) {
				 
				 if (my_friends.includes(', '+ item.key.name + ',') || my_friends.includes('['+ item.key.name + ']') ||  my_friends.includes('['+ item.key.name + ',') || my_friends.includes(', '+ item.key.name + ']')) {
					 class_array[item.key.name] = 'btn btn-danger btn-sm';
					 text[item.key.name] = "Unfollow";
				 } else {
					 class_array[item.key.name] = 'btn btn-primary btn-sm';
					 text[item.key.name] = "Follow";

				 }
			 });
			 
		  	return m('div', {class: 'card'}, [
		  			m('div', {class: 'card-header'}, [
		  				m('div', {class: 'row'}, [
		  					m('div', {class: 'col-8'}, [
		  						m('h5', {class: 'card-title'}, "Suggestion")
		  					]),
		  					m('div', {class: 'col-4 member-col'}, [
		  						m('a', {href: '/members', class: '', role: 'button'}, "See all")
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
		  								m('a', {class: class_array[item.key.name], id: item.key.name, onclick: function() {follow(item.key.name)}}, text[item.key.name])
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

function updateLike(id) {
	console.log('debut like pour ' + id);
	
	if (document.getElementById(id) != null) {
		let aclass = document.getElementById(id).className; 
		if (aclass.includes('fa-heart-o')) {
			document.getElementById(id).className = 'fa fa-heart';
			
			var data = {'id_post': id,
 					'id_user_to_add': "${KeyFactory.keyToString(entity.key)}"}
 	    	
			console.log("put:" + data)
     		
			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/like/" + "${KeyFactory.keyToString(entity.key)}",
             	params: data,
         	}).then(function(result) {
     	 			console.log("ok:",result)
         	 });
			
		} else {
			document.getElementById(id).className = 'fa fa-heart-o';
			
			var data = {'id_post': id,
 					'id_user_to_remove': "${KeyFactory.keyToString(entity.key)}"}
 	    	
			console.log("put:" + data)
     		
			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/unlike/" + "${KeyFactory.keyToString(entity.key)}",
             	params: data,
         	}).then(function(result) {
     	 			console.log("ok:",result)
         	 });
		}
	} else {
		setTimeout(updateLike(id), 1000);
	}
	
	console.log('ok like');
}

function follow(id) {
	if (document.getElementById(id) != null) {
		let aclass = document.getElementById(id).className; 
		if (aclass.includes('primary')) {
			document.getElementById(id).className = 'btn btn-danger btn-sm';
			document.getElementById(id).innerHTML = 'Unfollow';
			
			var data = {'id_user': "${KeyFactory.keyToString(entity.key)}",
					'id_user_to_add': id}
	 	
			console.log("put:" + data)
				
			m.request({
		 		method: "PUT",
		 		url: "_ah/api/tinyGramApi/v1/put/users/" + "${KeyFactory.keyToString(entity.key)}" + "/follow/" + id,
		     	params: data,
		 	}).then(function(result) {
			 	console.log("ok:",result)
		 	 });
		} else {
			document.getElementById(id).className = 'btn btn-primary btn-sm';
			document.getElementById(id).innerHTML = 'Follow';
			
			var data = {'id_user': "${KeyFactory.keyToString(entity.key)}",
					'id_user_to_remove': id}
	 	
			console.log("put:" + data)
				
			m.request({
		 		method: "PUT",
		 		url: "_ah/api/tinyGramApi/v1/put/users/" + "${KeyFactory.keyToString(entity.key)}" + "/unfollow/" + id,
		     	params: data,
		 	}).then(function(result) {
			 	console.log("ok:",result)
		 	 });
			}
	}
}

</script>

</body>
</html>