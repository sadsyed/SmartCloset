package ssar.smartcloset;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import ssar.smartcloset.types.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.NeverUsedFragment.OnNeverUsedFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NeverUsedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NeverUsedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnNeverUsedFragmentInteractionListener onNeverUsedFragmentInteractionListener;

    private CheckBox neverUsedCheckBox;
    private Button neverUsedSearchButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NeverUsedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NeverUsedFragment newInstance(String param1, String param2) {
        NeverUsedFragment fragment = new NeverUsedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NeverUsedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_never_used, container, false);

        neverUsedCheckBox = (CheckBox) view.findViewById(R.id.neverUsedCheckBox);
        neverUsedSearchButton = (Button) view.findViewById(R.id.neverUsedSearchButton);
        neverUsedSearchButton.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                String neverUsedFilterValue = null;

                if(neverUsedCheckBox.isChecked()) {
                    neverUsedFilterValue = "true";
                } else {
                    neverUsedFilterValue = "false";
                }

                //invoke SearchArticles API to get articles
                String searchType = "neverused";

                User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

                //callback the MainActivity to display list of articles
                onNeverUsedFragmentInteractionListener.onNeverUsedFragmentInteraction(searchType, neverUsedFilterValue, loggedInUser.getUserEmail());
            }
        });

        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onNeverUsedFragmentInteractionListener != null) {
            onNeverUsedFragmentInteractionListener.onNeverUsedFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onNeverUsedFragmentInteractionListener = (OnNeverUsedFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNeverUsedFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNeverUsedFragmentInteractionListener = null;
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
    public interface OnNeverUsedFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onNeverUsedFragmentInteraction(String searchType, String searchValue, String email);
    }

}
