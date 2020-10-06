package com.nsc.covidscore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;
import com.nsc.covidscore.api.VolleyStringCallback;

import org.json.JSONObject;

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
        Requests.getCounty(this, "king", new VolleyStringCallback() {
            @Override
            public void getStringData(String response) {
                Log.i(TAG, "getCounty: " + response);
                textView.setText(response);
            }
        });
        Requests.getCountyHistorical(this, "puyallup,washington", "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.i(TAG, "getCountyHistorical: " + response);
//                JSONObject timeline = response.getJSONObject("timeline");
//                JSONObject cases = timeline.getJSONObject("cases");
//                JSONObject deaths = timeline.getJSONObject("deaths");
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.i(TAG, "getJsonException: " + exception);
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
