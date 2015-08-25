package app.vz.hr.fabula.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

import app.vz.hr.fabula.R;

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
    public Database getDatabaseInstance(Context ctx) {
        if(this.manager == null){
            getManagerInstance(ctx);
        }
        if (this.database == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            try {
                this.database = manager.getDatabase(sp.getString(GlobalUtil.DB_NAME_KEY, ctx.getString(R.string.app_name).toLowerCase()));
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
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
