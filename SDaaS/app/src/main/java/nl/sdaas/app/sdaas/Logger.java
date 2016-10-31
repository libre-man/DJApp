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
    private Server server;

    /* Accelerometer variables. */
    private Sensor accelerometer;


    public Logger(Context context, int amountOfChannels) {
        this.AMOUNT_OF_CHANNELS = amountOfChannels;
        this.context = context;
        this.server = new Server("http://10.1.10.179:5000");
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

            this.server.logData(this.context, Encoder.encodeDataLogMessage(data));

            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                this.isRunning = false;
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
