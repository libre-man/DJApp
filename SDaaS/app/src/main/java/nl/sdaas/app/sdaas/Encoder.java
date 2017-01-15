package nl.sdaas.app.sdaas;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;


public class Encoder {
    private final static String TAG = Encoder.class.getName();
    private final static String[] REQUIRED_KEYS = {"client_id", "session_id", "channel_id"};

    public static JSONObject encodeNewClientMessage(int year, int month, int day, String gender) {
        try {
            JSONObject message = new JSONObject();

            message.put("time", System.currentTimeMillis() / 1000);

            JSONObject data = new JSONObject();
            data.put("birth_year", year);
            data.put("birth_month", month);
            data.put("birth_day", day);
            data.put("gender", gender);

            message.put("data", data);

            return message;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

    public static JSONObject encodeJoinSessionMessage(int clientId, String code) {
        try {
            JSONObject message = new JSONObject();

            message.put("time", System.currentTimeMillis() / 1000);

            JSONObject data = new JSONObject();
            data.put("client_id", clientId);
            data.put("session_id", Integer.parseInt(code));

            message.put("data", data);

            return message;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

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
