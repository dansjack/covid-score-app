package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFindOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFindOptionsFragment extends Fragment {

    private static final String TAG = LocationFindOptionsFragment.class.getSimpleName();

    private Button btnNavLocationSelect;
    private Button btnNavLocationGps;

    public LocationFindOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment LocationFindOptionsFragment.
     */
    // TODO: pass location to fragment here via parameters
    public static LocationFindOptionsFragment newInstance() {
        LocationFindOptionsFragment fragment = new LocationFindOptionsFragment();
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
//        if (savedInstanceState == null){
//            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.frag_placeholder, new LocationFindOptionsFragment());
//            ft.commit();
//        }
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        Log.d(TAG, "onCreate: invoked");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_find_options, container, false);
        Log.d(TAG, "onCreateView invoked");

        return v;
    }

//     This event is triggered soon after onCreateView().
    //     onViewCreated() is only called if the view returned from onCreateView() is non-null.
    //     Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        btnNavLocationSelect = (Button) v.findViewById(R.id.location_btn);
        btnNavLocationGps = (Button) v.findViewById(R.id.gps_button);

        btnNavLocationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Going to Manual location selection", Toast.LENGTH_SHORT).show();

                ((MainActivity) getActivity()).setViewPager(1);
            }
        });

        btnNavLocationGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Going to GPS location selection", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(2);
            }
        });
        Log.d(TAG, "onViewCreated: invoked");
    }
}