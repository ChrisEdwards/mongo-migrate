package com.bancvue.mongomigrate;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import java.net.UnknownHostException;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 5:08 AM
 */
public class MigrationHistoryRepository implements IMigrationHistoryRepository {

	private final JacksonDBCollection<Migration, String> collection;
	private final String databaseName;

	public MigrationHistoryRepository( String host, String databaseName ) throws UnknownHostException {
		this.databaseName = databaseName;
		MongoClient mongoClient = new MongoClient( host );
		DB db = mongoClient.getDB( databaseName );
		DBCollection dbCollection = db.getCollection( "MigrationHistory" );
		collection = JacksonDBCollection.wrap( dbCollection, Migration.class, String.class );
	}

	@Override
	public void save( Migration migration ) {
		collection.save( migration );
	}

	@Override
	public Migration find( String name ) {
		return collection.findOneById( name );
	}

	@Override
	public List<Migration> findAll(){
		return collection.find().toArray();
	}

	@Override
	public void remove( Migration migration ){
		collection.removeById( migration.getName() );
	}

	@Override
	public void removeAll(){
		collection.remove( DBQuery.empty() );
	}

	@Override
	public String getDatabaseName() {
		return databaseName;
	}
}
