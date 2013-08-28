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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 12:20 PM
 */
public class MigrationFileRepository implements IMigrationFileRepository {


	private final File dir;

	public MigrationFileRepository( String pathToMigrationFiles ) {
		this.dir = new File( pathToMigrationFiles );
	}

	@Override
	public List<File> findAll() {
		return new ArrayList<File>(FileUtils.listFiles( dir, new String[]{ "js" }, true ));
	}
}
