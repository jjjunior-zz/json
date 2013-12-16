package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSONException;
import org.runmyprocess.json.JSONObject;

/**
 * User: sgaide & dboulay
 * Date: 15/01/13
 * Time: 18:11
 */
public class CharToHexaTest extends TestCase {

    public void testHexaConvert() throws JSONException {
        String json = "{\"a\":\"toto bidule \u0041\"}";
        JSONObject object = JSONObject.fromString(json);
        assertEquals("toto bidule A", object.get("a"));
    }
}
