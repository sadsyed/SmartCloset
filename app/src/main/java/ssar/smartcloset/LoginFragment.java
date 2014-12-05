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

import org.json.JSONException;
import org.json.JSONObject;

import ssar.smartcloset.types.CustomGridAdapter;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private static final String CLASSNAME = LoginFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SmartClosetRequestReceiver smartClosetRequestReceiver;
    private OnLoginFragmentInteractionListener onLoginFragmentInteractionListener;
    private SharedPreferences sharedPreferences;
    IntentFilter filter;

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button logInButton;
    private User userAccount;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = (EditText) view.findViewById(R.id.emailEditText);

        logInButton = (Button) view.findViewById(R.id.logInButton);
        logInButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onLoginFragmentInteractionListener = (OnLoginFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onLoginFragmentInteractionListener = null;
    }

    @Override
    public void onClick(View view) {
        User currentUser = new User();
        currentUser.setUserEmail(emailEditText.getText().toString());
        //currentUser.setUserPin(passwordEditText.getText().toString());

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        smartClosetRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.GET_USER_ACCOUNT);
        getActivity().registerReceiver(smartClosetRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("email", currentUser.getUserEmail());
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Exception while creating an request JSON.");
        }
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Starting Get User Account request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.GET_USER_ACCOUNT);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Finished Creating intent");
        getActivity().startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Started intent service");
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
                    String username = (String) json.get("userName");

                    // get list of articles in the selected category
                    userAccount = JsonParserUtil.jsonToUser(serviceUrl, responseJSON);

                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + "User name: " + userAccount.getUserName());
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + "User email: " + userAccount.getUserEmail());

                    //add profile to apps preference
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SmartClosetConstants.PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sharedPreferences.edit();

                    e.putString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, userAccount.getUserName());
                    e.putString(SmartClosetConstants.SHAREDPREFERENCE_FIRST_NAME, userAccount.getFirstName());
                    e.putString(SmartClosetConstants.SHAREDPREFERENCE_LAST_NAME, userAccount.getLastName());
                    e.putString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, userAccount.getUserEmail());
                    //e.putString(SmartClosetConstants.SHAREDPREFERENCE_PASSWORD, userAccount.ge);
                    e.commit();

                    onLoginFragmentInteractionListener.onLoginFragmentInteraction();
                } catch (Exception e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
                    ToastMessage.displayShortToastMessage(context, "Login failed");
                }
            } catch (JSONException e)
            {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
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
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onLoginFragmentInteraction();
    }

}
