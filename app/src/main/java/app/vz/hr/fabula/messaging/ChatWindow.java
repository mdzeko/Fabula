package app.vz.hr.fabula.messaging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import app.vz.hr.fabula.R;
import app.vz.hr.fabula.UserPreferenceActivity;
import app.vz.hr.fabula.util.DBUtil;


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

        // Set up the drawer.
        //helloCBL();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(String contactsPhone, String name) {
        contactsPhone = contactsPhone.replace("[", "");
        contactsPhone = contactsPhone.replace("]", "");
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ChatHolderFragment.newInstance(contactsPhone, name))
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
                    if (!etPhone.getText().toString().isEmpty() && PhoneNumberUtils.isGlobalPhoneNumber(etPhone.getText().toString()))
                        addConversation(etPhone.getText().toString());
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

    private void addConversation(String phone) {
        onNavigationDrawerItemSelected(phone, phone);
        /*Database db = DBUtil.getDBUtil().getDatabaseInstance(this);
        Document doc = db.createDocument();
        Map<String, Object> properties = new HashMap<>();
        properties.put("to", phone);
        properties.put("from", PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalUtil.PHONE_NUM_KEY, ""));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ROOT);
        String current = sdf.format(new Date());
        properties.put("datetime", current);
        properties.put("message", "");
        properties.put("name", PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalUtil.USER_NAME_KEY, null));
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }*/
        //mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBUtil.getDBUtil().getManagerInstance(this).close();
    }
}
