package org.runmyprocess.json.parser;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONException;

/**
 * User: sgaide & dboulay
 * Date: 21/02/13
 * Time: 10:32
 */
public class StrictParser extends DefaultParser {
    public StrictParser( String text ) {
        super(text);
    }
    @Override
    protected boolean acceptKey() throws JSONException {
        return acceptString();
    }

    @Override
    protected boolean acceptKeyValueSeparator() throws JSONException {
        if( isKeyValueSeparator()) {
            incIndex();
            return true;
        }
        throw new JSONException( String.format( formatMessage("Key and value must be separated by \"%s\""), JSON.KEY_VALUE_SEPARATOR));
    }

    @Override
    protected boolean acceptSeparator(char separator, char end) throws JSONException {
        return accept(separator);
    }
}
