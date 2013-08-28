package com.bancvue.mongomigrate;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 8:53 PM
 */
public class MongoShellExecutor implements IMongoShellExecutor {
	private final String pathToMongoExe;
	private final String mongoConnectionString;
	private final String databaseName;

	public MongoShellExecutor( String pathToMongoExe, String mongoConnectionString, String databaseName ) {
		this.pathToMongoExe = pathToMongoExe;
		this.mongoConnectionString = mongoConnectionString;
		this.databaseName = databaseName;
	}

	@Override
	public int executeJsFile( String pathToJsFile ) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(pathToMongoExe, mongoConnectionString + "/" + databaseName, pathToJsFile);

		final Process process = builder.start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		InputStream errorStream = process.getErrorStream();
		InputStreamReader errorStreamReader = new InputStreamReader( errorStream );
		BufferedReader errorStreamBufferedReader = new BufferedReader( errorStreamReader );

		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		while ((line = errorStreamBufferedReader.readLine()) != null) {
			System.out.println("ERROR: " + line);
		}

		int exitCode = process.waitFor();
		return exitCode;
	}
}
