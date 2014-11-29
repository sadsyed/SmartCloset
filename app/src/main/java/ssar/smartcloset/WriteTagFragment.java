package ssar.smartcloset;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.WriteTagFragment.OnWriteTagFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WriteTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteTagFragment extends Fragment {
    public final static String CLASSNAME = WriteTagFragment.class.getSimpleName();

    // the fragment initialization parameters
    private static final String ARG_ARTICLE_ID = "articleId";

    private OnWriteTagFragmentInteractionListener onWriteTagFragmentInteractionListener;
    private String articleId;
    private Button writeTagButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articleId Parameter 1.
     * @return A new instance of fragment WriteTagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteTagFragment newInstance(String articleId) {
        WriteTagFragment fragment = new WriteTagFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    public WriteTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_ARTICLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_write_tag, container, false);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": launching NFC Write...");

        ((MainActivity)getActivity()).articleUuid = articleId;
        ((MainActivity)getActivity()).writeMode = true;
        /*writeTagButton = (Button) view.findViewById(R.id.button);
        writeTagButton.setPressed(true);
        writeTagButton.invalidate();*/

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String articleId) {
        if (onWriteTagFragmentInteractionListener != null) {
            onWriteTagFragmentInteractionListener.onWriteTagFragmentInteraction(articleId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onWriteTagFragmentInteractionListener = (OnWriteTagFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onWriteTagFragmentInteractionListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWriteTagFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onWriteTagFragmentInteraction(String articleId);
    }

}
