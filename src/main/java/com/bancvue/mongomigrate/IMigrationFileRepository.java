package com.bancvue.mongomigrate;

import java.io.File;
import java.util.Collection;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/2/13
 * Time: 4:11 PM
 */
public interface IMigrationFileRepository {
	List<File> findAll();
}

