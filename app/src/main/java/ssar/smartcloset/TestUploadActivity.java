package ssar.smartcloset;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import ssar.smartcloset.CustomOnItemSelectedListener;
import ssar.smartcloset.util.SmartClosetConstants;


public class TestUploadActivity extends Activity {

    private Spinner articleTypeSelector;
    private Button createArticleButton;
    public TestUploadRequestReceiver createArticleRequestReceiver;
    public TestUploadRequestReceiver testUploadRequestReceiver;
    private static final String CLASSNAME = TestUploadActivity.class.getSimpleName();
    IntentFilter filter;
    Uri selectedimg;
    String imagePath;
    String currentUuid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void updateArticleId(View view) {
        EditText articleIdEditText = (EditText) findViewById(R.id.articleId);
        articleIdEditText.setText(currentUuid);
    }

    @Override
    public void onDestroy() {
        if(testUploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(testUploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(createArticleRequestReceiver != null) {
            try {
                this.unregisterReceiver(createArticleRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(testUploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(testUploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(createArticleRequestReceiver != null) {
            try {
                this.unregisterReceiver(createArticleRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(testUploadRequestReceiver != null) {
            this.registerReceiver(testUploadRequestReceiver, filter);
        }
        if(createArticleRequestReceiver != null) {
            this.registerReceiver(createArticleRequestReceiver, filter);
        }
        super.onResume();
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

                EditText articleNameEditText = (EditText) findViewById(R.id.articleNameEditText);
                EditText articleDescriptionEditText = (EditText) findViewById(R.id.articleDescriptionEditText);
                EditText articleTagsEditText = (EditText) findViewById(R.id.articleTagsEditText);
                EditText articlePriceEditText = (EditText) findViewById(R.id.articlePriceEditText);
                EditText articleOwnerEditText = (EditText) findViewById(R.id.articleOwnerEditText);
                CheckBox articleOkToSellCheckbox = (CheckBox) findViewById(R.id.articleOkToSellCheckbox);
                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                createArticleRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
                registerReceiver(createArticleRequestReceiver, filter);

                //set the JSON request object
                JSONObject requestJSON = new JSONObject();
                try {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_CANCELED)
        {
            // action cancelled
        }
        if(resultCode==RESULT_OK) {
            selectedimg = data.getData();
            imagePath = SmartClosetFileService.getRealPathFromURI(selectedimg, this);
            Log.i(CLASSNAME, "Selected img path is: " + imagePath);
            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            testUploadRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
            registerReceiver(testUploadRequestReceiver, filter);

            Intent msgIntent = new Intent(TestUploadActivity.this, SmartClosetIntentService.class);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
            EditText articleIdEditText = (EditText) findViewById(R.id.articleId);
            currentUuid = articleIdEditText.getText().toString();
            msgIntent.putExtra("articleId", currentUuid);
            msgIntent.putExtra("ImagePath", imagePath);
            startService(msgIntent);
        }
    }

    public void chooseImageFile(View view) {
        //Select file
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentChooser, "Choose Picture"), 1);
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
            if(testUploadRequestReceiver != null) {
                try {
                    context.unregisterReceiver(testUploadRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
                }
            }
            if(createArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(createArticleRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
                }
            }
            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(CLASSNAME, "Service response JSON: " + responseJSON);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(responseJSON);
                try {
                    currentUuid = (String) json.get("returnval");
                } catch (Exception e) {
                    Integer temp = (Integer)json.get("returnval");
                }
            } catch (JSONException e)
            {
                Log.i(CLASSNAME, "Exception creating json object: " + e.getMessage());
            }
        }
    }
}
