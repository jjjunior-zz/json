package org.runmyprocess.json;

/**
 * User: sgaide & dboulay
 * Date: 08/01/13
 * Time: 15:48
 */
public class JSONException extends RuntimeException {
    public JSONException() {
        super();
    }

    public JSONException(String s) {
        super(s);
    }

    public JSONException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JSONException(Throwable throwable) {
        super(throwable);
    }
}
