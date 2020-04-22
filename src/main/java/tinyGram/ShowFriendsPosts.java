package tinyGram;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@WebServlet(name = "Homepage", urlPatterns = { "/homepage" })
public class ShowFriendsPosts extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		UserService userService = UserServiceFactory.getUserService();		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
		
		//Create User if it's first connection
		Principal newUser = request.getUserPrincipal();	
		Entity e;
		Key keyNewUser = KeyFactory.createKey("User", request.getUserPrincipal().getName());
		try {
			e = datastore.get(keyNewUser);
		} catch (EntityNotFoundException ex) {
			
			e = new Entity("User", request.getUserPrincipal().getName());
			e.setProperty("mail", userService.getCurrentUser().getEmail());
			e.setProperty("firstName", userService.getCurrentUser().getNickname());
			datastore.put(e);
		}	


		
		//first 5 members from database
		Query qfm = new Query("User");
		PreparedQuery pqfm = datastore.prepare(qfm);
		List<Entity> fiveMembers = pqfm.asList(FetchOptions.Builder.withLimit(5));
		request.setAttribute("users", fiveMembers);

		
		
		//all friends for the connected User
		ArrayList<String> listOfFriends = new ArrayList<String>();
		ArrayList<Key> friendsKeys = new ArrayList<Key>();	
		ArrayList<Key> friendsPostsKeys = new ArrayList<Key>();	

		Entity user;
		Key userKey = KeyFactory.createKey("User", request.getUserPrincipal().getName());
		try {
			user = datastore.get(userKey);
			fiveMembers.remove(user);
			listOfFriends = (ArrayList<String>) user.getProperty("friends");
		} catch (EntityNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		//Query to get all Friends informations 
		if(listOfFriends != null) {
			
			for(String friend : listOfFriends){
				friendsKeys.add(KeyFactory.createKey("User", friend));
			}
			Filter propertyFilter =   new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN,friendsKeys);
			Query qu = new Query("User").setFilter(propertyFilter);
			PreparedQuery pqu = datastore.prepare(qu);
			List<Entity> resultFriends = pqu.asList(FetchOptions.Builder.withDefaults());
			
			request.setAttribute("friends", resultFriends);
			
			//Then get All posts from friends
			ArrayList<String> posts = new ArrayList<String>();			
			for(Entity r : resultFriends) {
				if(r.getProperty("posts") != null){
					posts.addAll((ArrayList<String>) r.getProperty("posts"));
				}
			}
		
			//Query to get all posts informations
			if(posts.size()>0) {
				for(String post : posts) {
					friendsPostsKeys.add(KeyFactory.createKey("Post", post));
				}
				if(friendsPostsKeys.size()>0) {
					List<Entity> resultPosts = null;
					Collections.sort(friendsPostsKeys);
					try {
						//System.out.println(request.getParameter("last"));
						request.setAttribute("currentLast", request.getParameter("last"));
						Key keyLastUser = KeyFactory.createKey("Post", request.getParameter("last"));
						Entity last = datastore.get(keyLastUser);
						Filter propertyPostsFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, friendsPostsKeys);
						Filter propertyPostsFilterPaginate = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, last.getKey());
						Query qp = new Query("Post").setFilter(CompositeFilterOperator.and(propertyPostsFilter, propertyPostsFilterPaginate));
						PreparedQuery pqp = datastore.prepare(qp);
						resultPosts = pqp.asList(FetchOptions.Builder.withLimit(5));
					} catch (EntityNotFoundException | java.lang.IllegalArgumentException ex) {
						Filter propertyPostsFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.IN, friendsPostsKeys);
						Query qp = new Query("Post").setFilter(propertyPostsFilter);
						PreparedQuery pqp = datastore.prepare(qp);
						resultPosts = pqp.asList(FetchOptions.Builder.withLimit(5));
					}
					
					
					//Sort by Collections.sort because App Engine has limited Parallel Queries 
					//Collections.sort(resultPosts, (y, x) -> ((Date)x.getProperty("date")).compareTo((Date)y.getProperty("date")));
					
					if (resultPosts.size() > 0) {
						request.setAttribute("last", resultPosts.get(resultPosts.size() - 1).getKey().toString().split("\"")[1]);
					}
					//System.out.println(resultPosts.toString());
					request.setAttribute("posts", resultPosts);
				}
				
			}
		}
		
		
		request.getRequestDispatcher("/homepage.jsp").forward(request, response);
		
	}
	
}

