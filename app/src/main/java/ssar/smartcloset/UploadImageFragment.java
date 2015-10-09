package ssar.smartcloset;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.UploadImageFragment.OnUploadImageFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadImageFragment extends Fragment {
    private static final String CLASSNAME = UploadImageFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE = 10;

    // the fragment initialization parameters
    public static final String ARG_ARTICLE_ID = "articleId";

    public TestUploadRequestReceiver testUploadRequestReceiver;
    public ColorExtractionRequestReceiver colorExtractionRequestReceiver;
    public UpdateImageColorsRequestReceiver updateImageColorsRequestReceiver;

    IntentFilter filter;
    private String articleId;
    Uri selectedimg;
    String imagePath;
    //String imageColors;
    JSONArray imageColors;
    String currentUuid="";
    String imageUrl;

    private OnUploadImageFragmentInteractionListener onUploadImageFragmentInteractionListener;
    private ProgressDialog progressDialog;

    private Button selectFileButton;
    private Button takeAPicture;
    private Button writeTagButton;
    private TextView selectImageTextView;
    private ImageView selectedImageView;
    Uri destination;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articleId Parameter 1.
     * @return A new instance of fragment UploadImageFragment.
     */
    public static UploadImageFragment newInstance(String articleId) {
        UploadImageFragment fragment = new UploadImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);;
        fragment.setArguments(args);
        return fragment;
    }

    public UploadImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_ARTICLE_ID);
        }

        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_image, container, false);

        selectFileButton = (Button) view.findViewById(R.id.selectFileButton);
        takeAPicture = (Button) view.findViewById(R.id.takeAPicture);
        selectedImageView = (ImageView) view.findViewById(R.id.selectedImageView);
        selectImageTextView = (TextView) view.findViewById(R.id.selectImageTextView);

        writeTagButton = (Button) view.findViewById(R.id.writeTagButton);
        writeTagButton.setVisibility(View.GONE);

        selectImageTextView.setVisibility(View.VISIBLE);

        //add button action listeners
        takeAPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                takeAPicture(v);
            }
        });
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFile(v);
            }
        });
        writeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWriteTag();
            }
        });

        return view;
    }

    public void chooseImageFile(View view) {
        //Select file
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentChooser, "Choose Picture"), 1);
    }

    public void takeAPicture(View view) {
        //launch camera to capture picture
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    destination = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE);
                }
            }
        } catch (ActivityNotFoundException e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e.getMessage());
        }
    }

    public void launchWriteTag() {
        onUploadImageFragmentInteractionListener.onUploadImageFragmentInteraction(articleId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==android.app.Activity.RESULT_CANCELED)
        {
            // action cancelled
        }
        if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Camera request activity");
            try {
                imagePath = SmartClosetFileService.getRealPathFromURI(destination, getActivity());

                selectImageTextView.setVisibility(View.GONE);
                //display the image
                displayImage(imagePath);

                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Captured img path is: " + imagePath);
                startUploadIntentService(articleId, imagePath);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e.getMessage());
            }
        }
        else if(resultCode==android.app.Activity.RESULT_OK) {
            selectedimg = data.getData();
            imagePath = SmartClosetFileService.getRealPathFromURI(selectedimg, getActivity());

            selectImageTextView.setVisibility(View.GONE);
            //display the image
            displayImage(imagePath);

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ":Selected img path is: " + imagePath);
            startUploadIntentService(articleId, imagePath);
        }
    }

    private void displayImage (String imagePath) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": displaying the image.");

        File imageFile = new File("");
        imageFile = new File(imagePath);
        try {
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Got data storage directory connexus");
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                imageFile.createNewFile();
                Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Image test file did not exist.");
            } else {
                //display the image
                try {
                    FileInputStream fileInputStream = new FileInputStream(imagePath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    Bitmap userImage = BitmapFactory.decodeStream(fileInputStream, null, options);
                    selectedImageView.setImageBitmap(userImage);
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Image test file exists.");
                } catch (FileNotFoundException e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": FileNotFoundException while displaying image : " + e );

                }
            }
        } catch (IOException e) {
            Log.d(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": IO Exception writing to log file.");
        }
    }

    private void startUploadIntentService(String articleId, String imagePath) {
        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        testUploadRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
        ((MainActivity)getActivity()).registerReceiver(testUploadRequestReceiver, filter);

        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
        msgIntent.putExtra("articleId", articleId);
        msgIntent.putExtra("ImagePath", imagePath);
        getActivity().startService(msgIntent);

        progressDialog.setMessage("Uploading article image...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void startColorExtractionService(String imagePath) {
        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        colorExtractionRequestReceiver = new ColorExtractionRequestReceiver(SmartClosetConstants.EXTRACT_ARTICLE_IMAGE_COLORS);
        ((MainActivity)getActivity()).registerReceiver(colorExtractionRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("imageurl", imagePath);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting removeBackground request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.EXTRACT_ARTICLE_IMAGE_COLORS);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        //msgIntent.putExtra("articleId", articleId);
        //msgIntent.putExtra("ImagePath", imagePath);
        getActivity().startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME +  ": Started intent service");


        progressDialog.setMessage("Removing backgorund =| ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void startUpdateImageColorsService(JSONArray imageColors) {
        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        updateImageColorsRequestReceiver = new UpdateImageColorsRequestReceiver(SmartClosetConstants.UPDATE_ARTICLE_IMAGE_COLORS);
        ((MainActivity)getActivity()).registerReceiver(updateImageColorsRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("articleId", articleId);
            requestJSON.put("imageColors", imageColors);
            //requestJSON.put("imageColors", "red");
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": requestJson for UpdateArticleImageColors: " + requestJSON.toString());
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting UpdateArticleImageColors request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.UPDATE_ARTICLE_IMAGE_COLORS);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        getActivity().startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME +  ": Started intent service");

        progressDialog.setMessage("Updating article with image colors ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private File createImageFile() throws IOException {
        String mCurrentPhotoPath;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onUploadImageFragmentInteractionListener = (OnUploadImageFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUploadImageFragmentInteractionListener = null;
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

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(CLASSNAME, "Image Upload Service response JSON: " + responseJSON);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(responseJSON);
                try {
                    imagePath = (String)json.get("file");
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Image URL: " + imagePath);

                    /*if(errorcode == 0) {
                        ToastMessage.displayShortToastMessage(getActivity(), "Article was updated successfully");
                    }*/
                    //callback to launch UploadImageFragment upon the successful creation of new article
                    //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
                } catch (Exception e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
                }
            } catch (JSONException e)
            {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
            }

            progressDialog.dismiss();

            //callback to launch UploadImageFragment upon the successful creation of new article
            ToastMessage.displayLongToastMessage(context, "Article successfully created");
            //disable selectFileButton and takeAPictureButton
            selectFileButton.setVisibility(View.GONE);
            takeAPicture.setVisibility(View.GONE);
            writeTagButton.setVisibility(View.VISIBLE);

            //remove the background from the image and extract three most common colors
            startColorExtractionService(imagePath);
        }
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
    public interface OnUploadImageFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUploadImageFragmentInteraction(String articleId);
    }


    private class ColorExtractionRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = ColorExtractionRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public ColorExtractionRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(colorExtractionRequestReceiver != null) {
                try {
                    context.unregisterReceiver(colorExtractionRequestReceiver);

                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Amazon Color Extraction response JSON: " + responseJSON);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(responseJSON);
                try {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = json.getJSONArray("colors");
                    imageColors = jsonArray;
                    //imageColors = (String)json.get("colors");
                    /*Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Image hexcolors: " + imageColors);

                    List<String> colors = JsonParserUtil.jsonToStringList(responseJSON);

                    StringBuilder colorStringBuilder = new StringBuilder();

                    for (String color : colors) {
                        colorStringBuilder.append(color).append(",");
                    }
                    imageColors = colorStringBuilder.toString();*/
                    //imageColors = colors.toString();

                    /*if(errorcode == 0) {
                        ToastMessage.displayShortToastMessage(getActivity(), "Article was updated successfully");
                    }*/
                    //callback to launch UploadImageFragment upon the successful creation of new article
                    //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
                } catch (Exception e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object: " + e.getMessage());
                }
            } catch (JSONException e)
            {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
            }

            progressDialog.dismiss();

            //disable selectFileButton and takeAPictureButton
            selectFileButton.setVisibility(View.GONE);
            takeAPicture.setVisibility(View.GONE);
            writeTagButton.setVisibility(View.VISIBLE);

            //callback to launch UploadImageFragment upon the successful creation of new article
            ToastMessage.displayLongToastMessage(context, "Background removed successfully =D");

            //update the article with extracted colors
            startUpdateImageColorsService(imageColors);
        }
    }

    private class UpdateImageColorsRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = UpdateImageColorsRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public UpdateImageColorsRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(updateImageColorsRequestReceiver != null) {
                try {
                    context.unregisterReceiver(updateImageColorsRequestReceiver);

                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Updated article with image colors: " + responseJSON);
            JSONObject json = new JSONObject();
            /*try {
                json = new JSONObject(responseJSON);
                try {
                    imageColors = (String)json.get("colors");
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Image hexcolors: " + imageColors);

                    /*if(errorcode == 0) {
                        ToastMessage.displayShortToastMessage(getActivity(), "Article was updated successfully");
                    }*/
                    //callback to launch UploadImageFragment upon the successful creation of new article
                    //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
            /*    } catch (Exception e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
                }
            } catch (JSONException e)
            {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
            }*/

            progressDialog.dismiss();

            //disable selectFileButton and takeAPictureButton
            selectFileButton.setVisibility(View.GONE);
            takeAPicture.setVisibility(View.GONE);
            writeTagButton.setVisibility(View.VISIBLE);

            //callback to launch UploadImageFragment upon the successful creation of new article
            ToastMessage.displayLongToastMessage(context, "Article colors update successfully =D");
        }
    }
}
