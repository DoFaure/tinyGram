package tinyGram;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Entity;

/* ---------------------------------------------- */
/* DOESNT WORK CURRENTLY --- IT SHOWS EVERY USERS */
/* ---------------------------------------------- */

@WebServlet(name = "Followers", urlPatterns = { "/followers" })
public class FollowersServlet extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(20);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity principal = null;
		Key keyNewUser = KeyFactory.createKey("User", request.getUserPrincipal().getName());

		try {
			principal = datastore.get(keyNewUser);
		} catch (EntityNotFoundException ex) {
			
		}
		
		String startCursor = request.getParameter("cursor");
		if (startCursor != null) {
		      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
	    }
	
		Query q = new Query("User");
	    PreparedQuery pq = datastore.prepare(q);
	    QueryResultList<Entity> results;
	    
	    try {
	    	
	      results = pq.asQueryResultList(fetchOptions);
	      
	      //Remove current user from the list
		    if(results.contains(principal)) {
		    	results.remove(principal);
		    }
		    
		  //Remove Users who don't follow the current profile
	    	for(Entity e : results) {
	    		ArrayList<String> listOfFriendsFollower = (ArrayList<String>) e.getProperty("friends");
	    		if(listOfFriendsFollower != null) {
	    			if(!listOfFriendsFollower.contains(principal.getKey().getName())) {
	    				results.remove(e);	
	    			}
	    		}
	    	}
	    	
	    } catch (IllegalArgumentException e) {
	      // IllegalArgumentException happens when an invalid cursor is used.
	      // A user could have manually entered a bad cursor in the URL or there
	      // may have been an internal implementation detail change in App Engine.
	      // Redirect to the page without the cursor parameter to show something
	      // rather than an error.
	      response.sendRedirect("/homepage");
	      return;
	    }
	    
	    PrintWriter w = response.getWriter();
	    w.println("<!DOCTYPE html>");
	    w.println("<head>\n" + 
	    		"<meta charset=\"UTF-8\">\n" + 
	    		"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + 
	    		"<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" + 
	    		"<title>TinyGram</title>\n" + 
	    		"<link rel=\"stylesheet\" href=\"resources/style/style.css\">\n" + 
	    		"<link rel=\"shortcut icon\" href=\"resources/img/logo.png\"\n" + 
	    		"	type=\"image/x-icon\">\n" + 
	    		"<link type=\"text/css\" rel=\"stylesheet\"\n" + 
	    		"	href=\"/bootstrap/css/bootstrap.css\" rel=\"stylesheet\">\n" + 
	    		"<link type=\"text/css\" rel=\"stylesheet\"\n" + 
	    		"	href=\"/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">\n" + 
	    		"<link rel=\"stylesheet\" href=\"/font-awesome/css/font-awesome.min.css\">\n" + 
	    		"<script\n" + 
	    		"	src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" + 
	    		"<script src=\"/bootstrap/js/bootstrap.min.js\"></script>\n" + 
	    		"<script src=\"/bootstrap/js/bootstrap.js\"></script>\n" + 
	    		"\n" + 
	    		"</head>\n" + 
	    		"<body>\n" + 
	    		"	<!-- 	navbar / menu -->\n" + 
	    		"	<nav class=\"navbar navbar-light bg-light align-items-center\">\n" + 
	    		"		<a class=\"nav navbar-nav navbar-left navbar-brand\" href=\"/homepage\">\n" + 
	    		"			<img class=\"icon-nav\" src=\"resources/img/logo.png\" alt=\"\">TinyGram\n" + 
	    		"		</a>\n" + 
	    		"		<form class=\"form-inline nav navbar-nav navbar-center\">\n" + 
	    		"			<div class=\"input-group\">\n" + 
	    		"				<div class=\"input-group-prepend\">\n" + 
	    		"					<span class=\"input-group-text\" id=\"basic-addon1\">@</span>\n" + 
	    		"				</div>\n" + 
	    		"				<input type=\"text\" class=\"form-control\" placeholder=\"Username\"\n" + 
	    		"					aria-label=\"Username\" aria-describedby=\"basic-addon1\">\n" + 
	    		"			</div>\n" + 
	    		"		</form>\n" + 
	    		"		<div class=\"nav navbar-nav navbar-right justify-content-end\">\n" + 
	    		"			<div class=\"nav navbar-nav navbar-right\">\n" + 
	    		"	 	  		<a class=\"like\" href=\"/followers\"><img class=\"icon-nav\" src=\"/resources/img/heart.png\"></a>\n" + 
	    		" 	 	  		<a class=\"profile\" href=\"/profile?key="+KeyFactory.keyToString(principal.getKey())+"\"><img class=\"icon-nav\" src=\"/resources/img/user.png\"></a>\n" + 
	    		"	  		</div>\n" + 
	    		"		</div>\n" + 
	    		"	</nav>\n" +
	    		" <div class=\"container\"" +
    			"    <div class=\"card\">\n" + 
    			"		<div class=\"list-members\">\n" + 
	    		"");

	    for (Entity entity : results) {
	    Key userKey = KeyFactory.createKey("User",entity.getKey().getName());

	    //userKey = KeyFactory.keyToString(userKey);
	      w.println("<div class=\"content\">\n" + 
	      		"		<div class=\"identity\">\n" + 
	      		"			<p class=\"card-text btn-align\">\n" + 
	      		"				<a class=\"profile-link\" href=\"/profile?key="+KeyFactory.keyToString(entity.getKey())+"\">\n" + 
	      		"				<b>"+entity.getProperty("firstName") +" "+entity.getProperty("lastName") +"</b>\n" + 
	      		"											</a>\n" + 
	      		"										</p>\n" + 
	      		"									</div>\n" + 
	      		"									<div class=\"follow\">\n" +
	      		"");
	      
		ArrayList<String> friends = (ArrayList<String>) principal.getProperty("friends");   
		   if(friends != null) {
			   if(friends.contains(entity.getKey().getName())) {
				   w.println("<a href=\"/unfollow?url="+request.getRequestURI()+"&key="+KeyFactory.keyToString(entity.getKey()) +"\"\n" + 
				   		"class=\"btn btn-danger btn-sm\">Unfollow</a>");
			   }else{
				   w.println("<a href=\"/follow?url="+request.getRequestURI()+"&key="+KeyFactory.keyToString(entity.getKey()) +"\"\n" + 
				   		"class=\"btn btn-primary btn-sm\">Follow</a");
			   }
		   }else{
			   		w.println("<a href=\"/follow?url="+request.getRequestURI()+"&key="+KeyFactory.keyToString(entity.getKey())+"\"\n" + 
			   				"class=\"btn btn-primary btn-sm\">Follow</a>");
		   }
		   
		   w.println("</div>\n" + 
	      		"</div>");
	    }
    
	    String cursorString = results.getCursor().toWebSafeString();

	    // This servlet lives at '/people'.
	    w.println("<div class=\"next\">"
	    		+ "<a href='/members?cursor=" + cursorString + "'>Next page</a>"
				+ "</div>");
	    
	    
	    w.println("	</div>\n" + 
		"\n" + 
		"</body>\n" + 
		"</html>");
		
	}
	
}