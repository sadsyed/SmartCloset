package ssar.smartcloset;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import ssar.smartcloset.util.SmartClosetConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ssar.smartcloset.ProfileFragment.OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    public static final String CLASSNAME = ProfileFragment.class.getSimpleName();
    public static final String PREF_NAME = "smartProfilePreference";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PROFILE_FRAGMENT = "profileFragment";

    // TODO: Rename and change types of parameters
    private String profileFragment;
    private String mParam2;

    private OnProfileFragmentInteractionListener onProfileFragmentInteractionListener;
    private SharedPreferences sharedPreferences;

    private EditText userNameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button onSubmitButton;

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, 0);

        userName = sharedPreferences.getString("UserName", null);
        firstName = sharedPreferences.getString("FirstName", null);
        lastName = sharedPreferences.getString("LastName", null);
        email = sharedPreferences.getString("Email", null);
        password = sharedPreferences.getString("Password", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(password != null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_profile, container, false);

            userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
            firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
            lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
            emailEditText = (EditText) view.findViewById(R.id.emailEditText);

            userNameEditText.setEnabled(false);
            userNameEditText.setText(userName);
            firstNameEditText.setEnabled(false);
            firstNameEditText.setText(firstName);
            lastNameEditText.setEnabled(false);
            lastNameEditText.setText(lastName);
            emailEditText.setEnabled(false);
            emailEditText.setText(email);
            //passwordEditText.setText(password);
        } else {
            // Inflate create profile fragment
            view = inflater.inflate(R.layout.fragment_create_profile, container, false);

            userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
            firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
            lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
            emailEditText = (EditText) view.findViewById(R.id.emailEditText);
            passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

            onSubmitButton = (Button) view.findViewById(R.id.submitProfileButton);
            onSubmitButton.setOnClickListener(this);
        }
        return view;
    }

    private void updateProfileUI() {

    }

    @Override
    public void onClick (View view) {
        //create profile
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();

        e.putString("UserName", userNameEditText.getText().toString());
        e.putString("FirstName", firstNameEditText.getText().toString());
        e.putString("LastName", lastNameEditText.getText().toString());
        e.putString("Email", emailEditText.getText().toString());
        e.putString("Password", passwordEditText.getText().toString());
        e.commit();

        //launch profile fragment
        onProfileFragmentInteractionListener.onProfileFragmentInteraction();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (onProfileFragmentInteractionListener != null) {
            onProfileFragmentInteractionListener.onProfileFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onProfileFragmentInteractionListener = (OnProfileFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProfileFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onProfileFragmentInteractionListener = null;
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
    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onProfileFragmentInteraction();
    }

}
