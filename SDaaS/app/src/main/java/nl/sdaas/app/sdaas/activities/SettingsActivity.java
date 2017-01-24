package nl.sdaas.app.sdaas.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Calendar;

import nl.sdaas.app.sdaas.Encoder;
import nl.sdaas.app.sdaas.R;
import nl.sdaas.app.sdaas.SdaasApplication;
import nl.sdaas.app.sdaas.Server;

public class SettingsActivity extends AppCompatActivity {

    private String gender = "m";
    private boolean initialLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
        if (prefs.contains("client_id")) {
            this.initialLaunch = false;
            ((EditText) findViewById(R.id.dayText)).setText(Integer.toString(prefs.getInt("client_birth_day", 0)), TextView.BufferType.EDITABLE);
            ((EditText) findViewById(R.id.monthText)).setText(Integer.toString(prefs.getInt("client_birth_month", 0)), TextView.BufferType.EDITABLE);
            ((EditText) findViewById(R.id.yearText)).setText(Integer.toString(prefs.getInt("client_birth_year", 0)), TextView.BufferType.EDITABLE);
            if (prefs.getString("client_gender", "m") == "m") {
                ((RadioButton) findViewById(R.id.maleRadioButton)).setChecked(true);
                this.gender = "m";
            } else {
                ((RadioButton) findViewById(R.id.femaleRadioButton)).setChecked(true);
                this.gender = "f";
            }
            ((Button)findViewById(R.id.deleteButton)).setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreferences();
            }
        });
        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePreferences();
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
        final SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            final int year, month, day;
            // Worst code ever incoming:
            try {
                year = Integer.parseInt(((EditText) findViewById(R.id.yearText)).getText().toString());
                if (year < 1895 || year > Calendar.getInstance().get(Calendar.YEAR))
                    throw new UnsupportedOperationException();
            } catch(Exception e) {
                ((EditText) findViewById(R.id.yearText)).setError("Year wrong!");
                return;
            }
            try {
                month = Integer.parseInt(((EditText) findViewById(R.id.monthText)).getText().toString());
                if (month < 1 || month > 12)
                    throw new UnsupportedOperationException();
            } catch(Exception e) {
                ((EditText) findViewById(R.id.monthText)).setError("Month wrong!");
                return;
            }
            try {
                day = Integer.parseInt(((EditText) findViewById(R.id.dayText)).getText().toString());
                if (day < 1 || day > 31)
                    throw new UnsupportedOperationException();
            } catch(Exception e) {
                ((EditText) findViewById(R.id.dayText)).setError("Day wrong!");
                return;
            }
            editor.putInt("client_birth_day", day);
            editor.putInt("client_birth_month", month);
            editor.putInt("client_birth_year", year);
            editor.putString("client_gender", gender);

            final Server server = ((SdaasApplication)this.getApplication()).getServer();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (initialLaunch) {
                        JSONObject message = Encoder.encodeNewClientMessage(year, month, day,
                                                                            gender);
                        server.createClient(getApplicationContext(), message);
                    } else {
                        JSONObject message =
                                Encoder.encodeChangeClientMessage(prefs.getInt("client_id", 0),
                                                                  year, month, day, gender);
                        server.editClient(getApplicationContext(), message);
                    }
                }
            });

            thread.start();
            thread.join();

            JSONObject response = server.getResponse();

            if (response.getBoolean("success") && this.initialLaunch) {
                editor.putInt("client_id", response.getInt("client_id"));
            }
            editor.apply();
            if (this.initialLaunch)
                startActivity(new Intent(this, JoinSessionActivity.class));
            else
                startActivity(new Intent(this, SessionActivity.class));

        } catch (NullPointerException|InterruptedException|JSONException e) {
            e.printStackTrace();
        }
    }

    private void deletePreferences() {
        SharedPreferences prefs = getSharedPreferences("sdaas", MODE_PRIVATE);
        final JSONObject message = Encoder.encodeDeleteClientMessage(prefs.getInt("client_id", 0));
        final Server server = ((SdaasApplication)this.getApplication()).getServer();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!initialLaunch) {
                    server.deleteClient(getApplicationContext(), message);
                }
            }
        });
        thread.start();
        prefs.edit().clear().commit();
        finish();
        startActivity(getIntent());
    }
}
