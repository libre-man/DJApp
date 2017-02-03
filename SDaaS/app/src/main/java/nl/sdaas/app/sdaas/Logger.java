package nl.sdaas.app.sdaas;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Logger implements SensorEventListener{
    private final static String TAG = Logger.class.getName();
    private final static int INTERVAL = 5000;

    private final int AMOUNT_OF_CHANNELS;

    private int currentChannel = 0;
    private int channelId = 0;
    private boolean isRunning = true;
    private Context context;
    private Server server;
    private String client_id;

    /* Accelerometer variables. */
    private Sensor accelerometer;
    private SensorManager manager;

    public Logger(Context context, int amountOfChannels, Server server) {
        this.AMOUNT_OF_CHANNELS = amountOfChannels;
        this.context = context;
        this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if ((this.accelerometer = checkForAccelerometer()) != null) {
            this.manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        this.server = server;
        SharedPreferences prefs = this.context.getSharedPreferences("sdaas", MODE_PRIVATE);
        this.client_id = Integer.toString(prefs.getInt("client_id", -1));
    }

    public void setCurrentChannel(int channelIndex, int channelId) {
        this.currentChannel = channelIndex;
        this.channelId = channelId;

        Log.d(TAG, "Channel switched: " + this.currentChannel);
    }

    public void nextChannel(ArrayList<Channel> channels) {
        int nextChannel = (currentChannel + 1) % AMOUNT_OF_CHANNELS;
        this.setCurrentChannel(nextChannel, channels.get(nextChannel).getChannelId());
    }

    public void intervalLogger() {
        while (this.isRunning) {
            HashMap data = new HashMap<String, String>();
            data.put("client_id", client_id);
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

    public Sensor checkForAccelerometer() {
        if (this.manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            return this.manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.d("X CHANGED: ", Float.toString(event.values[0]));
//        Log.d("Y CHANGED: ", Float.toString(event.values[1]));
//        Log.d("Z CHANGED: ", Float.toString(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy Changed to: " + Integer.toString(accuracy));
        return;
    }

    public int getCurrentChannel() { return this.currentChannel; }

    public void onDestroy() {
        if (this.accelerometer != null) {
            this.manager.unregisterListener(this, accelerometer);
        }
    }
}