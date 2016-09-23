package nl.sdaas.app.sdaas;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Devinhillenius on 23/09/16.
 */

public class Parser {

    private final static String TAG = Parser.class.getName();

    public static Session parseInitialSessionResponse(String jsonString) {
        try {
            JSONObject sessionInfo = new JSONObject(jsonString);

            if (sessionInfo.getBoolean("success")) {
                Session session = new Session(sessionInfo.getString("session_name"));

                /* Get the channels in the session. */
                JSONArray channels = sessionInfo.getJSONArray("channels");
                for (int i = 0; i < channels.length(); i++) {
                    JSONObject channel = channels.getJSONObject(i);
                    session.addChannel(channel.getInt("color"), channel.getInt("channel_id"),
                            channel.getString("url"));
                }
                return session;
            }
        } catch (JSONException ex) {
            Log.d(TAG, ex.getMessage());
        }

        return null;
    }

}
