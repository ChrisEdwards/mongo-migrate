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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Mockito.when;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/2/13
 * Time: 4:05 PM
 */
@RunWith( JUnit4.class )
public class MigrationsToRunFinderTests {

	@Mock private IMigrationFileRepository migrationFileRepository;
	@Mock private IMigrationHistoryRepository migrationHistoryRepository;

	private Migration migration1;
	private File migrationFile1;
	private Migration migration2;
	private File migrationFile2;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks( this );

		migration1 = new Migration();
		migration1.setName( "20130629200000_migration1.js" );
		migrationFile1 = new File( "20130629200000_migration1.js" );

		migration2 = new Migration();
		migration2.setName( "20130629200000_migration2.js" );
		migrationFile2 = new File( "20130629200000_migration2.js" );
	}

	@Test
	public void findingMigrationsToRunWhenAllMigrationsExistInHistoryShouldReturnNoMigrations() {
		List<File> allMigrations = Arrays.asList(
				migrationFile1,
				migrationFile2
		);
		when( migrationFileRepository.findAll() ).thenReturn( allMigrations );

		List<Migration> historicalMigrations = Arrays.asList(
				migration1,
				migration2
		);
		when( migrationHistoryRepository.findAll() ).thenReturn( historicalMigrations );

		IMigrationsToRunFinder IMigrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );

		List<String> migrationsToRun = IMigrationsToRunFinder.findMigrationsToRun();

		assertThat( "Should not have returned any migrations to run.", migrationsToRun, is( empty() ) );
	}

	@Test
	public void findingMigrationsToRunWhenOneMigrationDoesNotExistInHistoryShouldReturnThatOneMigration() {
		File expectedMigration = new File( "IHaventBeenRunYet_migration.js" );

		List<File> allMigrations = Arrays.asList(
				migrationFile1,
				migrationFile2,
				expectedMigration
		);
		when( migrationFileRepository.findAll() ).thenReturn( allMigrations );

		List<Migration> historicalMigrations = Arrays.asList(
				migration1,
				migration2
		);
		when( migrationHistoryRepository.findAll() ).thenReturn( historicalMigrations );

		IMigrationsToRunFinder IMigrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );

		List<String> migrationsToRun = IMigrationsToRunFinder.findMigrationsToRun();

		assertThat( "Should have only contained one item.", migrationsToRun, hasSize( 1 ) );
		assertThat( "Should have contained the expected migration that has not yet been run.", migrationsToRun, hasItem( expectedMigration.getName() ) );
	}

	@Test
	public void findingMigrationsToRunNoMigrationsExistInHistoryShouldReturnAllMigrations() {
		List<File> allMigrations = Arrays.asList(
				migrationFile1,
				migrationFile2
		);
		when( migrationFileRepository.findAll() ).thenReturn( allMigrations );

		when( migrationHistoryRepository.findAll() ).thenReturn( new ArrayList<Migration>() );

		IMigrationsToRunFinder IMigrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );

		List<String> migrationsToRun = IMigrationsToRunFinder.findMigrationsToRun();

		assertThat( "Should have contained 2 migrations.", migrationsToRun, hasSize( 2 ) );
		assertThat( "Didn't contain the expected migration1.", migrationsToRun, hasItem( migrationFile1.getName() ) );
		assertThat( "Didn't contain the expected migration2.", migrationsToRun, hasItem( migrationFile2.getName() ) );
	}

	@Test
	public void findingMigrationsToRuWhenNoFileMigrationsExistShouldReturnNoMigrations() {
		when( migrationFileRepository.findAll() ).thenReturn( new ArrayList<File>() );

		List<Migration> historicalMigrations = Arrays.asList(
				migration1,
				migration2
		);
		when( migrationHistoryRepository.findAll() ).thenReturn( historicalMigrations );

		IMigrationsToRunFinder IMigrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );

		List<String> migrationsToRun = IMigrationsToRunFinder.findMigrationsToRun();

		assertThat( "Should not have returned any migrations to run.", migrationsToRun, is( empty() ) );
	}

	@Test
	public void findingMigrationsShouldReturnTheMigrationsInOrderByMigrationFileName() {
		List<File> migrationFileInReverseOrder = Arrays.asList(
				migrationFile2,
				migrationFile1
		);
		when( migrationFileRepository.findAll() ).thenReturn( migrationFileInReverseOrder );

		when( migrationHistoryRepository.findAll() ).thenReturn( new ArrayList<Migration>() );

		IMigrationsToRunFinder IMigrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );

		List<String> migrationsToRun = IMigrationsToRunFinder.findMigrationsToRun();

		assertThat( "Should have contained 2 migrations.", migrationsToRun, hasSize( 2 ) );
		assertThat( "Expected migration1 to be first.", migrationsToRun.get(0), is( migrationFile1.getName() ) );
		assertThat( "Expected migration2 to be second.", migrationsToRun.get(1), is( migrationFile2.getName() ) );
	}
}
