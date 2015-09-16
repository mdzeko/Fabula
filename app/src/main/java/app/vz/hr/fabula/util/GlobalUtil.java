package app.vz.hr.fabula.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by miso on 8/22/15
 */
public class GlobalUtil {
    public static final String DB_NAME_KEY = "db_name";
    public static final String PHONE_NUM_KEY = "prefPhone";
    public static final String USER_NAME_KEY = "prefUsername";
    public static final String SERVER_URL = "http://46.101.163.158:5984/";

    public static String makeConvName(JSONArray participants){
        String name = participants.length() == 0 ? "??": "";
        for(int i = 0; i < participants.length(); i++){
            try {
                JSONObject participant = participants.getJSONObject(i);
                name += participant.getString("name");
                if(i != participants.length()-1)
                    name += ", ";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public static String digestString(String s){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes("UTF-8"));
            return String.format("%064x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
