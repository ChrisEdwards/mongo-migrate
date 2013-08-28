package com.bancvue.mongomigrate;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 12:20 PM
 */
public class MigrationFileRepository implements IMigrationFileRepository {


	private final File dir;

	public MigrationFileRepository( String pathToMigrationFiles ) {
		this.dir = new File( pathToMigrationFiles );
	}

	@Override
	public List<File> findAll() {
		return new ArrayList<File>(FileUtils.listFiles( dir, new String[]{ "js" }, true ));
	}
}
