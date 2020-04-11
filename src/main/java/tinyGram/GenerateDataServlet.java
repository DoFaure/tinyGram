package tinyGram;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Random r = new Random();
		// Create users
		for (int i = 0; i < 500; i++) {
			Entity user = new Entity("User", "u" + i);
			user.setProperty("firstName", "first" + i);
			user.setProperty("lastName", "last" + i);
			user.setProperty("age",r.nextInt(100) + 1);
			user.setProperty("mail", "mail"+i+"@gmail.com");
			
			// Create user friends
			HashSet<String> friends = new HashSet<String>();
			while(friends.size() < 200) {
				friends.add("friend" + r.nextInt(500));
			}
			user.setProperty("friends", friends);

			//Create user posts
			HashSet<String> posts = new HashSet<String>();
			for(int j=0;j<r.nextInt(100);j++) {
				Entity post = new Entity("Post","post"+j);
				post.setProperty("user", KeyFactory.keyToString(user.getKey()));
				post.setProperty("body", "body"+j);		
				//Random time stamp
				Date date = new Date();	
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
				try {
					Calendar c = Calendar.getInstance();			
					date = format.parse(c.get(Calendar.YEAR)+"-"+r.nextInt(12)+"-"+r.nextInt(30)+" "+r.nextInt(24)+":"+r.nextInt(60)+":00");
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  			
				post.setProperty("date", date);
				post.setProperty("URL", "http://imagePostTinyGram"+j+".com");
				HashSet<String> likes = new HashSet<String>();
				for(int k=0; k < 150;k++ ) {
					likes.add("user"+r.nextInt(500));
				}
				post.setProperty("likes", likes);
				
				datastore.put(post);
				
			}
			user.setProperty("posts", posts);
			
			datastore.put(user);


			response.getWriter().print("<li> created friend:" + user.getKey() + "<br>" + friends + "<br>");

		}
	}
}
 