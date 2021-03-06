package com.nsc.covidscore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton censusLink = view.findViewById(R.id.censusLink);
        ImageButton appLink = view.findViewById(R.id.appGithubLink);
        ImageButton diseaseLink = view.findViewById(R.id.diseaseGithubLink);
        ImageButton gTechLink = view.findViewById(R.id.gTechLink);
        ImageButton atlanticLink = view.findViewById(R.id.atlanticLink);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.aboutTitle);

        censusLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.census.gov/data/developers/data-sets/popest-popproj/popest.html"));
            startActivity(intent);
        });

        appLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dansjack/covid-score-app"));
            startActivity(intent);
        });

        diseaseLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/disease-sh/API"));
            startActivity(intent);
        });

        gTechLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://covid19risk.biosci.gatech.edu/"));
            startActivity(intent);
        });

        atlanticLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://covidtracking.com/"));
            startActivity(intent);
        });

    }
}