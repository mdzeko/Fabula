package app.vz.hr.fabula;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import app.vz.hr.fabula.messaging.ChatWindow;
import app.vz.hr.fabula.util.GlobalUtil;

/**
 * Created by miso on 8/22/15
 */
public class Splash extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalUtil.PHONE_NUM_KEY, null) != null){
            startActivity(new Intent(this, ChatWindow.class));
            finish();
        }
        else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}
