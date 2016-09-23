package nl.sdaas.app.sdaas;

/**
 * Created by Devinhillenius on 23/09/16.
 */

public class Logger {
    private int currentChannel = 0;
    private boolean isChannelSwitched = false;

    public void setCurrentChannel(int channelIndex) {
        this.isChannelSwitched = this.currentChannel != channelIndex;
        this.currentChannel = channelIndex;
    }
}
