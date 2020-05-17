<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>

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
 	 	  <a class="profile" href="/profile?user=${actualU.properties.mail}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  </div>
	</nav>
 
 <div class="container">
	<div id="n_posts"></div>
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

var User = {
		data: [],
	    loadList: function() {
		    console.log("request")
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users/${KeyFactory.keyToString(entity.key)}"})
	        .then(function(result) {
	        	User.data=result.properties;
	        	console.log("got:",User.data)
	        	setTimeout(() => {
		            	printFollowers(result.key.name);
		         }, 200);
            });
	    },
}



var Posts = {	
		list: [],
	    nextToken: "",
	    loadList: function() {
		    console.log("request")
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/posts"})
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
		            
		            Ob
		         }, 200);
            });    
	    },
	    next: function() {
	        return m.request({
	            method: "GET",
	            url: "_ah/api/tinyGramApi/v1/get/users/" + "${KeyFactory.keyToString(entity.key)}" + "/posts?next="+Posts.nextToken})
	        .then(function(result) {
	        	console.log("got:",result)
	        	result.items.map(function(item){Posts.list.push(item)})
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


var ProfileView = {
			 oninit: User.loadList(),
			 view: function() {
				 let size = Object.size(Posts.list);
			 return m('div', {class:'media profile-information'},[
				  	m('img', {class:'align-self-center mr-3 profile-logo', src: "/resources/img/user.png"}),
			  		m('div', {class: 'media-body profile-information-body'}, [
						m('h5', {class: 'mt-0'}, User.data.name),
				  		m('div', {class: 'row'}, [
				  			m('div', {class: 'col'}, [
	 			  				m('b', {class: ''}, size + " posts"),
				  			]),
				  			m('div', {class: 'col'}, [
				  				m('b', {class: '', id: "followers"}, "nb followers")
				  			]),
				  			m('div', {class: 'col'}, [
	 			  				m('b', {class: ''}, Object.size(User.data.friends)+" follows")
				  			])
				  		]),
				  		m('div', {class: 'row'}, [
				  			m('div', {class: 'col'}, [
				  				m('b', {class: ''}, User.data.mail),
				  			])
						])
				  	]),
				  	m('hr', {class:'solid'})
				])
		 	}
	};



var PostView = {
			 oninit: Posts.loadList(),
			 view: function() {
				 var class_array = [];
				 
				 Posts.list.map(function(item) {
					 if(item.properties.likes){
						 if (item.properties.likes.includes("${KeyFactory.keyToString(actualU.key)}")) {
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
	 		console.log("name_" + name);
	 		document.getElementById('title' + id).innerHTML = name;
 	});
}	
	
var view = {
	view: function() {
		return m('div', [
			m('div', m(ProfileView)),
		   	m('div', {class: 'row justify-content-center' }, [
		   		m("div", {class: 'col-8'}, m(PostView)),
		   	]) 	
	    ])
	}
};

m.mount(document.getElementById("script"), view);
	
function updateLike(id) {

	
	if (document.getElementById(id) != null) {
		let aclass = document.getElementById(id).className; 
		if (aclass.includes('fa-heart-o')) {
			document.getElementById(id).className = 'fa fa-heart';
			
			var data = {'id_post': id,
 					'id_user_to_add': "${KeyFactory.keyToString(actualU.key)}"}
 	    	
     		
			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/like/" + "${KeyFactory.keyToString(actualU.key)}",
         	}).then(function(result) {
     	 			Likes.loadLikes(id)
         	 });
			
		} else {
			document.getElementById(id).className = 'fa fa-heart-o';
			
			var data = {'id_post': id,
 					'id_user_to_remove': "${KeyFactory.keyToString(entity.key)}"}
 	    	
     		
			m.request({
         		method: "PUT",
         		url: "_ah/api/tinyGramApi/v1/put/posts/" + id + "/unlike/" + "${KeyFactory.keyToString(actualU.key)}",
         	}).then(function(result) {
     	 			Likes.loadLikes(id)
         	 });
		}
	} else {
		setTimeout(updateLike(id), 1000);
	}
	
}

function printFollowers(id) {
	m.request({
 		method: "GET",
 		url: "_ah/api/tinyGramApi/v1/get/users/"+ id +"/followers",
 	}).then(function(result) {
	 		let name = result;
	 		if(result == null){
	 			document.getElementById("followers").innerHTML = "0 followers"
	 		}else{
	 			document.getElementById("followers").innerHTML = Object.size(result) + " followers"
	 		}
 	});
}



</script>

</body>
</html>