package com.bancvue.mongomigrate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 12:15 PM
 */
@RunWith( JUnit4.class )
public class MigrationFileRepositoryTests {

	@After
	public void tearDown() {
		// Delete all js files in that the test may have created.
		File directory = new File( "." );
		Collection<File> files = FileUtils.listFiles( directory, new WildcardFileFilter( "*.js" ), null );
		for ( File file : files ) {
			FileUtils.deleteQuietly( file );
		}
	}

	@Test
	public void shouldBeAbleToLoadAllMigrationFilesFromFolder() throws IOException {
		Files.createFile( Paths.get( "20130629200000_migration1.js" ) );
		Files.createFile( Paths.get( "20130629200000_migration2.js" ) );

		IMigrationFileRepository repository = new MigrationFileRepository( "." );

		Collection<File> files = repository.findAll();

		assertThat( files.toArray(), hasItemInArray( hasProperty( "name", equalTo( "20130629200000_migration1.js" ) ) ) );
		assertThat( files.toArray(), hasItemInArray( hasProperty( "name", equalTo( "20130629200000_migration2.js" ) ) ) );
	}
}
