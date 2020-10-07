package com.nsc.covidscore;

import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class VolleyJsonCallbackUnitTestS {

    @Test
    public void getJsonData_invoked() throws JSONException {
       final VolleyJsonCallback callback = mock(VolleyJsonCallback.class);
        JSONObject jsonObject = new JSONObject();
        callback.getJsonData(jsonObject);
        verify(callback).getJsonData(jsonObject);
    }

    @Test
    public void getJsonException_invoked() {
        final VolleyJsonCallback callback = mock(VolleyJsonCallback.class);
        JSONException jsonException = new JSONException("Test Exception");
        callback.getJsonException(jsonException);
        verify(callback).getJsonException(jsonException);
    }
}