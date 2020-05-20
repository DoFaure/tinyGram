package tinyGram;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.com.google.datastore.v1.PropertyOrder.Direction;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.QueryResults;

import entity.Post;

import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterable;


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
	public CollectionResponse<Entity> posts(@Nullable @Named("next") String cursorString) {
			Query q =new Query("Post").addSort("date", SortDirection.DESCENDING);

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
	 * Return the list of the users that likes the post (string given) 
	 * @param id - Post String key
	 * @return List<String> List of users 
	 */
	@ApiMethod(name = "likes_post", path="get/posts/{id}/likes", httpMethod = HttpMethod.GET)
	public List<String> postsLikes(@Named("id") String id){
		
		Key post = KeyFactory.createKey("Post", id);
		Query q = new Query("Post").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, post));
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
		
	    if (cursorString != null) {
		    	fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
	    
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
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
	 * Return the followers's list of the user (no hash string key given)
	 * @param id  - User no hash String key
	 * @return List<String> name user 
	 */
	@ApiMethod(name = "user_followers", path="get/users/{id}/followers", httpMethod = HttpMethod.GET)
	public List<String> userFollowers(@Named("id") String id, @Nullable @Named("next") String cursorString){
		
		Key user_key = KeyFactory.createKey("User", id);
		Query q = new Query("User").setFilter(new FilterPredicate("__key__", FilterOperator.EQUAL, user_key));
		PreparedQuery pq = datastore.prepare(q);
		Entity user = pq.asSingleEntity();
		List<String> followers = (List<String>) user.getProperty("followers");
		
		return followers;
		
	}
	
	
	
	/**
     * Return Collection of the post that the user is able to see
	 * @param id - User String key
     * @param cursorString
     * @return CollectionResponse<Entity> TGPost
     * @throws UnauthorizedException
     * @throws EntityNotFoundException
     */
	@ApiMethod(name = "user", path="get/users/{id}/receive", httpMethod = HttpMethod.GET)
    public CollectionResponse<Entity> postsUserCanSee(@Named("id") String id, @Nullable @Named("next") String cursorString) throws EntityNotFoundException {
		
		if(cursorString != null) {
			Key post = KeyFactory.createKey("Post", cursorString);
			Filter propertyPostsFilterPaginate = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, post);
			Filter userFilter = new FilterPredicate("receivers", FilterOperator.EQUAL, id);
	        Query q = new Query("Post").setFilter(CompositeFilterOperator.and(userFilter, propertyPostsFilterPaginate)).addSort("__key__", SortDirection.ASCENDING);
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        PreparedQuery pq = datastore.prepare(q);
	        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(20);

	        // retriving all the posts

	        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	        return CollectionResponse.<Entity>builder().setItems(results).build();  
		}else {
	        Query q = new Query("Post").setFilter(new FilterPredicate("receivers", FilterOperator.EQUAL, id)).addSort("__key__", SortDirection.ASCENDING);
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        PreparedQuery pq = datastore.prepare(q);
	        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(20);

	        // retriving all the posts

	        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	        return CollectionResponse.<Entity>builder().setItems(results).build();   
		}       
       
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
			
			/* ADD user_to_add to friends list */
			Entity user = datastore.get(KeyFactory.stringToKey(id_user));
			ArrayList<String> friends = (ArrayList<String>) user.getProperty("friends");
			if(friends == null) {
				friends = new ArrayList<String>();
			}	
			if(!friends.contains(id_user_to_add)) {
				friends.add(id_user_to_add);			
			}
			user.setProperty("friends", friends);
			/* ******************************* */
			
			/* ADD id_user to followers list */
			Key user_to_add = KeyFactory.createKey("User", id_user_to_add);
			Entity user_add = datastore.get(user_to_add);
			ArrayList<String> followers = (ArrayList<String>) user_add.getProperty("followers");
			if(followers == null) {
				followers = new ArrayList<String>();
			}
			if(!followers.contains(id_user)) {
				followers.add(id_user);			
			}
			user_add.setProperty("followers", followers);
			/* ****************************** */
			
			datastore.put(transac,user);
			datastore.put(transac,user_add);
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
			
			/* REMOVE user_to_remove to friends list */
			Entity user = datastore.get(KeyFactory.stringToKey(id_user));
			ArrayList<String> friends = (ArrayList<String>) user.getProperty("friends");
			
			if(friends.contains(id_user_to_remove)) {
				friends.remove(id_user_to_remove);			
			}
			user.setProperty("friends", friends);
			/* ****************************** */
			
			/* REMOVE id_user to followers list */
			Key user_to_remove = KeyFactory.createKey("User", id_user_to_remove);
			Entity user_remove = datastore.get(user_to_remove);
			ArrayList<String> followers = (ArrayList<String>) user_remove.getProperty("followers");
			if(followers != null) {
				if(followers.contains(id_user)) {
					followers.remove(id_user);			
				}
			}
			user_remove.setProperty("followers", followers);
			/* ****************************** */
			
			datastore.put(transac,user);	
			datastore.put(transac,user_remove);
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
	 * @throws EntityNotFoundException 
	 */
	@ApiMethod(name = "like_post", path="put/posts/{id_post}/like/{id_user_to_add}", httpMethod = HttpMethod.PUT)
	public Entity likepost(@Named("id_post") String id_post, @Named("id_user_to_add") String id_user_to_add) throws EntityNotFoundException{
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key postKey = KeyFactory.createKey("Post", id_post);
		Entity post = datastore.get(postKey);
		Transaction transac = datastore.beginTransaction();
		
		try {
		
			ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
			//create list if it's first like
			if(likes == null) {
				likes = new ArrayList<String>();
			}
			if(!likes.contains(id_user_to_add)) {
				likes.add(id_user_to_add);			
			}
			post.setProperty("likes", likes);
			datastore.put(transac,post);		
			transac.commit();

		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}
		
		return post;

	}
	
	/**
	 * Unlike a post. Update the Post likes list given by id_post 
	 * by removing the friend id given by id_user_to_add variable.
	 *  
	 * @param id_post
	 * @param id_user_to_remove
	 * @return 
	 * @throws EntityNotFoundException 
	 */
	@ApiMethod(name = "unlike_post", path="put/posts/{id_post}/unlike/{id_user_to_remove}", httpMethod = HttpMethod.PUT)
	public Entity unlikepost(@Named("id_post") String id_post, @Named("id_user_to_remove") String id_user_to_remove) throws EntityNotFoundException{
		
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key postKey = KeyFactory.createKey("Post", id_post );
		Entity post = datastore.get(postKey);
		Transaction transac = datastore.beginTransaction();
		
		try {
			ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
			
			if(likes.contains(id_user_to_remove)) {
				likes.remove(id_user_to_remove);	
			}
			post.setProperty("likes", likes);
			datastore.put(transac,post);		
			transac.commit();
			
		}
		finally {
			if(transac.isActive()) {
				transac.rollback();
			}
		}
		
		return post;
	}
	
	/**
	 * Update receivers posts from an owner. Update the Post receivers list from owner given by removing an user
	 *  
	 * @param id_owner
	 * @param id_to_remove
	 * @return 
	 * @return 
	 * @throws EntityNotFoundException 
	 */
	@ApiMethod(name = "remove_receivers_posts", path="put/posts/owner/{id_owner}/receivers/remove/{id_to_remove}", httpMethod = HttpMethod.PUT)
	public QueryResultList<Entity> removereceiverposts(@Named("id_owner") String id_owner, @Named("id_to_remove") String id_to_remove) throws EntityNotFoundException{
		
		Key owner = KeyFactory.createKey("User", id_owner);
		Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, KeyFactory.keyToString(owner)));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(20);

        // retriving all the posts

        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);

		for(Entity post: results) {
			ArrayList<String> receivers =  (ArrayList<String>) post.getProperty("receivers");
			if(receivers.contains(id_to_remove)) {
				receivers.remove(id_to_remove);
			}
			post.setProperty("receivers", receivers);	
			
			Transaction txn = datastore.beginTransaction();
			datastore.put(post);
			txn.commit();
		}
		
		 return results;
	}
	
	/**
	 * Update receivers posts from an owner. Update the Post receivers list from owner given by adding an user
	 *  
	 * @param id_owner
	 * @param id_to_add
	 * @return 
	 * @return 
	 * @throws EntityNotFoundException 
	 */
	@ApiMethod(name = "add_receivers_posts", path="put/posts/owner/{id_owner}/receivers/add/{id_to_add}", httpMethod = HttpMethod.PUT)
	public QueryResultIterable<Entity> addreceiverposts(@Named("id_owner") String id_owner, @Named("id_to_add") String id_to_add) throws EntityNotFoundException{
		
		Key owner = KeyFactory.createKey("User", id_owner);
		Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, KeyFactory.keyToString(owner)));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);
        // retriving all the posts

        QueryResultIterable<Entity> results = pq.asQueryResultIterable();

		for(Entity post: results) {
			ArrayList<String> receivers =  (ArrayList<String>) post.getProperty("receivers");
			
			if(receivers == null) {
				receivers = new ArrayList<String>();
			}
			
			if(!receivers.contains(id_to_add)) {
				receivers.add(id_to_add);
			}
			post.setProperty("receivers", receivers);	
    		
			Transaction txn = datastore.beginTransaction();
    		datastore.put(post);
			txn.commit();
		}
         
        return results;
	}


//--------------------------------------------------- ALL THE POST ---------------------------------------------------------//

	/**
	 * 
	 * @param Post 
	 * @return Created Post
	 * @throws EntityNotFoundException 
	 */
	@ApiMethod(name = "createpost", path="post/posts/create", httpMethod = HttpMethod.POST)
	public Entity createPost(Post pm) throws EntityNotFoundException {

		long epochNow = Instant.now().getEpochSecond();
		String id = Long.toString(Long.MAX_VALUE-epochNow);
		
		Entity user = datastore.get(KeyFactory.stringToKey(pm.owner));
		Entity e = new Entity("Post", id);
		e.setProperty("owner", pm.owner);
		if(pm.url.equals("")) {
			Instant instant = Instant.ofEpochSecond(epochNow);	
			Date date = Date.from(instant);
			e.setProperty("URL", "https://dummyimage.com/600x600/000/fff&text="+date);
		}else {
			e.setProperty("URL", pm.url);
		}
		e.setProperty("body", pm.body);
		e.hasProperty("likes");
		e.setProperty("receivers", user.getProperty("followers"));
		e.setProperty("date", epochNow);

		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
	}
}
		
