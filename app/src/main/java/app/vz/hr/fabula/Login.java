package app.vz.hr.fabula;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.replicator.Replication;

import java.net.MalformedURLException;
import java.net.URL;

import app.vz.hr.fabula.messaging.ChatWindow;
import app.vz.hr.fabula.util.DBUtil;
import app.vz.hr.fabula.util.GlobalUtil;

/**
 * Created by miso on 8/23/15
 */
public class Login extends Activity implements View.OnClickListener {
    EditText ETPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ETPhone = (EditText) findViewById(R.id.input_phone);
        Button save = (Button) findViewById(R.id.save_button);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                String num = ETPhone.getText().toString();
                if (ETPhone != null && !num.isEmpty() && PhoneNumberUtils.isGlobalPhoneNumber(num)) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    sp.edit().putString(GlobalUtil.PHONE_NUM_KEY, num).apply();
                    createInitialSync();
                    startActivity(new Intent(this, ChatWindow.class));
                    finish();
                }
                else
                    Toast.makeText(this, R.string.phone_num_not_valid, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void createInitialSync(){
        try {
            Database db = DBUtil.getDBUtil().getDatabaseInstance(this);
            URL remote;
            remote = new URL("http://dzeko.iriscouch.com/" + getString(R.string.app_name).toLowerCase());
            Replication pull = db.createPullReplication(remote);
            pull.setContinuous(true);
            pull.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
