package tinyGram;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

import entity.Post;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.appengine.api.datastore.Key;


@Api(name = "tinyGramApi",
version="v1",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))
public class TinyGramEndpoint {

	public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	
//--------------------------------------------------- ALL THE GET ---------------------------------------------------------//

	
	/*
	 * Return the first Posts by limit 
	 */
	@ApiMethod(name = "posts", path="get/posts",	httpMethod = HttpMethod.GET)
	public List<Entity> posts() {
			Query q =new Query("Post").addSort("date", SortDirection.ASCENDING);

			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
			return result;
	}	
	
	/**
	 * Return the list of the users who can see the post (string key given)
	 * @param id - Post String key
	 * @return List<String> receiversPost
	 */
	@ApiMethod(name = "receivers_post", path="get/posts/{id}/receivers", httpMethod = HttpMethod.GET)
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
	 * @return List<String> post likes
	 */
	@ApiMethod(name = "likes_post", path="get/posts/{id}/likes", httpMethod = HttpMethod.GET)
	public List<String> postsLikes(@Named("id") String id){
		
		Query q = new Query("Post").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		List<String> likes = (List<String>) result.getProperty("likes");
		return likes;
		
	}
	
	/*
	 * Return the first Users by limit
	 */
	@ApiMethod(name = "users", path="get/users", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> users(@Nullable @Named("next") String cursorString) {
			Query q =new Query("User");

			PreparedQuery pq = datastore.prepare(q);
		    
		    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(5);
		    
		    if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
			}
		    
		    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		    cursorString = results.getCursor().toWebSafeString();
		    
		    return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();    
	}	
	
	/**
	 * Return User by the given Key
	 * @param id - User String key
	 * @return Entity User 
	 */
	@ApiMethod(name = "user", path="get/users/{id}", httpMethod = HttpMethod.GET)
	public Entity user(@Named("id") String id){
		
		Query q = new Query("User").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity user = pq.asSingleEntity();

		return user;
		
	}
	
	/**
	 * Return the list of the user's posts (string key given)
	 * @param id  - User String key
	 * @return List<Entity>User 
	 */
	@ApiMethod(name = "user_posts", path="get/users/{id}/posts", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> userPosts(@Named("id") String id,@Nullable @Named("next") String cursorString){
		
		Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, id));
		PreparedQuery pq = datastore.prepare(q);
		
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(5);
		
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    
	    if (cursorString != null) {
		    	fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
		

		cursorString = results.getCursor().toWebSafeString();
		
		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();

		
	}
	
