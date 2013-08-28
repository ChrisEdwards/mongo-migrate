package com.bancvue.mongomigrate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 10:07 PM
 */
@RunWith( JUnit4.class )
public class MigrateCommandTests {

	@Mock IMigrationsToRunFinder finder;
	@Mock IMongoShellExecutor executor;
	@Mock IMigrationHistoryRepository migrationHistoryRepository;
	private MigrateCommand migrateCommand;

	@Before
	public void Setup() {
		MockitoAnnotations.initMocks( this );

		List<String> migrationsToRun = Arrays.asList(
				"migration1.js",
				"migration2.js"
		);
		when( finder.findMigrationsToRun() ).thenReturn( migrationsToRun );

		migrateCommand = new MigrateCommand( finder, executor, migrationHistoryRepository );
	}

	@Test
	public void shouldExecuteEachMigrationFileAgainstMongoInTheRightOrder() throws IOException, InterruptedException {
		migrateCommand.execute();

		InOrder inOrder = inOrder( executor );
		inOrder.verify( executor ).executeJsFile( "migration1.js" );
		inOrder.verify( executor ).executeJsFile( "migration2.js" );
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void applyMigrationShouldUpdateMigrationHistoryWhenMigrationSucceeds() throws IOException, InterruptedException {
		// Setup both to succeed.
		when( executor.executeJsFile( "migration1.js" ) ).thenReturn( 0 );
		when( executor.executeJsFile( "migration2.js" ) ).thenReturn( 0 );

		migrateCommand.execute();

		// Verify 2 calls to save migration occurred
		ArgumentCaptor<Migration> argument = ArgumentCaptor.forClass( Migration.class );
		verify( migrationHistoryRepository, times( 2 ) ).save( argument.capture() );

		// And the calls saved the two migrations we expected.
		List<Migration> savedMigrations = argument.getAllValues();
		assertThat( savedMigrations.size(), is(2));
		assertThat( savedMigrations.get(0).getName(), is( "migration1.js" ) );
		assertThat( savedMigrations.get(1).getName(), is( "migration2.js" ) );
	}

	@Test
	public void applyMigrationShouldNotUpdateMigrationHistoryWhenMigrationFails() throws IOException, InterruptedException {
		// Setup only the first to succeed.
		when( executor.executeJsFile( "migration1.js" ) ).thenReturn( 0 );
		when( executor.executeJsFile( "migration2.js" ) ).thenReturn( 1 ); //failure

		migrateCommand.execute();

		// Verify only one call to save migration occurred
		ArgumentCaptor<Migration> argument = ArgumentCaptor.forClass( Migration.class );
		verify( migrationHistoryRepository, times( 1 ) ).save( argument.capture() );

		// And it was only for the successful migration
		List<Migration> savedMigrations = argument.getAllValues();
		assertThat( savedMigrations.size(), is(1));
		assertThat( savedMigrations.get(0).getName(), is( "migration1.js" ) );
	}

	@Test
	public void applyMigrationShouldStopOnTheFirstFailure() throws IOException, InterruptedException {
		// Setup the first migration to fail.
		when( executor.executeJsFile( "migration1.js" ) ).thenReturn( 1 ); // failure
		when( executor.executeJsFile( "migration2.js" ) ).thenReturn( 0 ); // success

		migrateCommand.execute();

		// Should only execute the first migration.
		verify( executor ).executeJsFile( "migration1.js" );
		verifyNoMoreInteractions( executor );

		// Should not have saved any migrations to history since none succeeded.
		verifyZeroInteractions( migrationHistoryRepository );
	}
}
