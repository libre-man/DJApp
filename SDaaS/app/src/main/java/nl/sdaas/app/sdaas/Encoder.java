package nl.sdaas.app.sdaas;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Devinhillenius on 07/10/16.
 */

public class Encoder {
    private final static String TAG = Encoder.class.getName();
    private final static String[] REQUIRED_KEYS = {"client_id", "session_id", "channel_id"};

    public static JSONObject encodeDataLogMessage(HashMap<String, String> data) {
        try {
            if (containsRequiredKeys(data)) {
                JSONObject dataLogMessage = new JSONObject();
                dataLogMessage.put("time", System.currentTimeMillis() / 1000);
                dataLogMessage.put("data", new JSONObject(data));
                return dataLogMessage;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

    private static boolean containsRequiredKeys(HashMap<String, String> data) {
        for (String key : REQUIRED_KEYS) {
            if (!data.containsKey(key)) return false;
        }
        return true;
    }
}
