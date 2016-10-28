package nl.sdaas.app.sdaas;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Session implements Parcelable {
    private ArrayList<Channel> channels;
    private String name;
    private int currentChannel;

    public Session(String name) {
        this.name = name;
        this.channels = new ArrayList<>();
        this.currentChannel = 0;
    }

    /**
     * Add a new channel to the list of channels in this session.
     * @param color: The color of the channel to add, using the constants in android.graphics.Color.
     * @param channelId: The id of the channel to add.
     * @param channelUrl: The URL of the stream of the channel to add.
     */
    public void addChannel(int color, int channelId, String channelUrl) {
        channels.add(new Channel(color, channelId, channelUrl));
    }

    public String getName() {
        return this.name;
    }

    public int getAmountOfChannels() {
        return this.channels.size();
    }

    public Channel getChannel(int index) {
        return this.channels.get(index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
