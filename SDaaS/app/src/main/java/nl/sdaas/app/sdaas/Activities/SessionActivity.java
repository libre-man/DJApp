package nl.sdaas.app.sdaas.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import nl.sdaas.app.sdaas.ChannelAdapter;
import nl.sdaas.app.sdaas.Encoder;
import nl.sdaas.app.sdaas.Logger;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.SdaasApplication;
import nl.sdaas.app.sdaas.Server;
import nl.sdaas.app.sdaas.Session;
import nl.sdaas.app.sdaas.services.SdaasService;

public class SessionActivity extends AppCompatActivity {
    SdaasService service;
    volatile boolean bound = false;

    private final static String TAG = SessionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent startIntent = new Intent(this, SdaasService.class);
        bindService(startIntent, connection, Context.BIND_AUTO_CREATE);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Button pauseButton = (Button) findViewById(R.id.pauseButton);
                                String buttonText = " Current: ";
                                buttonText += getSession().getChannel(getLogger().getCurrentChannel()).getName();
                                pauseButton.setText(buttonText);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        ArrayList<String> classStack;
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshAction();
                return true;

            case R.id.back:
                startActivity(new Intent(this, JoinSessionActivity.class));
                return true;

            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                classStack = new ArrayList<>();
                classStack.add(getIntent().getComponent().getClassName());
                intent.putExtra("caller", classStack);
                startActivity(intent);
                return true;

            case R.id.help:
                intent = new Intent(this, AboutSdaasActivity.class);
                classStack = new ArrayList<>();
                classStack.add(getIntent().getComponent().getClassName());
                intent.putExtra("caller", classStack);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void refreshAction() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!bound)
                    return;
                Server server = ((SdaasApplication)getApplication()).getServer();
                SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
                final int clientId = prefs.getInt("client_id", -1);

                server.joinSession(getApplicationContext(),
                        Encoder.encodeJoinSessionMessage(clientId,
                                getSession().
                                        getJoinCode()));
                JSONObject response = server.getResponse();

                if (response.optBoolean("success")) {
                    service.createSession(response.toString(), getSession().getJoinCode());
                }

                finish();
                startActivity(getIntent());
            }
        });
        thread.start();
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

    private Logger getLogger() {
        if (this.bound)
            return service.getLogger();

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
