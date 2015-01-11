package ssar.smartcloset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.CustomGridAdapter;
import ssar.smartcloset.types.CustomListItem;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.JsonParserUtil;
import ssar.smartcloset.util.SmartClosetConstants;
import ssar.smartcloset.util.ToastMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.MatchFragment.OnMatchFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MatchFragment extends Fragment {
    private static final String CLASSNAME = MatchFragment.class.getSimpleName();

    // the fragment initialization parameters
    //private static final String ARG_ARTICLE_ID = "articleId";
    private static final String ARG_ARTICLE = "article";
    private static final String ARG_MATCH_FILTER = "matchFilter";

    //private String articleId;
    private Article article;
    private Boolean matchFilter;

    private OnMatchFragmentInteractionListener onMatchFragmentInteractionListener;

    private ImageView imageView;
    private Spinner articleTypeSelector;
    private Button findMatchButton;

    public static MatchFragment newInstance(Article article, Boolean matchFilter) {
        MatchFragment matchFragment = new MatchFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ARTICLE, article);
        args.putBoolean(ARG_MATCH_FILTER, matchFilter);
        matchFragment.setArguments(args);
        return matchFragment;
    }

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            article = getArguments().getParcelable(ARG_ARTICLE);
            //articleId = getArguments().getString(ARG_ARTICLE_ID);
            matchFilter = getArguments().getBoolean(ARG_MATCH_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        if(!matchFilter) {
            // Inflate the tag fragment
            view = inflater.inflate(R.layout.fragment_match, container, false);
            ((MainActivity) getActivity()).matchMode = true;
        } else {
            // Inflate the filter selection fragment
            view = inflater.inflate(R.layout.fragment_match_filter, container, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.selectedImageView);

            // set the image
            if(article.getItemImageURL() != null){
                LoadImage loadImage = new LoadImage(imageView);
                Log.v(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Loading URL: " + article.getItemImageURL());
                loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, article.getItemImageURL());
            }

            articleTypeSelector = (Spinner) view.findViewById(R.id.articleTypeSelector);
            articleTypeSelector.setOnItemSelectedListener(new CustomOnItemSelectedListener());
            findMatchButton = (Button) view.findViewById(R.id.findMatchButton);
            findMatchButton.setOnClickListener(new Spinner.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String category = String.valueOf(articleTypeSelector.getSelectedItem());

                   User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

                   // callback the MainActivity to find matches for the selected articles
                   //onMatchFragmentInteractionListener.onMatchFragmentInteraction(articleId, category, loggedInUser);
                   onMatchFragmentInteractionListener.onMatchFragmentInteraction(article, category);
               }
            });

        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String articleId, String category) {
        if (onMatchFragmentInteractionListener != null) {
            onMatchFragmentInteractionListener.onMatchFragmentInteraction(article, category);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onMatchFragmentInteractionListener = (OnMatchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMatchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onMatchFragmentInteractionListener = null;
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
    public interface OnMatchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMatchFragmentInteraction(Article article, String category);
    }

    private class LoadImage extends AsyncTask<String, Integer, Drawable> {
        private final WeakReference<ImageView> weakReference;

        public LoadImage(ImageView imageView) {
            weakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading Streams.... ");
            progressDialog.show();*/
        }

        @Override
        protected Drawable doInBackground(String... args) {
            try {
                return Drawable.createFromStream((InputStream)new URL(args[0]).getContent(), "src");
            } catch (Exception e) {
                Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            ImageView imgView = weakReference.get();
            if (imgView != null) {
                imgView.setImageDrawable(drawable);
            }
        }
    }
}
