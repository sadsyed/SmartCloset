package ssar.smartcloset;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import java.io.InputStream;


import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.User;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.ArticleFragment.OnArticleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment implements View.OnClickListener{
    private static final String CLASSNAME = ArticleFragment.class.getSimpleName();

    // the fragment initialization parameters
    public static final String ARG_ARTICLE_SELECTED = "article";

    private Article article;

    private OnArticleFragmentInteractionListener onArticleFragmentInteractionListener;

    private Button updateButton;

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
        args.putParcelable(ARG_ARTICLE_SELECTED, article);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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

        updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
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

    @Override
    public void onClick (View view) {
        //callback the MainActivity to launch UpdateArticle fragment
        onArticleFragmentInteractionListener.onArticleFragmentInteraction(article);
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
        public void onArticleFragmentInteraction(Article article);
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
}
