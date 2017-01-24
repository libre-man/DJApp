package nl.sdaas.app.sdaas.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import nl.sdaas.app.sdaas.Encoder;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.SdaasApplication;
import nl.sdaas.app.sdaas.Server;
import nl.sdaas.app.sdaas.services.SdaasService;

public class JoinSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);

        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);

        if (!prefs.contains("client_id")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        } else if (!checkClientId(prefs.getInt("client_id", 0))) {
            prefs.edit().clear().commit();
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);

                String code = editText.getText().toString();
                sendRequest(code);
            }
        });
    }

    private void sendRequest(final String code) {
        final Server server = ((SdaasApplication)this.getApplication()).getServer();
        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
        final int clientId = prefs.getInt("client_id", 0);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                server.joinSession(getApplicationContext(), Encoder.encodeJoinSessionMessage(clientId, code));
            }
        });

        thread.start();
        try {
            thread.join();
            JSONObject response = server.getResponse();
            if (response.optBoolean("success")) {
                Intent intent = new Intent(this, SdaasService.class);
                intent.putExtra("initial_data", response.toString());
                intent.putExtra("join_code", code);
                startService(intent);
                startActivity(new Intent(this, SessionActivity.class));
            } else {
                ((EditText) findViewById(R.id.editText)).setError("Join code not found!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkClientId(int clientId) {
        final JSONObject message = Encoder.encodeCheckClientMessage(clientId);
        final Server server = ((SdaasApplication)this.getApplication()).getServer();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                server.checkClient(getApplicationContext(), message);
            }
        });

        thread.start();

        try {
            thread.join();
            JSONObject response = server.getResponse();
            System.out.println(response.toString());
            return response.optBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
