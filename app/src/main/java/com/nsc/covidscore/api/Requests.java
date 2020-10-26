package com.nsc.covidscore.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.nsc.covidscore.Constants;

import org.json.JSONArray;
import org.json.JSONException;

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
                state + "?lastdays=" + days;
        final String TAG = Constants.COUNTY_HISTORICAL;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.COUNTY_HISTORICAL, response, county, state, cb),
                error -> Log.d(TAG, "onErrorResponse: " + error));
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * <p>Returns the last x days of COVID stats for the U.S. within a callback</p>
     * <p>The data comes from John Hopkins University through the
     * <a href="https://github.com/disease-sh/API">NovelCOVID api</a></p>
     * @param days how many days back to retrieve data. Get all available data with "all"
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getUSHistorical(Context context, String days, final VolleyJsonCallback cb) {
        String url = "https://corona.lmao.ninja/v2/historical/usa" + "?lastdays=" + days;
        final String TAG = Constants.COUNTRY_HISTORICAL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> APIHelpers.handleResponse(
                        Constants.COUNTRY_HISTORICAL, response, "", "", cb),
                error -> {
                });
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * <p>Returns the estimated population of the given location for 2019</p>
     * <p>The data comes from the <a href="https://www.census.gov/data/developers/data-sets/popest-popproj/popest.html">U.S. Census Bureau</a></p>
     * @param location the county and state the user selected, separated by a comma. e.g. "king,washington"
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getCountyPopulation(Context context, String location, final VolleyJsonCallback cb) {
        final String TAG = Constants.COUNTY_POPULATION;
        String[] fipsLocationArray = APIHelpers.getLocationFIPS(context, location);
        if (fipsLocationArray != null) {
            StringBuilder url = new StringBuilder(
                    "https://api.census.gov/data/2019/pep/population?get=NAME,POP&for=county:")
                    .append(fipsLocationArray[2]).append("&in=state:")
                    .append(fipsLocationArray[1]).append("&key=")
                    .append(Constants.CENSUS_API_KEY);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                    response -> APIHelpers.handleResponse(
                            Constants.POPULATION, response, "", "", cb),
                    error -> Log.d(TAG, "onErrorResponse: " + error));
            stringRequest.setTag(TAG);

            // Add the request to the RequestQueue.
            RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            throw new Resources.NotFoundException(Constants.ERROR_STATE_COUNTY);
        }
    }

    /**
     * <p>Returns the estimated population of the given state for 2019</p>
     * <p>The data comes from the <a href="https://www.census.gov/data/developers/data-sets/popest-popproj/popest.html">U.S. Census Bureau</a></p>
     * @param location the county and state the user selected, separated by a comma. e.g. "king,washington"
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getStatePopulation(Context context, String location, final VolleyJsonCallback cb) {
        final String TAG = Constants.STATE_POPULATION;
        Log.i(TAG, "getStatePopulation: FAILED LOCATION " + location);
        String[] fipsLocationArray = APIHelpers.getLocationFIPS(context, location);
        if (fipsLocationArray != null) {
            StringBuilder url = new StringBuilder(
                    "https://api.census.gov/data/2019/pep/population?get=NAME,POP&for=state:")
                    .append(fipsLocationArray[1]).append("&key=")
                    .append(Constants.CENSUS_API_KEY);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                    response -> APIHelpers.handleResponse(
                            Constants.POPULATION, response, "", "", cb),
                    error -> Log.d(TAG, "onErrorResponse: " + error));
            stringRequest.setTag(TAG);

            // Add the request to the RequestQueue.
            RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            throw new Resources.NotFoundException(Constants.ERROR_STATE_COUNTY);
        }
    }

    /**
     * <p>Returns the estimated population of the U.S. for 2019</p>
     * <p>The data comes from the <a href="https://www.census.gov/data/developers/data-sets/popest-popproj/popest.html">U.S. Census Bureau</a></p>
     * @param cb callback class (see VolleyJsonCallback interface)
     */
    public static void getCountryPopulation(Context context, final VolleyJsonCallback cb) {
        final String TAG = Constants.COUNTRY_POPULATION;
        StringBuilder url = new StringBuilder(
                "https://api.census.gov/data/2019/pep/population?get=NAME,POP&for=us:*")
                .append("&key=").append(Constants.CENSUS_API_KEY);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                response -> APIHelpers.handleResponse(
                        Constants.POPULATION, response, "", "", cb),
                error -> Log.d(TAG, "onErrorResponse: " + error));
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
