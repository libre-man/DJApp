package nl.sdaas.app.sdaas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import nl.sdaas.app.sdaas.R;

/**
 * Created by Devinhillenius on 23/01/17.
 */

public class AboutSdaasActivity extends AppCompatActivity {
    private String prevClass = "JoinSessionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_sdaas);
        this.prevClass = getIntent().getStringExtra("caller");

        TextView about = (TextView)findViewById(R.id.about_sdaas);
        about.setText(Html.fromHtml("<center><strong><h2>sdaas: silent disco as a service</h2></strong></center><br>" +
                                    "<center>Devin Hillenius</center><br><center>Thomas Schaper</center><br><center>Youri Voet</center><br><br>" +
                                    "<center>Thanks to dr. J.A. Burgoyne</center><br>" +
                                    "<center>University of Amsterdam</center>"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    startActivity(new Intent(this, Class.forName(this.prevClass)));
                } catch (Exception e) {
                    startActivity(new Intent(this, JoinSessionActivity.class));
                    Log.d("ERROR:", e.toString());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
