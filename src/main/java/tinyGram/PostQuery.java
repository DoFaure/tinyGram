package tinyGram;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

@WebServlet(name = "Post", urlPatterns = { "/post" })
public class PostQuery {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();	
		Key postToLikeKey = KeyFactory.stringToKey(request.getParameter("key"));
		Key userKey = KeyFactory.createKey("User", request.getUserPrincipal().getName());
		Entity user;
		try {
			//get user before start transaction, if not it's consider as cross-group transaction
			user = datastore.get(userKey);
			
			//Then start transaction
			Transaction transac = datastore.beginTransaction();
			try {			
			
				Entity post = datastore.get(postToLikeKey);
				ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
				//create list if it's first like
				if(likes == null) {
					likes = new ArrayList<String>();
				}	
				likes.add(user.getKey().getName());			
				post.setProperty("likes", likes);
				datastore.put(transac,post);		
				transac.commit();
				response.sendRedirect("/homepage");
				
			//catch is necessary for the datastore.get() method
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				response.sendRedirect("/homepage");

			}
			finally {
				if(transac.isActive()) {
					transac.rollback();
				}
			}
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
