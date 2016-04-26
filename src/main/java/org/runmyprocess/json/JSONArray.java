package org.runmyprocess.json;

import org.runmyprocess.json.parser.Parser;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 14:34
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public class JSONArray extends JSON implements List {

    private static final long serialVersionUID = 1L;

    protected List data;

    public JSONArray() {
        createList();
    }

    public JSONArray(Collection object) {
        this();
        addAll( object );
    }

    protected void createList(){
        data = new ArrayList();
    }

    public <T> JSONArray( T[] array ) {
        this();
        if( array != null ) addAll(Arrays.asList(array));
    }

    public JSONArray(int[] array) {
        this();
        if( array != null ) {
            for( int i : array ) {
                add(i);
            }
        }
    }

    public JSONArray(long[] array) {
        this();
        if( array != null ) {
            for( long l : array ) {
                add(l);
            }
        }
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean contains(Object o) {
        return data.contains(o);
    }

    public Iterator iterator() {
        return data.iterator();
    }

    public Object[] toArray() {
        return data.toArray();
    }

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
     public Object[] toArray(Object[] a) {
        return data.toArray(new Object[0]);
    }


    public boolean add(Object t) {
        return data.add(sanitize(t));
    }

    public boolean remove(Object o) {
        return data.remove(o);
    }

    public boolean containsAll(Collection objects) {
        return data.containsAll(objects);
    }

    public boolean addAll(Collection ts) {
        boolean result = false;
        if( ts != null ) for( Object t : ts ) result = add(t) || result;
        return result;
    }

    public boolean addAll(int index, Collection ts) {
        if( ts != null ) {
            for( Object t : ts ) add( index++, sanitize(t));
            return ts.size()>0;
        }
        return false;

    }

    public boolean removeAll(Collection objects) {
        return data.removeAll(objects);
    }

    public boolean retainAll(Collection objects) {
        return data.retainAll(objects);
    }

    public void clear() {
        data.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == data) return true;
        if (!(o instanceof List)) return false;

        ListIterator e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2))) return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    public Object get(int i) {
        return data.get(i);
    }

    public Object set(int i, Object t) {
        return data.set(i, t);
    }

    public void add(int i, Object t) {
        data.add(i, t);
    }

    public Object remove(int i) {
        return data.remove(i);
    }

    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return data.listIterator();
    }

    public ListIterator listIterator(int i) {
        return data.listIterator(i);
    }

    public List subList(int i, int i2) {
        return data.subList(i, i2);
    }

    public static JSONArray fromObject( Object object ) {
        JSON json = JSON.fromObject(object);
        return json != null ? json.toJSONArray() : null;
    }

    public JSONArray copy() {
        JSON json = super.copy();
        return json != null ? json.toJSONArray() : null;
    }

    public static JSONArray copy( JSON source ) {
        JSON json = JSON.copy(source);
        return json != null ? json.toJSONArray() : null;
    }

    public void appendJSON(StringBuilder builder)  {
        appendDelimitedSeparatedJSON( builder, START_ARRAY, OBJECTS_SEPARATOR, END_ARRAY, data );
    }

    public void appendJSON(Writer writer) throws IOException {
        appendDelimitedSeparatedJSON( writer, START_ARRAY, OBJECTS_SEPARATOR, END_ARRAY, data );
    }

    @Override
    protected void accept( String key, Object object ) {
        addRaw(object);
    }

    @Override
    public boolean isJSONArray() {
        return true;
    }

    @Override
    public JSONArray toJSONArray() {
        return this;
    }

    public static JSONArray fromString( String text ) throws JSONException {
        return (JSONArray)JSON.fromString(text);
    }

    public static JSONArray parse( Parser parser ) throws JSONException {
        return (JSONArray)JSON.parse(parser);
    }

    protected void addRaw(Object object) {
        data.add(object);
    }

    public String getString( int index ) {
        return super.getString(get(index));
    }

    public String getString( int index, String def ) {
        return super.getString(get(index), def);
    }

    public String castString( int index ) {
        return super.castString(get(index));
    }

    public String castString( int index, String def ) {
        return super.castString(get(index), def);
    }

    public Long getLong( int index ) {
        return super.getLong(get(index));
    }

    public Long getLong( int index, Long def ) {
        return super.getLong(get(index), def);
    }

    public Long castLong( int index ) {
        return super.castLong(get(index));
    }
    public Long castLong( int index, Long def ) {
        return super.castLong(get(index), def);
    }

    public Integer getInteger( int index ) {
        return super.getInteger(get(index));
    }

    public Integer getInteger( int index, Integer def ) {
        return super.getInteger(get(index), def);
    }

    public Integer castInteger( int index ) {
        return super.castInteger(get(index));
    }
    public Integer castInteger( int index, Integer def ) {
        return super.castInteger(get(index), def);
    }

    public Double getDouble(  int index ) {
        return super.getDouble(get(index));
    }
    public Double getDouble(  int index, Double def ) {
        return super.getDouble(get(index), def);
    }

    public Double castDouble(  int index ) {
        return super.castDouble(get(index));
    }
    public Double castDouble(  int index, Double def ) {
        return super.castDouble(get(index), def);
    }

    public Float getFloat(  int index ) {
        return super.getFloat(get(index));
    }
    public Float getFloat(  int index, Float def ) {
        return super.getFloat(get(index), def);
    }

    public Float castFloat(  int index ) {
        return super.castFloat(get(index));
    }
    public Float castFloat(  int index, Float def ) {
        return super.castFloat(get(index), def);
    }

    public Number getNumber( int index ) {
        return super.getNumber(get(index));
    }
    public Number getNumber( int index, Number def ) {
        return super.getNumber(get(index), def);
    }

    public Number castNumber( int index ) {
        return super.castNumber(get(index));
    }
    public Number castNumber( int index, Number def ) {
        return super.castNumber(get(index), def);
    }

    public Boolean getBoolean(  int index ) {
        return super.getBoolean(get(index));
    }

    public Boolean castBoolean(  int index ) {
        return super.castBoolean(get(index));
    }

    public JSONObject getJSONObject(  int index ) {
        return (JSONObject)get(index);
    }

    public JSONObject getJSONObject( int index, JSONObject def ) {
        return super.getJSONObject(get(index), def);
    }

    public JSONObject safeGetJSONObject(int index){
        return super.safeGetJSONObject(get(index));
    }

    public JSONArray safeGetJSONArray(int index){
        return super.safeGetJSONArray(get(index));
    }

    public JSON getJSON(  int index ) {
        return (JSON)get(index);
    }

    public  JSONArray getJSONArray(  int index ) {
        return (JSONArray)get(index);
    }

    public  JSONSet getJSONSet(  int index ) {
        return (JSONSet)get(index);
    }

    public JSONObject castJSONObject( int index, JSONObject def ) {
        return super.castJSONObject(get(index), def);
    }

    public JSONObject castJSONObject(int index) {
        return super.castJSONObject(get(index), null);
    }

    public JSONArray castJSONArray(  int index, JSONArray def ) {
        return super.castJSONArray(get(index), def);
    }

    public JSONArray castJSONArray( int index) {
        return super.castJSONArray(get(index), null);
    }
}
