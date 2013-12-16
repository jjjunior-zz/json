package org.runmyprocess.json;

import org.runmyprocess.json.parser.Parser;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 14:30
 */
public class JSONObject extends JSON implements Map<String,Object> {

    private static final long serialVersionUID = 1L;

    protected Map<String,Object> data;

    public JSONObject() {
        createMap();
    }

    protected void createMap(){
        data = new HashMap<String,Object>();
    }

    @SuppressWarnings("unchecked")
    public JSONObject(Map object) {
        this();
        putAll( object );
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean containsKey(Object o) {
        return data.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return data.containsValue(o);
    }

    public Object get(Object o) {
        return data.get(o);
    }

    public Object put(String s, Object o) {
        return data.put(s, sanitize(o));
    }

    public Object remove(Object o) {
        return data.remove(o);
    }

    public void putAll(Map<? extends String, ?> map) {
        if( map != null ) {
            for(Map.Entry entry : map.entrySet() ) {
                put( (String)entry.getKey(), sanitize(entry.getValue()));
            }
        }
    }

    public void putAllExcept(Map<? extends String, ?> map, String key) {
        if( map != null ) {
            for(Map.Entry entry : map.entrySet() ) {
                String entryKey = entry.getKey().toString();
                if( !entryKey.equals(key) )
                    put( entryKey, sanitize(entry.getValue()));
            }
        }
    }

    public void putAllExcept(Map<? extends String, ?> map, Set<String> keys) {
        if( map != null ) {
            for(Map.Entry entry : map.entrySet() ) {
                String entryKey = entry.getKey().toString();
                if( !keys.contains(entryKey) )
                    put( entryKey, sanitize(entry.getValue()));
            }
        }
    }

    public void clear() {
        data.clear();
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    public Collection<Object> values() {
        return data.values();
    }

    public Set<Entry<String,Object>> entrySet() {
        return data.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return data.equals(o);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    public static JSONObject fromObject( Object object ) {
        JSON json = JSON.fromObject(object);
        return json != null ? json.toJSONObject() : null;
    }

    public JSONObject copy() {
        JSON json = super.copy();
        return json != null ? json.toJSONObject() : null;
    }

    public static JSONObject copy( JSON source ) {
        JSON json = JSON.copy(source);
        return json != null ? json.toJSONObject() : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void appendJSON(StringBuilder builder) {
        boolean first = true;
        builder.append(START_OBJECT);
        for( Map.Entry<String,Object> entry : data.entrySet() ) {
            if( !first ) builder.append(OBJECTS_SEPARATOR);
            first = false;
            quoteString(builder, entry.getKey());
            builder.append(KEY_VALUE_SEPARATOR);
            Object value = entry.getValue();
            if( value == null ) builder.append(JSONKeyword.NULL.getLiteral());
            else {
                for( Class clazz : getAppendStringClasses()) {
                    if( clazz.isAssignableFrom(value.getClass())) {
                        getConverters().get(clazz).appendJSONValue( builder, value);
                        break;
                    }
                }
            }
        }
        builder.append(END_OBJECT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void appendJSON( Writer writer) throws IOException {
        boolean first = true;
        writer.append(START_OBJECT);
        for( Map.Entry<String,Object> entry : data.entrySet() ) {
            if( !first ) writer.append(OBJECTS_SEPARATOR);
            first = false;
            StringBuilder quotedKey  = new StringBuilder();
            quoteString( quotedKey, entry.getKey());
            writer.append(quotedKey);
            writer.append(KEY_VALUE_SEPARATOR);
            Object value = entry.getValue();
            if( value == null ) writer.append(JSONKeyword.NULL.getLiteral());
            else {
                for( Class clazz : getAppendStringClasses()) {
                    if( clazz.isAssignableFrom(value.getClass())) {
                        StringBuilder stringValue = new StringBuilder();
                        getConverters().get(clazz).appendJSONValue( stringValue, value);
                        writer.append(stringValue);
                        break;
                    }
                }
            }
        }
        writer.append(END_OBJECT);
    }

    @Override
    protected void accept( String key, Object object ) {
        putRaw( key, object);
    }

    @Override
    public boolean isJSONObject() {
        return true;
    }

    @Override
    public JSONObject toJSONObject() {
        return this;
    }

    public static JSONObject fromString( String text ) throws JSONException {
        return (JSONObject)JSON.fromString(text);
    }

    public static JSONObject parse( Parser parser) throws JSONException {
        return (JSONObject)JSON.parse(parser);
    }

    protected void putRaw(String key, Object object) {
        data.put(key, object);
    }

    public String getString( String key ) {
        return super.getString(get(key));
    }

    public String getString( String key, String def ) {
        return super.getString(get(key), def);
    }

    public String castString( String key ) {
        return super.castString(get(key));
    }
    public String castString( String key, String def ) {
        return super.castString(get(key), def);
    }

    public Long getLong( String key ) {
        return super.getLong(get(key));
    }

    public Long getLong( String key, Long def ) {
        return super.getLong(get(key), def);
    }

    public Long castLong( String key) {
        return super.castLong(get(key));
    }
    public Long castLong( String key, Long def) {
        return super.castLong(get(key), def);
    }

    public Integer getInteger( String key ) {
        return super.getInteger(get(key));
    }

    public Integer getInteger( String key, Integer def ) {
        return super.getInteger(get(key), def);
    }

    public Integer castInteger( String key ) {
        return super.castInteger(get(key));
    }
    public Integer castInteger( String key, Integer def ) {
        return super.castInteger(get(key), def);
    }

    public Double getDouble( String key ) {
        return super.getDouble(get(key));
    }
    public Double getDouble( String key, Double def ) {
        return super.getDouble(get(key), def);
    }

    public Double castDouble( String key ) {
        return super.castDouble(get(key));
    }
    public Double castDouble( String key, Double def ) {
        return super.castDouble(get(key), def);
    }

    public Float getFloat( String key ) {
        return super.getFloat(get(key));
    }
    public Float getFloat( String key, Float def ) {
        return super.getFloat(get(key), def);
    }

    public Float castFloat( String key ) {
        return super.castFloat(get(key));
    }
    public Float castFloat( String key, Float def ) {
        return super.castFloat(get(key), def);
    }

    public Number getNumber( String key ) {
        return (Number)get(key);
    }
    public Number getNumber( String key, Number def ) {
        return super.getNumber(key, def);
    }


    public Boolean getBoolean( String key ) {
        return super.getBoolean(get(key));
    }

    public Boolean getBoolean( String key, Boolean def ) {
        return super.getBoolean(get(key),def);
    }

    public Boolean castBoolean( String key ) {
        return super.castBoolean(get(key));
    }

    public Boolean castBoolean( String key, Boolean def ) {
        return super.castBoolean(get(key),def);
    }

    public JSONObject getJSONObject( String key ) {
        return (JSONObject)get(key);
    }

    public JSONObject getJSONObject( String key, JSONObject def ) {
        return super.getJSONObject(get(key), def);
    }

    public JSONObject safeGetJSONObject(String key){
        return super.safeGetJSONObject(get(key));
    }

    public JSONArray safeGetJSONArray(String key){
        return super.safeGetJSONArray(get(key));
    }

    public JSON getJSON( String key ) {
        return (JSON)get(key);
    }

    public  JSONArray getJSONArray( String key ) {
        return (JSONArray)get(key);
    }

    public  JSONSet getJSONSet( String key ) {
        return (JSONSet)get(key);
    }

    public JSONObject castJSONObject( String key, JSONObject def ) {
        return super.castJSONObject(get(key), def);
    }

    public JSONObject castJSONObject( String key) {
        return super.castJSONObject(get(key), null);
    }

    public JSONArray castJSONArray(  String key, JSONArray def ) {
        return super.castJSONArray(get(key), def);
    }

    public JSONArray castJSONArray(  String key) {
        return super.castJSONArray(get(key), null);
    }

    public JSONObject accumulate( String key, Object value ) {
        if( containsKey(key) ) {
            Object currentValue = get( key );
            if( currentValue instanceof JSONArray ) {
                ((JSONArray) currentValue).add(value);
            } else {
                JSONArray newValue = new JSONArray();
                newValue.add(currentValue);
                newValue.add(value);
                put( key, newValue );
            }
        } else {
            put( key, value );
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public JSONObject accumulateAll( Map map ) {
        for( Map.Entry entry : (Set<Map.Entry<?,?>>)map.entrySet() ) {
            accumulate( entry.getKey().toString(), entry.getValue() );
        }
        return this;
    }

    public JSONObject discard( String key ) {
        data.remove(key);
        return this;
    }

    public JSONObject element( String key, Object object ) {
        if( object == null ) return discard(key);
        else {
            put( key, object );
            return this;
        }
    }
}
