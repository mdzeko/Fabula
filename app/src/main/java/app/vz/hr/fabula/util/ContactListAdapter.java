package app.vz.hr.fabula.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.couchbase.lite.QueryRow;

import java.util.List;

/**
 * Created by miso on 8/22/15
 */
public class ContactListAdapter extends ArrayAdapter<QueryRow>{

    List<QueryRow> conversations;
    public ContactListAdapter(Context context, int resource, int textViewResourceId, List<QueryRow> objects) {
        super(context, resource, textViewResourceId);
        this.conversations = objects;

    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
