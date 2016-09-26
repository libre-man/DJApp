package nl.sdaas.app.sdaas;

import android.util.Log;

public class Logger {
    private final static String TAG = Logger.class.getName();

    private int currentChannel = 0;
    private boolean isChannelSwitched = false;

    public void setCurrentChannel(int channelIndex) {
        this.isChannelSwitched = this.currentChannel != channelIndex;
        this.currentChannel = channelIndex;
    }
}
