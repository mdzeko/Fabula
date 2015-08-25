package app.vz.hr.fabula.messaging;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.vz.hr.fabula.R;
import app.vz.hr.fabula.util.Conversation;
import app.vz.hr.fabula.util.DBUtil;
import app.vz.hr.fabula.util.GlobalUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatHolderFragment extends Fragment implements LiveQuery.ChangeListener, View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    String phone;
    String name;
    private static final String CONTACTS_PHONE = "contacts_phone";
    private static final String CONTACTS_NAME = "contacts_name";
    LiveQuery liveMessages;
    LinearLayout chatHolder;
    EditText edtMessage;
    SharedPreferences sp;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ChatHolderFragment newInstance(String contactsPhone, String name) {
        ChatHolderFragment fragment = new ChatHolderFragment();
        Bundle args = new Bundle();
        args.putString(CONTACTS_PHONE, contactsPhone);
        args.putString(CONTACTS_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public ChatHolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.phone = getArguments().getString(CONTACTS_PHONE);
        this.name = getArguments().getString(CONTACTS_NAME);
        ((ChatWindow) activity).onSectionAttached(this.name);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatHolder = (LinearLayout) getActivity().findViewById(R.id.chatHolder);
        edtMessage = (EditText) getActivity().findViewById(R.id.edtMessage);
        ImageView sendButton = (ImageView) getActivity().findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            Database db = DBUtil.getDBUtil().getDatabaseInstance(getActivity());
            URL remote = new URL("http://dzeko.iriscouch.com/" + getString(R.string.app_name).toLowerCase());
            Replication push = db.createPushReplication(remote);
            push.setContinuous(true);
            Replication pull = db.getActiveReplicator(remote, false);
            if(pull == null) {
                pull = db.createPullReplication(remote);
                pull.setContinuous(true);
                pull.start();
            }
            push.start();
            Query messagesQuery = Conversation.getMessages(db, phone, sp.getString(GlobalUtil.PHONE_NUM_KEY, "")).createQuery();
            messagesQuery.setLimit(300);
            messagesQuery.setDescending(false);
            liveMessages = messagesQuery.toLiveQuery();
            attachMessages(liveMessages.getRows());
            liveMessages.addChangeListener(this);
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    private void attachMessages(QueryEnumerator rows) {
        if(rows == null)
            return;
        chatHolder.removeAllViews();
        while (rows.hasNext()) {
            QueryRow row = rows.next();
            Document doc = row.getDocument();
            if(doc.getProperty("to").toString().isEmpty() || doc.getProperty("from").toString().isEmpty() || doc.getProperty("datetime").toString().isEmpty())
                return;
            View msg;
            if(doc.getProperty("from").equals(sp.getString(GlobalUtil.PHONE_NUM_KEY, "")))
                msg = getActivity().getLayoutInflater().inflate(R.layout.my_message, null);
            else
                msg = getActivity().getLayoutInflater().inflate(R.layout.interlocutor_message, null);
            if(msg instanceof TextView)
                ((TextView) msg).setText(doc.getProperty("message").toString());
            int index = chatHolder.getChildCount();
            chatHolder.addView(msg, index);
        }
    }

    @Override
    public void changed(final LiveQuery.ChangeEvent event) {
        if(liveMessages != null)
        if(event.getSource().equals(this.liveMessages))
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                attachMessages(event.getRows());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        if(edtMessage == null || edtMessage.getText().toString().isEmpty())
            return;
        if(sp.getString(GlobalUtil.USER_NAME_KEY, null) == null){
            Toast.makeText(getActivity(), R.string.update_profile_name, Toast.LENGTH_SHORT).show();
            return;
        }
        Database db = DBUtil.getDBUtil().getDatabaseInstance(getActivity());
        Document doc = db.createDocument();
        Map<String, Object> properties = new HashMap<>();
        properties.put("to", phone);
        properties.put("from", sp.getString(GlobalUtil.PHONE_NUM_KEY, ""));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ROOT);
        String current = sdf.format(new Date());
        properties.put("datetime", current);
        properties.put("message", edtMessage.getText().toString());
        properties.put("name", sp.getString(GlobalUtil.USER_NAME_KEY, null));
        properties.put("timestamp", System.currentTimeMillis() / 1000L);
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        edtMessage.getText().clear();
    }
}
