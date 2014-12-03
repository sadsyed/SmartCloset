package ssar.smartcloset;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ssar.smartcloset.util.SmartClosetConstants;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.FragmentRouter.OnFragmentRouterInteractionListener} interface
 * to handle interaction events.
 *
 */
public class FragmentRouter extends Fragment implements View.OnClickListener{
    private static final  String CLASSNAME = FragmentRouter.class.getSimpleName();

    // the fragment initialization parameters
    private static final String ARG_LOGGED_IN = "loggedIn";

    private Boolean isLoggedIn;

/*    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
*/
    private Button closetButton;
    private Button searchButton;
    private Button newTagButton;
    private Button createAccountButton;
    private Button logInButton;
    private TextView readTagTextView;

    private OnFragmentRouterInteractionListener fragmentRouterInteractionListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isLoggedIn Parameter 1.
     * @return A new instance of fragment NewTagFragment.
     */
    public static FragmentRouter newInstance(Boolean isLoggedIn) {
        FragmentRouter fragment = new FragmentRouter();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LOGGED_IN, isLoggedIn);
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
            isLoggedIn = getArguments().getBoolean(ARG_LOGGED_IN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_router, container, false);

        createAccountButton = (Button) view.findViewById(R.id.createAccountButton);
        logInButton = (Button) view.findViewById(R.id.logInButton);
        readTagTextView = (TextView) view.findViewById(R.id.readTagEditText);

        Drawable drawable = getResources().getDrawable(R.drawable.app_background);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        int nh  = (int) (bitmap.getHeight() * (512.0 /bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

        view.setBackground(new BitmapDrawable(getResources(), scaled));

        if(!isLoggedIn) {
            readTagTextView.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.VISIBLE);

            createAccountButton.setOnClickListener(this);
            logInButton.setOnClickListener(this);
        } else {
            createAccountButton.setVisibility(View.GONE);
            logInButton.setVisibility(View.GONE);

            readTagTextView.setVisibility(View.VISIBLE);
        }

        //closetButton = (Button) view.findViewById(R.id.closetButton);
        //searchButton = (Button) view.findViewById(R.id.searchButton);
        //newTagButton = (Button) view.findViewById(R.id.newTagButton);

        //closetButton.setOnClickListener(this);
        //searchButton.setOnClickListener(this);
        //newTagButton.setOnClickListener(this);

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
                    + " must implement OnArticleFragmentInteractionListener");
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
