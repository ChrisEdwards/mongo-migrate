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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 5:06 AM
 */
@RunWith( JUnit4.class )
public class MigrationHistoryRepositoryTests {

	private IMigrationHistoryRepository repository;

	@Before
	public void setUp() throws UnknownHostException {
		repository = new MigrationHistoryRepository( "localhost", "test" );
		repository.removeAll();
	}

	@Test
	public void savingAMigrationShouldStoreItInTheDatabase() {
		// Arrange.
		Migration migration = new Migration();
		migration.setName( "20130629200000_migration.js" );
		migration.setDateApplied( new Date() );

		// Act.
		repository.save( migration );

		// Assert.
		Migration actualMigration = repository.find( "20130629200000_migration.js" );
		Assert.assertNotNull( "Did not find expected migration in the database.", actualMigration );
		Assert.assertEquals( "Found migration had incorrect name", migration.getName(), actualMigration.getName() );
		Assert.assertEquals( "Found migration had incorrect date applied", migration.getDateApplied(), actualMigration.getDateApplied() );
	}

	@Test
	public void shouldBeAbleToRetrieveAllMigrations() {
		// Arrange.
		Migration migration1 = new Migration();
		migration1.setName( "20130629200000_migration1.js" );
		migration1.setDateApplied( new Date() );
		repository.save( migration1 );

		Migration migration2 = new Migration();
		migration2.setName( "20130629200000_migration2.js" );
		migration2.setDateApplied( new Date() );
		repository.save( migration2 );

		// Act.
		List<Migration> migrations = repository.findAll();

		// Assert.
		assertThat( "Expected Migrations not found.", migrations.size(), greaterThanOrEqualTo( 2 ) );
	}

	@Test
	public void shouldBeAbleToRemoveAMigration() {
		// Arrange.
		Migration migration1 = new Migration();
		migration1.setName( "20130629200000_migration1.js" );
		migration1.setDateApplied( new Date() );
		repository.save( migration1 );

		// Act.
		repository.remove( migration1 );

		// Assert.
		Migration retrievedMigration = repository.find( migration1.getName() );
		assertThat( "Migration was not deleted.", retrievedMigration, is( nullValue() ) );
	}
}
