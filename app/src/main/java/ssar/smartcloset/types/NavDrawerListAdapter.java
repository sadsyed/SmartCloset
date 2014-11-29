package ssar.smartcloset.types;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ssar.smartcloset.R;

/**
 * Created by ssyed on 11/26/14.
 */
public class NavDrawerListAdapter extends BaseAdapter {
    private final static String CLASSNAME = NavDrawerListAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);

        titleTextView.setText(navDrawerItems.get(position).getTitle());

        return convertView;
    }
}
