package app.vz.hr.fabula;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import app.vz.hr.fabula.async_requests.GETRequest;
import app.vz.hr.fabula.async_requests.PUTRequest;
import app.vz.hr.fabula.messaging.ChatWindow;
import app.vz.hr.fabula.util.GlobalUtil;
import app.vz.hr.fabula.util.JSONParse;

/**
 * Created by miso on 8/23/15
 */
public class Login extends Activity implements View.OnClickListener {
    BootstrapEditText ETPhone;
    BootstrapEditText ETName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ETPhone = (BootstrapEditText) findViewById(R.id.input_phone);
        ETName = (BootstrapEditText) findViewById(R.id.input_name);
        BootstrapButton save = (BootstrapButton) findViewById(R.id.save_button);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                if(ETName == null || ETPhone == null)
                    return;
                if(ETPhone.getText().toString().isEmpty())
                    ETPhone.setState(BootstrapEditText.TextState.DANGER);
                if(ETName.getText().toString().isEmpty())
                    ETName.setState(BootstrapEditText.TextState.DANGER);

                String num = ETPhone.getText().toString();
                String name = ETName.getText().toString();

                if (!num.isEmpty() && PhoneNumberUtils.isGlobalPhoneNumber(num) && !name.isEmpty()) {
                    GETRequest task = new GETRequest();
                    task.execute(GlobalUtil.SERVER_URL + "db" + ETPhone.getText().toString().toLowerCase().replace("+", "") + "/meta");
                    try {
                        responsePositive(JSONParse.METADocumentExists(task.get()));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public void responsePositive(boolean positive) {
        boolean ok = positive;
        try {
            if(positive) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putString(GlobalUtil.PHONE_NUM_KEY, ETPhone.getText().toString()).apply();
                sp.edit().putString(GlobalUtil.USER_NAME_KEY, ETName.getText().toString()).apply();
            }
            else{
                PUTRequest task = new PUTRequest(GlobalUtil.SERVER_URL + "db" + ETPhone.getText().toString().toLowerCase().replace("+", ""));
                JSONObject data = new JSONObject();
                data.put("phone", ETPhone.getText().toString());
                data.put("user_name", ETName.getText().toString());
                task.execute(data.toString(), "meta");
                ok = JSONParse.responseOK(task.get());
            }
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            ok = false;
        }
        if(ok) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putString(GlobalUtil.PHONE_NUM_KEY, ETPhone.getText().toString()).apply();
            sp.edit().putString(GlobalUtil.USER_NAME_KEY, ETName.getText().toString()).apply();
            startActivity(new Intent(this, ChatWindow.class));
            finish();
        }
        else
            Toast.makeText(this, R.string.error_in_request, Toast.LENGTH_SHORT).show();
    }
}
