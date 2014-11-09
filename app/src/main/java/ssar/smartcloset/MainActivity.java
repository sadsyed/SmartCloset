package ssar.smartcloset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private static final String TAG = "SmartCloset";
    private static final String CLASSNAME = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;

    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.statusTextView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            statusTextView.setText("NFC is disabled.");
        } else {
            statusTextView.setText("NFC is enabled.");
        }

        handleIntent(getIntent());
    }

    private void enableForegroundMode() {
        Log.d(TAG, CLASSNAME + ": enableForegroundMode...");

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode...");
        nfcAdapter.disableForegroundDispatch(this);
    }

    /* OnNewIntent read the tag */
    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, CLASSNAME + ": onNewIntent...");
        setIntent(intent);
        readTag(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, CLASSNAME + ": handling event...");
        setIntent(intent);
        readTag(intent);
    }

    private void readTag(Intent intent) {
        Log.d(TAG, CLASSNAME + ": readTag...");
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

            EditText tagDataEditText = (EditText) findViewById(R.id.tagDataEditText);
            //tagDataEditText.setText("test string");
            tagDataEditText.setText(tagDataBuilder.toString());
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, CLASSNAME + ": onPause.... ");
        super.onPause();
        disableForegroundMode();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, CLASSNAME + ": on Resume.... ");
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
}
