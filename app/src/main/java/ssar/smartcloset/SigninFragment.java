package ssar.smartcloset;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.SigninFragment.OnSiginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SigninFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        View.OnTouchListener {
    private static final String CLASSNAME = SigninFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOGOUT = "logout";
    private static final String ARG_SIGNIN = "signin";

    // TODO: Rename and change types of parameters
    private Boolean logout = false;
    private Boolean signin;

    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK = -1;

    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    IntentFilter filter;

    //-- Google Signin ---
    // Request code used to invoke sign in user interactions.
    public static final int RC_SIGN_IN = 0;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Is there a ConnectionResult resolution in progress
    private boolean mIsResolving = false;

    // Should we automatically resolve ConnectionResult when possible?
    private boolean mShouldResolve = false;
    private static final String SERVER_CLIENT_ID = "40560021354-k08ugq82ifbisuc8k0nh79pv91jhcmq2.apps.googleusercontent.com";

    ProgressDialog progressDialog;

    //-- Authenticate with a Backend Server
    // tokenId for authentication with a backend server
    String tokenId;
    public AuthenticationRequestReceiver authenticationRequestReceiver;

    private String userName;
    private String userEmail;

    private OnSiginFragmentInteractionListener onSigninFragmentInteractionListener;

    //private SignInButton googleSigninButton;
    private Button googleSigninButton;
    private Button googlesignUpButton;
    private Button googleSignoutButton;
    private TextView signInTextView;

    //-- Create user account with SCI server
    private Boolean signingUp = true;
    private CreateProfileRequestReceiver createProfileRequestReceiver;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param logout Parameter 1.
     * @return A new instance of fragment SigninFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SigninFragment newInstance(Boolean logout, Boolean signin) {
        SigninFragment fragment = new SigninFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LOGOUT, logout);
        args.putBoolean(ARG_SIGNIN, signin);
        fragment.setArguments(args);
        return fragment;
    }

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            logout = getArguments().getBoolean(ARG_LOGOUT);
            signin = getArguments().getBoolean(ARG_SIGNIN);
        }

        //-- Google Signin ---
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        //progressDialog = new ProgressDialog(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        googleSigninButton = (Button) view.findViewById(R.id.googleSignInButton);
        googleSigninButton.setOnClickListener(this);

        googlesignUpButton = (Button) view.findViewById(R.id.googleSignUpButton);
        googlesignUpButton.setOnClickListener(this);

        googleSignoutButton = (Button) view.findViewById(R.id.signoutButton);
        googleSignoutButton.setOnClickListener(this);

        signInTextView = (TextView) view.findViewById(R.id.signInTextView);
        signInTextView.setOnTouchListener(this);

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " logout: " + logout);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " signin: " + signin);

        if (logout) {
            googleSigninButton.setVisibility(View.GONE);
            googlesignUpButton.setVisibility(View.GONE);
            signInTextView.setVisibility(View.GONE);
            googleSignoutButton.setVisibility(View.VISIBLE);
        } else if (signin) {
            googleSigninButton.setVisibility(View.VISIBLE);
            signInTextView.setVisibility(View.GONE);
            googlesignUpButton.setVisibility(View.GONE);
            googleSignoutButton.setVisibility(View.GONE);
        } else {
            googleSigninButton.setVisibility(View.GONE);
            signInTextView.setVisibility(View.VISIBLE);
            googlesignUpButton.setVisibility(View.VISIBLE);
            googleSignoutButton.setVisibility(view.GONE);
        }

        //Find the +1 button
        //mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    //-- Google Signin ---
    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onConnected:" + bundle);
        mShouldResolve = false;

        //only works if user has a google+ profile
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            userName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Logged in as: " + userName);
            ToastMessage.displayLongToastMessage(getActivity(), "Logged in as : " + userName);
        } else {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": User doesn't have a google plus profile =(...");
        }

        userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": user email address is : " + userEmail);

        if (signingUp) {
            // create account with SCI server
            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            createProfileRequestReceiver = new CreateProfileRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
            getActivity().registerReceiver(createProfileRequestReceiver, filter);

            //set the JSON request object
            JSONObject requestJSON = new JSONObject();
            try {
                requestJSON.put("username",userName);
                requestJSON.put("email", userEmail);
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
        }
        // reset user tokenId and set the user Profile
        new GetIdTokenTask(getActivity()).execute();
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": tokenId: " + tokenId);

        // load HomeFragment
        if (!logout) {
            onSigninFragmentInteractionListener.onSigninFragmentInteraction(false, false);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically attemot to resolve any errors that occur
        mShouldResolve = true;

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Sign in button clicked");
        signingUp = false;
        mGoogleApiClient.connect();

        //Show a message to the user that we are signing in
        ToastMessage.displayLongToastMessage(getActivity(), "Signing in =D");
    }

    private void onSignUpClicked() {
        // User clicked the sign-up button, so begin the sign-in process and automatically attempt tp resolve any errors that occur
        mShouldResolve = true;

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Sign up button clicked");
        signingUp  = true;
        mGoogleApiClient.connect();

        //show a message to the user that we are creating an account
        ToastMessage.displayLongToastMessage(getActivity(), "Signing up with Google Account");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": startResolutionForResult");
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error Dialog");
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Signed Out UI");

            //launch LogIn/Create Account page
            //Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
            //display Category Fragment - mark isLoggenIn to false
            FragmentRouter fragmentRouter = new FragmentRouter().newInstance(false);
 //           updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
 //           setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    private void logoutCurrentUser () {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Logout current user..... ");
        mGoogleApiClient.connect();
        if(mGoogleApiClient.isConnected()) {
            //start the progress dialog
            progressDialog = ProgressDialog.show(getActivity(), "", "Logging out...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //log out currently logged user
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect();
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error while loggin out");
                        Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + e.toString());
                    }
                    //dismiss the progress dialog
                    progressDialog.dismiss();
                }
            }).start();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SmartClosetConstants.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sharedPreferences.edit();

            e.putString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, null);
            e.putString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, null);
            e.putString(SmartClosetConstants.SHAREDPREFERENCE_TOKEN_ID, null);
            e.commit();

            // callback the MainActivity indicating user is logged out
            onSigninFragmentInteractionListener.onSigninFragmentInteraction(true, false);
       }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        //mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.googleSignInButton) {
            onSignInClicked();
        } else if (v.getId() == R.id.googleSignUpButton) {
            onSignUpClicked();
        } else if (v.getId() == R.id.signoutButton) {
            logoutCurrentUser();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // callback the MainActivity indicating user wants to sign in with an existing account
        onSigninFragmentInteractionListener.onSigninFragmentInteraction(false, true);
        return true;
    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onSigninFragmentInteractionListener != null) {
            onSigninFragmentInteractionListener.onSigninFragmentInteraction(uri);
        }
    }
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onSigninFragmentInteractionListener = (OnSiginFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSigninFragmentInteractionListener = null;
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
    public interface OnSiginFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSigninFragmentInteraction(Boolean loggedOut, Boolean siginIn);
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

        private Context mContext;

        public GetIdTokenTask (Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Account name is: " + accountName);

            //Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            //String scopes = "oauth2:server:client_id:" + SERVER_CLIENT_ID + ":api_scope:https://www.googleapis.com/auth/plus.login"; // Not the app's client ID.
            //String scopes = "oauth2:googleapis.com/auth/userinfo.profile";
            //String scopes = "oauth2:https://www.googleapis.com/auth/plus.login";
            //String scopes = "https://www.googleapis.com/auth/userinfo.profile";
            String scopes = "oauth2:" + Scopes.PROFILE;

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": scope with server client id : " + scopes );
            try {
                tokenId = GoogleAuthUtil.getToken(getActivity(), accountName, scopes);

                // update user preferences
                //add profile to apps preference
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SmartClosetConstants.PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedPreferences.edit();

                e.putString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, userName);
                e.putString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, userEmail);
                e.putString(SmartClosetConstants.SHAREDPREFERENCE_TOKEN_ID, tokenId);
                e.commit();

                return tokenId;
            } catch (IOException e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error retrieving ID token.", e);
                return null;
            } catch (GoogleAuthException e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Error retrieving ID token.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": ID token: " + result);
            if (result != null) {
                // Successfully retrieved ID Token
                tokenId = result;

                //authenticateWithBackendServer();
            } else {
                // There was some error getting the ID Token
                // ...
            }
        }

        private void authenticateWithBackendServer() {
            //send tokenId to backend server
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting authentication with backend server using tokenId: " + tokenId);

            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            authenticationRequestReceiver = new AuthenticationRequestReceiver(SmartClosetConstants.TOKEN_SIGNIN);
            mContext.registerReceiver(authenticationRequestReceiver, filter);

            //set the tokenId in the JSON request object
            JSONObject requestJSON = new JSONObject();
            try {
                requestJSON.put("tokenId", tokenId);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating a JSON request with tokenId");
            }

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting TokenSigin request");
            Intent msgIntent = new Intent(mContext, SmartClosetIntentService.class);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.TOKEN_SIGNIN);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
            msgIntent.putExtra("tokenId", tokenId);
            mContext.startService(msgIntent);

            progressDialog.setMessage("Authenticating with a Backend Server...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setIndeterminate(true);
            progressDialog.show();
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Authenticating with Backend Server... ");
        }
    }

    public class AuthenticationRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = AuthenticationRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public AuthenticationRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(authenticationRequestReceiver != null) {
                try {
                    context.unregisterReceiver(authenticationRequestReceiver);

                } catch (IllegalArgumentException e) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": TestSignin service response JSON: " + responseJSON);
            JSONObject json = new JSONObject();
            Boolean valid;

            try {
                json = new JSONObject(responseJSON);
                JSONObject accessTokenStatus = (JSONObject) json.get("access_token_status");
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": access_token_status: " + accessTokenStatus);
                valid = (Boolean) accessTokenStatus.get("valid");
                //imagePath = (String)json.get("file");
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Log in is successful: " + valid);

                if(valid) {
                    ToastMessage.displayShortToastMessage(context, "User was authenticated successfully");
                } else {
                    ToastMessage.displayLongToastMessage(context, "Server authentication failed");
                }
                //callback to launch UploadImageFragment upon the successful creation of new article
                //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
            }

            progressDialog.dismiss();

           /* //callback to launch UploadImageFragment upon the successful creation of new article
            ToastMessage.displayLongToastMessage(context, "Article successfully created");
            //disable selectFileButton and takeAPictureButton
            selectFileButton.setVisibility(View.GONE);
            takeAPicture.setVisibility(View.GONE);
            writeTagButton.setVisibility(View.VISIBLE);

            //remove the background from the image and extract three most common colors
            startColorExtractionService(imagePath);*/
        }
    }

    public class CreateProfileRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = CreateProfileRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public CreateProfileRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(createProfileRequestReceiver != null) {
                try {
                    context.unregisterReceiver(createProfileRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Create Profile Service response JSON: " + responseJSON);
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

}
