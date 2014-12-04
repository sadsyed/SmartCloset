package ssar.smartcloset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import ssar.smartcloset.types.Category;
import ssar.smartcloset.types.CustomListAdapter;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class ClosetFragment extends Fragment implements AdapterView.OnItemClickListener{
    private String CLASSNAME = ClosetFragment.class.getSimpleName();
    private SmartClosetRequestReceiver getCategoriesRequestReceiver;
    IntentFilter filter;

    private OnCategorySelectedListener categorySelectedListener;
    private List<CustomListItem> categories;

    /**
     * The fragment's ListView/GridView.
     */
    private GridView gridView;

    private TextView emptyClosetTextView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CustomListAdapter customListAdapter;

/*    public static ClosetFragment newInstance(int position) {
        ClosetFragment fragment = new ClosetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_SELECTED, position);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClosetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getCategoriesRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.GET_CATEGORIES);
        getActivity().registerReceiver(getCategoriesRequestReceiver, filter);

        //get current user
        User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("emailFilter", loggedInUser.getUserEmail());
        } catch (Exception e) {
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Exception while creating an request JSON.");
        }

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Starting GetCategories request");
        Intent msgIntent = new Intent(getActivity(), SmartClosetIntentService.class);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_URL, SmartClosetConstants.GET_CATEGORIES);
        msgIntent.putExtra(SmartClosetIntentService.REQUEST_JSON, requestJSON.toString());
        getActivity().startService(msgIntent);
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME +  ": Started intent service");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closet, container, false);

        emptyClosetTextView = (TextView) view.findViewById(R.id.emptyClosetTextView);
        emptyClosetTextView.setVisibility(View.GONE);

        // Set the adapter
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(customListAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        gridView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            categorySelectedListener = (OnCategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnArticleFragmentInteractionListener");
        }

        if(getCategoriesRequestReceiver != null) {
            getActivity().registerReceiver(getCategoriesRequestReceiver, filter);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        categorySelectedListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*(if (null != viewSelectedListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            viewSelectedListener.OnViewSelectedListener(MainMenu.ITEMS.get(position).id);
        }*/

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (null != categorySelectedListener) {
            Category categorySelected = (Category) categories.get(position);
            categorySelectedListener.onCategorySelected(categorySelected.getName());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
       /* View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }*/
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
    public interface OnCategorySelectedListener {
        // TODO: Update argument type and name
        public void onCategorySelected(String categorySelected);
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
            if(getCategoriesRequestReceiver != null) {
                try {
                    context.unregisterReceiver(getCategoriesRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

            categories = JsonParserUtil.jsonToCategory(serviceUrl, responseJSON);

            if(categories != null) {
                emptyClosetTextView.setVisibility(View.GONE);

                customListAdapter = new CustomListAdapter(getActivity(), categories);
                gridView.setAdapter(customListAdapter);
            } else {
                emptyClosetTextView.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            }
        }
    }
}
