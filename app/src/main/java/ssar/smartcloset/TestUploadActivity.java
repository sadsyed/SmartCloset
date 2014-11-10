package ssar.smartcloset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ssar.smartcloset.CustomOnItemSelectedListener;
import ssar.smartcloset.util.SmartClosetConstants;


public class TestUploadActivity extends Activity {

    private Spinner articleTypeSelector;
    private Button createArticleButton;
    public TestUploadRequestReceiver testUploadRequestReceiver;
    private static final String CLASSNAME = TestUploadActivity.class.getSimpleName();
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection() {
        articleTypeSelector = (Spinner) findViewById(R.id.articleTypeSelector);
        articleTypeSelector.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        articleTypeSelector = (Spinner) findViewById(R.id.articleTypeSelector);
        createArticleButton = (Button) findViewById(R.id.createArticleButton);

        createArticleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*Toast.makeText(TestUploadActivity.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + String.valueOf(articleTypeSelector.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();*/
                EditText articleIdEditText = (EditText) findViewById(R.id.articleIdEditText);
                EditText articleNameEditText = (EditText) findViewById(R.id.articleNameEditText);
                EditText articleDescriptionEditText = (EditText) findViewById(R.id.articleDescriptionEditText);
                EditText articleTagsEditText = (EditText) findViewById(R.id.articleTagsEditText);
                EditText articlePriceEditText = (EditText) findViewById(R.id.articlePriceEditText);
                EditText articleOwnerEditText = (EditText) findViewById(R.id.articleOwnerEditText);
                CheckBox articleOkToSellCheckbox = (CheckBox) findViewById(R.id.articleOkToSellCheckbox);
                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                testUploadRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
                registerReceiver(testUploadRequestReceiver, filter);

                //set the JSON request object
                JSONObject requestJSON = new JSONObject();
                try {
                    requestJSON.put("articleId", articleIdEditText.getText().toString());
                    requestJSON.put("articleName", articleNameEditText.getText().toString());
                    requestJSON.put("articleDescription", articleDescriptionEditText.getText().toString());
                    requestJSON.put("articleType", String.valueOf(articleTypeSelector.getSelectedItem()));
                    requestJSON.put("articleTags", articleTagsEditText.getText().toString());
                    requestJSON.put("articlePrice", articlePriceEditText.getText().toString());
                    requestJSON.put("articleOkToSell", articleOkToSellCheckbox.getText().toString());
                    requestJSON.put("articleOwner", articleOwnerEditText.getText().toString());
                } catch (Exception e) {
                    Log.e(CLASSNAME, "Exception while creating an request JSON.");
                }
                Log.i(CLASSNAME, "Starting Create Article request");
                Intent msgIntent = new Intent(TestUploadActivity.this, SmartClosetIntentService.class);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.CREATE_ARTICLE);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
                Log.i(CLASSNAME, "Finished Creating intent");
                startService(msgIntent);
                Log.i(CLASSNAME, "Started intent service");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_upload, menu);
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

    public void testUpload(View view) {
        Intent intent = new Intent(this, TestUploadActivity.class);
        intent.putExtra("message", "Test Upload Activity Test");
        startActivity(intent);
    }

    public class TestUploadRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = TestUploadRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public TestUploadRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(CLASSNAME, "Service response JSON: " + responseJSON);
        }
    }
}
