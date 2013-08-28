package com.bancvue.mongomigrate;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 10:11 PM
 */
public class MigrateCommand {
	private final IMigrationsToRunFinder migrationsToRunFinder;
	private final IMongoShellExecutor mongoShellExecutor;
	private final IMigrationHistoryRepository migrationHistoryRepository;
	private final int SUCCESS = 0;
	private final int FAILURE = 1;

	public static MigrateCommand Create( String mongoConnectionString, String databaseName, String migrationScriptsPath, String pathToMongoExe ) throws UnknownHostException {
		MigrationFileRepository migrationFileRepository = new MigrationFileRepository( migrationScriptsPath );
		MigrationHistoryRepository migrationHistoryRepository = new MigrationHistoryRepository( mongoConnectionString, databaseName );
		MigrationsToRunFinder migrationsToRunFinder = new MigrationsToRunFinder( migrationFileRepository, migrationHistoryRepository );
		MongoShellExecutor mongoShellExecutor = new MongoShellExecutor( pathToMongoExe, mongoConnectionString, databaseName );
		return new MigrateCommand( migrationsToRunFinder, mongoShellExecutor, migrationHistoryRepository );
	}

	public MigrateCommand( IMigrationsToRunFinder migrationsToRunFinder, IMongoShellExecutor mongoShellExecutor, IMigrationHistoryRepository migrationHistoryRepository ) {
		this.migrationsToRunFinder = migrationsToRunFinder;
		this.mongoShellExecutor = mongoShellExecutor;
		this.migrationHistoryRepository = migrationHistoryRepository;
	}

	public int execute() {
		int exitCode = SUCCESS;
		try {
			List<String> migrationsToRun = migrationsToRunFinder.findMigrationsToRun();

			int totalMigrationCount = migrationsToRun.size();
			if (totalMigrationCount == 0){
				System.out.println( "\nDatabase is up to date. No migrations to run." );
				return SUCCESS;
			}

			System.out.println( "\nRunning " + totalMigrationCount + " migration(s)..." );
			int currentMigrationCount = 0;
			for ( final String migrationFile : migrationsToRun ) {
				System.out.println( String.format( "  (%d of %d) %s", ++currentMigrationCount, totalMigrationCount, migrationFile ) );
				exitCode = mongoShellExecutor.executeJsFile( migrationFile );

				// If a migration failed, exit. Perhaps we need to show an error (or throw one?)
				if ( exitCode != SUCCESS )
					return exitCode;

				migrationHistoryRepository.save(
						new Migration() {{
							setName( FileNameUtils.StripPath( migrationFile ) );
							setDateApplied( new Date() );
						}}
				);
			}
		} catch ( Exception e ) {
			exitCode = FAILURE;
			e.printStackTrace();
		}
		return exitCode;
	}
}
