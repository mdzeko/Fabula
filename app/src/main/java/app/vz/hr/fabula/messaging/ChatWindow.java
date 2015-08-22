package app.vz.hr.fabula.messaging;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import app.vz.hr.fabula.R;
import app.vz.hr.fabula.util.GlobalUtil;


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

    public static final String DB_NAME = "miso";
    public static final String TAG = "CB";
    Manager manager = null;
    Database database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        helloCBL();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void helloCBL() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString(GlobalUtil.DB_NAME_KEY, "miso").apply();
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(sp.getString(GlobalUtil.DB_NAME_KEY, "miso"));
        } catch (Exception e) {
            Log.e(TAG, "Error getting database", e);
            return;
        }
        // Create the document
        String documentId;
        documentId = createDocument(database);
        // retrieve the document from the database
        Document retrievedDocument;
        retrievedDocument = database.getDocument(documentId);

        // display the retrieved document
        if(retrievedDocument != null)
            Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
        /* Update the document and add an attachment
        updateDoc(database, documentId);
        // Add an attachment
        addAttachment(database, documentId);*/
    }

    @Override
    public void onNavigationDrawerItemSelected(String docID) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ChatHolderFragment.newInstance(docID))
                .commit();
    }

    public void onSectionAttached(String docID) {
        //TODO napravit logiku da uzima dokument po docID-u i onda title postavlja na ime i prezime
        Document doc = database.getDocument(docID);
        mTitle = doc.getProperty("name").toString();
        /*switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }*/
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String createDocument(Database database) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Miki");
        map.put("location", "My House");
        Document doc2 = database.createDocument();
        try {
            // Save the properties to the document
            document.putProperties(map);
            map.put("name", "Maus");
            doc2.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }

    private void updateDoc(Database database, String documentId) {
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put("eventDescription", "Everyone is invited!");
            updatedProperties.put("address", "123 Elm St.");
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }

    private void addAttachment(Database database, String documentId) {
        Document document = database.getDocument(documentId);
        try {
        /* Add an attachment with sample data as POC */
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] { 0, 0, 0, 0 });
            UnsavedRevision revision = document.getCurrentRevision().createRevision();
            revision.setAttachment("binaryData", "application/octet-stream", inputStream);
        /* Save doc & attachment to the local DB */
                    revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
    }
}
