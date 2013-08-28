package com.bancvue.mongomigrate;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/27/13
 * Time: 11:44 AM
 */
public class CreateCommand {
	private final String migrationName;
	private final String migrationsFolder;
	private IClock clock = new Clock();

	public CreateCommand() {
		this( null, null );
	}

	public CreateCommand( String migrationName ) {
		this(migrationName, null);
	}

	public CreateCommand( String migrationName, String migrationsFolder ) {
		this.migrationName = migrationName;
		this.migrationsFolder = migrationsFolder == null ? "." : migrationsFolder;
	}

	public int execute() {
		int exitCode = 0;
		Path file = null;
		try {
			// Ensure migrations folder exists.
			Path folder = Paths.get( migrationsFolder );
			if (!Files.exists( folder )){
				Files.createDirectories( folder );
			}

			// Generate filename and ensure it doesn't already exist.
			file = Paths.get( migrationsFolder, generateMigrationFileName( migrationName ) );
			if ( Files.exists( file ) )
				throw new FileAlreadyExistsException( file.toAbsolutePath().toString() );

			// Write the file.
			BufferedWriter writer = Files.newBufferedWriter( file, Charset.forName( "UTF-8" ) );
			writer.write( "// Add migration javascript here.\n" );
			writer.write( "db.<collection_name>.update(\n" );
			writer.write( "    { <query> },\n" );
			writer.write( "    { <update> },\n" );
			writer.write( "    { multi: true }\n" );
			writer.write( ");\n" );
			writer.flush();
			writer.close();

			System.out.println( "Created migration file: " + file.toString() );

		} catch ( FileAlreadyExistsException e ) {
			System.err.println( "The specified migration file " + file.toString() + " already exists." );
			exitCode = 1;
		} catch ( IOException e ) {
			e.printStackTrace();
			exitCode = 1;
		}
		return exitCode;
	}

	private String generateMigrationFileName( String userProvidedName ) {
		String dateTimePrefix = DateTimeFormat.forPattern( "yyyyMMddHHmmss" ).print( clock.getCurrentDateTime() );
		String namePostFix = StringUtils.isNotEmpty( userProvidedName )
				? userProvidedName
				: "migration";
		return dateTimePrefix + "_" + namePostFix + ".js";
	}

	public void setClock( IClock clock ) {
		this.clock = clock;
	}
}
