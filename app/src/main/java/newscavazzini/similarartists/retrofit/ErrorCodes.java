package newscavazzini.similarartists.retrofit;

import org.json.JSONObject;

/**
 * List of Last.fm Web Service error codes
 * https://www.last.fm/api/errorcodes
 */

public class ErrorCodes {

    public static String getErrorMessage(String errorBody) {

        try {
            return new JSONObject(errorBody).get("message").toString();

        } catch (Exception e) {
            return null;

        }
    }

    public static int getErrorCode(String errorBody) {

        try {
            return Integer.parseInt(new JSONObject(errorBody).get("error").toString());

        } catch (Exception e) {
            return 0;

        }
    }

}
