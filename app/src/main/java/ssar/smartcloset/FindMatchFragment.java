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
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONObject;

import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.CustomGridAdapter;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.FindMatchFragment.OnFindMatchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindMatchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String CLASSNAME = FindMatchFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ARTICLE_ID = "articleId";
    private static final String ARG_CATEGORY = "category";

    private String articleId;
    private String category;
    private List<CustomListItem> articles;

    private SmartClosetRequestReceiver findMatchRequestReceiver;
    IntentFilter filter;
    private OnFindMatchFragmentInteractionListener onFindMatchFragmentInteractionListener;

    /**
     * The fragment's GridView
     */
    private GridView gridView;

    /**
     * The Adapter which will be used to populate the GridView with Views
     */
    private CustomGridAdapter customListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articleId Parameter 1.
     * @param category Parameter 2.
     * @return A new instance of fragment FindMatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindMatchFragment newInstance(String articleId, String category) {
        FindMatchFragment fragment = new FindMatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public FindMatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_ARTICLE_ID);
            category = getArguments().getString(ARG_CATEGORY);
        }
        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        findMatchRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.FIND_MATCH);
        getActivity().registerReceiver(findMatchRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("articleId", articleId);
            requestJSON.put("category", category);
            //TODO: requestJSON.put("email", loggedInUseremail);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting FindMatch request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.FIND_MATCH);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        getActivity().startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME +  ": Started intent service");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Set the adapter
        gridView = (GridView) view.findViewById(R.id.articleGridView);
        gridView.setAdapter(customListAdapter);

        //get onItemClickListener so we can be notified on item clicks
        gridView.setOnItemClickListener(this);

        Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": activating Match mode");
        ((MainActivity)getActivity()).matchMode = true;

        // Inflate the layout for this fragment
        return view;
    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onFindMatchFragmentInteractionListener != null) {
            onFindMatchFragmentInteractionListener.onFindMatchFragmentInteraction(uri);
        }
    }
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onFindMatchFragmentInteractionListener = (OnFindMatchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFindMatchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFindMatchFragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (null != onFindMatchFragmentInteractionListener) {
            Article articleSelected = (Article) articles.get(position);
            onFindMatchFragmentInteractionListener.onFindMatchFragmentInteraction(articleSelected);
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
    public interface OnFindMatchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFindMatchFragmentInteraction(Article articleSelected);
    }

    //--------------- RequestReceiver ---------------

    public class SmartClosetRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = SmartClosetRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public SmartClosetRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(findMatchRequestReceiver != null) {
                try {
                    context.unregisterReceiver(findMatchRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

            // get list of articles in the selected category
            articles = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);

            if (articles == null || articles.size() == 0) {
                ToastMessage.displayShortToastMessage(getActivity(), "Your search did not match any articles.");

                //load the searchTab again
                onFindMatchFragmentInteractionListener.onFindMatchFragmentInteraction(null);

            } else {
                //Display search results
                customListAdapter = new CustomGridAdapter(getActivity(), articles);
                gridView.setAdapter(customListAdapter);
            }
        }
    }

}
