package tinyGram;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

@WebServlet(name = "GenerateData", urlPatterns = { "/generateDatas" })
public class GenerateDataServlet extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		response.getWriter().print("<a href = '/../index.jsp'> Retour </a>");		

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Random r = new Random();
		//Incremental Post key 
		int numberOfPost = 1;
		
		//Create lists to avoid saturation of datastore.put
		ArrayList<Entity> postsList = new ArrayList();
		ArrayList<Entity> usersList = new ArrayList();
		// Create users
		for (int i = 1; i <= 500; i++) {
			Entity user = new Entity("User", "u" + i);
			user.setProperty("firstName", "first" + i);
			user.setProperty("lastName", "last" + i);
			user.setProperty("age",r.nextInt(100) + 1);
			user.setProperty("mail", "mail"+i+"@gmail.com");

			//Create user friends
			HashSet<String> friends = new HashSet<String>();
			while(friends.size() < r.nextInt(200)) {
				friends.add("u" + (r.nextInt(500-1)+1));
			} 
			user.setProperty("friends", friends);
			
			//Create user posts
			HashSet<String> posts = new HashSet<String>();
			for(int j=1;j<(r.nextInt(100-1)+1);j++) {
				Entity post = new Entity("Post","post"+numberOfPost);
				numberOfPost++;
				post.setProperty("user", KeyFactory.keyToString(user.getKey()));
				post.setProperty("body", "body"+j);	
				
				//From StackOverflow -- generate random date time with Timestamp using Google App Engine
				Date date = new Date();	
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
				try {
					Calendar c = Calendar.getInstance();			
					date = format.parse(c.get(Calendar.YEAR)+"-"+r.nextInt(12)+"-"+r.nextInt(30)+" "+r.nextInt(24)+":"+r.nextInt(60)+":"+r.nextInt(60));
				} catch (ParseException e) {
					e.printStackTrace();
				} 
				
				
				post.setProperty("date", date);
				post.setProperty("URL", "http://imagePostTinyGram"+j+".com");
				
				//Create likes for Posts
				HashSet<String> likes = new HashSet<String>();
				for(int k=0; k < 150; k++ ) {
					likes.add("u"+r.nextInt(500));
				}
				
				post.setProperty("likes", likes);
				posts.add(post.getKey().getName());
				postsList.add(post);
				
			}
			//add all posts to user
			user.setProperty("posts", posts);
			
			//add user to the Users list
			usersList.add(user);


		response.getWriter().print("<br><li> created friend:" + user.getKey() + "<br> Friends : " + friends + "<br>" + "<br> Posts : " + posts.toString() + "<br>");
		}
		
//		for(Entity u : usersList) {
//			// Create user friends
//			HashSet<String> friends = new HashSet<String>();
//			while(friends.size() < r.nextInt(200)) {
//				friends.add("u" + (r.nextInt(500-1)+1));
//			} 
//			u.setProperty("friends", friends);
//		}
		
		//Push all Lists to datastore
		datastore.put(usersList);
		datastore.put(postsList);
	}
}
 