package org.runmyprocess.test;

import junit.framework.TestCase;
import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONException;
import org.runmyprocess.json.parser.StrictParser;

import java.io.IOException;
import java.io.StringWriter;

/**
 * User: sgaide & dboulay
 * Date: 09/01/13
 * Time: 17:42
 */
public class RunMyProcessTimingTest extends TestCase {
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

    public final static int LOOPS = 50000;

    public void testTiming() throws JSONException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; ++i) {
            JSON json = JSON.fromString(STRING_OBJECT);
        }
        long rmpTime = System.currentTimeMillis() - start;
        System.out.println(String.format("parse rmp:%d", rmpTime));
    }

    public void testStrictTiming() throws JSONException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; ++i) {
            JSON json = JSON.parse(new StrictParser(STRING_OBJECT));
        }
        long rmpTime = System.currentTimeMillis() - start;
        System.out.println(String.format("strict parse rmp:%d", rmpTime));
    }

    public void testTimingSerialization() throws JSONException {
        JSON json = JSON.fromString(STRING_OBJECT);
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; ++i) {
            json.toString();
        }
        long rmpTime = System.currentTimeMillis() - start;
        System.out.println(String.format("stringify rmp:%d", rmpTime));
    }

    public void testTimingWriter() throws JSONException, IOException {
        JSON json = JSON.fromString(STRING_OBJECT);
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; ++i) {
            StringWriter writer = new StringWriter();
            json.write(writer);
        }
        long rmpTime = System.currentTimeMillis() - start;
        System.out.println(String.format("writer rmp:%d", rmpTime));
    }
}
