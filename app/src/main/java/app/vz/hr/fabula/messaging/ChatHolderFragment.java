package app.vz.hr.fabula.messaging;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.vz.hr.fabula.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatHolderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String DOCUMENT_ID = "document_id";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ChatHolderFragment newInstance(String docID) {
        ChatHolderFragment fragment = new ChatHolderFragment();
        Bundle args = new Bundle();
        args.putString(DOCUMENT_ID, docID);
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
        ((ChatWindow) activity).onSectionAttached(
                getArguments().getString(DOCUMENT_ID));
    }
}
