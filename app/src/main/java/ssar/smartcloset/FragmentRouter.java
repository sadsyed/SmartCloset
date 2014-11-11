package ssar.smartcloset;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.FragmentRouter.OnFragmentRouterInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRouter#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentRouter extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    private Button closetButton;
    private Button searchButton;
    private Button newTagButton;

    private OnFragmentRouterInteractionListener fragmentRouterInteractionListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRouter newInstance(String param1, String param2) {
        FragmentRouter fragment = new FragmentRouter();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentRouter() {
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
        View view = inflater.inflate(R.layout.fragment_new_tag, container, false);

        closetButton = (Button) view.findViewById(R.id.closetButton);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        newTagButton = (Button) view.findViewById(R.id.newTagButton);

        closetButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        newTagButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        //newTagButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (fragmentRouterInteractionListener != null) {
            fragmentRouterInteractionListener.onFragmentRouterInteraction(view);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentRouterInteractionListener = (OnFragmentRouterInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentRouterInteractionListener = null;
    }

    @Override
    public void onClick (View view) {
        fragmentRouterInteractionListener.onFragmentRouterInteraction(view);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentRouterInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentRouterInteraction(View view);
    }

}
