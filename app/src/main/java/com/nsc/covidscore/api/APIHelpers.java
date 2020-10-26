package com.nsc.covidscore.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.nsc.covidscore.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class APIHelpers {
    public static void handleResponse(
            String type, String response, String county, String state, VolleyJsonCallback cb) {
        try {
            switch (type) {
                case Constants.COUNTY: {
                    JSONArray counties = new JSONArray(response);
                    if (counties.length() > 1) {
                        for (int i = 0; i < counties.length(); i++) {
                            JSONObject jsonObject = counties.getJSONObject(i);
                            String stateName = jsonObject.optString(Constants.PROVINCE);
                            if (state.equalsIgnoreCase(stateName.toLowerCase())) {
                                cb.getJsonData(jsonObject);
                                break;
                            }
                        }
                    } else {
                        cb.getJsonData(counties.getJSONObject(0));
                    }
                    break;
                }
                case Constants.COUNTY_HISTORICAL: {
                    JSONArray counties = new JSONArray(response);
                    for (int i = 0; i < counties.length(); i++) {
                        JSONObject jsonObject = counties.getJSONObject(i);
                        String countyName = jsonObject.optString(Constants.COUNTY);
                        if (countyName.equalsIgnoreCase(county)) {
                            cb.getJsonData(jsonObject);
                            break;
                        }
                    }
                    break;
                }
                case Constants.COUNTY_POPULATION:
                case Constants.STATE_POPULATION:
                case Constants.POPULATION:
                    String countyPopulation = new JSONArray(response).getJSONArray(1).getString(1);
                    cb.getString(countyPopulation);
                    break;
                case Constants.PROVINCE:
                    JSONObject jsonObject = new JSONObject(response);
                    String stateName = jsonObject.optString(Constants.STATE);
                    if (stateName.equalsIgnoreCase(state)) {
                        cb.getJsonData(jsonObject);
                    }
                    break;
                case Constants.COUNTRY_HISTORICAL:
                    cb.getJsonData(new JSONObject(response));
                    break;
                default:
                    throw new JSONException(Constants.ERROR_STATE_COUNTY);
            }
        } catch (JSONException | IOException e) {
            cb.getJsonException(e);
            e.printStackTrace();
        }
    }

    /**
     * Get JSON from a file in assets. Taken from
     * <a href="https://stackoverflow.com/questions/19945411/how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listview/19945484#19945484">Stack Overflow</a>
     * @param fileName name of the file to parse json from
     */
    public static String getJsonFromFile(Context context, String fileName) {
        String jsonString;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            int read = inputStream.read(buffer);
            if (read == -1) {
                inputStream.close();
            }
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            return jsonString;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Runs through a JSON file filled with the Name, Population, and FIPS codes for each county
     * in the U.S.
     * @param location the county and state the user selected, separated by a comma. e.g. "king,washington"
     * @return a JSONArray of the Name, Population, State FIPS, and County FIPS
     */
    public static String[] getLocationFIPS(Context context, String location) {
        String jsonString = getJsonFromFile(context, "county_fips.json");
        if (jsonString != null) {
            try {
                location = formatLocationFIPS(location);
                JSONArray fipsArray = new JSONArray(jsonString);
                int fipsArrayLen = fipsArray.length();
                int i = 0;

                while (i < fipsArrayLen) {
                    JSONArray fipsLocationArray = fipsArray.getJSONArray(i);
                    String fipsArrayLocationName = fipsLocationArray.getString(0).toLowerCase();
                    if (location.equals(fipsArrayLocationName)) {
                        int j = 0;
                        String[] resultArray = new String[fipsLocationArray.length()];
                        while (j < fipsLocationArray.length()) {
                            resultArray[j] = fipsLocationArray.getString(j);
                            j++;
                        }
                        return resultArray;
                    }
                    i++;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String formatLocationFIPS(String location) {
        location = location.toLowerCase();
        String county = location.split(",")[0];
        String state = location.split(",")[1];
        String countyString = Constants.LOCATION_FIPS_COUNTY_STRING;
        if (county.equals(Constants.DISTRICT_OF_COLUMBIA)) {
            countyString = ", ";
        }
        return county + countyString + state;
    }
}
