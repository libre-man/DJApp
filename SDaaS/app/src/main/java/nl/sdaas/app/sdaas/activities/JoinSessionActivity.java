package nl.sdaas.app.sdaas.activities;

import android.content.SharedPreferences;
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

public class JoinSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);

        System.out.println("Test0");
        this.createClient();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);

                System.out.println(editText.getText());
            }
        });
    }

    private void sendRequest(String code) {
        Server server = ((SdaasApplication)this.getApplication()).getServer();
    }

    private void createClient() {
        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);

        /* If there is no client, request a client from the server. */
        if (!prefs.contains("client_id")) {
            final Server server = ((SdaasApplication)this.getApplication()).getServer();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    server.createClient(getApplicationContext(), Encoder.encodeNewClientMessage("TestUser", System.currentTimeMillis() / 1000));
                }
            });

            thread.start();

            try {
                thread.join();

                JSONObject response = server.getResponse();

                try {
                    if (response.getBoolean("success")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("client_id", response.getInt("client_id"));
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
