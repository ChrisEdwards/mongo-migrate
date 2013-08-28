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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 8/2/13
 * Time: 4:10 PM
 */
public class MigrationsToRunFinder implements IMigrationsToRunFinder {
	private final IMigrationFileRepository migrationFileRepository;
	private final IMigrationHistoryRepository migrationHistoryRepository;

	public MigrationsToRunFinder( IMigrationFileRepository migrationFileRepository, IMigrationHistoryRepository migrationHistoryRepository ) {
		this.migrationFileRepository = migrationFileRepository;
		this.migrationHistoryRepository = migrationHistoryRepository;
	}

	@Override
	public List<String> findMigrationsToRun() {
		List<String> migrationFileNames = selectFileNames( migrationFileRepository.findAll() );
		System.out.println( "  Found " + migrationFileNames.size() + " migration file(s) in migrations folder." );

		List<String> historicalMigrationFileNames = selectFileNames( migrationHistoryRepository.findAll() );
		System.out.println( "  Found " + historicalMigrationFileNames.size() + " migration(s) already applied in " + migrationHistoryRepository.getDatabaseName() + ".MigrationHistory" );

		List<String> unappliedMigrationFileNames = GetUnappliedMigrations( migrationFileNames, historicalMigrationFileNames );

		Collections.sort( unappliedMigrationFileNames );
		return unappliedMigrationFileNames;
	}

	private List<String> GetUnappliedMigrations( List<String> migrationFileNames, List<String> appliedMigrationFileNames ) {
		List<String> unappliedMigrations = new ArrayList<String>();

		for ( String migrationFileName : migrationFileNames ) {
			if ( !appliedMigrationFileNames.contains( FileNameUtils.StripPath( migrationFileName ) ) )
				unappliedMigrations.add( migrationFileName );
		}
		return unappliedMigrations;
	}

	// TODO: There MUST be an easier way to do this.
	// Lambdas already? Please?
	private List<String> selectFileNames( Collection<Migration> migrations ) {
		List<String> results = new ArrayList<String>();
		for ( Migration migration : migrations ) {
			results.add( migration.getName() );
		}
		return results;
	}

	private List<String> selectFileNames( List<File> files ) {
		List<String> results = new ArrayList<String>();
		for ( File file : files ) {
			results.add( file.toString() );
		}
		return results;
	}
}
