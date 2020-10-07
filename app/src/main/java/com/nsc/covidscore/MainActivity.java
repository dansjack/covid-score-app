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

import org.json.JSONException;
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
        Requests.getCounty(this, "king,washington", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                Log.i(TAG, "getCounty : " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.i(TAG, "getJsonException: " + exception.getMessage());
            }
        });
        Requests.getCountyHistorical(this, "whatcom,washington", "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                Log.i(TAG, "getCountyHistorical : " + response);
//                JSONObject timeline = response.getJSONObject("timeline");
//                JSONObject cases = timeline.getJSONObject("cases");
//                JSONObject deaths = timeline.getJSONObject("deaths");
//                Log.i(TAG, "getCountyHistorical - cases, last 30 days - Whatcom, WA: " + cases);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.i(TAG, "getJsonException: " + exception.getMessage());
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
