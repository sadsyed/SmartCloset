package ssar.smartcloset;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ssar.smartcloset.types.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.BaseSearchFragment.OnBaseSearchFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BaseSearchFragment extends Fragment implements View.OnClickListener {
    private OnBaseSearchFragmentInteractionListener onBaseSearchFragmentInteractionListener;

    private Button baseSearchButton;
    private EditText searchEditText;

    public BaseSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_search, container, false);

        searchEditText = (EditText) view.findViewById(R.id.searchEditText);

        baseSearchButton = (Button) view.findViewById(R.id.baseSearchButton);
        baseSearchButton.setOnClickListener(this);
        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onBaseSearchFragmentInteractionListener != null) {
            onBaseSearchFragmentInteractionListener.onBaseSearchFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onBaseSearchFragmentInteractionListener = (OnBaseSearchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBaseSearchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onBaseSearchFragmentInteractionListener = null;
    }

    @Override
    public void onClick (View view) {
        //invoke SearchArticles API to get articles
        String searchType = "string";
        String searchValue = searchEditText.getText().toString();

        User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

        //callback the MainActivity to display list of articles
        onBaseSearchFragmentInteractionListener.onBaseSearchFragmentInteraction(searchType, searchValue, loggedInUser.getUserEmail());
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
    public interface OnBaseSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onBaseSearchFragmentInteraction(String searchType, String searchValue, String email);
    }

}
