package tinyGram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(name = "ProfileServlet", urlPatterns = { "/profile" })
public class ProfileServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		UserService userService = UserServiceFactory.getUserService();
		String requestKey = request.getParameter("key");
		Key userProfile = KeyFactory.stringToKey(requestKey);	
		
		try {
			Entity user = datastore.get(userProfile);
			request.setAttribute("user", user);
			ArrayList<String> posts = (ArrayList<String>) user.getProperty("posts");
			ArrayList<String> numberOfFollow = (ArrayList<String>) user.getProperty("friends");
			if(posts != null) {
				request.setAttribute("numberOfPosts", posts.size());
			}else {
				request.setAttribute("numberOfPosts", 0);
			}
			if(numberOfFollow != null) {
				request.setAttribute("numberOfFollow", numberOfFollow.size());

			}else {
				request.setAttribute("numberOfFollow", 0);
			}
			
			ArrayList<Key> postsKeys = new ArrayList<Key>();
			//Get user's posts
			if(posts !=null) {
				for(String p : posts) {
					postsKeys.add(KeyFactory.createKey("Post", p));
				}
				Filter propertyPostsFilter =   new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN,postsKeys);
				Query qp = new Query("Post").setFilter(propertyPostsFilter);
				PreparedQuery pqp = datastore.prepare(qp);
				List<Entity> resultPosts = pqp.asList(FetchOptions.Builder.withDefaults());
				
				//Sort by Collections.sort because App Engine has limited Parallel Queries 
				Collections.sort(resultPosts, (y, x) -> ((Date)x.getProperty("date")).compareTo((Date)y.getProperty("date")));
				
				request.setAttribute("posts", resultPosts );
			}else {
				request.setAttribute("posts", null);
			}
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get numbers of followers
		int numbersOfFollowers = 0;
		Query q = new Query("User");
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> listOfUsers = pq.asList(FetchOptions.Builder.withDefaults());
		for(Entity u: listOfUsers) {
			ArrayList<String> listOfFriends = (ArrayList<String>) u.getProperty("friends");
			if(listOfFriends != null) {
				if(listOfFriends.contains(userProfile.getName())) {
					numbersOfFollowers++;
				}
			}
		}
		request.setAttribute("followers", numbersOfFollowers);

		request.getRequestDispatcher("/profile.jsp").forward(request, response);
	}
}
