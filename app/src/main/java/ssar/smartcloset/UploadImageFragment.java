package ssar.smartcloset;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

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

    // the fragment initialization parameters
    public static final String ARG_ARTICLE_ID = "articleId";

    public TestUploadRequestReceiver testUploadRequestReceiver;
    IntentFilter filter;
    private String articleId;
    Uri selectedimg;
    String imagePath;
    String currentUuid="";

    private OnUploadImageFragmentInteractionListener onUploadImageFragmentInteractionListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_upload_image, container, false);
        //addListenerOnUpdateArticleButton(thisView);
        addListenerOnSelectFileForUploadButton(thisView);

        EditText editText = (EditText) thisView.findViewById(R.id.articleId);
        editText.setText(articleId);
        return thisView;
    }

    /*public void addListenerOnUpdateArticleButton(View view) {
        Button button = (Button) view.findViewById(R.id.updateArticleIdButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateArticleId(v);
            }
        });
    }*/

    public void addListenerOnSelectFileForUploadButton(View view) {
        Button button = (Button) view.findViewById(R.id.selectFileButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseImageFile(v);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==android.app.Activity.RESULT_CANCELED)
        {
            // action cancelled
        }
        if(resultCode==android.app.Activity.RESULT_OK) {
            selectedimg = data.getData();
            imagePath = SmartClosetFileService.getRealPathFromURI(selectedimg, getActivity());
            Log.i(CLASSNAME, "Selected img path is: " + imagePath);
            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            testUploadRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
            ((MainActivity)getActivity()).registerReceiver(testUploadRequestReceiver, filter);

            Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.UPLOAD_ARTICLE_IMAGE);
            msgIntent.putExtra("articleId", articleId);
            msgIntent.putExtra("ImagePath", imagePath);
            ((MainActivity)getActivity()).startService(msgIntent);
        }
    }

    public void updateArticleId(View view) {
        EditText articleIdEditText = (EditText) getView().findViewById(R.id.articleId);
        Log.i(CLASSNAME, "Article ID: " + currentUuid);
        if(articleIdEditText != null) {
            articleIdEditText.setText(currentUuid);
        }
    }

    public void chooseImageFile(View view) {
        //Select file
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentChooser, "Choose Picture"), 1);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String currentUuid) {
        if (onUploadImageFragmentInteractionListener != null) {
            onUploadImageFragmentInteractionListener.onUploadImageFragmentInteraction(currentUuid);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onUploadImageFragmentInteractionListener = (OnUploadImageFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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

            //callback to launch UploadImageFragment upon the successful creation of new article
            ToastMessage.displayLongToastMessage(context, "Article successfully created");
            onUploadImageFragmentInteractionListener.onUploadImageFragmentInteraction(articleId);
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
        public void onUploadImageFragmentInteraction(String currentUuid);
    }

}
