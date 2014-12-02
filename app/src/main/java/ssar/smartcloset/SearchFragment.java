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
 * {@link ssar.smartcloset.SearchFragment.OnSearchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String CLASSNAME = SearchFragment.class.getSimpleName();

    private static final String ARG_SEARCH_TYPE = "searchType";
    private static final String ARG_SEARCH_VALUE = "searchValue";
    private static final String ARG_EMAIL = "email";

    private String searchType;
    private String searchValue;
    private String loggedInUseremail;
    private List<CustomListItem> articles;

    private OnSearchFragmentInteractionListener onSearchFragmentInteractionListener;
    private SmartClosetRequestReceiver searchRequestReceiver;
    IntentFilter filter;

    /**
     * The fragment's ListView/GridView.
     */
    private GridView gridView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CustomGridAdapter customListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchType Parameter 1.
     * @param searchValue Parameter 2.
     * @param loggedInUseremail Parameter 3
     * @return A new instance of fragment BaseSearchFragment.
     */
    public static SearchFragment newInstance(String searchType, String searchValue, String loggedInUseremail) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TYPE, searchType);
        args.putString(ARG_SEARCH_VALUE, searchValue);
        args.putString(ARG_EMAIL, loggedInUseremail);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchType = getArguments().getString(ARG_SEARCH_TYPE);
            searchValue = getArguments().getString(ARG_SEARCH_VALUE);
            loggedInUseremail = getArguments().getString(ARG_EMAIL);
        }

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        searchRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.SEARCH_ARTICLES);
        getActivity().registerReceiver(searchRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("filterType", searchType);
            requestJSON.put("filterString", searchValue);
            requestJSON.put("email", loggedInUseremail);
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting GetCategory request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.SEARCH_ARTICLES);
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

        //get OnItemClickListener so we can be notified on item clicks
        gridView.setOnItemClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onSearchFragmentInteractionListener = (OnSearchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSearchFragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (null != onSearchFragmentInteractionListener) {
            Article articleSelected = (Article) articles.get(position);
            onSearchFragmentInteractionListener.onSearchFragmentInteraction(articleSelected);
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
    public interface OnSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSearchFragmentInteraction(Article articleSelected);
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
            if(searchRequestReceiver != null) {
                try {
                    context.unregisterReceiver(searchRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

            // get list of articles in the selected category
            articles = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);

            if (articles != null || articles.size() == 0) {
                customListAdapter = new CustomGridAdapter(getActivity(), articles);
                gridView.setAdapter(customListAdapter);
            } else {
                ToastMessage.displayShortToastMessage(getActivity(), "Your search did not match any articles.");
            }
        }
    }

}
