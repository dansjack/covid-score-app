package com.nsc.covidscore.api;

import org.json.JSONException;
import org.json.JSONObject;

public interface VolleyJsonCallback {
    void getJsonData(JSONObject response) throws JSONException;
    void getJsonException(Exception exception);
}
