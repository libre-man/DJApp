package nl.sdaas.app.sdaas;

import android.util.Log;

public class Logger {
    private final static String TAG = Logger.class.getName();
    private final static int INTERVAL = 5000;

    private int currentChannel = 0;
    private boolean isRunning = true;

    public void setCurrentChannel(int channelIndex) {
        this.currentChannel = channelIndex;
    }

    public void intervalLogger() {
        while (this.isRunning) {
            System.out.println("Data: " + this.currentChannel);

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
