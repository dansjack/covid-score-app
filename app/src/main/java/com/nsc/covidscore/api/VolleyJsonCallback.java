package com.nsc.covidscore.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface VolleyJsonCallback {
    void getJsonData(JSONObject response) throws JSONException, IOException;
    void getJsonException(Exception exception);
    void getString(String response);
}
