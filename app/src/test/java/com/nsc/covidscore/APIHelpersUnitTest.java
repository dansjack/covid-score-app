package com.nsc.covidscore;

import com.nsc.covidscore.api.APIHelpers;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class APIHelpersUnitTest {
    private static final String testJsonObject = "{testKey: testValue}";
    private static final String testJsonException = new JSONException(
            "A JSONObject text must begin with '{' at 1 [character 2 line 1]").toString();

    @Test
    public void handleResponse_county(){
        APIHelpers.handleResponse(
                "", testJsonObject, "", "", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                System.out.println(response.toString());
                assertEquals(new JSONObject(testJsonObject).toString(), response.toString());
            }
            @Override
            public void getJsonException(Exception exception) {}
        });
    }

    @Test
    public void handleException_county() {
        APIHelpers.handleResponse(
                "", testJsonException, "", "", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {
                assertEquals(testJsonException, exception.toString());
            }
        });
    }
}
