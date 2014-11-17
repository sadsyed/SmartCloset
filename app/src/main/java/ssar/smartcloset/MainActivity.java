package ssar.smartcloset;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import java.nio.charset.Charset;

import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


public class MainActivity extends Activity implements
        CategoryFragment.OnCategorySelectedListener,
        FragmentRouter.OnFragmentRouterInteractionListener,
        NewTagFragment.OnNewTagFragmentInteractionListener,
        SearchFragment.OnSearchFragmentInteractionListener,
        UploadImageFragment.OnUploadImageFragmentInteractionListener,
        WriteTagFragment.OnWriteTagFragmentInteractionListener {
    private static final String CLASSNAME = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;

    public boolean writeMode = false;
    public String articleUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNfcAdapter();
        handleNfcIntent(getIntent());

        if (findViewById(R.id.main_fragment_container) != null) {
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

    }

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
            String body = new String(ndefMessages[0].getRecords()[0].getPayload());

            StringBuilder tagDataBuilder = new StringBuilder();
            tagDataBuilder.append("Tag Data: ").append(body);

            ToastMessage.displayLongToastMessage(this, tagDataBuilder.toString());
            //EditText tagDataEditText = (EditText) findViewById(R.id.tagDataEditText);
            //tagDataEditText.setText("test string");
            //tagDataEditText.setText(tagDataBuilder.toString());
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentRouterInteraction(View view) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": On Menu Tag Button CLicked.");
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, view.getId() + " was clicked.");
        //ToastMessage.displayShortToastMessage(this, "Wheeeee...");

        switch (view.getId()) {
            case R.id.closetButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Closet Fragment..... ");
                //display Closet Fragment
                CategoryFragment categoryFragment = new CategoryFragment();
                updateFragment(categoryFragment);
                break;
            case R.id.searchButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Search Fragment..... ");
                //display Search Fragment
                SearchFragment searchFragment = new SearchFragment();
                updateFragment(searchFragment);
                break;
            case R.id.newTagButton:
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": New Tag Fragment..... ");
                //display New Tag Fragment
                NewTagFragment newTagFragment = new NewTagFragment();
                updateFragment(newTagFragment);
                break;
        }
    }

    public void onNewTagFragmentInteraction(String articleId) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onNewTagFragmentInteraction......");

        //launch UploadImageFragment for the given articleId
        UploadImageFragment uploadImageFragment = UploadImageFragment.newInstance(articleId);
        updateFragment(uploadImageFragment);
    }

    public void onSearchFragmentInteraction(Uri uri) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onSearchFragmentInteraction......");
    }

    public void onCategorySelected(int position) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onCategorySelected.......");

        ViewFragment viewFragment = new ViewFragment();
        Bundle args = new Bundle();
        args.putInt(ViewFragment.ARG_POSITION, position);
        viewFragment.setArguments(args);

        updateFragment(viewFragment);
    }

    public void onUploadImageFragmentInteraction(String currentUuid) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onUploadImageFragmentInteraction.......");

        WriteTagFragment writeTagFragment = WriteTagFragment.newInstance(currentUuid);
        updateFragment(writeTagFragment);
    }

    public void onWriteTagFragmentInteraction(String articleId){
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": onWriteTagFragmentInteraction.......");
        //articleUuid = articleId;
        //beginWrite();
    }

    private void updateFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
