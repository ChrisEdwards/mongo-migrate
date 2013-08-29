MongoMigrate
============

MongoMigrate is a command line tool to automatically apply javascript migration scripts to a MongoDB database. This allows you to keep multiple mongo databases up to date with changes you have made to document structures or data. This is similar to Ruby Migrations. This tool uses native mongo javascript to migrate the database.

Creating a Migration File
-------------------------

    java -jar mongomigrate.jar create MyFirstMigration

This will create a .js file with the migration name prefixed with a timestamp. `20130829091400_MyFirstMigration.js` for example.

Add your js code to modify your mongo database into this file.

You don't have to use the create command to create your scripts. MongoMigrate will apply any scripts it finds, this command is included for convenience.

Running Migrations
------------------

    java -jar mongomigrate.jar migrate TestDatabase

This will apply all the migration files in the current folder to the TestDatabase on your local mongo instance. There are additional arguments you can use to specify the folder containing the migrations and the target mongo server.

How does it work?
-----------------

MongoMigrate simply applies all migration scripts found in the specified folder to the mongo database. If a script has already been applied to the database, it is not applied again. The scripts are applied in sort order of their filenames. Filenames of the scripts should be prefixed with a timestamp so they sort chronologically. MongoMigrate creates a collection called `MigrationHistory` in the database to track which migration scripts have already been applied.