	/**
	 * Return the list of friends of the user (string key given)
	 * @param id  - User String key
	 * @return List<String> name user 
	 */
	@ApiMethod(name = "user_friends", path="get/users/{id}/friends", httpMethod = HttpMethod.GET)
	public List<String> userFriends(@Named("id") String id){
		
		Query q = new Query("User").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, KeyFactory.stringToKey(id)));
		PreparedQuery pq = datastore.prepare(q);
		Entity user = pq.asSingleEntity();
		List<String> friends = (List<String>) user.getProperty("friends");

		return friends;
		
	}
	
	/**
	 * Return Collection of the post that the user is able to see
	 * @param id - User String key
	 * @return Entity User 
	 */
	@ApiMethod(name = "user", path="get/users/{id}/receive", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> postsUserCanSee(@Named("id") String id, @Nullable @Named("next") String cursorString){
		
		Query q = new Query("Post").addSort("date", SortDirection.DESCENDING);
		PreparedQuery pq = datastore.prepare(q);
		
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(5);
		
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    
		for(Entity post : results) {
			List<String> receivers = (List<String>) post.getProperty("receivers");
			if(receivers != null) {
				if(!receivers.contains(id)) {
					results.remove(post);
				}
			}else {
				results.remove(post);
			}
		
		}
		   
	    if (cursorString != null) {
		    	fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
		

		cursorString = results.getCursor().toWebSafeString();
		
		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}

//--------------------------------------------------- ALL THE PUT ---------------------------------------------------------//
	
	
	/**
	 * Follow someone. Update the User friends list given by id_user 
	 * by adding the new friend id by given by id_user_to_add variable.
	 *  
	 * @param id_user
	 * @param id_user_to_add 
	 */
	@ApiMethod(name = "follow_user", path="put/users/{id_user}/follow/{id_user_to_add}", httpMethod = HttpMethod.PUT)
	public void followUser(@Named("id_user") String id_user, @Named("id_user_to_add") String id_user_to_add){
		
		Transaction transac = datastore.beginTransaction();
		try {			
			Entity user = datastore.get(KeyFactory.stringToKey(id_user));
			ArrayList<String> friends = (ArrayList<String>) user.getProperty("friends");
			if(friends == null) {
				friends = new ArrayList<String>();
			}	
			friends.add(id_user_to_add);			
			user.setProperty("friends", friends);
			datastore.put(transac,user);		
			transac.commit();
			
		//catch is necessary for the datastore.get() method
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}
		
	}
	
	/**
	 * Unfollow someone. Update the User friends list given by id_user 
	 * by removing the friend id given by id_user_to_add variable.
	 *  
	 * @param id_user
	 * @param id_user_to_remove
	 */
	@ApiMethod(name = "unfollow_user", path="put/users/{id_user}/unfollow/{id_user_to_remove}", httpMethod = HttpMethod.PUT)
	public void unfollowUser(@Named("id_user") String id_user, @Named("id_user_to_remove") String id_user_to_remove){
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Transaction transac = datastore.beginTransaction();
		try {			
			Entity user = datastore.get(KeyFactory.stringToKey(id_user));
			ArrayList<String> friends = (ArrayList<String>) user.getProperty("friends");
			friends.remove(id_user_to_remove);			
			user.setProperty("friends", friends);
			datastore.put(transac,user);		
			transac.commit();
			
		//catch is necessary for the datastore.get() method
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}	
	}
	
	/**
	 * Like a post. Update the Post likes list given by id_post 
	 * by adding the friend id given by id_user_to_add variable.
	 *  
	 * @param id_post
	 * @param id_user_to_add
	 */
	@ApiMethod(name = "like_post", path="put/posts/{id_post}/like/{id_user_to_add}", httpMethod = HttpMethod.PUT)
	public void likepost(@Named("id_post") String id_post, @Named("id_user_to_add") String id_user_to_add){
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Transaction transac = datastore.beginTransaction();
		try {			
			
			Entity post = datastore.get(KeyFactory.stringToKey(id_post));
			ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
			//create list if it's first like
			if(likes == null) {
				likes = new ArrayList<String>();
			}	
			likes.add(id_user_to_add);			
			post.setProperty("likes", likes);
			datastore.put(transac,post);		
			transac.commit();
				
		
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}
	}
	
	/**
	 * Unlike a post. Update the Post likes list given by id_post 
	 * by removing the friend id given by id_user_to_add variable.
	 *  
	 * @param id_post
	 * @param id_user_to_remove
	 */
	@ApiMethod(name = "unlike_post", path="put/posts/{id_post}/unlike/{id_user_to_remove}", httpMethod = HttpMethod.PUT)
	public void unlikepost(@Named("id_post") String id_post, @Named("id_user_to_remove") String id_user_to_remove){
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Transaction transac = datastore.beginTransaction();
		try {			
			
			Entity post = datastore.get(KeyFactory.stringToKey(id_post));
			ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
			likes.remove(id_user_to_remove);	
			post.setProperty("likes", likes);
			datastore.put(transac,post);		
			transac.commit();
				
		
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}
	}

//--------------------------------------------------- ALL THE POST ---------------------------------------------------------//

	/**
	 * 
	 * @param Post 
	 * @return Created Post
	 */
	@ApiMethod(name = "createpost", path="post/posts/create", httpMethod = HttpMethod.POST)
	public Entity createPost(Post pm) {

		long epochMilliNow = Instant.now().toEpochMilli();
		String id = Long.toString(Long.MAX_VALUE-epochMilliNow);
		
		Instant instant = Instant.ofEpochMilli(epochMilliNow);	
		Date date = Date.from(instant);
		
		
		Entity e = new Entity("Post", id);
		e.setProperty("owner", pm.owner);
		e.setProperty("URL", pm.url);
		e.setProperty("body", pm.body);
		e.hasProperty("likes");
		e.hasProperty("receivers");
		e.setProperty("date", date);

		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
	}
}
		
