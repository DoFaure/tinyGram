package entity;

import java.util.Date;
import java.util.List;

public class Post {
	public String id;
	public String owner;
	public String body;
	public String url;
	public Date date;
	public List<String> receivers;
	public List<String> likes;
	
	
	public Post() {
		
	}
	
}
