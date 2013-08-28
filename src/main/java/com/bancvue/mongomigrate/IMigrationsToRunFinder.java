package com.bancvue.mongomigrate;

import java.io.File;
import java.util.Collection;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/4/13
 * Time: 8:46 PM
 */
public interface IMigrationsToRunFinder {
	List<String> findMigrationsToRun();
}
