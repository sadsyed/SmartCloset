package ssar.smartcloset;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ssar.smartcloset.types.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.SearchFragment.OnSearchFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class SearchFragment extends Fragment {
/*    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    private OnSearchFragmentInteractionListener onSearchFragmentInteractionListener;

/*    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
/*    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActionBar().setTitle("Search");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewpager_main, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        PagerTabStrip pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTextColor(Color.GRAY);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor("#ff007777"));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (onSearchFragmentInteractionListener != null) {
            onSearchFragmentInteractionListener.onSearchFragmentInteraction(view);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onSearchFragmentInteractionListener = (OnSearchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSearchFragmentInteractionListener = null;
    }

    private ActionBar getActionBar() {
        return (getActivity()).getActionBar();
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
    public interface OnSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSearchFragmentInteraction(Uri uri);
    }

}
