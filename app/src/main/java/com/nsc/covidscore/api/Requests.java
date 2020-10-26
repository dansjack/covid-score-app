package com.nsc.covidscore.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.nsc.covidscore.Constants;

public class Requests {
    /**
     * <p>Returns cumulative COVID stats for a U.S. county within a callback</p>
     * <p>The data comes from John Hopkins University through the
     * <a href="https://github.com/disease-sh/API">NovelCOVID api</a></p>
     * @param location the county and state the user selected, separated by a comma.
     *                 e.g. "king,washington"
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getCounty(Context context, String location, final VolleyJsonCallback cb) {
        location = location.toLowerCase();
        final String county = location.split(",")[0];
        final String state = location.split(",")[1];
        String url = "https://corona.lmao.ninja/v2/jhucsse/counties/" + county;
        Log.i("FFF ", "getCounty: URL" + url);
        final String TAG = Constants.COUNTY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.COUNTY, response, county, state, cb),
                error -> {
                });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public static void getState(Context context, String location, final VolleyJsonCallback cb) {
        location = location.toLowerCase();
        final String county = location.split(",")[0];
        final String state = location.split(",")[1];
        String url = "https://disease.sh/v3/covid-19/states/" + state;
        final String TAG = Constants.PROVINCE;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.PROVINCE, response, county, state, cb),
                error -> {
                });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * <p>Returns the last x days of COVID stats for a U.S. county within a callback</p>
     * <p>The data comes from John Hopkins University through the
     * <a href="https://github.com/disease-sh/API">NovelCOVID api</a></p>
     * @param location the county and state the user selected, separated by a comma.
     *                 e.g. "king,washington"
     * @param days how many days back to retrieve data. Get all available data with "all"
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getCountyHistorical(
            Context context, String location, String days, final VolleyJsonCallback cb) {
        location = location.toLowerCase();
        final String county = location.split(",")[0];
        final String state = location.split(",")[1];
        String url = "https://corona.lmao.ninja/v2/historical/usacounties/" +
                location.split(",")[1] + "?lastdays=" + days;
        final String TAG = Constants.COUNTY_HISTORICAL;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.COUNTY_HISTORICAL, response, county, state, cb),
                error -> Log.i(TAG, "onErrorResponse: " + error));
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public static void getUSHistorical(Context context, String days, final VolleyJsonCallback cb) {
        String url = "https://corona.lmao.ninja/v2/historical/usa" + "?lastdays=" + days;
        final String TAG = Constants.COUNTY_HISTORICAL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.COUNTRY, response, "", "", cb),
                error -> {
                });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
