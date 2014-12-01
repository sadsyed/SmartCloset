package ssar.smartcloset;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import ssar.smartcloset.types.Article;
import ssar.smartcloset.types.Category;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.BaseSearchFragment.OnBaseSearchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseSearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnBaseSearchFragmentInteractionListener onBaseSearchFragmentInteractionListener;

    private Button baseSearchButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseSearchFragment newInstance(String param1, String param2) {
        BaseSearchFragment fragment = new BaseSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseSearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_string_search, container, false);

        baseSearchButton = (Button) view.findViewById(R.id.baseSearchButton);
        baseSearchButton.setOnClickListener(this);
        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onBaseSearchFragmentInteractionListener != null) {
            onBaseSearchFragmentInteractionListener.onBaseSearchFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onBaseSearchFragmentInteractionListener = (OnBaseSearchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBaseSearchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onBaseSearchFragmentInteractionListener = null;
    }

    @Override
    public void onClick (View view) {
        //invoke SearchArticles API to get articles

        //callback the MainActivity to display list of articles
        //onBaseSearchFragmentInteractionListener.onBaseSearchFragmentInteraction(articles);
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
    public interface OnBaseSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onBaseSearchFragmentInteraction(List<Article> articles);
    }

}
