package com.nsc.covidscore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    RequestQueue queue;
    RequestSingleton requestManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();
        makeApiCalls();
        Log.d(TAG,"onCreate invoked");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
    }

    private void makeApiCalls() {
        Requests.getCounty(this, "king,washington", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getCountyHistorical(this, "whatcom,washington", "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getUSHistorical(this, "1", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
    }
}
