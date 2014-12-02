package ssar.smartcloset;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.Calendar;
import java.util.TimeZone;

import ssar.smartcloset.types.User;
import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.UsageFilterFragment.OnUsageFilterFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsageFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsageFilterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnUsageFilterFragmentInteractionListener onUsageFilterFragmentInteractionListener;

    private RadioGroup usageFilterRadioGroup;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsageFilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsageFilterFragment newInstance(String param1, String param2) {
        UsageFilterFragment fragment = new UsageFilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UsageFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage_filter, container, false);

        usageFilterRadioGroup = (RadioGroup) view.findViewById(R.id.usageFilterRadioGroup);
        usageFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String usageFilterValue = null;

                //find which usage filter is selected
                switch(checkedId) {
                    case R.id.last30DaysRadioButton:
                        usageFilterValue = getLast30DaysDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last 30 days date: " + usageFilterValue);
                        break;
                    case R.id.last6MonthsRadioButton:
                        usageFilterValue = getLast6MonthsDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last 6 months date: " + usageFilterValue);
                        break;
                    case R.id.lastOneYearRadioButton:
                        usageFilterValue = getLastOneYearDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last one year: " + usageFilterValue);
                        break;
                    case R.id.lastTwoYearsRadioButton:
                        usageFilterValue = getLastTwoYearsDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last Two years: " + usageFilterValue);
                        break;
                    case R.id.lastThreeYearsRadioButton:
                        usageFilterValue = getLastThreeYearsDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last Three years: " + usageFilterValue);
                        break;
                    case R.id.lastFourYearsRadioButton:
                        usageFilterValue = getLastFourYearsDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last Four years: " + usageFilterValue);
                        break;
                    case R.id.lastFiveYearsRadioButton:
                        usageFilterValue = getLastFiveYearsDate();
                        Log.i(SmartClosetConstants.SMARTCLOSET_DEBUG_TAG, "Last Five years: " + usageFilterValue);
                        break;
                }

                //invoke SearchArticles API to get articles
                String searchType = "usagefilter";
                String searchValue = usageFilterValue;

                //test with today's date
                /*Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getDefault());
                calendar.add(Calendar.DATE, 0);
                String searchValue = getDate(calendar);*/

                User loggedInUser = ((MainActivity)getActivity()).getExistingUser();

                //callback the MainActivity to display list of articles
                onUsageFilterFragmentInteractionListener.onUsageFilterFragmentInteraction(searchType, searchValue, loggedInUser.getUserEmail());
            }
        });

        return view;
    }

    private String getLast30DaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.DATE, -30);

        return getDate(calendar);
    }

    private String getLast6MonthsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.MONTH, -6);

        return getDate(calendar);
    }

    private String getLastOneYearDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.YEAR, -1);

        return getDate(calendar);
    }

    private String getLastTwoYearsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.YEAR, -2);

        return getDate(calendar);
    }

    private String getLastThreeYearsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.YEAR, -3);

        return getDate(calendar);
    }

    private String getLastFourYearsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.YEAR, -4);

        return getDate(calendar);
    }

    private String getLastFiveYearsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.add(Calendar.YEAR, -5);

        return getDate(calendar);
    }

    private String getDate(Calendar calendar) {
        StringBuilder dateString = new StringBuilder();

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);

        dateString.append(calendar.get(Calendar.YEAR)).append("-")
                .append(month < 10 ? "0" + month : month).append("-")
                .append(day < 10 ? "0" + day : day);

        return dateString.toString();
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onUsageFilterFragmentInteractionListener != null) {
            onUsageFilterFragmentInteractionListener.onUsageFilterFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onUsageFilterFragmentInteractionListener = (OnUsageFilterFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUsageFilterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUsageFilterFragmentInteractionListener = null;
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
    public interface OnUsageFilterFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUsageFilterFragmentInteraction(String searchType, String searchValue, String email);
    }

}
