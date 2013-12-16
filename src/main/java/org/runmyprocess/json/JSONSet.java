package org.runmyprocess.json;

import org.runmyprocess.json.parser.Parser;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * User: sgaide & dboulay
 * Date: 21/02/13
 * Time: 15:53
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public class JSONSet extends JSON implements Set {
    private static final long serialVersionUID = 1L;

    private Set data;

    public JSONSet() {
        data = new HashSet();
    }

    public JSONSet(Collection object) {
        this();
        addAll( object );
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    public Iterator iterator() {
        return data.iterator();
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public Object[] toArray(Object[] a) {
        return data.toArray(new Object[0]);
    }

    @Override
    public boolean add(Object o) {
        return data.add(sanitize(o));
    }

    @Override
    public boolean remove(Object o) {
        return data.remove(o);
    }

    public boolean containsAll(Collection objects) {
        return data.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection collection) {
        return data.addAll(collection);
    }

    public boolean retainAll(Collection objects) {
        return data.retainAll(objects);
    }

    public boolean removeAll(Collection objects) {
        return data.removeAll(objects);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public boolean equals(Object o) {
        return data.equals(o);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    protected void accept( String key, Object object ) {
        addRaw(object);
    }

    @Override
    public boolean isJSONSet() {
        return false;
    }

    @Override
    public JSONSet toJSONSet() {
        return this;
    }

    public static JSONSet fromString( String text ) throws JSONException {
        return (JSONSet)JSON.fromString(text);
    }

    public static JSONSet parse(Parser parser ) throws JSONException {
        return (JSONSet)JSON.parse(parser);
    }

    protected void addRaw(Object object) {
        data.add(object);
    }

    public void appendJSON( StringBuilder builder ) {
        appendDelimitedSeparatedJSON( builder, START_SET, OBJECTS_SEPARATOR, END_SET, data );
    }

    public void appendJSON(Writer writer) throws IOException {
        appendDelimitedSeparatedJSON( writer, START_SET, OBJECTS_SEPARATOR, END_SET, data );
    }

}
