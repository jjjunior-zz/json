package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;

/**
 * User: sgaide & dboulay
 * Date: 21/01/13
 * Time: 14:44
 */
public class JSONObjectTest extends TestCase {

    public void testJSONPut() {
        JSONObject json = new JSONObject();
        json.put( "str", "Toto");
        json.put( "object", new Long(1));

        assertEquals( "Toto", json.getString("str"));
        assertEquals( new Long(1), json.getLong("object"));
        assertEquals( new Integer(1), json.getInteger("object"));
        assertEquals( 1L, json.getNumber("object"));
    }

    public void testJSONAccumulate() {
        JSONObject json = new JSONObject();
        json.accumulate( "acc", "toto" );
        assertEquals("toto", json.getString("acc"));
        json.accumulate("acc", "titi");
        assertTrue(((JSON)json.get("acc")).isJSONArray());
    }

    public void testJSONAccumulateAll() {
        JSONObject json = new JSONObject();
        json.put( "a", 1 );
        json.put( "b", 2 );
        JSONArray cArray = new JSONArray();
        cArray.add(1);
        cArray.add(2);
        json.put( "c", cArray);

        JSONObject other = new JSONObject();
        other.put( "a", 2 );
        other.put( "b", 3 );
        other.put( "c", 3 );
        other.put( "d", 1 );

        json.accumulateAll(other);
        assertEquals( 4, json.size());
        assertEquals(2, json.getJSONArray("a").size());
        assertEquals(2, json.getJSONArray("b").size());
        assertEquals(3, json.getJSONArray("c").size());
        assertTrue(json.getInteger("d")!=null);
    }

    public void testConverter() {
        final int[] intArray = {1,2,3,4};

        JSONObject json = new JSONObject();

        json.put( "a", 2 );
        json.put( "b", 4L );
        json.put( "c", "titi" );
        json.put( "d", new Object[] { "one", "two", "three" } );
        json.put( "e",  intArray );

        assertEquals( 2, json.get("a"));
        assertEquals( 4L, json.get("b"));
        assertEquals( "titi", json.get("c"));
        assertEquals( 3, json.getJSONArray("d").size());
        assertEquals( 4, json.getJSONArray("e").size());
        assertEquals( 2, json.getJSONArray("e").get(1));


    }
}
