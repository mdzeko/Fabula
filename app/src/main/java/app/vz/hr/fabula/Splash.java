package app.vz.hr.fabula;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
        Handler handler = new Handler();
        handler.getLooper();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PreferenceManager.getDefaultSharedPreferences(Splash.this).getString(GlobalUtil.PHONE_NUM_KEY, null) != null){
                    startActivity(new Intent(Splash.this, ChatWindow.class));
                    finish();
                }
                else {
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }
            }
        }, 2000);
    }
}
