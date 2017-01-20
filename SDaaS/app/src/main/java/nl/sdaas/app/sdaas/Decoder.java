package nl.sdaas.app.sdaas;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Decoder {

    private final static String TAG = Decoder.class.getName();

    public static Session parseInitialSessionResponse(String jsonString) {
        try {
            JSONObject sessionInfo = new JSONObject(jsonString);

            if (sessionInfo.getBoolean("success")) {
                Session session = new Session(sessionInfo.getString("session_name"), sessionInfo.getLong("session_start"));

                /* Get the channels in the session. */
                JSONArray channels = sessionInfo.getJSONArray("channels");
                for (int i = 0; i < channels.length(); i++) {
                    JSONObject channel = channels.getJSONObject(i);
                    System.out.println(channel.toString());
                    session.addChannel(channel.getString("color"), channel.getInt("channel_id"),
                                       channel.getString("url"), channel.getString("name"));
                }
                return session;
            }
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
        }

        return null;
    }

}
