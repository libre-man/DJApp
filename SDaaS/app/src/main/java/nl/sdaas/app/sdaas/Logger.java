package nl.sdaas.app.sdaas;

import android.util.Log;

import java.util.HashMap;

public class Logger {
    private final static String TAG = Logger.class.getName();
    private final static int INTERVAL = 5000;

    private int currentChannel = 0;
    private boolean isRunning = true;

    public void setCurrentChannel(int channelIndex) {
        this.currentChannel = channelIndex;

        Log.d(TAG, "Channel switched: " + this.currentChannel);
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

}
