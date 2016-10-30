package nl.sdaas.app.sdaas;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class Logger {
    private final static String TAG = Logger.class.getName();
    private final static int INTERVAL = 5000;

    private final int AMOUNT_OF_CHANNELS;

    private int currentChannel = 0;
    private boolean isRunning = true;
    private Context context;

    /* Accelerometer variables. */
    private Sensor accelerometer;


    public Logger(Context context, int amountOfChannels) {
        this.AMOUNT_OF_CHANNELS = amountOfChannels;
        this.context = context;
    }

    public void setCurrentChannel(int channelIndex) {
        this.currentChannel = channelIndex;

        Log.d(TAG, "Channel switched: " + this.currentChannel);
    }

    public void nextChannel() {
        this.setCurrentChannel((currentChannel + 1) % AMOUNT_OF_CHANNELS);
    }

    public void intervalLogger() {
        while (this.isRunning) {
            HashMap data = new HashMap<String, String>();
            data.put("client_id", "0");
            data.put("session_id", "0");
            data.put("channel_id", Integer.toString(currentChannel));

            JSONObject encoded = Encoder.encodeDataLogMessage(data);

            try {
                StringEntity entity = new StringEntity(encoded.toString());

                SyncHttpClient client = new SyncHttpClient();

                client.post(this.context, "http://10.1.10.179:5000/log_data", entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println("Great success!");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println("Great failure! " + statusCode);
                    }
                });

                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    this.isRunning = false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void toggleRunning() {
        this.isRunning = !this.isRunning;
    }

    public boolean checkForAccelerometer() {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            this.accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            return true;
        }

        return false;
    }

}
