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

    public WelcomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WelcomeMessageFragment.
     */
    public static WelcomePageFragment newInstance() {
        WelcomePageFragment fragment = new WelcomePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: invoked");
        return fragment;
    }

    // access parameters from newInstance() here
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_page, container, false);
    }
}