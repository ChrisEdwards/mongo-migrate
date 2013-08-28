package com.bancvue.mongomigrate;

import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: Chris.Edwards
 * Date: 7/27/13
 * Time: 2:29 PM
 */
public interface IClock {
    DateTime getCurrentDateTime();
}
