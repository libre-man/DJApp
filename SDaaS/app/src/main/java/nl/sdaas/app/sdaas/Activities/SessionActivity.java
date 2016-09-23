package nl.sdaas.app.sdaas.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import nl.sdaas.app.sdaas.ChannelAdapter;
import nl.sdaas.app.sdaas.Parser;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.Session;

public class SessionActivity extends AppCompatActivity {

    private final static String TAG = SessionActivity.class.getName();

    private Session session;

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

        if ((this.session = Parser.parseInitialSessionResponse(dummyResponse)) == null) {
            Log.d(TAG, "Cannot parse session");
            return;
        }

        /* Set session name. */
        TextView sessionName = (TextView) findViewById(R.id.sessionName);
        if (sessionName != null)
            sessionName.setText(String.format("#%s", this.session.getName()));

        ChannelAdapter adapter = new ChannelAdapter(this, this.session);

        ListView listView = (ListView) findViewById(R.id.channelListView);
        if (listView != null)
            listView.setAdapter(adapter);
    }


}
