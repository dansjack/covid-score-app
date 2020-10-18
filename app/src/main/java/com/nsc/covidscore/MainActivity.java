package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentAdapter mFragmentAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.frag_placeholder);
        setupViewPager(mViewPager);


        Log.d(TAG, "onCreate invoked");
    }

    private void setupViewPager(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new WelcomePageFragment(), "WelcomePageFragment");
        adapter.addFragment(new LocationSelectionPageFragment(), "LocationSelectionPageFragment");
        adapter.addFragment(new GpsPageFragment(), "GpsPageFragment");
        adapter.addFragment(new RiskDetailPageFragment(), "RiskDetailPageFragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

//    public void onLocationNavBtnClick(View view) {
//        // Begin the transaction
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        // Replace the contents of the container with the new fragment
//        // or         ft.replace(R.id.fragment_placeholder, new LocationSelectionFragment());
//        ft.add(R.id.fragment_placeholder, new LocationSelectionFragment());
//        // Complete the changes added above
//        ft.commit();
//        setContentView(R.layout.fragment_location_selection);
//    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

}