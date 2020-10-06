package com.nsc.covidscore.api;

import org.json.JSONObject;

public interface VolleyJsonCallback {
    void getJsonData(JSONObject response);
    void getJsonException(Exception exception);
}
