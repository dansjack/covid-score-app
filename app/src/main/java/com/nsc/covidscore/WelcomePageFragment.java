package com.nsc.covidscore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A full-screen fragment.
 */
public class WelcomePageFragment extends Fragment {

    private static final String TAG = WelcomePageFragment.class.getSimpleName();

    public WelcomePageFragment() {
        // Required empty public constructor
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