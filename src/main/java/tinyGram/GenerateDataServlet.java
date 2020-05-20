package tinyGram;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

@WebServlet(name = "GenerateData", urlPatterns = { "/generatedatas" })
public class GenerateDataServlet extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		response.getWriter().print("<a href = '/../index.jsp'> Back to index </a>");		

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Random r = new Random();
		//Incremental Post key 
		
		
		//Create lists to avoid saturation of datastore.put
		ArrayList<Entity> postsList = new ArrayList();
		ArrayList<Entity> usersList = new ArrayList();
		// Create users
		for (int i = 1; i <= 500; i++) {
			Entity user = new Entity("User", "u" + i);
			user.setProperty("name", "name" + i);
			user.setProperty("mail", "mail"+i+"@gmail.com");

			//Create user friends
			HashSet<String> friends = new HashSet<String>();
			while(friends.size() < r.nextInt(200)) {
				friends.add("u" + (r.nextInt(500-1)+1));
			} 
			user.setProperty("friends", friends);
			
			//Create user posts
			HashSet<String> posts = new HashSet<String>();
			for(int j=1;j<=(r.nextInt(20-1)+1);j++) {
				
				//EPOCH random value between 2008 and now.
				long epoch = ThreadLocalRandom.current().nextLong((long) 1199221200, Instant.now().getEpochSecond());
				//ID of posts is MAX 64-BIT Value minus EPOCH value to get posts order by date.
				String id = Long.toString(Long.MAX_VALUE-epoch); 
				Entity post = new Entity("Post", id);
				post.setProperty("owner", KeyFactory.keyToString(user.getKey()));
				post.setProperty("body", "Message of the post"+j);	
				
				Instant instant = Instant.ofEpochSecond(epoch);	
				Date date = Date.from(instant);
				
				post.setProperty("date", epoch);
				post.setProperty("URL", "https://dummyimage.com/600x600/000/fff&text="+date);
				
				
				//Create receivers list --- NULL for now
				post.hasProperty("receivers");
				//Create likes for Posts
				HashSet<String> likes = new HashSet<String>();
				for(int k=0; k < 150; k++ ) {
					likes.add("u"+r.nextInt(500));
				}
				
				post.setProperty("likes", likes);
				posts.add(post.getKey().getName());
				postsList.add(post);
				
			}
			
			//add user to the Users list
			usersList.add(user);


		response.getWriter().print("<br><li> created friend:" + user.getKey() + "<br> Friends : " + friends + "<br>" + "<br> Posts : " + posts.toString() + "<br>");
		}
		
		
		//Push all Lists to datastore
		datastore.put(usersList);
		datastore.put(postsList);
	}
}
 