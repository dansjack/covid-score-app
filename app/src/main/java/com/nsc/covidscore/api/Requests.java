package com.nsc.covidscore.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Requests {
    /**
     * <p>Returns cumulative COVID stats for a U.S. county within a callback</p>
     * @param county the county selected by the user e.g. "king,washington" (lowercase)
     * @param cb callback class (see VolleyStringCallback interface)
     */
    public static void getCounty(Context context, String county, final VolleyStringCallback cb) {
        String url = "https://corona.lmao.ninja/v2/jhucsse/counties/" + county;
        final String TAG = "getCounty";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cb.getStringData(response);
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
     * @param cb callback class (see VolleyStringCallback interface)
     */
    public static void getCountyHistorical(Context context, String location, String days, final VolleyJsonCallback cb) {
        final String county = location.split(",")[0];
        String state = location.split(",")[1];
        String url = "https://corona.lmao.ninja/v2/historical/usacounties/" + state + "?lastdays=" + days;
        final String TAG = "getCountyHistorical";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray counties = new JSONArray(response);
                            boolean found = false;
                            for (int i = 0; i < counties.length(); i++) {
                                JSONObject jsonObject = counties.getJSONObject(i);
                                String countyName = jsonObject.optString("county");
                                if (countyName.equals(county)) {
                                    found = true;
                                    cb.getJsonData(jsonObject);
                                    break;
                                }
                            }

                            if (!found) {

                            }
                        } catch (JSONException e) {
                            cb.getJsonException(e);
                            e.printStackTrace();
                        }
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
