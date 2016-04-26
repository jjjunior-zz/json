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
        String json = "{\"a\":\"texte ici \\u0041\"}";
        JSONObject object = JSONObject.fromString(json);
        assertEquals("texte ici A", object.get("a"));
    }

    public void testHexaConvert2() throws JSONException {
        String json = "{\"name\":\"foo\\u00f4bar\"}";
        JSONObject object = JSONObject.fromString(json);
        assertEquals("fooôbar", object.get("name"));
    }
}
