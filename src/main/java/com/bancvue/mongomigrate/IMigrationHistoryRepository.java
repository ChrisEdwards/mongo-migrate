package com.bancvue.mongomigrate;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/2/13
 * Time: 4:12 PM
 */
public interface IMigrationHistoryRepository {
	void save( Migration migration );
	Migration find( String name );
	List<Migration> findAll();
	void remove( Migration migration );
	void removeAll();
	String getDatabaseName();
}
