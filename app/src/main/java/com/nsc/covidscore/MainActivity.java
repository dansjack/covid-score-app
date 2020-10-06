package com.nsc.covidscore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyCallback;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    RequestQueue queue;
    RequestSingleton requestManager;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();
        textView = findViewById(R.id.hello_world);
        Requests.getCounty(this, "king", new VolleyCallback() {
            @Override
            public void getResponse(String response) {
                Log.i(TAG, "getResponse: " + response);
                textView.setText(response);
            }
        });
        Requests.getCountyHistorical(this, "king,washington", "30", new VolleyCallback() {
            @Override
            public void getResponse(String response) {
                Log.i(TAG, "getResponse: " + response);
            }
        });
        Log.d(TAG,"onCreate invoked");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
    }
}
