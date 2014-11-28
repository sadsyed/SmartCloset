package ssar.smartcloset;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ssar.smartcloset.types.NavDrawerListAdapter;
import ssar.smartcloset.types.NavDrawerItem;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


public class MainActivity extends Activity implements
        ClosetFragment.OnCategorySelectedListener,
        CategoryFragment.OnViewFragmentInteractionListener,
        FragmentRouter.OnFragmentRouterInteractionListener,
        NewTagFragment.OnNewTagFragmentInteractionListener,
        SearchFragment.OnSearchFragmentInteractionListener,
        UploadImageFragment.OnUploadImageFragmentInteractionListener,
        WriteTagFragment.OnWriteTagFragmentInteractionListener {
    private static final String CLASSNAME = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    public SmartClosetRequestReceiver useArticleRequestReceiver;

    public boolean writeMode = false;
    public String articleUuid;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] navMenuTitles;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navDrawerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize NfcAdapter to start tag detection
        initializeNfcAdapter();
        handleNfcIntent(getIntent());

        //load slider menu items
        title = drawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));

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
                getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        if(savedInstanceState == null) {
            displayView(0);
        }

 /*       if (findViewById(R.id.main_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

        //display FragmentRouter
        FragmentManager fragmentManager = getFragmentManager();
        Fragment menuFragment = fragmentManager.findFragmentById(R.id.main_fragment_container);

        if (menuFragment == null) {
            menuFragment = new FragmentRouter();
            fragmentManager.beginTransaction().add(R.id.main_fragment_container, menuFragment).commit();
        }
*/
    }

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
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Home Fragment..... ");
                //display Category Fragment
                FragmentRouter fragmentRouter = new FragmentRouter();
                updateFragment(fragmentRouter, position);
                setFragmentTitle(position);
                break;
            case 1:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Category Fragment..... ");
                //display Category Fragment
                ClosetFragment closetFragment = new ClosetFragment();
                updateFragment(closetFragment, position);
                setFragmentTitle(position);
                break;
            case 2:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Search Fragment..... ");
                //display Search Fragment
                SearchFragment searchFragment = new SearchFragment();
                updateFragment(searchFragment, position);
                setFragmentTitle(position);
                break;
            case 3:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": New Tag Fragment..... ");
                //display New Tag Fragment
                NewTagFragment newTagFragment = new NewTagFragment();
                updateFragment(newTagFragment, position);
                setFragmentTitle(position);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(drawerList);
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

            NdefMessage[] ndefMessages =null;
            if (parcelables != null) {
                ndefMessages = new NdefMessage[parcelables.length];
                for (int i=0; i<parcelables.length; i++) {
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
            ToastMessage.displayLongToastMessage(this, tagDataBuilder.toString());

            String articleId = tagBody;

            //set the JSON request object
            JSONObject requestJSON = new JSONObject();
            try {
                requestJSON.put("articleId", articleId);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
            }
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME  + ": Starting Use Article request");
            Intent msgIntent = new Intent(this, SmartClosetIntentService.class);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.USE_ARTICLE);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
            this.startService(msgIntent);
            Log.i(CLASSNAME, "Started intent service");
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

    //--------------- FragmentInteraction Methods ---------------

    public void onFragmentRouterInteraction(View view) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": On Menu Tag Button CLicked.");
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, view.getId() + " was clicked.");
        //ToastMessage.displayShortToastMessage(this, "Wheeeee...");

        switch (view.getId()) {
            case R.id.closetButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Closet Fragment..... ");
                //display Category Fragment
                ClosetFragment closetFragment = new ClosetFragment();
                updateFragment(closetFragment, SmartClosetConstants.SLIDEMENU_CLOSET_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_CLOSET_ITEM);
                break;
            case R.id.searchButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Search Fragment..... ");
                //display Search Fragment
                SearchFragment searchFragment = new SearchFragment();
                updateFragment(searchFragment, SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_SEARCH_ITEM);
                break;
            case R.id.newTagButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": New Tag Fragment..... ");
                //display New Tag Fragment
                NewTagFragment newTagFragment = new NewTagFragment();
                updateFragment(newTagFragment, SmartClosetConstants.SLIDEMENU_NEWTAG_ITEM);
                setFragmentTitle(SmartClosetConstants.SLIDEMENU_NEWTAG_ITEM);
                break;
        }
    }

    public void onNewTagFragmentInteraction(String articleId) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onNewTagFragmentInteraction......");

        //launch UploadImageFragment for the given articleId
        UploadImageFragment uploadImageFragment = UploadImageFragment.newInstance(articleId);
        updateFragment(uploadImageFragment, SmartClosetConstants.SLIDEMENU_NEWTAG_ITEM);
    }

    public void onSearchFragmentInteraction(Uri uri) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onSearchFragmentInteraction......");
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

    public void onViewFragmentInteraction(Uri uri) {

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

    private void updateFragment(Fragment fragment, Integer position) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(position.toString());

        transaction.commit();
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
            if(useArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(useArticleRequestReceiver);

                    String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }
        }
    }
}
