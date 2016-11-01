package nl.sdaas.app.sdaas.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import nl.sdaas.app.sdaas.ChannelAdapter;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.Session;
import nl.sdaas.app.sdaas.services.SdaasService;

public class SessionActivity extends AppCompatActivity {
    SdaasService service;
    boolean bound = false;

    private final static String TAG = SessionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Intent startIntent = new Intent(this, SdaasService.class);
        bindService(startIntent, connection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    private void checkForClicks(ChannelAdapter adapter) {
        ListView listView = (ListView) findViewById(R.id.channelListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SessionActivity.this.service.setChannel(position);
                }
            });
        }
    }

    private void setSessionName(String name) {
        TextView sessionName = (TextView) findViewById(R.id.sessionName);
        if (sessionName != null)
            sessionName.setText(String.format("#%s", name));
    }

    private Session getSession() {
        if (this.bound)
            return service.getSession();

        return null;
    }
    private void unbindService() {
        if (this.bound) {
            unbindService(connection);
            this.bound = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder bService) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SdaasService.SdaasBinder binder = (SdaasService.SdaasBinder) bService;
            service = binder.getService();
            bound = true;
            Session session = getSession();

            /* Set session name. */
            if (session == null) {
                setSessionName("SessionError");
            } else {
                setSessionName(session.getName());
                checkForClicks(new ChannelAdapter(SessionActivity.this, session));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
}
