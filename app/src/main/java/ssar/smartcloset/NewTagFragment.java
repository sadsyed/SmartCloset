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
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import ssar.smartcloset.types.User;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.NewTagFragment.OnNewTagFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class NewTagFragment extends Fragment {
/*    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    private OnNewTagFragmentInteractionListener onNewTagFragmentInteractionListener;
    private Spinner articleTypeSelector;
    private Button createArticleButton;
    public TestUploadRequestReceiver createArticleRequestReceiver;
    public TestUploadRequestReceiver testUploadRequestReceiver;
    private static final String CLASSNAME = NewTagFragment.class.getSimpleName();
    IntentFilter filter;
    String currentUuid="";

/*    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClosetFragment.
     */
/*    // TODO: Rename and change types and number of parameters
    public static ClosetFragment newInstance(String param1, String param2) {
        ClosetFragment fragment = new ClosetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    public NewTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_newtag, container, false);
        addListenerOnButton(thisView);
        addListenerOnSpinnerItemSelection(thisView);

        return thisView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String str) {
        if (onNewTagFragmentInteractionListener != null) {
            onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(str);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onNewTagFragmentInteractionListener = (OnNewTagFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }
        if(testUploadRequestReceiver != null) {
            ((MainActivity)getActivity()).registerReceiver(testUploadRequestReceiver, filter);
        }
        if(createArticleRequestReceiver != null) {
            ((MainActivity)getActivity()).registerReceiver(createArticleRequestReceiver, filter);
        }
    }

    public void addListenerOnSpinnerItemSelection(View view) {
        articleTypeSelector = (Spinner) view.findViewById(R.id.articleTypeSelector);
        articleTypeSelector.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton(View view) {

        articleTypeSelector = (Spinner) view.findViewById(R.id.articleTypeSelector);
        createArticleButton = (Button) view.findViewById(R.id.createArticleButton);

        createArticleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText articleNameEditText = (EditText) getView().findViewById(R.id.articleNameEditText);
                EditText articleDescriptionEditText = (EditText) getView().findViewById(R.id.articleDescriptionEditText);
                EditText articleTagsEditText = (EditText) getView().findViewById(R.id.articleTagsEditText);
                EditText articlePriceEditText = (EditText) getView().findViewById(R.id.articlePriceEditText);
                //EditText articleOwnerEditText = (EditText) getView().findViewById(R.id.articleOwnerEditText);
                CheckBox articleOkToSellCheckbox = (CheckBox) getView().findViewById(R.id.articleOkToSellCheckbox);
                CheckBox articlePrivateCheckbox = (CheckBox) getView().findViewById(R.id.articlePrivateCheckbox);

                //get current user
                User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

                filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                createArticleRequestReceiver = new TestUploadRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
                ((MainActivity)getActivity()).registerReceiver(createArticleRequestReceiver, filter);

                //set the JSON request object
                JSONObject requestJSON = new JSONObject();
                try {
                    requestJSON.put("articleName", articleNameEditText.getText().toString());
                    requestJSON.put("articleDescription", articleDescriptionEditText.getText().toString());
                    requestJSON.put("articleType", String.valueOf(articleTypeSelector.getSelectedItem()));
                    requestJSON.put("articleTags", articleTagsEditText.getText().toString());
                    requestJSON.put("articlePrice", articlePriceEditText.getText().toString());
                    String okToSellTemp = "false";
                    if(articleOkToSellCheckbox.isChecked()) {
                        okToSellTemp = "true";
                    }
                    requestJSON.put("articleOkToSell", okToSellTemp);
                    String privateTemp = "false";
                    if(articlePrivateCheckbox.isChecked()) {
                        privateTemp = "true";
                    }
                    requestJSON.put("articlePrivate", privateTemp);
                    requestJSON.put("articleOwner", loggedInUser.getUserEmail());
                } catch (Exception e) {
                    Log.e(CLASSNAME, "Exception while creating an request JSON.");
                }
                Log.i(CLASSNAME, "Starting Create Article request");
                Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.CREATE_ARTICLE);
                msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
                Log.i(CLASSNAME, "Finished Creating intent");
                ((MainActivity)getActivity()).startService(msgIntent);
                Log.i(CLASSNAME, "Started intent service");
            }
        });
    }

    @Override
    public void onDetach() {
        if(testUploadRequestReceiver != null) {
            try {
                ((MainActivity)getActivity()).unregisterReceiver(testUploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(createArticleRequestReceiver != null) {
            try {
                ((MainActivity)getActivity()).unregisterReceiver(createArticleRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(CLASSNAME, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDetach();
        onNewTagFragmentInteractionListener = null;
    }

    public class TestUploadRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = TestUploadRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public TestUploadRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
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
                    //callback to launch UploadImageFragment upon the successful creation of new article
                    onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
                } catch (Exception e) {
                    Integer temp = (Integer)json.get("returnval");
                }
            } catch (JSONException e)
            {
                Log.i(CLASSNAME, "Exception creating json object: " + e.getMessage());
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNewTagFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onNewTagFragmentInteraction(String articleId);
    }

}
