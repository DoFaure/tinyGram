package tinyGram;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(name = "Profile", urlPatterns = { "/profile" })
public class ProfileServlet extends HttpServlet {

	 @Override
	 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
	 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	 UserService userService = UserServiceFactory.getUserService();
	 
		 if (!userService.isUserLoggedIn()) {
		    resp.sendRedirect("/login");
		 }else {
	     	Key actualUser = KeyFactory.createKey("User", req.getUserPrincipal().getName());
		    //Create key for the Profile user informations passed by the url
		    Key keyNewUser = KeyFactory.createKey("User", req.getParameter("user"));
			    try {
			    	Entity e = datastore.get(keyNewUser);
			        Entity actualU = datastore.get(actualUser);
			        req.setAttribute("entity", e);
			        req.setAttribute("actualU", actualU);
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
	    }	    
			    req.getRequestDispatcher("/profile.jsp").forward(req, resp);
	 }

}
