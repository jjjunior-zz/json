package org.runmyprocess.json.parser;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONException;
import org.runmyprocess.json.JSONObject;

/**
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 14:58
 */
public interface Parser {
    public JSON parse() throws JSONException;
}
