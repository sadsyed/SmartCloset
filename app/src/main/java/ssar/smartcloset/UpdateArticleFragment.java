package ssar.smartcloset;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.UpdateArticleFragment.OnUpdateArticleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateArticleFragment extends Fragment implements View.OnClickListener{
    private static final String CLASSNAME = UpdateArticleFragment.class.getSimpleName();

    // the fragment initialization parameters
    private static final String ARG_TOKEN_ID = "tokenId";
    private static final String ARG_ARTICLE = "article";

    private String tokenId;
    private Article article;

    private OnUpdateArticleFragmentInteractionListener onUpdateArticleFragmentInteractionListener;
    private SmartClosetRequestReceiver createArticleRequestReceiver;

    IntentFilter filter;

    EditText articleNameEditText;
    EditText articleDescriptionEditText;
    EditText articleTagsEditText;
    EditText articleColorsEditText;
    EditText articlePriceEditText;
    CheckBox articleOkToSellCheckbox;
    CheckBox articlePrivateCheckbox;
    private Spinner articleTypeSelector;

    Button submitChangesButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @return A new instance of fragment UpdateArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateArticleFragment newInstance(String tokenId, Article article) {
        UpdateArticleFragment fragment = new UpdateArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN_ID, tokenId);
        args.putParcelable(ARG_ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tokenId = getArguments().getString(ARG_TOKEN_ID);
            article = getArguments().getParcelable(ARG_ARTICLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_article, container, false);

        submitChangesButton = (Button) view.findViewById(R.id.submitChangesButton);

        articleNameEditText = (EditText) view.findViewById(R.id.articleNameEditText);
        articleNameEditText.setEnabled(false);
        articleDescriptionEditText = (EditText) view.findViewById(R.id.articleDescriptionEditText);
        articleTagsEditText = (EditText) view.findViewById(R.id.articleTagsEditText);
        articleColorsEditText = (EditText) view.findViewById(R.id.articleColorsEditText);
        articlePriceEditText = (EditText) view.findViewById(R.id.articlePriceEditText);
        articleOkToSellCheckbox = (CheckBox) view.findViewById(R.id.articleOkToSellCheckbox);
        articlePrivateCheckbox = (CheckBox) view.findViewById(R.id.articlePrivateCheckbox);
        articleTypeSelector = (Spinner) view.findViewById(R.id.articleTypeSelector);

        articleNameEditText.setText(article.getArticleName());
        articleDescriptionEditText.setText(article.getArticleDescription());
        articleTagsEditText.setText(article.getTagsStringValue());
        articleColorsEditText.setText(article.getColorsStringValue());
        articlePriceEditText.setText(article.getArticlePrice().toString());
        articleOkToSellCheckbox.setChecked(article.getArticleOkToSell());

        if(article.getArticlePrivate() != null) {
            articlePrivateCheckbox.setChecked(article.getArticlePrivate());
        }

        //get Category Array
        String[] stringArray = getResources().getStringArray(R.array.newtag_article_type_array);
        List<String> stringArrayList = new ArrayList<String> ();
        stringArrayList = Arrays.asList(stringArray);

        String currentSelection = article.getArticleType();
        articleTypeSelector.setSelection(stringArrayList.indexOf(currentSelection));

        submitChangesButton.setOnClickListener(this);

        //disable Next button


        return view;
    }

    @Override
    public void onClick(View v) {
        //get current user
        User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        createArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.CREATE_ARTICLE);
        getActivity().registerReceiver(createArticleRequestReceiver, filter);

        Article newArticle = new Article();

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("tokenId", tokenId);

            requestJSON.put("articleId", article.getArticleId());
            //newArticle.setArticleId(article.getArticleId());

            if(!article.getArticleDescription().equals(articleDescriptionEditText.getText().toString())){
                requestJSON.put("articleDescription", articleDescriptionEditText.getText().toString());
                //newArticle.setArticleDescription(articleDescriptionEditText.getText().toString());
            }

            if(!article.getArticleType().equals(String.valueOf(articleTypeSelector.getSelectedItem()))) {
                requestJSON.put("articleType", String.valueOf(articleTypeSelector.getSelectedItem()));
                //newArticle.setArticleType(String.valueOf((articleTypeSelector.getSelectedItem())));
            }

            if(!article.getTags().toString().equals(articleTagsEditText.getText().toString())) {
                requestJSON.put("articleTags", articleTagsEditText.getText().toString());
                //Create a list of
            }

            if(!article.getArticleColors().toString().equals(articleColorsEditText.getText().toString())) {
                Log.v(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": article colors: " + articleColorsEditText.getText().toString());
                //newArticle.setArticleColors(articleColorsEditText.getText().toString());
                //Log.v(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": new article colors: " + newArticle.getArticleColors());
                requestJSON.put("articleColors", articleColorsEditText.getText().toString());
            }

            if(!article.getArticlePrice().toString().equals(articlePriceEditText.getText().toString())) {
                requestJSON.put("articlePrice", articlePriceEditText.getText().toString());
            }

            if(!(article.getArticleOkToSell() == articleOkToSellCheckbox.isChecked())){
                String okToSellTemp = "false";
                if(articleOkToSellCheckbox.isChecked()) {
                    okToSellTemp = "true";
                }
                requestJSON.put("articleOkToSell", okToSellTemp);
            }

            if(!(article.getArticlePrivate() == articlePrivateCheckbox.isChecked())) {
                String privateTemp = "false";
                if(articlePrivateCheckbox.isChecked()) {
                    privateTemp = "true";
                }
                requestJSON.put("articlePrivate", privateTemp);
            }

            //always replace the whole list
            requestJSON.put("append", "false");
        } catch (Exception e) {
            Log.e(CLASSNAME, "Exception while creating an request JSON : " + e.getMessage());
        }
        Log.i(CLASSNAME, "Starting Update Article request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.UPDATE_ARTICLE);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        Log.i(CLASSNAME, "Finished Creating intent");
        getActivity().startService(msgIntent);
        Log.i(CLASSNAME, "Started intent service");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onUpdateArticleFragmentInteractionListener = (OnUpdateArticleFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUpdateArticleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUpdateArticleFragmentInteractionListener = null;
    }

    public class SmartClosetRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = SmartClosetRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public SmartClosetRequestReceiver (String serviceUrl) {
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
                    int errorcode = (int)json.get("errorcode");
                    if(errorcode == 0) {
                        ToastMessage.displayShortToastMessage(getActivity(), "Article was updated successfully");
                    }
                    //callback to launch UploadImageFragment upon the successful creation of new article
                    //onNewTagFragmentInteractionListener.onNewTagFragmentInteraction(currentUuid);
                } catch (Exception e) {
                    Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error reading the JSON return object");
                }
            } catch (JSONException e)
            {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception creating json object: " + e.getMessage());
            }
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
    public interface OnUpdateArticleFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUpdateArticleFragmentInteraction(Uri uri);
    }

}
