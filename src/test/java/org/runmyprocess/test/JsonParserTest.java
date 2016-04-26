package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.*;
import org.runmyprocess.json.parser.DefaultParser;
import org.runmyprocess.json.parser.Parser;
import org.runmyprocess.json.parser.StrictParser;

/**
 * User: sgaide & dboulay
 * Date: 09/01/13
 * Time: 15:48
 */
public class JsonParserTest extends TestCase {

    public void testParserEmptyObject() throws JSONException {
        String o = "{}";
        JSON json = JSON.fromString(o);
    }

    public void testParserEmptyArray() throws JSONException {
        String o = "[]";
        JSON json = JSON.fromString(o);
    }

    public void testParserSimpleArray() throws JSONException {
        String o = "[1,2]";
        JSON json = JSON.fromString(o);
    }

    public void testParserSimpleArrayWithSeparator() throws JSONException {
        String o = "[1,2,]";
        JSON json = JSON.fromString(o);
    }

    public void testParserSimpleObjectWithSeparator() throws JSONException {
        String o = "{\"foo\":\"bar\",}";
        JSON json = JSON.fromString(o);
    }
    public void testParserSimpleSetWithSeparator() throws JSONException {
        String o = "<1,2,>";
        JSON json = JSON.fromString(o);
    }
    public void testParserSimpleStructureWithSeparator() throws JSONException {
        String o = "{\"foo\":<1,2,>,\"bar\":[1,2,],}";
        JSON json = JSON.fromString(o);
    }

    public void testParserSimpleObject() throws JSONException {
        String o = "{\"name\": \"Denis\"}";
        JSON json = JSON.fromString(o);
        assertEquals(((JSONObject)json).get("name").toString(),"Denis");
    }

    public void testParserSimpleObjectTrim() throws JSONException {
        String o = "     {\"name\": \"Denis\"}";
        JSON json = JSON.fromString(o);
        assertEquals(((JSONObject)json).get("name").toString(),"Denis");
    }

    public void testParserSimpleObject2() throws JSONException {
        String o = "{\"name\": \"Denis\", \"age\":3.9e1}";
        JSONObject json = JSONObject.fromString(o);

        assertEquals("Denis", json.get("name").toString());
        assertEquals(39.0, json.get("age"));
    }

    public void testParserArrayArithmetic() throws JSONException {
        String o = "[1,2,3,-1,-2.2,-3.4e5,4.52e-4,6.7E8,1.360,2.999999999]";
        JSONArray json = JSONArray.fromString(o);

        assertEquals(10, json.size());
        assertEquals(1L, json.get(0));
        assertEquals(-1L, json.get(3));
        assertEquals(-2.2, json.get(4));
        assertEquals(-340000.0, json.get(5));
        assertEquals(4.52e-4, json.get(6));
        assertEquals(1.36, json.get(8));
        assertEquals(2.999999999, json.get(9));
    }

    public void testParserComplexObject() throws JSONException {
        String o = "{\"name\": \"Jean\", \"age\":3.9e1, \"work_address\":{\"street\":\"rue de la paix\", \"number\":22, \"city\":\"Paris\"},\"tags\":[false,true,null,1,\"titi\"]}";
        JSONObject json = JSONObject.fromString(o);

        assertEquals("Jean", json.get("name").toString());
        assertEquals(39.0, json.get("age"));
        assertTrue(((JSON)json.get("tags")).isJSONArray());
        assertEquals(Boolean.FALSE, ((JSONArray)json.get("tags")).get(0));
        assertEquals(Boolean.TRUE, ((JSONArray)json.get("tags")).get(1));
    }

    public void testUnquottedKey() {
        String q = "{\"a\":1,\"b\":2}";
        String o = "{ a : 1,b :2, 'c':3, d+c:45, d-t:67, d a b c :89, h'b:23, i\"b:12, i\\\\\":\"truc\"}";

        JSONObject strictObject = JSONObject.fromObject( q );
        JSONObject looseObject = JSONObject.fromObject( o );

        assertEquals( strictObject.getInteger("a"), looseObject.getInteger("a"));
        assertEquals( strictObject.getInteger("b"), looseObject.getInteger("b"));
    }

    public void testJSONSet() {
        String o = "<1,3,4,8,9,10,12>";
        String q = "<\"titi\",\"toto\",\"tata\",4,10>";

        JSONSet set1 = JSONSet.fromString(o);
        assertEquals( 7, set1.size());
        assertTrue( set1.contains(4L));

        JSONSet set2 = JSONSet.fromString(q);
        assertEquals( 5, set2.size());
        assertTrue( set2.contains("titi"));
        assertTrue( set2.contains(10L));

        JSONObject json = new JSONObject();
        json.put( "set", set1 );
        assertTrue( json.getJSONSet("set") != null);
    }

    public void testQuotedValue() {
        String q = "{\"a\":1,b:'2',\"c\":'toto\"titi'}";
        JSONObject json = JSONObject.fromString(q);

        assertEquals( 3, json.size());
        assertEquals( 1, (int)json.getInteger("a"));
        assertEquals( "2", json.getString("b"));
        assertEquals( "toto\"titi", json.getString("c"));
    }

    public void testUrl() {
        String q = "{\"href\":\"http:\\/\\/www.ibm.com\"}";
        JSONObject json = JSONObject.fromString(q);
        assertEquals( "http://www.ibm.com", json.getString("href"));
    }

    public void testEqualSeparator() {
        String q = "{\"a\"=1,'b'='2',\"c\":\"toto\"}";
        JSONObject json = JSONObject.fromString(q);

        assertEquals( 3, json.size());
        assertEquals( 1, (int)json.getInteger("a"));
        assertEquals( "2", json.getString("b"));
        assertEquals( "toto", json.getString("c"));
    }

    public void testAmpersand() {
        String q = "{'make_model':'AT&T blue'}";
        JSONObject json = JSONObject.fromString(q);
        assertEquals(1, json.size());
        assertEquals("AT&T blue", json.getString("make_model"));
    }
}
