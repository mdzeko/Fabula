package app.vz.hr.fabula.messaging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.replicator.Replication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import app.vz.hr.fabula.R;
import app.vz.hr.fabula.UserPreferenceActivity;
import app.vz.hr.fabula.async_requests.GETRequest;
import app.vz.hr.fabula.async_requests.PUTRequest;
import app.vz.hr.fabula.util.DBUtil;
import app.vz.hr.fabula.util.GlobalUtil;
import app.vz.hr.fabula.util.JSONParse;


public class ChatWindow extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Database db = DBUtil.getDBUtil().getDatabaseInstance(this, "db" + sp.getString(GlobalUtil.PHONE_NUM_KEY, "").replace("+", ""));
        URL remote;
        try {
            remote = new URL(GlobalUtil.SERVER_URL + "db" + sp.getString(GlobalUtil.PHONE_NUM_KEY, "").replace("+", ""));
            Replication pull = db.createPullReplication(remote);
            pull.setCreateTarget(true);
            pull.setContinuous(true);
            pull.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        // Set up the drawer.
        //helloCBL();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(String contactsPhone, String conversation) {
        JSONObject conv;
        try {
            conv = new JSONObject(conversation);
        } catch (JSONException e) {
            conv = new JSONObject();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ChatHolderFragment.newInstance(contactsPhone, conv))
                .commit();
    }

    public void onSectionAttached(String name) {
        mTitle = name;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chat_window, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, UserPreferenceActivity.class));
        }
        else if (id == R.id.action_new_conversation) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View dialogView = getLayoutInflater().inflate(R.layout.new_conversation_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText etPhone = (EditText) dialogView.findViewById(R.id.new_conversation_phone);
                    TextView txtMessage = (TextView) dialogView.findViewById(R.id.txtLoadingMessage);
                    if (!etPhone.getText().toString().isEmpty() && PhoneNumberUtils.isGlobalPhoneNumber(etPhone.getText().toString()))
                        newConversation(etPhone.getText().toString(), txtMessage);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void newConversation(String phone, TextView message) {
        String contactName;
        GETRequest task = new GETRequest(message);
        task.execute(GlobalUtil.SERVER_URL + "db" + phone.toLowerCase().replace("+", ""));
        try {
            if(JSONParse.DBExistsByName(task.get())){
                task = new GETRequest(message);
                task.execute(GlobalUtil.SERVER_URL + "db" + phone.toLowerCase().replace("+", "") + "/meta");
                JSONObject userData = new JSONObject(task.get());
                if(!userData.has("user_name")) {
                    message.setText(R.string.user_profile_error);
                    return;
                }
                contactName = userData.getString("user_name");
                message.setText(contactName);
            }
            else {
                message.setText(R.string.user_does_not_exist);
                return;
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String myPhone = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalUtil.PHONE_NUM_KEY, "");
            String myName = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalUtil.USER_NAME_KEY, "");
            String dbName = GlobalUtil.digestString(phone + myPhone);
            dbName = "db"+dbName;

            JSONObject myNewConv = new JSONObject();
            JSONObject contactNewConv = new JSONObject();

            JSONArray myParticipants = new JSONArray();
            JSONObject participant = new JSONObject();
            participant.put("phone", phone);
            participant.put("name", contactName);

            myParticipants.put(participant);
            myNewConv.put("participants", myParticipants);
            myNewConv.put("conversation_name", GlobalUtil.makeConvName(myParticipants));


            JSONArray contactParticipants = new JSONArray();
            JSONObject meParticipant = new JSONObject();
            meParticipant.put("phone", myPhone);
            meParticipant.put("name", myName);

            contactParticipants.put(meParticipant);
            contactNewConv.put("participants", contactParticipants);
            contactNewConv.put("conversation_name", GlobalUtil.makeConvName(contactParticipants));

            myNewConv.put("database_name", dbName);
            contactNewConv.put("database_name", dbName);

            PUTRequest newConvParticipant = new PUTRequest(GlobalUtil.SERVER_URL + "db" + phone.toLowerCase().replace("+", ""));
            newConvParticipant.execute(contactNewConv.toString(), myPhone.replace("+", ""));
            PUTRequest newConvMe = new PUTRequest(GlobalUtil.SERVER_URL + "db" + myPhone.toLowerCase().replace("+", ""));
            newConvMe.execute(myNewConv.toString(), phone.replace("+", ""));

            PUTRequest convDB = new PUTRequest(GlobalUtil.SERVER_URL + dbName);
            convDB.execute();

            onNavigationDrawerItemSelected(phone, myNewConv.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBUtil.getDBUtil().getManagerInstance(this).close();
    }

}
