package tinyGram;

import java.io.IOException;
import java.security.Principal;

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

@WebServlet(name = "Homepage", urlPatterns = { "/homepage" })
public class HomepageServlet extends HttpServlet {

	 @Override
	 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
	 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	 UserService userService = UserServiceFactory.getUserService();
	 
		 if (!userService.isUserLoggedIn()) {
		    resp.sendRedirect("/login");
		 }else {
			 //Get connected user informations
			    Principal newUser = req.getUserPrincipal();
			    Key keyNewUser = KeyFactory.createKey("User", req.getUserPrincipal().getName());
			    Entity e;
			    try {
			        e = datastore.get(keyNewUser);
			        req.setAttribute("entity", e);
			    } catch (EntityNotFoundException ex) {
			        // TODO Auto-generated catch block
			        e = new Entity("User", req.getUserPrincipal().getName());
			        e.setProperty("mail", userService.getCurrentUser().getEmail());
			        e.setProperty("name", userService.getCurrentUser().getNickname());
			        datastore.put(e);
			        try {
						e = datastore.get(e.getKey());
					} catch (EntityNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        req.setAttribute("entity", e);
			    }
			    
			    req.getRequestDispatcher("/homepage.jsp").forward(req, resp);
			 
		 }
	   
	 }

}
