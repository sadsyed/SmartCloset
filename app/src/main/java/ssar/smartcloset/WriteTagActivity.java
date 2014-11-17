package ssar.smartcloset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.nio.charset.Charset;

import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


public class WriteTagActivity extends Activity {
    private final static String CLASSNAME = WriteTagActivity.class.getSimpleName();
    private boolean writeMode;

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;

    private String articleUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        initializeNfcAdapter();

        beginWrite();
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

    /* OnNewIntent read the tag */
    @Override
    public void onNewIntent(Intent intent) {
        writeMode = false;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        writeTag(tag);
    }

    public void beginWrite() {
        writeMode = true;
        enableForegroundMode();
    }

    private void enableForegroundMode() {
        Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": enableForegroundMode...");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
