package com.bancvue.mongomigrate;

import org.joda.time.DateTime;


/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/27/13
 * Time: 2:34 PM
 */
public class FakeClock implements IClock {
	private final DateTime dateTime;

	public FakeClock( DateTime dateTime ) {
		this.dateTime = dateTime;
	}

	public FakeClock() {
		this.dateTime = new DateTime();
	}

	@Override

	public DateTime getCurrentDateTime() {
		return dateTime;
	}
}
