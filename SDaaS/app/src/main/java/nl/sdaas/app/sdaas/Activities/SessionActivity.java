package nl.sdaas.app.sdaas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import nl.sdaas.app.sdaas.ChannelAdapter;
import nl.sdaas.app.sdaas.Logger;
import nl.sdaas.app.sdaas.Decoder;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.Session;
import nl.sdaas.app.sdaas.services.SdaasService;

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
                "            \"color\": -65281,   \n" +
                "            \"url\": \"http://sdaas.nl/stream/0\" \n" +
                "        },\n" +
                "        {\n" +
                "            \"channel_id\": 1,   \n" +
                "            \"color\": -16711681,   \n" +
                "            \"url\": \"http://sdaas.nl/stream/1\"   \n" +
                "        }\n" +
                "    ],\n" +
                "    \"session_name\": \"CoolDisco\" \n" +
                "}";

        if ((this.session = Decoder.parseInitialSessionResponse(dummyResponse)) == null) {
            Log.d(TAG, "Cannot parse session");
            return;
        }

        /* Set session name. */
        TextView sessionName = (TextView) findViewById(R.id.sessionName);
        if (sessionName != null)
            sessionName.setText(String.format("#%s", this.session.getName()));

        Intent startIntent = new Intent(this, SdaasService.class);
        startService(startIntent);
        // TODO: Send session info to Logger!
        ChannelAdapter adapter = new ChannelAdapter(this, this.session);

        ListView listView = (ListView) findViewById(R.id.channelListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent channelChangeIntent = new Intent(SessionActivity.this, SdaasService.class);
                    channelChangeIntent.putExtra("channel", position);
                    startService(channelChangeIntent);
                }
            });
        }
    }
}
