package ssar.smartcloset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.CustomListAdapter;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.ArticleFragment.OnArticleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {
    private static final String CLASSNAME = ArticleFragment.class.getSimpleName();

    // the fragment initialization parameters
    public static final String ARG_ARTICLE_SELECTED = "article";
    public static final String ARG_ARTICLE_ID = "articleId";
    public static final String ARG_ARTICLE_NAME = "articleName";
    private String articleId;
    private String articleName;
    private Article article;

    private OnArticleFragmentInteractionListener onArticleFragmentInteractionListener;
//    private SmartClosetRequestReceiver getArticleRequestReceiver;
    private IntentFilter filter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @return A new instance of fragment ArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_ARTICLE_ID, articleId);
        //args.putString(ARG_ARTICLE_NAME, articleName);
        args.putParcelable(ARG_ARTICLE_SELECTED, article);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

/*    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            filter = new IntentFilter(SmartClosetConstants.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getArticleRequestReceiver = new SmartClosetRequestReceiver(SmartClosetConstants.GET_CATEGORY);
            getActivity().registerReceiver(getArticleRequestReceiver, filter);

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
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //articleId = getArguments().getString(ARG_ARTICLE_ID);
            //articleName = getArguments().getString(ARG_ARTICLE_NAME);
            article = getArguments().getParcelable(ARG_ARTICLE_SELECTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Launching Article Fragment" );
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Article: " + article.toString());
        TextView title = (TextView) view.findViewById(R.id.articleName);
        title.setText(article.getArticleName());

        new DownloadImageTask((ImageView)view.findViewById(R.id.articleImage)).execute(article.getItemImageURL());

        TextView lastUsedDateTextView = (TextView) view.findViewById(R.id.articleLastUsedDate);
        String[] lastUsedDate = article.getArticleLastUsed();
        if(lastUsedDate == null || lastUsedDate.length == 0) {
            lastUsedDateTextView.setText("Never Used");
        } else {
            lastUsedDateTextView.setText(lastUsedDate[lastUsedDate.length - 1]);
        }

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onArticleFragmentInteractionListener != null) {
            onArticleFragmentInteractionListener.onArticleFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onArticleFragmentInteractionListener = (OnArticleFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onArticleFragmentInteractionListener = null;
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
    public interface OnArticleFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onArticleFragmentInteraction(Uri uri);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;

            try {
                InputStream inputStream = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

/*    //--------------- RequestReceiver ---------------

    public class SmartClosetRequestReceiver extends BroadcastReceiver {
        public final String CLASSNAME = SmartClosetRequestReceiver.class.getSimpleName();
        private String serviceUrl;

        public SmartClosetRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(getArticleRequestReceiver != null) {
                try {
                    context.unregisterReceiver(getArticleRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Error unregistering receiver: " + e.getMessage());
                }
            }

            String responseJSON = intent.getStringExtra(SmartClosetIntentService.RESPONSE_JSON);
            Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Service response JSON: " + responseJSON);

            // get list of articles in the selected category
            article = JsonParserUtil.jsonToArticle(serviceUrl, responseJSON);


        }
    }
*/
}
