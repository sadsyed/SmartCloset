package ssar.smartcloset;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ARTICLE_ID = "articleId";

    // TODO: Rename and change types of parameters
    private String articleId;

    private OnWriteTagFragmentInteractionListener onWriteTagFragmentInteractionListener;

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
        return inflater.inflate(R.layout.fragment_write_tag, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onWriteTagFragmentInteractionListener != null) {
            onWriteTagFragmentInteractionListener.onWriteTagFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onWriteTagFragmentInteractionListener = (OnWriteTagFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
        public void onWriteTagFragmentInteraction(Uri uri);
    }

}
