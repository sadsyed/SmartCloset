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
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.Category;
import ssar.smartcloset.types.CustomListAdapter;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CategoryFragment extends Fragment implements AdapterView.OnItemClickListener{
    public final static String CLASSNAME = CategoryFragment.class.getSimpleName();

    public final static String ARG_CATEGORY_SELECTED = "categorySelected";

    private OnCategoryFragmentInteractionListener onCategoryFragmentInteractionListener;
    private SmartClosetRequestReceiver getCategoryArticlesRequestReceiver;
    IntentFilter filter;

    private String categorySelected;
    private String currentCatergory = null;
    private List<CustomListItem> articles;

    /**
     * The fragment's ListView/GridView.
     */
    private GridView gridView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CustomListAdapter customListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param categorySelected Parameter 1.
     * @return A new instance of fragment CategoryFragment.
     */
    public static CategoryFragment newInstance(String categorySelected) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_SELECTED, categorySelected);
        fragment.setArguments(args);
        return fragment;
    }
    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categorySelected = getArguments().getString(ARG_CATEGORY_SELECTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentCatergory = savedInstanceState.getString(ARG_CATEGORY_SELECTED);
        }

        View view = inflater.inflate(R.layout.fragment_view, container, false);

        // Set the adapter
        gridView = (GridView) view.findViewById(R.id.articleGridView);
        gridView.setAdapter(customListAdapter);

        //get OnItemClickListener so we can be notified on item clicks
        gridView.setOnItemClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            //set article based on argument passed in
            updateMenuView(args.getString(ARG_CATEGORY_SELECTED));

            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getCategoryArticlesRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.GET_CATEGORY);
            getActivity().registerReceiver(getCategoryArticlesRequestReceiver, filter);

            //set the JSON request object
            JSONObject requestJSON = new JSONObject();
            try {
                requestJSON.put("category", args.getString(ARG_CATEGORY_SELECTED));
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
            }

            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting GetCategory request");
            Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.GET_CATEGORY);
            msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
            getActivity().startService(msgIntent);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME +  ": Started intent service");
        } else if (currentCatergory != null) {
            updateMenuView(currentCatergory);
        }
    }

    public void updateMenuView(String categorySelected) {
        TextView viewTitle = (TextView) getActivity().findViewById(R.id.viewTitle);
        viewTitle.setText(categorySelected);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the current article selection in case we need to recreate the fragment
        outState.putString(ARG_CATEGORY_SELECTED, currentCatergory);
    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUploadImageFragmentInteraction(uri);
        }
    }
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onCategoryFragmentInteractionListener = (OnCategoryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }

        if(getCategoryArticlesRequestReceiver != null) {
            getActivity().registerReceiver(getCategoryArticlesRequestReceiver, filter);
        }
    }

/*    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (null != onCategoryFragmentInteractionListener) {
            Article articleSelected = (Article) articles.get(position);
            onCategoryFragmentInteractionListener.onCategoryFragmentInteraction(articleSelected);
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
    public interface OnCategoryFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCategoryFragmentInteraction(Article articleSelected);
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
            if(getCategoryArticlesRequestReceiver != null) {
                try {
                    context.unregisterReceiver(getCategoryArticlesRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

            // get list of articles in the selected category
            articles = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);

            customListAdapter = new CustomListAdapter(getActivity(), articles);
            gridView.setAdapter(customListAdapter);
        }
    }
}
