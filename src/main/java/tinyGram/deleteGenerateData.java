package tinyGram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Projection;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(name = "DeleteData", urlPatterns = { "/deletedata" })

public class deleteGenerateData extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {	
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();	
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		resp.getWriter().print("<a href = '/index.jsp'> Retour a l'index </a>");	
		Query q = new Query("User");		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());	
		ArrayList<Key> keys = new ArrayList<Key>();
		for (Entity entity : result) {
			keys.add(entity.getKey());			
		}
		q = new Query("Post");		
		pq = datastore.prepare(q);
		result = pq.asList(FetchOptions.Builder.withDefaults());			
		for (Entity entity : result) {
			keys.add(entity.getKey());			
		}
		datastore.delete(keys);		
	}
}


