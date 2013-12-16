package org.runmyprocess.json.parser;

import org.runmyprocess.json.*;

import java.util.HashMap;

/**
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 14:58
 */
public class DefaultParser implements Parser {

    private String text;
    private int index;
    private JSON.Context context;
    protected JSON.Factory factory;

    public DefaultParser( String text, JSON.Context context, JSON.Factory factory ) {
        this.context = context != null ? context : new JSON.Context();
        this.factory = factory != null ? factory : JSON.factory;
        setText(text);
    }
    public DefaultParser( String text, JSON.Context context ) {
        this( text, context, null);
    }
    public DefaultParser( String text, JSON.Factory factory ) {
        this( text, null, factory);
    }
    public DefaultParser( String text ) {
        this( text, null, null);
    }
    protected DefaultParser() {
        this(null, null, null);
    }

    private static class HexaMap extends HashMap<Character,Integer> {
        final static String hexavalues = "0123456789ABCDEF";

        {
            for( int value=0; value<16; ++value ) put( hexavalues.charAt(value), value );
        }

        public int getValue( char c ) {
            Integer v = super.get(c);
            if( v == null ) throw new IndexOutOfBoundsException(String.format("%s is not a valid hexadecimal value", c));
            return v;
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static HexaMap hexaMap = new HexaMap();

    public JSON parse() throws JSONException {
        this.index = 0;
        trim();
        if( acceptJSON() ) {
            return getContext().getJSON();
        } else {
            throw new JSONException("Malformed object");
        }
    }

    protected void trim() {
        currentIgnoreSpace();
    }

    protected boolean acceptJSONArray() throws JSONException {
        if( acceptStartArray()) {
            getContext().enter(factory.newJSONArray());
            if( !isEndArray()) {
                boolean hasNext = true;
                while (hasNext) {
                    acceptObject();
                    hasNext = acceptSeparator(JSON.OBJECTS_SEPARATOR, JSON.END_ARRAY);
                }
            }
            return acceptEndArray();
        }
        return false;
    }

    protected boolean acceptJSONSet() throws JSONException {
        if( acceptStartSet()) {
            getContext().enter(factory.newJSONSet());
            if( !isEndSet()) {
                boolean hasNext = true;
                while (hasNext) {
                    acceptObject();
                    hasNext = acceptSeparator(JSON.OBJECTS_SEPARATOR, JSON.END_SET);
                }
            }
            return acceptEndSet();
        }
        return false;
    }

    protected boolean acceptJSONObject() throws JSONException {
        if( acceptStartObject()) {
            getContext().enter(factory.newJSONObject());
            if( !isEndObject() ) {
                boolean hasNext = true;
                while (hasNext) {
                    acceptKeyValue();
                    hasNext = acceptSeparator(JSON.OBJECTS_SEPARATOR, JSON.END_OBJECT);
                }

            }
            return acceptEndObject();
        }
        return false;
    }

    protected boolean acceptKeyValue() throws JSONException {
        if( acceptKey() && acceptKeyValueSeparator() && acceptObject() ) {
            return true;
        } else {
            throw new JSONException(formatMessage("Malformed key value pair"));
        }
    }

    protected char current() {
        return this.getText().charAt(getIndex());
    }

    protected char currentIgnoreSpace() {
        while( true ) {
            char c = current();
            if( Character.isWhitespace(c) ) {
                incIndex();
            } else {
                return c;
            }
        }
    }

    protected boolean acceptKeyword() throws JSONException {
        StringBuilder str = new StringBuilder();
        char c;
        while (Character.isLowerCase(c = current())) {
            str.append(c);
            incIndex();
        }
        String kw = str.toString();
        if( kw.length()>0 ) {
            JSON.JSONKeyword keyword = JSON.JSONKeyword.valueOf(kw.toUpperCase());
            return getContext().accept(keyword.toObject());
        } else {
            return false;
        }
    }

    protected String acceptStringInternal( char endString ) {
        StringBuilder str = new StringBuilder();
        int startIndex = getIndex();
        char c;
        while ((c = current()) != endString ) {
            incIndex();
            if( c == JSON.ESCAPE ){
                str.append(getText().substring(startIndex, getIndex()-1));
                c = getEscapedChar(endString);
                str.append(c);
                startIndex = getIndex();
            }
        }
        str.append(getText().substring(startIndex, getIndex()));
        return str.toString();
    }

    protected boolean acceptString() throws JSONException {
        boolean isString = acceptStartString();
        boolean isQuoted = !isString && acceptQuote();
        char endString = isString ? JSON.END_STRING : (isQuoted ? JSON.QUOTE : 0);
        String string = null;
        boolean accepted = false;
        if( endString != 0 ) {
            string = acceptStringInternal(endString);
            accepted = isString?acceptEndString():acceptQuote();
        }
        return accepted && string != null && getContext().accept(string);
    }

    protected boolean acceptKey() throws JSONException {
        boolean isString = acceptStartString();
        boolean isQuoted = !isString && acceptQuote();
        char endKey = isString ? JSON.END_STRING : (isQuoted ? JSON.QUOTE : JSON.KEY_VALUE_SEPARATOR);
        String string = acceptStringInternal(endKey);
        boolean accepted = isString?acceptEndString():(isQuoted?acceptQuote():isKeyValueSeparator());
        String key = isString||isQuoted ? string : string.trim();
        return accepted && getContext().accept(key);
    }

    protected char getEscapedChar(char endString) throws JSONException {
        char c = current();
        incIndex();
        switch( c ) {
            case '/':
            case '\\': return c;
            case 'b': return '\b';
            case 't': return '\t';
            case 'n': return '\n';
            case 'r': return '\r';
            case 'f': return '\f';
            case 'u': return getUnicodeChar();
            default:
                if (c == endString) return c;
                else throw new JSONException(formatMessage(String.format("Unexpected character \"%s\"", c)));
        }
    }

    protected char getUnicodeChar() {
        int value = 0;
        for( int i=0; i<4; ++i ) {
            char c = current();
            value *= 10;
            value += hexaMap.getValue(Character.toUpperCase(c));
            this.incIndex();
        }
        return (char)value;
    }

    protected boolean acceptNumber() throws JSONException {
        Number result = null;
        char c = currentIgnoreSpace();
        if( Character.isDigit(c) || c==JSON.MINUS ) {
            long value = getLong();

            c = current();
            if( c == JSON.DECIMAL_SEPARATOR ) {
                incIndex();
                c = current();
                double decimalPart = 0;
                double divider = 1;
                while (Character.isDigit(c)) {
                    decimalPart *= 10;
                    divider *= 10;
                    decimalPart += (int) c - (int) '0';
                    this.incIndex();
                    c = current();
                }
                result = value+(value == 0?1:Math.signum(value))*decimalPart/divider;
            } else result = value;

            if( c == JSON.LOWER_EXP || c == JSON.UPPER_EXP ) {
                incIndex();
                long expValue = getLong();
                result = result.doubleValue() * Math.pow( 10L, expValue);
            }
        }
        return result != null  && getContext().accept(result);
    }

    protected long getLong() {
        int sign = 1;
        if( current() == JSON.MINUS ) {
            sign = -1;
            incIndex();
        }
        return sign*getPositiveLong();
    }

    protected long getPositiveLong(){
        long value = 0;
        char c = current();
        while (Character.isDigit(c)) {
            value *= 10;
            value += (int) c - (int) '0';
            this.incIndex();
            c = current();
        }
        return value;
    }

    protected boolean acceptStartString() throws JSONException {
        return accept(JSON.START_STRING);
    }

    protected boolean acceptQuote() throws JSONException {
        return accept(JSON.QUOTE);
    }

    protected boolean acceptEndString() throws JSONException {
        return accept(JSON.END_STRING, "String must end with \"%s\"");
    }

    protected boolean acceptKeyValueSeparator() throws JSONException {
        if( isKeyValueSeparator() || isEqualSeparator() ) {
            incIndex();
            return true;
        }
        throw new JSONException( String.format( formatMessage("Key and value must be separated by \"%s\""), JSON.KEY_VALUE_SEPARATOR));
    }

    protected boolean acceptStartArray() throws JSONException {
        return accept(JSON.START_ARRAY);
    }

    protected boolean acceptStartObject() throws JSONException {
        return accept(JSON.START_OBJECT);
    }

    protected boolean acceptStartSet() throws JSONException {
        return accept(JSON.START_SET);
    }

    protected boolean acceptEndObject() throws JSONException {
        return accept(JSON.END_OBJECT, "Object should end with \"%s\"") && getContext().exit();
    }

    protected boolean acceptEndArray() throws JSONException {
        return accept(JSON.END_ARRAY, "Array should end with \"%s\"") && getContext().exit();
    }

    protected boolean acceptEndSet() throws JSONException {
        return accept(JSON.END_SET, "Set should end with \"%s\"") && getContext().exit();
    }

    protected boolean acceptSeparator(char separator, char end) throws JSONException {
        return accept(separator) && currentIgnoreSpace() != end;
    }

    protected boolean accept(char c) throws JSONException {
        if (currentIgnoreSpace() == c) {
            this.incIndex();
            return true;
        }
        return false;
    }

    protected boolean accept(char c, String messageFormat ) throws JSONException {
        if (currentIgnoreSpace() == c) {
            this.incIndex();
            return true;
        }
        throw new JSONException( String.format( formatMessage(messageFormat), c));
    }

    protected boolean isEndArray() {
        return currentIgnoreSpace() == JSON.END_ARRAY;
    }

    protected boolean isEndSet() {
        return currentIgnoreSpace() == JSON.END_SET;
    }

    protected boolean isEndObject() {
        return currentIgnoreSpace() == JSON.END_OBJECT;
    }

    protected boolean isKeyValueSeparator() {
        return currentIgnoreSpace() == JSON.KEY_VALUE_SEPARATOR;
    }

    protected boolean isEqualSeparator() {
        return currentIgnoreSpace() == JSON.EQUAL;
    }

    protected boolean acceptJSON() throws JSONException {
        return acceptJSONObject() || acceptJSONArray() || acceptJSONSet();
    }

    protected boolean acceptObject() throws JSONException {
        if( acceptString() || acceptNumber() || acceptJSON() || acceptKeyword()) {
            return true;
        } else {
            throw new JSONException(formatMessage("Malformed object"));
        }
    }

    protected String getText() {
        return this.text;
    }

    protected void setText( String text ) {
        this.text = text;
    }

    protected int getIndex() {
        return this.index;
    }
    protected void setIndex(int index) {
        this.index = index;
    }

    protected void incIndex() {
        ++this.index;
    }

    protected JSON.Context getContext() {
        return context;
    }

    protected String formatMessage( String message ) {
        return String.format( "%s at %d, %s...", message, getIndex(), getText().substring(0, getIndex()+1));
    }
}
