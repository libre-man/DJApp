package nl.sdaas.app.sdaas.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.*;

import nl.sdaas.app.sdaas.ChannelAdapter;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.Session;

public class SessionActivity extends AppCompatActivity {

    private Session session;

    private String[] testListView = {"Test0", "Test1", "Test2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        String dummyResponse = "{\n" +
                "    \"success\": true,    \n" +
                "    \"channels\": [\n" +
                "        {\n" +
                "            \"channel_id\": 0,   \n" +
                "            \"color\": 2,   \n" +
                "            \"url\": \"http://sdaas.nl/stream/0\" \n" +
                "        }\n" +
                "    ],\n" +
                "    \"session_name\": \"CoolDisco\" \n" +
                "}";

        try {
            this.parseResponse(dummyResponse);

            System.out.println("Test");

            ChannelAdapter adapter = new ChannelAdapter(this, this.session);

            ListView listView = (ListView) findViewById(R.id.channelListView);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            System.out.println("Fuck");
            e.printStackTrace();
        }
    }

    private boolean parseResponse(String jsonString) throws JSONException {
        JSONObject sessionInfo = new JSONObject(jsonString);

        if (sessionInfo.getBoolean("success")) {
            /* Get the session name. */
            session = new Session(sessionInfo.getString("session_name"));

            /* Get the channels in the session. */
            JSONArray channels = sessionInfo.getJSONArray("channels");
            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                session.addChannel(channel.getInt("color"), channel.getInt("channel_id"),
                        channel.getString("url"));
            }
            return true;
        }

        throw new JSONException("Unsuccessful");
    }

}
