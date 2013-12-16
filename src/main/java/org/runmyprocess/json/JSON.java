package org.runmyprocess.json;

import org.runmyprocess.json.parser.DefaultParser;
import org.runmyprocess.json.parser.Parser;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Generic object to represent a JSON structure, which can be:
 * <ul>
 *     <li>JSONObject</li>
 *     <li>JSONArray</li>
 *     <li>JSONSet: This is not a Object described in the JSON's specification, this Object provides exactly the same
 *     features than the JSONArray but based on a Set (instead of List for JSONArray), it includes "no duplicate values",
 *     "unordered values"...</li>
 * </ul>
 *
 *
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 14:02
 * @see http://json.org/
 */
public abstract class JSON implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * First character of a JSONArray
     */
    public static final char START_ARRAY = '[';

    /**
     * Last character of a JSONArray
     */
    public static final char END_ARRAY = ']';

    /**
     * First character of a JSONObject
     */
    public static final char START_OBJECT = '{';

    /**
     * Last character of a JSONObject
     */
    public static final char END_OBJECT = '}';

    /**
     * First character of a JSONSet
     */
    public static final char START_SET = '<';

    /**
     * Last character of a JSONSet
     */
    public static final char END_SET = '>';

    /**
     * First character of a String element
     */
    public static final char START_STRING = '"';

    /**
     * Last character of a String element (termination of the String)
     */
    public static final char END_STRING = '"';

    /**
     * Simple Quote can be used instead of double quotes to identify a String element
     */
    public static final char QUOTE = '\'';

    /**
     * Character separator of the "key/value pair", elements of JSONObject
     */
    public static final char KEY_VALUE_SEPARATOR = ':';

    /**
     * Escape character, used to escape characters in a String
     */
    public static final char ESCAPE = '\\';

    /**
     * Character separator in a list of elements
     */
    public static final char OBJECTS_SEPARATOR = ',';

    /**
     * Minus character, used to identify negative number
     */
    public static final char MINUS = '-';

    /**
     * Dot character, used to separate decimal in a number
     */
    public static final char DECIMAL_SEPARATOR = '.';

    /**
     * Uppered exponential symbol
     */
    public static final char UPPER_EXP = 'E';

    /**
     * Lowered exponential symbol
     */
    public static final char LOWER_EXP = 'e';

    /**
     * Equal symbol is sometimes used instead of the colon (":") to sperator a key/value element
     */
    public static final char EQUAL = '=';

    /**
     * Factory provides a set of method to instantiate Object during the parse or an update of the JSON Objects
     */
    public static class Factory {

        /**
         * Called during the parse to instantiate a new JSONObject
         * @return new JSONObject
         */
        public JSONObject newJSONObject() {
            return new JSONObject();
        }

        /**
         * Called during the parse to instantiate a new JSONArray
         * @return new JSONArray
         */
        public JSONArray newJSONArray() {
            return new JSONArray();
        }

        /**
         * Called during the parse to instantiate a new JSONSet
         * @return new JSONSet
         */
        public JSONSet newJSONSet() {
            return new JSONSet();
        }

        /**
         * Called during the parse to handle a String
         * @param s - input string to parse
         * @return the JSON result of the parse
         */
        public JSON fromString(String s) {
            return JSON.fromString(s);
        }

        /**
         * Used to convert a Collection to a JSONArray
         * @param object - input collection
         * @return JSONArray, conversion of the collection
         */
        public JSONArray newJSONArray(Collection object) {
            return new JSONArray(object);
        }

        /**
         * Used to convert a Map to a JSONObject
         * @param object - input map
         * @return JSONObject, conversion of the map
         */
        public JSONObject newJSONObject(Map object) {
            return new JSONObject(object);
        }

        /**
         * Used to convert a integers array to a JSONArray
         * @param object - input integers array
         * @return JSONArray, conversion of the array
         */
        public JSONArray newJSONArray(int[] object) {
            return new JSONArray(object);
        }

        /**
         * Used to convert a longs array to a JSONArray
         * @param object - input longs array
         * @return JSONArray, conversion of the array
         */
        public JSONArray newJSONArray(long[] object) {
            return new JSONArray(object);
        }

        /**
         * Used to convert a Objects array to a JSONArray
         * @param object - input Objects array
         * @return JSONArray, conversion of the array
         */
        public JSONArray newJSONArray(Object[] object) {
            return new JSONArray(object);
        }
    }

    /**
     * Default factory
     */
    public static Factory factory = new Factory();


    /**
     * The Context Object is used during the parse of a String representation of a JSON Object
     * It provides methods which are called by the parser (@see org.runmyprocess.json.parser.Parser) to build the JSON Object
     * result of the String representation.
     */
    public static class Context {

        /**
         * Stack of JSON Object to represents the current Object being updated and its parents
         */
        protected Stack<JSON> context = new Stack<JSON>();
        private boolean expectingKey;

        /**
         * Current key of a key/value element
         */
        protected String key;

        private JSON root = null;

        /**
         * Parser will call this method when it enters in a JSON Object
         * @param json - The JSON Object whose is entered in
         * @return - boolean, true
         */
        public boolean enter( JSON json ) {
            if( context.isEmpty() ) root = json;
            else accept( json );
            expectingKey = json.isJSONObject();
            context.push( json );
            return true;
        }

        /**
         * Parser will call this method when it exists from JSON Object
         * @return - boolean, true
         */
        public boolean exit() {
            JSON accepted = context.pop();
            JSON current = !context.isEmpty() ? context.peek() : null;
            expectingKey = current != null && current.isJSONObject();
            if( current != null ) current.accepted( accepted );
            return true;
        }

        /**
         * Parser will call this method to accept an Object, this can be a key (for a key/value element) or a Number
         * or whatever Object a JSON can accept
         * @param object - the accpeted Object
         * @return boolean, true
         */
        @SuppressWarnings("unchecked")
        public boolean accept( Object object ) {
            if( expectingKey ) {
                key = (String)object;
                expectingKey = false;
            } else {
                JSON current = context.peek();
                if( validateKey(key) ) current.accept( key, object );
                expectingKey = current.isJSONObject();
            }
            return true;
        }

        /**
         * Return true if the given key is valid. An unvalidated key will not be added to to the JSON Object.
         * @param key - The input key to be validate
         * @return - boolean, true is the key is valid
         */
        protected boolean validateKey(String key) {
            return true;
        }

        /**
         * Return the current key of the current key/value element
         * @return String representation of the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Return the JSON result of the parse
         * @return - a JSON Object
         */
        public JSON getJSON() {
            return root;
        }
    }

    /**
     * Represents the accepted keyword of the JSON objects
     */
    public enum JSONKeyword {
        TRUE("true", Boolean.TRUE), FALSE("false", Boolean.FALSE), NULL("null", null);

        private Object object;
        private String literal;
        private JSONKeyword( String literal, Object object) {
            this.object = object;
            this.literal = literal;
        }
        public Object toObject() {
            return object;
        }
        public String getLiteral() {
            return literal;
        }
    }

    /**
     * Provides methods to convert a java Object to accepted value for JSON Object (Map -> JSONObject, List -> JSONArray, Set -> JSONSet)
     * and to stringify Object
     */
    public interface Converter {
        /**
         * Converts the given object to accepted value for JSON Object
         * @param object - the input object to convert
         * @return - JSON Object result of the conversion
         */
        public Object convert(Object object);

        /**
         * Appends the String representation of the given Object to the given builder
         * @param builder
         * @param object
         */
        public void appendJSONValue(StringBuilder builder, Object object);
    }

    /**
     * Convenient class to represents a Map of Converter
     */
    public static class Converters extends HashMap<Class, Converter> {}

    /**
     * Quotes and escapes the given String and appends it to the given builder
     * @param builder
     * @param string
     */
    protected static void quoteString(StringBuilder builder, String string) {
        builder.append(START_STRING);
        int maxIndex = string.length();
        for( int index=0; index<maxIndex; ++index ) {
            appendEscapedChar( builder, string.charAt(index) );
        }
        builder.append(END_STRING);
    }

    /**
     * Escapes the given character and appends it to the given builder
     * @param builder
     * @param c
     */
    protected static void appendEscapedChar( StringBuilder builder, char c) {
        if( c < 15 || c==34 || c==92) {
            switch( c ) {
                case '"':
                case '\\': builder.append(ESCAPE).append(c); break;
                case '\b': builder.append(ESCAPE).append('b'); break;
                case '\t': builder.append(ESCAPE).append('t'); break;
                case '\n': builder.append(ESCAPE).append('n'); break;
                case '\r': builder.append(ESCAPE).append('r'); break;
                case '\f': builder.append(ESCAPE).append('f'); break;
            }
        } else builder.append(c);
    }

    /**
     * Default converters
     */
    protected static final Converters converters = createConverters(factory);

    /**
     * Create the defaults converters with the given factory
     * @param factory
     * @return
     */
    protected static Converters createConverters( final Factory factory ) {
        return new Converters() {
            {
                put( String.class, new Converter() {
                    public Object convert(Object object) {
                        return object;
                    }

                    public void appendJSONValue(StringBuilder builder, Object object) {
                        quoteString(builder, (String) object);
                    }
                });
                put( Number.class, new Converter() {
                    public Object convert(Object object) {
                        return object;
                    }
                    public void appendJSONValue(StringBuilder builder, Object object) {
                        builder.append(object.toString());
                    }
                });
                put( Boolean.class, new Converter() {
                    public Object convert(Object object) {
                        return object;
                    }

                    public void appendJSONValue(StringBuilder builder, Object object) {
                        builder.append((Boolean) object ? JSONKeyword.TRUE.getLiteral() : JSONKeyword.FALSE.getLiteral());
                    }
                });
                put( Collection.class, new Converter() {
                    @SuppressWarnings("unchecked")
                    public Object convert(Object object) {
                        return factory.newJSONArray((Collection)object);
                    }
                    public void appendJSONValue(StringBuilder builder, Object object) {
                        ((JSONArray)object).appendJSON(builder);
                    }
                });
                put( Map.class, new Converter() {
                    public Object convert(Object object) {
                        return factory.newJSONObject((Map)object);
                    }
                    public void appendJSONValue(StringBuilder builder, Object object) {
                        ((JSONObject)object).appendJSON(builder);
                    }
                });
                put( JSON.class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return object;
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                        ((JSON)object).appendJSON(builder);
                    }
                });
                put( int[].class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return factory.newJSONArray((int[])object);
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
                put( long[].class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return factory.newJSONArray((long[])object);
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
                put( int.class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return object;
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
                put( long.class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return object;
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
                put( Object[].class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return factory.newJSONArray((Object[])object);
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
                put( Object.class, new Converter() {
                    @Override
                    public Object convert(Object object) {
                        return object != null ? factory.fromString(object.toString()) : null;
                    }

                    @Override
                    public void appendJSONValue(StringBuilder builder, Object object) {
                    }
                });
            }
        };
    }

    /**
     * Get the converters
     * @return - Converters
     */
    protected Converters getConverters() {
        return converters;
    }

    /**
     * Ordered list of Class which have to be converted before their insertion in the JSON Object
     */
    protected static final Class[] sanitizeClasses = {
        String.class, Number.class, Boolean.class, JSON.class, Map.class, Collection.class, int[].class, long[].class, Object[].class
    };
    /**
     * Ordered list of Class which have to be converted during the fromObject process
     */
    protected static final Class[] fromObjectClasses = {
        JSON.class, Map.class, Collection.class, Object[].class, int[].class, long[].class, Object.class
    };
    /**
     * Ordered list of Class which have to be converted during the toString process
     */
    protected static final Class[] appendStringClasses = {
            String.class, Number.class, Boolean.class, JSON.class
    };

    /**
     * construct a JSON object from an object
     * @param object the object to be used to create a json object
     * @return a json object
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    public static JSON fromObject( Object object ) throws JSONException {
        return (JSON)JSON.convert( JSON.converters, object, JSON.fromObjectClasses, true);
    }

    /**
     * Constructs a JSON Object from the given Object using the given converter
     * @param converters
     * @param object
     * @return
     * @throws JSONException
     */
    protected static JSON fromObject( Converters converters, Object object ) throws JSONException {
        return (JSON)JSON.convert( converters, object, JSON.fromObjectClasses, true);
    }

    public static JSONObject fromMap( Map object ) {
        return (JSONObject)JSON.converters.get(Map.class).convert(object);
    }
    protected static JSONObject fromMap( Converters converters, Map object ) {
        return (JSONObject)converters.get(Map.class).convert(object);
    }

    public static JSONArray fromCollection( Collection object ) {
        return (JSONArray)JSON.converters.get(Collection.class).convert(object);
    }
    protected static JSONArray fromCollection( Converters converters, Collection object ) {
        return (JSONArray)converters.get(Collection.class).convert(object);
    }

    public static JSON parse(Parser parser) throws JSONException {
        return parser.parse();
    }
    public static JSON fromString( String object ) throws JSONException {
        return JSON.parse(new DefaultParser(object));
    }

    public static JSON copy( JSON source ) {
        return source != null ? source.copy() : null;
    }

    public JSON copy() {
        return JSON.fromString(this.toString());
    }

    @SuppressWarnings("unchecked")
    protected Object sanitize(Object object) throws JSONException {
        return convert( getConverters(), object, JSON.sanitizeClasses, false);
    }

    @SuppressWarnings("unchecked")
    protected static Object convert( Map<Class, Converter> converters, Object object, Class[] classes, boolean safe) throws JSONException {
        if( object == null ) return object;

        for( Class clazz : classes ) {
            if( clazz.isAssignableFrom(object.getClass())) {
                return converters.get(clazz).convert(object);
            }
        }
        if( safe ) return null;
        else throw new JSONException(String.format("Illegal class : %s", object.getClass().getName()));
    }

    public boolean isJSONArray() {
        return false;
    }
    public boolean isJSONObject() {
        return false;
    }
    public boolean isJSONSet() {
        return false;
    }
    public JSONArray toJSONArray() {
        return null;
    }
    public JSONObject toJSONObject() {
        return null;
    }
    public JSONSet toJSONSet() {
        return null;
    }
    protected abstract void accept( String key, Object object );
    protected void accepted( JSON object ){}
    public abstract void appendJSON(StringBuilder builder);
    public abstract void appendJSON(Writer writer) throws IOException;

    /**
     *
     * @return string representation of this json object
     */
    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendJSON(builder);
        return builder.toString();
    }

    public void write( Writer writer ) throws IOException {
        appendJSON(writer);
    }

    @SuppressWarnings("unchecked")
    protected void appendDelimitedSeparatedJSON( StringBuilder builder, char start, char separator, char end, Collection values ) {
        builder.append(start);
        boolean first = true;
        for( Object value : values ) {
            if( !first ) builder.append(separator);
            else first = false;
            if( value == null ) builder.append(JSONKeyword.NULL.getLiteral());
            else {
                Class dClass = value.getClass();
                for( Class clazz : getAppendStringClasses()) {
                    if( clazz.isAssignableFrom(dClass)) {
                        getConverters().get(clazz).appendJSONValue( builder, value);
                        break;
                    }
                }
            }
        }
        builder.append(end);
    }

    @SuppressWarnings("unchecked")
    protected void appendDelimitedSeparatedJSON( Writer writer, char start, char separator, char end, Collection values ) throws IOException {
        writer.append(start);
        boolean first = true;
        for( Object value : values ) {
            if( !first ) writer.append(separator);
            else first = false;
            if( value == null ) writer.append(JSONKeyword.NULL.getLiteral());
            else {
                Class dClass = value.getClass();
                for( Class clazz : getAppendStringClasses()) {
                    if( clazz.isAssignableFrom(dClass)) {
                        StringBuilder stringValue = new StringBuilder();
                        getConverters().get(clazz).appendJSONValue( stringValue, value);
                        writer.append(stringValue);
                        break;
                    }
                }
            }
        }
        writer.append(end);
    }

    // transformers
    protected String getString( Object value) {
        return (String)value;
    }

    protected String getString( Object value, String def) {
        if (value == null) return def;
        return (String)value;
    }

    protected String castString( Object value) {
        return value instanceof String ? (String)value : value.toString();
    }

    protected String castString( Object value, String def) {
        if (value == null) return def;
        return value instanceof String ? (String)value : value.toString();
    }

    protected Long getLong( Object value ) {
        return getLong(value, null);
    }

    protected Long getLong( Object value, Long def ) {
        if( value == null ) return def;
        Number number = (Number)value;
        return number.longValue();
    }

    protected Long castLong( Object value ) {
        if( value == null ) return null;
        return value instanceof Number ? getLong(value) : Long.decode(value.toString());
    }

    protected Long castLong( Object value, Long def ) {
        if( value == null ) return def;
        return value instanceof Number ? getLong(value) : Long.decode(value.toString());
    }

    protected Integer getInteger( Object value ) {
        return getInteger(value, null);
    }

    protected Integer getInteger( Object value, Integer def ) {
        if( value == null ) return def;
        return ((Number)value).intValue();
    }

    protected Integer castInteger( Object value ) {
        if( value == null ) return null;
        return value instanceof Number ? getInteger(value) : Integer.decode(value.toString());
    }

    protected Integer castInteger( Object value, Integer def ) {
        if( value == null ) return def;
        return value instanceof Number ? getInteger(value) : Integer.decode(value.toString());
    }

    protected Double getDouble( Object value ) {
        if( value == null ) return null;
        return ((Number)value).doubleValue();
    }
    protected Double getDouble( Object value, Double def ) {
        if( value == null ) return def;
        return ((Number)value).doubleValue();
    }

    protected Double castDouble( Object value ) {
        if( value == null ) return null;
        return value instanceof Number ? getDouble(value) : Double.parseDouble(value.toString());
    }
    protected Double castDouble( Object value, Double def ) {
        if( value == null ) return def;
        return value instanceof Number ? getDouble(value) : Double.parseDouble(value.toString());
    }

    protected Float getFloat( Object value ) {
        if( value == null ) return null;
        return ((Number)value).floatValue();
    }
    protected Float getFloat( Object value, Float def ) {
        if( value == null ) return def;
        return ((Number)value).floatValue();
    }

    protected Float castFloat( Object value ) {
        if( value == null ) return null;
        return value instanceof Number ? getFloat(value) : Float.parseFloat(value.toString());
    }
    protected Float castFloat( Object value, Float def ) {
        if( value == null ) return def;
        return value instanceof Number ? getFloat(value) : Float.parseFloat(value.toString());
    }

    protected Number getNumber( Object value ) {
        return (Number)value;
    }
    protected Number getNumber( Object value, Number def ) {
        if (value == null) return def;
        return (Number)value;
    }

    protected Number castNumber( Object value ) {
        if( value == null ) return null;
        return value instanceof Number ? (Number)value : Double.parseDouble(value.toString());
    }
    protected Number castNumber( Object value, Number def ) {
        if( value == null ) return def;
        return value instanceof Number ? (Number)value : Double.parseDouble(value.toString());
    }

    protected Boolean getBoolean( Object value ) {
        return (Boolean)value;
    }

    protected Boolean getBoolean( Object value, Boolean def ) {
        return  value == null ? def : (Boolean)value;
    }

    protected JSONObject getJSONObject( Object value, JSONObject def ) {
        return  value == null ? def : (JSONObject)value;
    }
    protected JSONObject safeGetJSONObject( Object value) {
        return value != null?(JSONObject)value : new JSONObject();
    }
    protected JSONArray safeGetJSONArray( Object value) {
        return value != null?(JSONArray)value : new JSONArray();
    }


    protected Boolean castBoolean( Object value ) {
        if( value == null ) return null;
        return value instanceof Boolean ? (Boolean)value : Boolean.valueOf(value.toString());
    }

    protected Boolean castBoolean( Object value, Boolean def ) {
        if( value == null ) return def;
        return value instanceof Boolean ? (Boolean)value : Boolean.valueOf(value.toString());
    }

    protected JSONObject castJSONObject( Object value, JSONObject def ) {
        if( value == null ) return def;
        return value instanceof JSONObject ? (JSONObject)value : JSONObject.fromObject(value);
    }

    protected JSONArray castJSONArray( Object value, JSONArray def ) {
        if( value == null ) return def;
        return value instanceof JSONArray ? (JSONArray)value : JSONArray.fromObject(value);
    }

    protected Class[] getAppendStringClasses(){
        return JSON.appendStringClasses;
    }

    public static boolean mayBeJSON( String s ) {
        return mayBeJsonObject(s) || mayBeJsonArray(s);
    }


    public static Boolean mayBeJsonObject(String s) {
        if (s != null) {
            String trimmed = s.trim();
            return trimmed.startsWith("{") && trimmed.endsWith( "}" );
        }
        return false;
    }

    public static Boolean mayBeJsonArray(String s) {
        if (s != null) {
            String trimmed = s.trim();
            return trimmed.startsWith("[") && trimmed.endsWith("]");
        }
        return false;
    }
}
