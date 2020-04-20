package tinyGram;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(name = "ProfileServlet", urlPatterns = { "/profile" })
public class ProfileServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		UserService userService = UserServiceFactory.getUserService();		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("<a href = '/../homepage.jsp'> Back </a>");	
		
		if(userService.isUserLoggedIn()) {
			
			userService.getCurrentUser().getNickname();
			
		}else {
			response.sendRedirect("/login");
		}
		
	}
}
