package tinyGram;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Entity;


@WebServlet(name = "Unfollow", urlPatterns = { "/unfollow" })
public class UnfollowQuery extends HttpServlet {
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key userToUnfollowKey = KeyFactory.stringToKey(request.getParameter("key"));
		Key userKey = KeyFactory.createKey("User", request.getUserPrincipal().getName());
		request.setAttribute("last", request.getParameter("last"));
		String url = request.getParameter("url");

		
		Transaction transac = datastore.beginTransaction();
		try {	
			Entity user = datastore.get(userKey);
			ArrayList<String> friends = (ArrayList<String>) user.getProperty("friends");
			friends.remove(userToUnfollowKey.getName());
			user.setProperty("friends", friends);
			datastore.put(transac, user);
			transac.commit();
			
			
			if(url.compareTo("/members") == 0) {
				if(request.getParameter("cursor").compareTo("null") == 0) {
					response.sendRedirect(request.getParameter("url"));
				}else {
					response.sendRedirect(request.getParameter("url")+"?cursor="+request.getParameter("cursor"));
				}
			}else {
				if(request.getParameter("cursor").length() == 0) {
					response.sendRedirect(request.getParameter("url"));
				}else {
					response.sendRedirect(request.getParameter("url")+"?last="+request.getParameter("cursor"));
				}
			}
			
		//catch is necessary for the datastore.get() method
		}catch (EntityNotFoundException e) {
			e.printStackTrace();
			
			if(url.compareTo("/members") == 0) {
				if(request.getParameter("cursor").compareTo("null") == 0) {
					response.sendRedirect(request.getParameter("url"));
				}else {
					response.sendRedirect(request.getParameter("url")+"?cursor="+request.getParameter("cursor"));
				}
			}else {
				if(request.getParameter("cursor").length() == 0) {
					response.sendRedirect(request.getParameter("url"));
				}else {
					response.sendRedirect(request.getParameter("url")+"?last="+request.getParameter("cursor"));
				}
			}

		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}

	}
}
