package com.nsc.covidscore;

import com.nsc.covidscore.api.APIHelpers;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class APIHelpersUnitTest {
    private static final String COUNTY_OBJECT =
            "[{province: washington, county: king}, {province: texas, county: king}]";
    private static final String COUNTY_RETURN = "{\"province\":\"washington\",\"county\":\"king\"}";
    private static final String COUNTY_HISTORICAL_OBJECT =
            "[{province: washington, county: king}, {province: washington, county: pierce}]";
    private static final String COUNTY_HISTORICAL_OBJECT_NOTFOUND =
            "[{province: washington, county: pierce}, {province: washington, county: whatcom}]";
    private static final String COUNTRY_OBJECT = "{testKey: testValue}";

    private static final String TEST_COUNTY = "king";
    private static final String TEST_STATE = "washington";
    private static final String TEST_JSONOBJECT_EXCEPTION = new JSONException(
            "A JSONObject text must begin with '{' at 1 [character 2 line 1]").toString();
    private static final String TEST_JSONARRAY_EXCEPTION = new JSONException(
            "A JSONArray text must start with '[' at 1 [character 2 line 1]").toString();
    private static final String TEST_NOTFOUND_EXCEPTION =
            new JSONException(Constants.ERROR_STATE_COUNTY).toString();

    @Test
    public void handleResponse_country(){
        APIHelpers.handleResponse(
                "", COUNTRY_OBJECT, "", "", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                assertEquals(new JSONObject(COUNTRY_OBJECT).toString(), response.toString());
            }
            @Override
            public void getJsonException(Exception exception) {}
        });
    }

    @Test
    public void handleException_country() {
        APIHelpers.handleResponse(
                "", TEST_JSONOBJECT_EXCEPTION, "", "", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {
                assertEquals(TEST_JSONOBJECT_EXCEPTION, exception.toString());
            }
        });
    }

    @Test
    public void handleResponse_county(){
        APIHelpers.handleResponse(
                Constants.COUNTY, COUNTY_OBJECT, TEST_COUNTY, TEST_STATE, new VolleyJsonCallback() {
                    @Override
                    public void getJsonData(JSONObject response) {
                        assertEquals(COUNTY_RETURN, response.toString());
                    }
                    @Override
                    public void getJsonException(Exception exception) {}
                });
    }

    @Test
    public void handleException_county(){
        APIHelpers.handleResponse(
                Constants.COUNTY, TEST_JSONARRAY_EXCEPTION, TEST_COUNTY, TEST_STATE, new VolleyJsonCallback() {
                    @Override
                    public void getJsonData(JSONObject response) {}
                    @Override
                    public void getJsonException(Exception exception) {
                        assertEquals(TEST_JSONARRAY_EXCEPTION, exception.toString());
                    }
                });
    }

    @Test
    public void handleResponse_countyHistorical(){
        APIHelpers.handleResponse(
                Constants.COUNTY_HISTORICAL, COUNTY_HISTORICAL_OBJECT, TEST_COUNTY, TEST_STATE, new VolleyJsonCallback() {
                    @Override
                    public void getJsonData(JSONObject response) {
                        assertEquals(COUNTY_RETURN, response.toString());
                    }
                    @Override
                    public void getJsonException(Exception exception) {}
                });
    }

    @Test
    public void handleNotFoundException_countyHistorical(){
        APIHelpers.handleResponse(
                Constants.COUNTY_HISTORICAL, COUNTY_HISTORICAL_OBJECT_NOTFOUND, TEST_COUNTY, TEST_STATE, new VolleyJsonCallback() {
                    @Override
                    public void getJsonData(JSONObject response) {}
                    @Override
                    public void getJsonException(Exception exception) {
                        assertEquals(TEST_NOTFOUND_EXCEPTION, exception.toString());
                    }
                });
    }
}
