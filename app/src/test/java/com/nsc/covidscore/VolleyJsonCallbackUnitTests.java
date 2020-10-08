package com.nsc.covidscore;

import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class VolleyJsonCallbackUnitTests {
    @Test
    public void getJsonData_invoked() throws JSONException {
       final VolleyJsonCallback callback = mock(VolleyJsonCallback.class);
        JSONObject jsonObject = new JSONObject();
        callback.getJsonData(jsonObject);
        verify(callback, times(1)).getJsonData(jsonObject);
    }

    @Test
    public void getJsonException_invoked() {
        final VolleyJsonCallback callback = mock(VolleyJsonCallback.class);
        JSONException jsonException = new JSONException("Test Exception");
        callback.getJsonException(jsonException);
        verify(callback, times(1)).getJsonException(jsonException);
    }
}