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

import org.mongojack.Id;
import org.mongojack.MongoCollection;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/28/13
 * Time: 5:12 AM
 */
@MongoCollection( name = "MigrationHistory" )
public class Migration {
	private String name;
	private Date dateApplied;

	@Id
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Date getDateApplied() {
		return dateApplied;
	}

	public void setDateApplied( Date dateApplied ) {
		this.dateApplied = dateApplied;
	}
}
