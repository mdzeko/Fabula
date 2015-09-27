package app.vz.hr.fabula.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Base64;

import java.io.IOException;

/**
 * Created by miso on 8/23/15
 */
public class DBUtil {
    private static DBUtil util = null;
    Manager manager;
    Database database;
    protected DBUtil(){
    }
    public static DBUtil getDBUtil(){
        if(util == null)
            util = new DBUtil();
        return util;
    }

    public static String addAuthentication(){
        return "Basic " + Base64.encodeToString("mdzeko:7UBek4NA".getBytes(), Base64.NO_WRAP);
    }

    public Database getDatabaseInstance(Context ctx, String dbName) {
        if(this.manager == null){
            getManagerInstance(ctx);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        try {
            this.database = manager.getDatabase(sp.getString(GlobalUtil.DB_NAME_KEY, dbName.toLowerCase()));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return database;
    }
    public Manager getManagerInstance(Context context){
        AndroidContext ctx = new AndroidContext(context);
        if (manager == null) {
            try {
                manager = new Manager(ctx, Manager.DEFAULT_OPTIONS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return manager;
    }
}
