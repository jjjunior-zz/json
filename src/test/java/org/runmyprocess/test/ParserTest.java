package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.parser.RecursiveParser;

/**
 * User: sgaide & dboulay
 * Date: 14/02/13
 * Time: 16:25
 */
public class ParserTest extends TestCase {
    public void testArray() {
        final String array = "[10352,10353]";
        JSONArray jsonArray= JSONArray.fromString(array);
        assertEquals( 2, jsonArray.size());
    }

    public void testRecursive() {
        final String string = "[10352,'[1,2]']";
        JSON json = JSON.parse(new RecursiveParser(string));
        assertEquals( 2, ((JSONArray)json).size());
        assertEquals( (Integer)2, ((JSONArray)json).getJSONArray(1).getInteger(1));
    }
}
