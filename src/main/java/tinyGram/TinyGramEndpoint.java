package tinyGram;

import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;



@Api(name = "tinyGramApi",
version="v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))
public class TinyGramEndpoint {

	public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	/*
	 * Return the 200 first Users
	 */
	@ApiMethod(name = "users", path="users",	httpMethod = HttpMethod.GET)
	public List<Entity> users() {
			Query q =new Query("User");

			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
			return result;
	}	
	
	/*
	 * Return the 200 first Posts 
	 */
	@ApiMethod(name = "posts", path="posts",	httpMethod = HttpMethod.GET)
	public List<Entity> posts() {
			Query q =new Query("Post").addSort("date", SortDirection.ASCENDING);

			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
			return result;
	}	
	
	/**
	 * Return the list of the users who can see the post (string key given)
	 * @param id - Post String key
	 * @return List<String> receiversPost
	 */
	@ApiMethod(name = "receivers_post", path="posts/{id}/receivers", httpMethod = HttpMethod.GET)
	public List<String> postsReceivers(@Named("id") String id){
		
		Query q = new Query("Post").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		List<String> receivers = (List<String>) result.getProperty("receivers");
		return receivers;
		
	}
	
	/**
	 * Return the list of the users that like the post (string key given) 
	 * @param id - Post String key
	 * @return List<String> postlikes
	 */
	@ApiMethod(name = "likes_post", path="posts/{id}/likes", httpMethod = HttpMethod.GET)
	public List<String> postsLikes(@Named("id") String id){
		
		Query q = new Query("Post").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		List<String> likes = (List<String>) result.getProperty("likes");
		return likes;
		
	}
	/**
	 * Return the list of the user's posts (string key given)
	 * @param id  - User String key
	 * @return List<Entity>User 
	 */
	@ApiMethod(name = "user_posts", path="users/{id}/posts", httpMethod = HttpMethod.GET)
	public List<Entity> userposts(@Named("id") String id){
		
		Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> posts = pq.asList(FetchOptions.Builder.withDefaults());

		return posts;
		
	}
	
	/**
	 * Return User by the given Key
	 * @param id - User String key
	 * @return Entity User 
	 */
	@ApiMethod(name = "user", path="users/{id}", httpMethod = HttpMethod.GET)
	public Entity user(@Named("id") String id){
		
		Query q = new Query("User").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity user = pq.asSingleEntity();

		return user;
		
	}
	
	
}
		
