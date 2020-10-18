package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomePageFragment extends Fragment {

    private static final String TAG = WelcomePageFragment.class.getSimpleName();
    // TODO:Add fragment_location_find_options
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
//    private String mParam1;

    public WelcomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomePageFragment newInstance(String param1, String param2) {
        WelcomePageFragment fragment = new WelcomePageFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        Log.d(TAG, "newInstance: invoked");
        return fragment;
    }

    // access parameters from newInstance() here
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_page, container, false);
    }
}