package ssar.smartcloset;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import ssar.smartcloset.util.SmartClosetConstants;

/**
 * Created by Amy on 11/9/2014.
 */
public class SmartClosetIntentService extends IntentService {

    /**
     * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
     * a service on a separate handler thread.
     * <p>
     * TODO: Customize class - update intent actions and extra parameters.
     */
private static final String CLASSNAME = SmartClosetIntentService.class.getSimpleName();

    public static final String REQUEST_URL = "requestURL";
    public static final String REQUEST_JSON = "requestJSON";
    public static final String RESPONSE_JSON = "responseJSON";

    public SmartClosetIntentService() {
        super("SmartClosetIntentService");
    }

@Override
    protected void onHandleIntent(Intent intent) {
        String requestURL = intent.getStringExtra(REQUEST_URL);
        String requestJSON = intent.getStringExtra(REQUEST_JSON);
        String responseJSON;

        //Create HttpClient and HttpPost objects to execute the POST request
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(requestURL);

        Log.i(CLASSNAME, "Handling intent");

        try {
            StringEntity stringEntity;
            //Set requestJSON for services which require request input
            switch(requestURL) {


                case SmartClosetConstants.CREATE_ARTICLE:
                    stringEntity = new StringEntity(requestJSON);
                    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(stringEntity);
                    break;

                case SmartClosetConstants.UPLOAD_ARTICLE_IMAGE:
                    post.addHeader("Accept", "application/json");
                    post.addHeader("Content-type", "multipart/form-data");
                    post.addHeader("Streamname", intent.getStringExtra("Streamname"));
                    post.addHeader("DroidLatitude", intent.getStringExtra("latitude"));
                    post.addHeader("DroidLongitude", intent.getStringExtra("longitude"));
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    String imagePath = intent.getStringExtra("ImagePath");
                    File imageFile = new File("");
                    if(SmartClosetFileService.isExternalStorageReadable()) {
                        try {
                            //File dataDir = ConnexusFileService.getDataStorageDir("Connexus");
                            Log.i(CLASSNAME, "Got data storage directory connexus");
                            imageFile = new File(imagePath);
                            if (!imageFile.exists()) {
                                imageFile.createNewFile();
                                Log.i(CLASSNAME, "Image test file did not exist.");
                            } else {
                                Log.i(CLASSNAME, "Image test file exists.");
                            }
                        } catch (IOException e) {
                            Log.d(CLASSNAME, "IO Exception writing to log file.");
                        }

                        try {
                            FileNameMap fileNameMap = URLConnection.getFileNameMap();
                            String mimeType = fileNameMap.getContentTypeFor(imageFile.getName());
                            Log.i(CLASSNAME, "The file MIME type is: " + mimeType);
                            String boundary = "-------------" + System.currentTimeMillis();
                            post.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
                            builder.setBoundary(boundary);
                            builder.addPart("imageFile", new FileBody(imageFile, ContentType.create(mimeType),"conpic.jpg"));
                            Log.i(CLASSNAME, "Created multi-part request and added file data.");
                        } catch (Exception e) {
                            Log.e(CLASSNAME, "Unsupported encoding for multipart entity.");
                        }
                        post.setEntity(builder.build());
                        Log.i(CLASSNAME, "Finished post.");
                    }
                    break;
            }
            //Execute the POST request
            HttpResponse response = client.execute(post);
            Log.i(CLASSNAME,"Finished post.");

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                 responseJSON = EntityUtils.toString(response.getEntity());
                Log.i(CLASSNAME, "Http Response: " + response.toString());
                Log.i(CLASSNAME, "Response JSON: " + responseJSON.toString());
            } else {
                Log.w(CLASSNAME, statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.w(CLASSNAME, e);
            responseJSON = e.getMessage();
        } catch (IOException e) {
            Log.w(CLASSNAME, e);
            responseJSON = e.getMessage();
        } catch (Exception e) {
            Log.w(CLASSNAME, e);
            responseJSON = e.getMessage();
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SmartClosetConstants.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_JSON, responseJSON);
        sendBroadcast(broadcastIntent);
    }
}

