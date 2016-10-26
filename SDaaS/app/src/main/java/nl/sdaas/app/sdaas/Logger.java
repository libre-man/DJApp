package nl.sdaas.app.sdaas;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.HashMap;

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

    public boolean checkForAccelerometer() {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            this.accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            return true;
        }

        return false;
    }

}
