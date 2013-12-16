package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;
import org.runmyprocess.json.parser.StreamParser;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * User: sgaide & dboulay
 * Date: 26/03/13
 * Time: 17:38
 */
public class StreamParserTest extends TestCase {

    public final static String STRING_OBJECT = "{\n" +
            "        \"id\": 0,\n" +
            "        \"guid\": \"d07b403f-52bb-4ee5-95b2-6033806adb3a\",\n" +
            "        \"isActive\": true,\n" +
            "        \"balance\": \"$2,654.00\",\n" +
            "        \"picture\": \"http://placehold.it/32x32\",\n" +
            "        \"age\": 27,\n" +
            "        \"name\": \"Donna Sims\",\n" +
            "        \"gender\": \"female\",\n" +
            "        \"company\": \"Norali\",\n" +
            "        \"email\": \"donnasims@norali.com\",\n" +
            "        \"phone\": \"+1 (930) 499-3494\",\n" +
            "        \"address\": \"562 Fillmore Avenue, Westerville, Utah, 4856\",\n" +
            "        \"about\": \"Laborum nulla sunt ad id sit sunt voluptate ad consectetur id fugiat consequat. Magna fugiat enim anim ad incididunt do excepteur adipisicing. Irure consequat ex sit ex et ad reprehenderit voluptate qui Lorem tempor commodo. Quis sit exercitation tempor duis et esse laborum.\\r\\n\",\n" +
            "        \"registered\": \"2009-11-22T15:47:43 -01:00\",\n" +
            "        \"latitude\": -54.31424,\n" +
            "        \"longitude\": -33.585576,\n" +
            "        \"tags\": [\n" +
            "            \"velit\",\n" +
            "            \"aliquip\",\n" +
            "            \"nulla\",\n" +
            "            \"dolore\",\n" +
            "            \"non\",\n" +
            "            \"laborum\",\n" +
            "            \"incididunt\"\n" +
            "        ],\n" +
            "        \"friends\": [\n" +
            "            {\n" +
            "                \"id\": 0,\n" +
            "                \"name\": \"Harriett Valenzuela\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 1,\n" +
            "                \"name\": \"Josie Ratliff\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 2,\n" +
            "                \"name\": \"Dickerson Hodge\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"randomArrayItem\": \"cherry\"\n" +
            "    }";

    public void testStreamParser() throws UnsupportedEncodingException {
        ByteArrayInputStream bai = new ByteArrayInputStream(STRING_OBJECT.getBytes());
        JSONObject object = JSONObject.parse(new StreamParser(bai, "UTF-8"));
        assertEquals(19, object.size());
    }

    public void testStreamParserArray() throws UnsupportedEncodingException {
        String o = "[1,2,3,-1,-2.2,-3.4e5,4.52e-4,6.7E8]";
        ByteArrayInputStream bai = new ByteArrayInputStream(o.getBytes());
        JSONArray object = JSONArray.parse(new StreamParser(bai, "UTF-8"));
        assertEquals( 8, object.size());
    }
}
