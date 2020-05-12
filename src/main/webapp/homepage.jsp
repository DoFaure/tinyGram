<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.users.UserService"%>


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
 	 	  		<a class="profile" href="/profile?user=${entity.properties.mail}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  		</div>
		</div>
	</nav>
	<div class="container" id="logout">
		<div class="row signout">
			<div class="col-8">
				<a href='/post' class="btn btn-primary btn-md">Create post</a>
			</div>
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
	            url:"_ah/api/tinyGramApi/v1/get/posts"})
	           // url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/receive"})
	        .then(function(result) {
	        	Posts.list=result.items;
	        	console.log("got:",result)
 	            if ('nextPageToken' in result) {
 		        	Posts.nextToken= result.nextPageToken
 	            } else {
 	            	Posts.nextToken=""
 	            }
	        	
	        	setTimeout(() => {
		            Posts.list.map(function(item) {
		            	printOwner(item.properties.owner, item.key.name);
		            });
		         }, 200);
	        });
	    },
	    next: function() {
	        return m.request({
	            method: "GET",
	            url:"_ah/api/tinyGramApi/v1/get/posts?next="+Posts.nextToken})
	           // url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/receive?next="+Posts.nextToken})
	        .then(function(result) {
	        	console.log("got:",result)
	        	
	        	result.items.map(function(item){
	        		Posts.list.push(item);
	        	})
	        	
	            if ('nextPageToken' in result) {
		        	Posts.nextToken= result.nextPageToken
	            } else {
	            	Posts.nextToken=""
	            }
	        	
	        	setTimeout(() => {
		            // onload finished.
		            // and execute some code here like stat performance.
		            
		            Posts.list.map(function(item) {
		            	printOwner(item.properties.owner, item.key.name);
		            });
		         }, 500);
	        })
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

var Likes = {
		loadLikes: function(id){
			return m.request({
				method: "GET",
				url: "_ah/api/tinyGramApi/v1/get/posts/" + id + "/likes"})
			.then(function(result){
				if(result != null){
					document.getElementsByClassName('likes ' + id)[0].innerHTML = " " + result.items.length + " likes"
				}else{
					document.getElementsByClassName('likes ' + id)[0].innerHTML = " 0 likes"
				}

			})
		}
}


var PostView = {
	 oninit: Posts.loadList(),
	 view: function() {
		 var class_array = [];
		 
		 Posts.list.map(function(item) {
			 if(item.properties.likes){
				 if (item.properties.likes.includes("${KeyFactory.keyToString(entity.key)}")) {
					 class_array[item.key.name] = 'fa fa-heart';
				 } else {
					 class_array[item.key.name]= 'fa fa-heart-o';
				 }
			 }else{
				 class_array[item.key.name] = 'fa fa-heart-o';
			 }
		 });
		 
		return m('div', [
			Posts.list.map(function(item) {
				return m('div', {class: 'margin-top flex'}, [
					m('div', {class: 'card'}, [
						m('div', {class: 'card-header d-flex align-items-center'}, [
							m('h5',{class: '', id: 'title' + item.key.name}, " "), 
							m('div', {class: 'col-sm-5 ml-auto'}, [
								m('h6',{class: 'date'}, new Date(item.properties.date * 1000).toLocaleString() )
							])
						]),
						m('img',{class: 'card-img-top', 'src': item.properties.URL}),
						m('div', {class: 'card-body'}, [
							m('p', {class: ''}, item.properties.body)
						]),
						m('div', {class: 'card-footer text-muted'}, [
							m('div', {class: 'card-text user-action-likes'}, [
								m('div',[
									m('a', {class: 'icon-block'}, [
										m('i', {class: class_array[item.key.name], style: 'color: #FF0000', id: item.key.name, onclick: function() {updateLike(item.key.name)}, onload: function() {updateLike(item.key.name)}}, '')
									])
								]),
							    m('div',{class:'likes ' + item.key.name} ," " + Object.size(item.properties.likes) + " likes"),
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

function printOwner(owner, id) {
	m.request({
 		method: "GET",
 		url: "_ah/api/tinyGramApi/v1/get/users/" + owner,
 	}).then(function(result) {
	 		let name = result.properties.name;
	 		document.getElementById('title' + id).innerHTML = name;
 	});
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
		  									m('a', {class: 'profile-link', href: '/profile?user='+ item.key.name }, [
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
	
	if (document.getElementById(id) != null) {
		let aclass = document.getElementById(id).className; 
		if (aclass.includes('fa-heart-o')) {
			document.getElementById(id).className = 'fa fa-heart';
			
			var data = {'id_post': id,
 					'id_user_to_add': "${KeyFactory.keyToString(entity.key)}"}
 	    	

			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/like/" + "${KeyFactory.keyToString(entity.key)}",
         	}).then(function(result) {
     	 			Likes.loadLikes(id)
         	 });
			
		} else {
			document.getElementById(id).className = 'fa fa-heart-o';
			
			var data = {'id_post': id,
 					'id_user_to_remove': "${KeyFactory.keyToString(entity.key)}"}
 	    	
     		
			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/unlike/" + "${KeyFactory.keyToString(entity.key)}",
         	}).then(function(result) {
     	 			Likes.loadLikes(id)
         	 });
		}
	} else {
		setTimeout(updateLike(id), 1000);
	}
	
}

function follow(id) {
	if (document.getElementById(id) != null) {
		let aclass = document.getElementById(id).className; 
		if (aclass.includes('primary')) {
			document.getElementById(id).className = 'btn btn-danger btn-sm';
			document.getElementById(id).innerHTML = 'Unfollow';
			
			var data = {'id_user': "${KeyFactory.keyToString(entity.key)}",
					'id_user_to_add': id}
	 	
				
			m.request({
		 		method: "PUT",
		 		url: "_ah/api/tinyGramApi/v1/put/users/" + "${KeyFactory.keyToString(entity.key)}" + "/follow/" + id,
		     	params: data,
		 	}).then(function(result) {
		 	 });
		} else {
			document.getElementById(id).className = 'btn btn-primary btn-sm';
			document.getElementById(id).innerHTML = 'Follow';
			
			var data = {'id_user': "${KeyFactory.keyToString(entity.key)}",
					'id_user_to_remove': id}
	 	
				
			m.request({
		 		method: "PUT",
		 		url: "_ah/api/tinyGramApi/v1/put/users/" + "${KeyFactory.keyToString(entity.key)}" + "/unfollow/" + id,
		     	params: data,
		 	}).then(function(result) {
		 	 });
			}
	}
}

</script>

</body>
</html>