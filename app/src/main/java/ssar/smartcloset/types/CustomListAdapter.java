package ssar.smartcloset.types;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ssar.smartcloset.R;
import ssar.smartcloset.util.SmartClosetConstants;

/**
 * Created by ssyed on 11/24/14.
 */
public class CustomListAdapter extends BaseAdapter{
    public final static String CLASSNAME = CustomListAdapter.class.getSimpleName();

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

            //load the stream names
            holder.textView.setText(categories.get(position).getName());

            //load the stream image
            holder.imageView.setImageResource(prgmImages[0]);
        } catch (Exception e){
            Log.e(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, CLASSNAME + ": " + e);
        }
        //TODO add on click action listener to launch articles in a selected category

        return rowView;
    }

    public class Holder {
        TextView textView;
        ImageView imageView;
    }
}
