package ssar.smartcloset.types;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ssar.smartcloset.MainActivity;
import ssar.smartcloset.R;
import ssar.smartcloset.util.SmartClosetConstants;

/**
 * Created by ssyed on 11/24/14.
 */
public class CustomListAdapter extends BaseAdapter{
    public final static String CLASSNAME = CustomListAdapter.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "ssar.smartcloset.MESSAGE";

    //TODO: remove hardcoded image list
    public static int [] prgmImages = {R.drawable.ic_launcher};

    Context context;
    List<Category>  categories = new ArrayList<Category>();
    ProgressDialog progressDialog;

    private static LayoutInflater layoutInflater = null;

    public CustomListAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return categories.size(); }

    @Override
    public Object getItem(int position) { return position; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = null;

        try {
            rowView = layoutInflater.inflate(R.layout.fragment_category_view, null);
            holder.textView = (TextView) rowView.findViewById(R.id.categoryName);
            holder.imageView = (ImageView) rowView.findViewById(R.id.articleImage);

            //load the category
            holder.textView.setText(categories.get(position).getName());

            //load image using cover url
            LoadImage loadImage = new LoadImage(holder.imageView);
            Log.v(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": Loading Url: " + categories.get(position).getLastestUsedArticleImageUrl());
            loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, categories.get(position).getLastestUsedArticleImageUrl());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO onClick launch all the items from the selected category
                    //Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
                    //Need to launch the stream activity here.
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, categories.get(position).getName());
                    context.startActivity(intent);
                    //Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        }
        //TODO add on click action listener to launch articles in a selected category

        return rowView;
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

    public class Holder {
        TextView textView;
        ImageView imageView;
    }
}
