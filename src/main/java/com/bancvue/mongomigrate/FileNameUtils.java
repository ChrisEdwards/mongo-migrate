package com.bancvue.mongomigrate;

import java.io.File;


/**
 * User: Chris.Edwards
 * Date: 8/16/13
 * Time: 10:12 AM
 */
public class FileNameUtils {
	public static String StripPath( String migrationFileName ) {
		File migrationFile = new File( migrationFileName );
		return migrationFile.getName();
	}
}
