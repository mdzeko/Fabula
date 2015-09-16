package app.vz.hr.fabula.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miso on 9/16/15
 */
public class JSONParse {
    public static boolean METADocumentExists(String JSON){
        try {
            JSONObject response = new JSONObject(JSON);
            return response.has("_id") && response.has("user_name") && response.has("phone") && !response.getString("phone").isEmpty();
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean responseOK(String JSON){
        try {
            if(JSON == null)
                return false;
            JSONObject response = new JSONObject(JSON);
            return response.has("ok") && response.getString("ok").equals("true");
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean DBExists(String JSON){
        try {
            if(JSON == null)
                return false;
            JSONObject response = new JSONObject(JSON);
            return response.has("error") && response.getString("error").equals("file_exists");
        } catch (JSONException e) {
            return false;
        }
    }
    public static boolean DBExistsByName(String JSON){
        try {
            if(JSON == null)
                return false;
            JSONObject response = new JSONObject(JSON);
            return response.has("db_name") && !response.getString("db_name").isEmpty();
        } catch (JSONException e) {
            return false;
        }
    }
}
