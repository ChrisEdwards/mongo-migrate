/*
 * Copyright 2013 BancVue Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bancvue.mongomigrate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/27/13
 * Time: 11:39 AM
 */
@RunWith( JUnit4.class )
public class CreateCommandTests {

	private FakeClock fakeClock;

	@Before
	public void setUp() throws Exception {
		fakeClock = new FakeClock( new DateTime( 2013, 6, 29, 20, 0, 0 ) );
	}

	@After
	public void tearDown() throws IOException {
		// Delete all js files in that the test may have created.
		File directory = new File( "." );
		Collection<File> files = FileUtils.listFiles( directory, new WildcardFileFilter( "*.js" ), null );
		for ( File file : files ) {
			FileUtils.deleteQuietly( file );
		}

		// Delete migrations folder
		File migrationsFolder = new File( "migrations" );
		FileUtils.deleteDirectory( migrationsFolder );
	}

	@Test
	public void shouldCreateMigrationFilenameWithDateTimePrefix() {
		// Arrange.
		CreateCommand command = new CreateCommand();
		command.setClock( fakeClock );

		// Act.
		int exitCode = command.execute();

		// Assert.
		File expectedFile = new File( "20130629200000_migration.js" );
		Assert.assertTrue( "The created migration file's name did not include the correct datetime prefix.", expectedFile.exists() );
		assertThat( "Should have returned successful exit code.", exitCode, is( 0 ) );
	}

	@Test
	public void shouldCreateMigrationFilenameWithUserProvidedNameAsPostfix() {
		// Arrange.
		CreateCommand command = new CreateCommand( "my_migration_name" );
		command.setClock( fakeClock );

		// Act.
		int exitCode = command.execute();

		// Assert.
		File expectedFile = new File( "20130629200000_my_migration_name.js" );
		Assert.assertTrue( "The created migration file's name did not include the user-provided migration name.", expectedFile.exists() );
		assertThat( "Should have returned successful exit code.", exitCode, is( 0 ) );
	}

	@Test
	public void shouldReturnFailingExitCodeIfFileAlreadyExistsWithSameName() throws IOException {
		// Arrange.
		CreateCommand command = new CreateCommand();
		command.setClock( fakeClock );

		Files.createFile( Paths.get( "20130629200000_migration.js" ) );

		// Act.
		int exitCode = command.execute();

		// Assert.
		assertThat( "Should have returned a failing exit code.", exitCode, is( 1 ) );
	}

	@Test
	public void shouldCreateMigrationFileInSpecifiedMigrationsFolder() {
		// Arrange.
		CreateCommand command = new CreateCommand( "my_migration_name", "migrations" );
		command.setClock( fakeClock );

		// Act.
		int exitCode = command.execute();

		// Assert.
		File expectedFile = new File( "./migrations/20130629200000_my_migration_name.js" );
		Assert.assertTrue( "Should have created the migration in the specified sub-folder", expectedFile.exists() );
		assertThat( "Should have returned successful exit code.", exitCode, is( 0 ) );
	}
}

