package com.nsc.covidscore.api;

import android.util.Log;

import com.nsc.covidscore.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APIHelpers {
    public static void handleResponse(
            String type, String response, String county, String state, VolleyJsonCallback cb) {
        try {
            boolean found = false;
            if (type.equals(Constants.COUNTY)) {
                JSONArray counties = new JSONArray(response);
                if (counties.length() > 1) {
                    for (int i = 0; i < counties.length(); i++) {
                        JSONObject jsonObject = counties.getJSONObject(i);
                        String stateName = jsonObject.optString(Constants.PROVINCE);
                        if (state.equals(stateName.toLowerCase())) {
                            found = true;
                            cb.getJsonData(jsonObject);
                            break;
                        }
                    }
                } else {
                    cb.getJsonData(counties.getJSONObject(0));
                }
            } else if (type.equals(Constants.COUNTY_HISTORICAL)) {
                JSONArray counties = new JSONArray(response);
                for (int i = 0; i < counties.length(); i++) {
                    JSONObject jsonObject = counties.getJSONObject(i);
                    String countyName = jsonObject.optString(Constants.COUNTY);
                    if (countyName.equals(county)) {
                        found = true;
                        cb.getJsonData(jsonObject);
                        break;
                    }
                }
            } else if (type.equals(Constants.PROVINCE)) {
                JSONArray stats = new JSONArray(response);
                JSONObject jsonObject = stats.getJSONObject(0);
                Log.d("province: ", stats.toString());
                // TODO: fix this
//                for (int i = 0; i < counties.length(); i++) {
//                    JSONObject jsonObject = counties.getJSONObject(i);
//                    String countyName = jsonObject.optString(Constants.COUNTY);
//                    if (countyName.equals(county)) {
//                        found = true;
//                        cb.getJsonData(jsonObject);
//                        break;
//                    }
//                }
                found = true;
                cb.getJsonData(jsonObject);
            } else {
                found = true;
                cb.getJsonData(new JSONObject(response));
            }
            if (!found) {
                throw new JSONException(Constants.ERROR_STATE_COUNTY);
            }
        } catch (JSONException e) {
            cb.getJsonException(e);
            e.printStackTrace();
        }
    }
}
