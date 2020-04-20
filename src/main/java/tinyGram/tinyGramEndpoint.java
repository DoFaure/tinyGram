package tinyGram;

import java.util.List;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


@Api(name = "tinyGramApi",
namespace = @ApiNamespace(ownerDomain = "helloworld.example.com",
    ownerName = "helloworld.example.com",
    packagePath = ""))
public class tinyGramEndpoint {
	
	Random r=new Random();

	@ApiMethod(name = "users",	httpMethod = HttpMethod.GET)
	public List<Entity> users() {
			Query q =new Query("User");

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(250));
			return result;
	}	
}
		
