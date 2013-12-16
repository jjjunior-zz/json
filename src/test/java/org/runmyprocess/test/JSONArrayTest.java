package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;

/**
 * User: sgaide & dboulay
 * Date: 25/02/13
 * Time: 17:23
 */
public class JSONArrayTest extends TestCase {

    public void testJSONArrayCreation() {
        final String[] array = { "a","b","c","d" };

        JSONArray json = JSONArray.fromObject(array);
        assertEquals( 4, json.size());
    }

    public void testJSONArrayPut() {
        final String[] array = { "a","b","c","d" };

        JSONObject json = new JSONObject();
        json.put( "array", array );
        assertEquals( 1, json.size());
        assertEquals( 4, json.getJSONArray("array").size());
    }
}
