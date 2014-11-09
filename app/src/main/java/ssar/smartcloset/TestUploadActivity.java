package ssar.smartcloset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import ssar.smartcloset.CustomOnItemSelectedListener;


public class TestUploadActivity extends Activity {

    private Spinner articleTypeSelector;
    private Button createArticleButton;

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

                Toast.makeText(TestUploadActivity.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : " + String.valueOf(articleTypeSelector.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
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
        //EditText editText = (EditText) findViewById(R.id.viewStreamsButton);
        intent.putExtra("message", "Test Upload Activity Test");
        startActivity(intent);
    }
}
