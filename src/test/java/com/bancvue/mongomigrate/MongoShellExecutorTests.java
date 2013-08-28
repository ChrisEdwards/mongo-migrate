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

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 8:49 PM
 */
@RunWith( JUnit4.class )
public class MongoShellExecutorTests {

	private DBCollection testCollection;
	private String jsFileName = "test.js";
	private String testDbName = "MongoMigrateTests";

	@Before
	public void Setup() throws IOException {
		MongoClient mongoClient = new MongoClient( "localhost" );
		DB db = mongoClient.getDB( testDbName );
		testCollection = db.getCollection( "MongoShellTest" );
		testCollection.drop();
	}

	@Test
	public void executingAJsFileShouldSucceed() throws IOException, InterruptedException {
		CreateJsFile( "db.MongoShellTest.insert( { testResult: 'Pass' } );\n", jsFileName );

		IMongoShellExecutor executor = new MongoShellExecutor( "mongo.exe", "localhost", testDbName );
		int exitCode = executor.executeJsFile( jsFileName );

		assertThat( "Should have returned successful exit code (zero).", exitCode, is( 0 ) );
		assertThat( "Should have executed the js script and inserted a document.", testCollection.find().count(), is( 1 ) );
	}

	@Test
	public void executingAJsFileWithErrorsShouldReturnFailingExitCode() throws IOException, InterruptedException {
		CreateJsFile( "invalid javascript\n", jsFileName );

		IMongoShellExecutor executor = new MongoShellExecutor( "mongo.exe", "localhost", testDbName );
		int exitCode = executor.executeJsFile( jsFileName );

		assertThat( "Exit Code should have returned failure (non-zero).", exitCode, is( not( 0 ) ) );
	}

	@Test
	public void executingAJsFileThatDoesNotExistShouldReturnFailingExitCode() throws IOException, InterruptedException {
		IMongoShellExecutor executor = new MongoShellExecutor( "mongo.exe", "localhost", testDbName );
		int exitCode = executor.executeJsFile( "not-a-real-file.js" );

		assertThat( "Exit Code should have returned failure (non-zero).", exitCode, is( not( 0 ) ) );
	}

	private static void CreateJsFile( String jsCode, String fileName ) throws IOException {
		Path jsFile = Paths.get( fileName );
		if ( Files.exists( jsFile ) ) Files.delete( jsFile );

		BufferedWriter writer = Files.newBufferedWriter( jsFile, Charset.forName( "UTF-8" ) );
		writer.write( jsCode );
		writer.flush();
		writer.close();
	}
}