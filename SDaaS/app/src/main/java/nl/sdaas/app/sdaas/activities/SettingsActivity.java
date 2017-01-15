package nl.sdaas.app.sdaas.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import nl.sdaas.app.sdaas.Encoder;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.SdaasApplication;
import nl.sdaas.app.sdaas.Server;

public class SettingsActivity extends AppCompatActivity {

    private String gender = "m";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferences();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.maleRadioButton:
                if (checked)
                    this.gender = "m";
                    break;
            case R.id.femaleRadioButton:
                if (checked)
                    this.gender = "f";
                    break;
        }
    }

    private void setPreferences() {
        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            final int year = Integer.parseInt(((EditText)findViewById(R.id.yearText)).getText().toString());
            final int month = Integer.parseInt(((EditText)findViewById(R.id.monthText)).getText().toString());
            final int day = Integer.parseInt(((EditText)findViewById(R.id.dayText)).getText().toString());

            editor.putInt("client_birth_day", day);
            editor.putInt("client_birth_month", month);
            editor.putInt("client_birth_year", year);

            final Server server = ((SdaasApplication)this.getApplication()).getServer();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject message = Encoder.encodeNewClientMessage(year, month, day, gender);
                    System.out.println(message.toString());
                    server.createClient(getApplicationContext(), message);
                }
            });

            thread.start();
            thread.join();

            JSONObject response = server.getResponse();

            if (response.getBoolean("success")) {
                editor.putInt("client_id", response.getInt("client_id"));
                editor.apply();
            }

            startActivity(new Intent(this, JoinSessionActivity.class));
        } catch (NullPointerException|InterruptedException|JSONException e) {
            e.printStackTrace();
        }


    }
}
