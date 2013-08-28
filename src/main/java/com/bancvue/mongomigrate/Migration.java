package com.bancvue.mongomigrate;

import org.mongojack.Id;
import org.mongojack.MongoCollection;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 5:12 AM
 */
@MongoCollection( name = "MigrationHistory" )
public class Migration {
	private String name;
	private Date dateApplied;

	@Id
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Date getDateApplied() {
		return dateApplied;
	}

	public void setDateApplied( Date dateApplied ) {
		this.dateApplied = dateApplied;
	}
}
