package ssar.smartcloset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ssar.smartcloset.types.User;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.ProfileFragment.OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    public static final String CLASSNAME = ProfileFragment.class.getSimpleName();
    public static final String PREF_NAME = "smartProfilePreference";

    // the fragment initialization parameters
    private static final String ARG_PROFILE_FRAGMENT = "profileFragment";

    private String profileFragment;

    public SmartClosetRequestReceiver smartClosetRequestReceiver;
    private OnProfileFragmentInteractionListener onProfileFragmentInteractionListener;
    private SharedPreferences sharedPreferences;
    IntentFilter filter;

    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText pinEditText;
    private EditText passwordEditText;
    private Button onSubmitButton;

    private User existingUser;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, 0);

        existingUser = new User();
        existingUser.setUserName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, null));
        existingUser.setFirstName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_FIRST_NAME, null));
        existingUser.setLastName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_LAST_NAME, null));
        existingUser.setUserEmail(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, null));
        existingUser.setUserPin(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_PASSWORD, null));
        existingUser.setUserPassword(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_PIN, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(existingUser.getUserEmail() != null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_profile, container, false);

            userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
            emailEditText = (EditText) view.findViewById(R.id.emailEditText);

            TextView profileTitleTextView = (TextView) view.findViewById(R.id.profileTitleTextView);

            if(existingUser.getFirstName() == null) {
                profileTitleTextView.setText("Welcome " + existingUser.getUserName());
            } else {
                profileTitleTextView.setText("Welcome " + existingUser.getFirstName());
            }
            userNameEditText.setEnabled(false);
            userNameEditText.setText(existingUser.getUserName());

            emailEditText.setEnabled(false);
            emailEditText.setText(existingUser.getUserEmail());
        } else {
            // Inflate create profile fragment
            view = inflater.inflate(R.layout.fragment_create_profile, container, false);

            userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
            emailEditText = (EditText) view.findViewById(R.id.emailEditText);
            passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
            pinEditText = (EditText) view.findViewById(R.id.pinEditText);

            onSubmitButton = (Button) view.findViewById(R.id.submitProfileButton);
            onSubmitButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick (View view) {
        //send create profile request to the server
        User newUser = new User();
        newUser.setUserName(userNameEditText.getText().toString());
        newUser.setUserEmail(emailEditText.getText().toString());
        newUser.setUserPassword(passwordEditText.getText().toString());
        newUser.setUserPin(pinEditText.getText().toString());

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        smartClosetRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
        getActivity().registerReceiver(smartClosetRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("username", newUser.getUserName());
            requestJSON.put("name", newUser.getFirstName());
            requestJSON.put("lastname", newUser.getLastName());
            requestJSON.put("email", newUser.getUserEmail());
            requestJSON.put("password", newUser.getUserPin());
            requestJSON.put("pin", newUser.getUserPin());
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Exception while creating an request JSON.");
        }
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Starting Create Profile request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.CREATE_PROFILE);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Finished Creating intent");
        ((MainActivity)getActivity()).startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Started intent service");

        //TODO: move to the boradcast receiever
        //add profile to apps preference
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();

        e.putString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, userNameEditText.getText().toString());
        e.putString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, emailEditText.getText().toString());
        e.putString(SmartClosetConstants.SHAREDPREFERENCE_PASSWORD, passwordEditText.getText().toString());
        e.putString(SmartClosetConstants.SHAREDPREFERENCE_PIN, pinEditText.getText().toString());
        e.commit();

        //launch profile fragment
        onProfileFragmentInteractionListener.onProfileFragmentInteraction();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onProfileFragmentInteractionListener != null) {
            onProfileFragmentInteractionListener.onProfileFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onProfileFragmentInteractionListener = (OnProfileFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProfileFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onProfileFragmentInteractionListener = null;
    }

    public class SmartClosetRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = SmartClosetRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public SmartClosetRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(smartClosetRequestReceiver != null) {
                try {
                    context.unregisterReceiver(smartClosetRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(responseJSON);
                try {
                    Integer errorcode = (Integer) json.get("errorcode");
                    if(errorcode == 0)  {
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": User created successfully");
                    }
                } catch (Exception e) {
                    Integer temp = (Integer)json.get("errorcode");
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Failed to create user profile");
                }
            } catch (JSONException e)
            {
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
            }
        }
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
    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onProfileFragmentInteraction();
    }

}
