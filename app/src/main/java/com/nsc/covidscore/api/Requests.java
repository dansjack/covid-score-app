package com.nsc.covidscore.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Requests {
    /**
     * <p>Returns cumulative COVID stats for a U.S. county within a callback</p>
     * @param county the county selected by the user e.g. "king,washington" (lowercase)
     * @param cb callback class (see VolleyCallback interface)
     */
    public static void getCounty(Context context, String county, final VolleyCallback cb) {
        String url = "https://corona.lmao.ninja/v2/jhucsse/counties/" + county;
        final String TAG = "getCounty";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cb.getResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * <p>Returns the last x days of COVID stats for a U.S. county within a callback</p>
     * @param location the county and state the user selected, separated by a comma.
     *                 e.g. "king,washington" (lowercase)
     * @param days how many days back to retrieve data. Get all available data with "all"
     * @param cb callback class (see VolleyCallback interface)
     */
    public static void getCountyHistorical(Context context, String location, String days, final VolleyCallback cb) {
        String county = location.split(",")[0];
        String state = location.split(",")[1];
        String url = "https://corona.lmao.ninja/v2/historical/usacounties/" + state + "?lastdays=" + days;
        final String TAG = "getCountyHistorical";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cb.getResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error);
            }
        });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
