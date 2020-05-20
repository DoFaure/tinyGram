<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>

<!DOCTYPE html>
<html>
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
				<!-- <div class="input-group-prepend">
					<span class="input-group-text" id="basic-addon1">@</span>
				</div> -->
				<!-- <input type="text" class="form-control" placeholder="Username"
					aria-label="Username" aria-describedby="basic-addon1"> -->
			</div>
		</form>
		<div class="nav navbar-nav navbar-right justify-content-end">
			<div class="nav navbar-nav navbar-right">
	 	  	<!-- 	<a class="like" href="/followers.jsp"><img class="icon-nav" src="/resources/img/heart.png"></a> -->
 	 	  		<a class="profile" href="/profile?user=${entity.properties.mail}"><img class="icon-nav" src="/resources/img/user.png"></a>
	  		</div>
		</div>
	</nav>

	<h1 class="titlePost">Create your post</h1>

	<div class="container" id="script"></div>

	<script>	
	
function redirection() {alert('Your post has been created, you will be redirect');
document.location.href="/profile?user=${entity.properties.mail}";}	

var MyPost = {
	    postMessage: function() {
	    	document.getElementById("buttonPost").disabled = true;
 			var data={
 					'owner':"${KeyFactory.keyToString(entity.key)}",
 					'url':PostForm.url,
 					'body':PostForm.body}
     		return m.request({
         		method: "POST",
         		url: "_ah/api/tinyGramApi/v1/post/posts/create",
             	params: data,
         	})
  	    	.then(function(result) {
     	 			console.log("got:",result),
     	 			redirection()
         	 	})
     	}
}

 
var PostForm = {
		url:"",
		body:"",
		  view: function() {
		    return m("form", {
		      onsubmit: function(e) {
		        e.preventDefault()
				if (PostForm.body=="") {PostForm.body="bla bla bla \n"+Date.now()}
		        MyPost.postMessage()
		      }}, 
		      [
		    	m('div', {class:'form-group row'},[
			         m("label", {class:'label'},"URL"),
			         m("input[type=text]", {
		          class:'form-control is-rounded',
		          placeholder:"Your url",
		             oninput: function(e) {PostForm.url = e.target.value}}),
//		         m("img",{"src":this.url}),
		        ]),
		      m('div',{class:'form-group row'},[
		    	  m("label", {class: 'label'},"Body"),
		          m("textarea", {
		        class:'form-control',
		        placeholder:"Your text",
		        oninput: function(e) { PostForm.body = e.target.value }}),
		        ]),
		      m('div',{class:'control'},m("button[type=submit]", {class:'btn btn-primary is-link', id:'buttonPost'},"Post")),
		    ])
		  }
		}

m.mount(document.getElementById("script"), PostForm)	


</script>
</body>
</html>