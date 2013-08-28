package com.bancvue.mongomigrate;

import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 10:09 PM.
 */
public interface IMongoShellExecutor {
	int executeJsFile( String pathToJsFile ) throws IOException, InterruptedException;
}
