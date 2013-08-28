package com.bancvue.mongomigrate;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

import java.net.UnknownHostException;


public class Main {

	public static void main( String[] args ) {
		Namespace namespace = parseArguments( args );

		//System.out.println( namespace );

		if ( namespace.getString( "command" ) == "create" ) {
			CreateCommand command = new CreateCommand( namespace.getString( "name" ), namespace.getString( "migration_folder" ) );
			System.exit( command.execute() );
		} else if ( namespace.getString( "command" ) == "migrate" ) {
			try {
				System.out.println( "Migrating database " + namespace.getString( "database" ) );

				MigrateCommand command = MigrateCommand.Create(
						namespace.getString( "mongo_host" ),
						namespace.getString( "database" ),
						namespace.getString( "migration_folder" ),
						namespace.getString( "mongo_exe_path" ) );

				System.exit( command.execute() );
			} catch ( UnknownHostException e ) {
				e.printStackTrace();
				System.exit( 1 );
			}
		}
	}

	private static Namespace parseArguments( String[] args ) {
		Namespace namespace = null;

		// Create and configure the parser for command line arguments.
		ArgumentParser parser = ArgumentParsers.newArgumentParser( "mongomigrate" )
				.usage( "mongomigrate COMMAND [options]\n" +
						"  use 'mongomigrate --help' to see a list of commands.\n" +
						"  use 'mongomigrate COMMAND --help' for help on individual commands." )
				.description( "MongoMigrate (c)2013 BancVue Ltd.\n" +
						"MongoMigrate is a database migrations tool for MongoDB." )
				.version( "${prog} v1.0" );

		// Create parser collection for sub-commands.
		Subparsers subparsers = parser.addSubparsers().metavar( "COMMAND" ).help( "Available Commands:" );

		// Configure the Create Command.
		Subparser createCommandParser = subparsers.addParser( "create" )
				.setDefault( "command", "create" )
				.description( "Create command:\n" +
						"---------------\n" +
						"The create command creates a new migration script with the specified name containing a default template." )
				.help( "creates a new migration script." );
		createCommandParser.addArgument( "name" ).setDefault( "migration" ).help( "optional name of the migration." );
		createCommandParser.addArgument( "-f", "--migration-folder" ).metavar( "PATH" ).setDefault( "." ).help( "folder containing migrations." );

		// Configure the Migrate Command.
		Subparser migrateCommandParser = subparsers.addParser( "migrate" )
				.setDefault( "command", "migrate" )
				.description( "Migrate command:\n" +
						"----------------\n" +
						"The migrate command migrates the specified database to the most recent version." )
				.help( "migrate a mongo database" );
		migrateCommandParser.addArgument( "database" ).required( true ).help( "the name of the database to migrate." );
		migrateCommandParser.addArgument( "-f", "--migration-folder" ).metavar( "PATH" ).setDefault( "." ).help( "folder containing migrations." );
		migrateCommandParser.addArgument( "-m", "--mongo-host" ).metavar( "MONGOHOST" ).setDefault( "localhost" ).help( "connection string to the mongo host." );
		//migrateCommandParser.addArgument( "-d", "--database" ).metavar( "DATABASE" ).setDefault( "localhost" ).help( "name of the mongo database to migrate." );
		migrateCommandParser.addArgument( "-p", "--mongo-exe-path" ).metavar( "MONGOPATH" ).setDefault( "mongo.exe" ).help( "path to mongo.exe." );

		try {
			namespace = parser.parseArgs( args );
		} catch ( ArgumentParserException e ) {
			parser.handleError( e );
			System.exit( 1 );
		}
		return namespace;
	}
}
