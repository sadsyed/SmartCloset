package ssar.smartcloset;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.types.NavDrawerListAdapter;
import ssar.smartcloset.types.NavDrawerItem;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        FragmentRouter.OnFragmentRouterInteractionListener,
        ClosetFragment.OnCategorySelectedListener,
        CategoryFragment.OnCategoryFragmentInteractionListener,
        ArticleFragment.OnArticleFragmentInteractionListener,
        NewTagFragment.OnNewTagFragmentInteractionListener,
        SearchTabFragment.OnSearchTabFragmentInteractionListener,
        UploadImageFragment.OnUploadImageFragmentInteractionListener,
        WriteTagFragment.OnWriteTagFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener,
        BaseSearchFragment.OnBaseSearchFragmentInteractionListener,
        TagSearchFragment.OnTagSearchFragmentInteractionListener,
        SearchFragment.OnSearchFragmentInteractionListener,
        UsageFilterFragment.OnUsageFilterFragmentInteractionListener,
        NeverUsedFragment.OnNeverUsedFragmentInteractionListener,
        SellFilterFragment.OnSellFilterFragmentInteractionListener,
        UpdateArticleFragment.OnUpdateArticleFragmentInteractionListener,
        LoginFragment.OnLoginFragmentInteractionListener,
        MatchFragment.OnMatchFragmentInteractionListener,
        FindMatchFragment.OnFindMatchFragmentInteractionListener{
    private static final String CLASSNAME = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    public SmartClosetRequestReceiver useArticleRequestReceiver;
    public SmartClosetRequestReceiver readArticleRequestReceiver;
    public SmartClosetRequestReceiver matchArticleRequestReceiver;
    public CreateProfileRequestReceiver createProfileRequestReceiver;
    IntentFilter filter;

    public boolean writeMode = false;
    public boolean searchMode = false;
    public String articleUuid;
    private List<CustomListItem> articles;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] navMenuTitles;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navDrawerListAdapter;

    private SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "smartProfilePreference";

    public boolean matchMode = false;

    //-- Google Signin ---
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 0;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize NfcAdapter to start tag detection
        initializeNfcAdapter();
        handleNfcIntent(getIntent());

        //load slider menu items
        loadSliderMenu(savedInstanceState);

        //-- Google Signin ---
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    private void loadSliderMenu(Bundle savedInstanceState) {
        title = drawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7]));

        drawerList.setOnItemClickListener(new SlideMenuClickListener());

        navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        drawerList.setAdapter(navDrawerListAdapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_launcher,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                setTitle(title);
                //getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                setTitle(drawerTitle);
                //getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        /*if(savedInstanceState == null) {

            if(getExistingUser().getUserName() != null) {
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
                //display read tag message - mark isLoggedIn to true
                FragmentRouter fragmentRouter = new FragmentRouter().newInstance(true);
                updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);
            } else {
                //launch LogIn/Create Account page
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
                //display Category Fragment - mark isLoggenIn to false
                FragmentRouter fragmentRouter = new FragmentRouter().newInstance(false);
                updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);
            }

        }*/
    }

    //-- Google Signin ---
    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
        //ToastMessage.displayLongToastMessage(this, "Signed in UI - category view");
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
        //display read tag message - mark isLoggedIn to true
        FragmentRouter fragmentRouter = new FragmentRouter().newInstance(true);
        updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
        setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);

        //only works if user has a google+ profile
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            userName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Logged in as: " + userName);
            ToastMessage.displayLongToastMessage(this, "Logged in as : " + userName);
        } else {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": User doesn't have a google plus profile =(...");
        }

        userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": user email address is : " + userEmail);

        new GetIdTokenTask(this).execute();

        if(getExistingUser().getUserName() == null) {
            //Create user profile with Backend Server
            createUserProfile();
        }
    }

    private void createUserProfile() {
        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        createProfileRequestReceiver = new CreateProfileRequestReceiver(SmartClosetConstants.CREATE_PROFILE);
        this.registerReceiver(createProfileRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("username", userName);
            requestJSON.put("email", userEmail);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Exception while creating an request JSON.");
        }
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Starting Create Profile request");
        Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.CREATE_PROFILE);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Finished Creating intent");
        this.startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + " :Started intent service");

        //TODO: move to the boradcast receiever
        //add profile to apps preference
        sharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();

        e.putString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, userName);
        e.putString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, userEmail);
        e.commit();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }*/
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically attemot to resolve any errors that occur
        mShouldResolve = true;
        mGoogleApiClient.connect();

        //Show a message to the user that we are signing in
        ToastMessage.displayLongToastMessage(this, "Signing in =D");
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
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
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
            //display Category Fragment - mark isLoggenIn to false
            FragmentRouter fragmentRouter = new FragmentRouter().newInstance(false);
            updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
            setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);
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

    //-- Slider Menu ---
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    @Override
    protected void onPause() {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onPause.... ");
        super.onPause();
        disableForegroundMode();
    }

    @Override
    protected void onResume() {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": on Resume.... ");
        super.onResume();
        enableForegroundMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        int t = getFragmentManager().getBackStackEntryCount();

        if(t==1) {
            finish();
        }

        if(t>1) {
            int tr = Integer.parseInt(getFragmentManager().getBackStackEntryAt(t-2).getName());
            setTitle(navMenuTitles[tr]);
            super.onBackPressed();
       }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
                    //display Category Fragment
                    FragmentRouter fragmentRouter = new FragmentRouter().newInstance(true);
                    updateFragment(fragmentRouter, position);
                    setFragmentTitle(position);
                }
                break;
            case 1:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Closet Fragment..... ");
                    //display Category Fragment
                    ClosetFragment closetFragment = new ClosetFragment().newInstance(tokenId);
                    updateFragment(closetFragment, position);
                    setFragmentTitle(position);
                }
                break;
            case 2:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Search Fragment..... ");
                    //display Search Fragment
                    SearchTabFragment searchTabFragment = new SearchTabFragment();
                    updateFragment(searchTabFragment, position);
                    setFragmentTitle(position);
                }
                break;
            case 3:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": New Tag Fragment..... ");
                    //display New Tag Fragment
                    NewTagFragment newTagFragment = new NewTagFragment();
                    updateFragment(newTagFragment, position);
                    setFragmentTitle(position);
                }
                break;
            case 4:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Profile Fragment..... ");
                    //display Profile Fragment
                    ProfileFragment profileFragmenet = new ProfileFragment();
                    updateFragment(profileFragmenet, position);
                    setFragmentTitle(position);
                }
                break;
            case 5:
                if(isUserLoggedIn()) {
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Match Fragment..... ");
                    //display Match Fragment
                    MatchFragment matchFragment = MatchFragment.newInstance(null, false);
                    updateFragment(matchFragment, position);
                    setFragmentTitle(position);
                }
                break;
            case 6:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Log out..... ");
                if(mGoogleApiClient.isConnected()) {
                    //start the progress dialog
                    progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging out...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //log out currently logged user
                                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error while loggin out");
                            }
                            //dismiss the progress dialog
                            progressDialog.dismiss();
                        }
                    }).start();

                    //launch LogIn/Create Account page
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
                    //display Category Fragment - mark isLoggenIn to false
                    FragmentRouter fragmentRouter = new FragmentRouter().newInstance(false);
                    updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
                    setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);
                }
            default:
                break;
        }

        drawerLayout.closeDrawer(drawerList);
    }

    private boolean isUserLoggedIn() {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": isUserLoggedIn: " + getExistingUser().getUserName());
        if(getExistingUser().getUserName() == null) {
            ToastMessage.displayLongToastMessage(this, "Please sign in or create a new account");
            /*// launch log/in Create Account page
            ToastMessage.displayLongToastMessage(this, "Please sign in or create a new account");
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
            //display Category Fragment
            FragmentRouter fragmentRouter = new FragmentRouter().newInstance(false);
            updateFragment(fragmentRouter, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
            setFragmentTitle(SmartClosetConstants.SLIDEMENU_HOME_ITEM);*/
            return false;
        }
        return true;
    }

    private void setFragmentTitle(int position) {
        drawerList.setItemChecked(position, true);
        drawerList.setSelection(position);
        setTitle(navMenuTitles[position]);
    }
    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle saveInstanceState) {
        super.onPostCreate(saveInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        actionBarDrawerToggle.onConfigurationChanged(configuration);
    }

    //--------------- NFC Processing Methods ---------------

    private void initializeNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ((Object) this).getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcAdapter == null) {
            ToastMessage.displayShortToastMessage(this,  "NFC is not supported on this device.");
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            ToastMessage.displayShortToastMessage(this,  "NFC is disabled.");
        } else {
            ToastMessage.displayShortToastMessage(this,  "NFC is enabled.");
        }
    }

    public void enableForegroundMode() {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": enableForegroundMode...");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableForegroundMode() {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "disableForegroundMode...");
        nfcAdapter.disableForegroundDispatch(this);
    }

    /* OnNewIntent read the tag */
    @Override
    public void onNewIntent(Intent intent) {
        if(writeMode) {
            writeMode = false;
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag);
        } else {
            Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onNewIntent...");
            setIntent(intent);
            readTag(intent);
        }
    }

    private void handleNfcIntent(Intent intent) {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": handling event...");
        setIntent(intent);
        readTag(intent);
    }

    public void beginWrite() {
        writeMode = true;
        enableForegroundMode();
    }

    private void readTag(Intent intent) {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": readTag...");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage[] ndefMessages = null;
            if (parcelables != null) {
                ndefMessages = new NdefMessage[parcelables.length];
                for (int i = 0; i < parcelables.length; i++) {
                    ndefMessages[i] = (NdefMessage) parcelables[i];
                }
            }
            if (ndefMessages == null || ndefMessages.length == 0) {
                return;
            }

            String tagId = new String(ndefMessages[0].getRecords()[0].getType());
            String tagBody = new String(ndefMessages[0].getRecords()[0].getPayload());

            StringBuilder tagDataBuilder = new StringBuilder();
            tagDataBuilder.append("Tag Data: ").append(tagBody);
            //ToastMessage.displayLongToastMessage(this, tagDataBuilder.toString());

            String articleId = tagBody;

            //set the JSON request object
            JSONObject requestJSON = new JSONObject();
            try {
                requestJSON.put("articleId", articleId);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
            }

            if (matchMode) {
                matchMode = false;
                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                matchArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.READ_ARTICLE);
                this.registerReceiver(matchArticleRequestReceiver, filter);

                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": ** Starting Match Article request");
                Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.READ_ARTICLE);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
                this.startService(msgIntent);
                Log.i(CLASSNAME, "Started intent service");
            }
            //search details for tagged item
            else if (searchMode) {
                searchMode = false;
                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                readArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.READ_ARTICLE);
                this.registerReceiver(readArticleRequestReceiver, filter);

                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting Read Article request");
                Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.READ_ARTICLE);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
                this.startService(msgIntent);
                Log.i(CLASSNAME, "Started intent service");
            }
            //if searchMode is not active, mark the usage
            else {
                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                useArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.USE_ARTICLE);
                this.registerReceiver(useArticleRequestReceiver, filter);

                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting Use Article request");
                Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.USE_ARTICLE);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
                this.startService(msgIntent);
                Log.i(CLASSNAME, "Started intent service");
            }
        }
    }

    private boolean writeTag(Tag tag) {
        byte[] payload = articleUuid.getBytes();
        byte[] mimeBytes = "text/plain".getBytes(Charset.forName("US-ASCII"));

        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { ndefRecord });

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if(!ndef.isWritable()) {
                    ToastMessage.displayShortToastMessage(this, "This is a read-only tag");
                    return false;
                }

                int size = ndefMessage.toByteArray().length;
                if (ndef.getMaxSize() < size ) {
                    ToastMessage.displayShortToastMessage(this, "There is not enough space to write.");
                    return false;
                }

                ndef.writeNdefMessage(ndefMessage);

                ToastMessage.displayShortToastMessage(this, "Write successful.");
                launchArticleFragment();

                return true;
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    try {
                        ndefFormatable.connect();
                        ndefFormatable.format(ndefMessage);
                        ToastMessage.displayShortToastMessage(this, "Write successful\nLaunch a scanning app or scan and choose to read.");
                        return true;
                    } catch (Exception e){
                        ToastMessage.displayShortToastMessage(this, "Unable to ndefFormatable tag to NDEF");
                        return false;
                    }
                } else {
                    ToastMessage.displayShortToastMessage(this, "Tag doesn't appear to support NDEF ndefFormatable.");
                    return false;
                }
            }
        } catch (Exception e) {
            ToastMessage.displayShortToastMessage(this, "Write failed.");
        }
        return false;
    }

    private void launchArticleFragment() {

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("articleId", articleUuid);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        readArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.READ_ARTICLE);
        this.registerReceiver(readArticleRequestReceiver, filter);

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting Read Article request");
        Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.READ_ARTICLE);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        this.startService(msgIntent);
        Log.i(CLASSNAME, "Started intent service");
    }

    //--------------- FragmentInteraction Methods ---------------

    public void onFragmentRouterInteraction(View view) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": On Menu Tag Button CLicked.");
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, view.getId() + " was clicked.");
        //ToastMessage.displayShortToastMessage(this, "Wheeeee...");

        switch (view.getId()) {
            case R.id.createAccountButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Profile Fragment..... ");
                //launch profile fragment
                ProfileFragment profileFragment = new ProfileFragment();
                updateFragment(profileFragment, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_PROFILE_ITEM);
                break;
            case R.id.logInButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": LogIn Fragment.....");
                //launch login fragment
                LoginFragment loginFragment = new LoginFragment();
                updateFragment(loginFragment, SmartClosetConstants.SLIDEMENU_PROFILE_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_PROFILE_ITEM);
                break;
            case R.id.googleSignInButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Sign in with Google....");
                onSignInClicked();
                break;
        }
    }

    public void onNewTagFragmentInteraction(String articleId) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onNewTagFragmentInteraction......");

        //launch UploadImageFragment for the given articleId
        UploadImageFragment uploadImageFragment = UploadImageFragment.newInstance(articleId);
        updateFragment(uploadImageFragment, SmartClosetConstants.SLIDEMENU_NEWTAG_ITEM);
    }

    public void onSearchTagFragmentInteraction(Uri uri) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onSearchTagFragmentInteraction......");
    }

    public void onCategorySelected(String categorySelected) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onCategorySelected.......");

        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(CategoryFragment.ARG_CATEGORY_SELECTED, categorySelected);
        categoryFragment.setArguments(args);

        updateFragment(categoryFragment, SmartClosetConstants.SLIDEMENU_CLOSET_ITEM);
        setTitle(categorySelected);
    }

    public void onCategoryFragmentInteraction(Article articleSelected) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onCategoryFragmentInteraction..... ");

        //launch article view for the selected article
        ArticleFragment articleFragment = new ArticleFragment().newInstance(articleSelected);

        updateFragment(articleFragment, SmartClosetConstants.SLIDEMENU_ARTICLE_ITEM);
        setTitle(articleSelected.getArticleType());
    }

    public void onArticleFragmentInteraction(Article article) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onUpdateArticleFragment..... ");

        //launch UpdateArticleFragment for the selected article
        UpdateArticleFragment updateArticleFragment = UpdateArticleFragment.newInstance(article);

        updateFragment(updateArticleFragment, SmartClosetConstants.SLIDEMENU_ARTICLE_ITEM);
        setTitle(article.getArticleName());
    }

    public void onUpdateArticleFragmentInteraction(Uri uri) {
    }

    public void onUploadImageFragmentInteraction(String currentUuid) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onUploadImageFragmentInteraction.......");

        WriteTagFragment writeTagFragment = WriteTagFragment.newInstance(currentUuid);
        updateFragment(writeTagFragment, SmartClosetConstants.SLIDEMENU_NEWTAG_ITEM);
    }

    public void onWriteTagFragmentInteraction(String articleId){
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onWriteTagFragmentInteraction.......");
        //articleUuid = articleId;
        //beginWrite();
    }

    public void onProfileFragmentInteraction() {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onProfileFragmentInteraction......");

        //launch profile fragment
        ProfileFragment profileFragment = new ProfileFragment();
        updateFragment(profileFragment, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
        setFragmentTitle(SmartClosetConstants.SLIDEMENU_PROFILE_ITEM);
        ToastMessage.displayShortToastMessage(this, "Profile created successfully.");
    }

    public void onBaseSearchFragmentInteraction(String searchType, String searchValue, String email){
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": OnBaseSearchFragmentInteraction......");

        //launch search fragment to invoke search and get results
        launhSearchFragment(searchType, searchValue, email);
    }

    public void onTagSearchFragmentInteraction(Uri uri){
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onTagSearchFragmentInteraction......");
    }

    public void onSearchFragmentInteraction(Article articleSelected) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onSearchFragmentInteraction......");

        //re-launch SearchTabFragment
        if(articleSelected == null) {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Search Fragment..... ");
            //display Search Fragment
            SearchTabFragment searchTabFragment = new SearchTabFragment();
            updateFragment(searchTabFragment, SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);
            setFragmentTitle(SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);

        } else {
            //launch article view for the selected article
            ArticleFragment articleFragment = new ArticleFragment().newInstance(articleSelected);

            updateFragment(articleFragment, SmartClosetConstants.SLIDEMENU_ARTICLE_ITEM);
            setTitle(articleSelected.getArticleType());
        }
    }

    public void onUsageFilterFragmentInteraction(String searchType, String searchValue, String email) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onUsageFilterFragmentInteraction......");

        //launch search fragment to invoke search and get results
        launhSearchFragment(searchType, searchValue, email);
    }

    public void onNeverUsedFragmentInteraction(String searchType, String searchValue, String email) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onNeverUsedFragmentInteraction......");

        //launch search fragment to invoke search and get results
        launhSearchFragment(searchType, searchValue, email);
    }

    public void onSellFilterFragmentInteraction(String searchType, String searchValue, String email) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onSellFilterFragmentInteraction......");
        launhSearchFragment(searchType, searchValue, email);
    }

    public void onLoginFragmentInteraction() {
        //launch profile fragment
        ProfileFragment profileFragment = new ProfileFragment();
        updateFragment(profileFragment, SmartClosetConstants.SLIDEMENU_HOME_ITEM);
        setFragmentTitle(SmartClosetConstants.SLIDEMENU_PROFILE_ITEM);
        ToastMessage.displayShortToastMessage(this, "User Logged in successfully.");
    }

    private void launhSearchFragment(String searchType, String searchValue, String email) {
        //launch search fragment to invoke search and get results
        SearchFragment searchFragment = new SearchFragment().newInstance(searchType, searchValue, email);
        updateFragment(searchFragment, SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);
        //ToastMessage.displayLongToastMessage(this, "Search Type: " + searchType + ", Search Value: " + searchValue + ", Email: " + email);
    }

    public void onMatchFragmentInteraction(Article article, String category) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onMatchFragmentInteraction......");
        // launch findMatchFragment to find match for selected article
        FindMatchFragment findMatchFragment = FindMatchFragment.newInstance(article.getArticleId(), category);
        updateFragment(findMatchFragment, SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);
    }

    public void onFindMatchFragmentInteraction(Article articleSelected) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onFindMatchFragmentInteraction......");

        //re-launch SearchTabFragment
        if(articleSelected == null) {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Match Fragment..... ");
            //display Match Fragment
            MatchFragment matchFragment = MatchFragment.newInstance(null, false);
            updateFragment(matchFragment, SmartClosetConstants.SLIDEMENU_MATCH_ITEM);
            setFragmentTitle(SmartClosetConstants.SLIDEMENU_MATCH_ITEM);
        } else {
            //launch article view for the selected article
            ArticleFragment articleFragment = new ArticleFragment().newInstance(articleSelected);

            updateFragment(articleFragment, SmartClosetConstants.SLIDEMENU_ARTICLE_ITEM);
            setTitle(articleSelected.getArticleType());
        }
    }

    private void updateFragment(Fragment fragment, Integer position) {
       FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment_container, fragment);

        if(position != null)
            transaction.addToBackStack(position.toString());

        transaction.commit();
    }

    //--------------- Helper Methods --------------
    public User getExistingUser() {
        sharedPreferences = getSharedPreferences(SmartClosetConstants.PREF_NAME, 0);

        User existingUser = new User();
        existingUser.setUserName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_USER_NAME, null));
        existingUser.setFirstName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_FIRST_NAME, null));
        existingUser.setLastName(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_LAST_NAME, null));
        existingUser.setUserEmail(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_EMAIL, null));
        existingUser.setUserPin(sharedPreferences.getString(SmartClosetConstants.SHAREDPREFERENCE_PASSWORD, null));

        return existingUser;
    }

    //--------------- RequestReceiver ---------------

    public class SmartClosetRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = SmartClosetRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public SmartClosetRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(serviceUrl.equals(SmartClosetConstants.USE_ARTICLE) && useArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(useArticleRequestReceiver);

                    String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

                    JSONObject json = new JSONObject();
                    try {
                        json = new JSONObject(responseJSON);
                        try {
                            int errorcode = (int)json.get("errorcode");
                            if(errorcode == 0) {
                                ToastMessage.displayShortToastMessage(context, "Usage marked");
                            }
                            //callback to launch UploadImageFragment upon the successful creation of new article
                            //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
                        } catch (Exception e) {
                            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
                        }
                    } catch (JSONException e)
                    {
                        Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
                    }
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }
            else if(serviceUrl.equals(SmartClosetConstants.READ_ARTICLE) && readArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(readArticleRequestReceiver);

                    String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

                    // get list of articles in the selected category
                    articles = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);

                    //launch article fragment for searched article
                    Article article = (Article) articles.get(0);
                    ArticleFragment articleFragment = new ArticleFragment().newInstance(article);

                    updateFragment(articleFragment, SmartClosetConstants.SLIDEMENU_ARTICLE_ITEM);
                    setTitle(article.getArticleType());
                }
                catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }
            else if(serviceUrl.equals(SmartClosetConstants.READ_ARTICLE) && matchArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(matchArticleRequestReceiver);

                    String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

                    // get list of articles in the selected category
                    articles = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);

                    //launch match filter fragment for searched article
                    Article article = (Article) articles.get(0);

                    MatchFragment matchFragment = MatchFragment.newInstance(article, true);
                    updateFragment(matchFragment, SmartClosetConstants.SLIDEMENU_MATCH_ITEM);
                    setTitle("Match");
                }
                catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }
        }
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
                return GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
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

                authenticateWithBackendServer();
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


}


