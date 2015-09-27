package app.vz.hr.fabula.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by miso on 9/27/15
 */
public class ContactListAdapter extends ArrayAdapter<String>{
    int layout;
    int textView;
    Context ctx;
    List<String> namesList;

    public ContactListAdapter(Context context, int resourceId, int textViewResourceId, List<String> names){
        super(context, resourceId, textViewResourceId, names);
        this.ctx = context;
        this.layout = resourceId;
        this.textView = textViewResourceId;
        this.namesList = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(layout, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else
            holder = (ViewHolder) row.getTag();
        if(namesList == null)
            return null;
        String name = namesList.get(position);
        if(name != null)
            holder.txtName.setText(name);
        return row;
    }

    /*public boolean updateDataSet(List<String> names){
        if (names != null){
            this.namesList.clear();
            for (String name : names){
                this.namesList.add(name);
            }
            notifyDataSetChanged();
            return true;
        }
        return false;
    }*/

    class ViewHolder
    {
        TextView txtName;
        ViewHolder(View v)
        {
            txtName = (TextView) v.findViewById(textView);
        }
    }
}
