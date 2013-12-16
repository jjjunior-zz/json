package org.runmyprocess.json.parser;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONException;

/** *
 * User: dboulay & sgaide
 * Date: 14/06/13
 * Time: 09:34

 */
public class RecursiveParser extends DefaultParser{

    public RecursiveParser(String text, JSON.Context context, JSON.Factory factory) {
        super(text, context, factory);
    }

    public RecursiveParser(String text, JSON.Factory factory) {
        super(text, factory);
    }
    public RecursiveParser(String text) {
        super(text);
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
        Object value = string;
        if (JSON.mayBeJSON(string)){
            try{
                value = JSON.parse(new RecursiveParser(string));
            }catch(Exception e){
                // ignore, we accept the original string
            }
        }
        return accepted && string != null && getContext().accept(value);
    }


}
