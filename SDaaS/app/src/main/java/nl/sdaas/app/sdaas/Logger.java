package nl.sdaas.app.sdaas;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.HashMap;

public class Logger implements SensorEventListener{
    private final static String TAG = Logger.class.getName();
    private final static int INTERVAL = 5000;

    private final int AMOUNT_OF_CHANNELS;

    private int currentChannel = 0;
    private boolean isRunning = true;
    private Context context;

    /* Accelerometer variables. */
    private Sensor accelerometer;
    private SensorManager manager;


    public Logger(Context context, int amountOfChannels) {
        this.AMOUNT_OF_CHANNELS = amountOfChannels;
        this.context = context;
        this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if ((this.accelerometer = checkForAccelerometer()) != null) {
            this.manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
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
            System.out.println(Encoder.encodeDataLogMessage(data));

            try {
                Thread.sleep(INTERVAL);
            } catch(InterruptedException e) {
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
        Log.d("X CHANGED: ", Float.toString(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy Changed to: " + Integer.toString(accuracy));
        return;
    }

    public void onDestroy() {
        if (this.accelerometer != null) {
            this.manager.unregisterListener(this, accelerometer);
        }
    }
}
