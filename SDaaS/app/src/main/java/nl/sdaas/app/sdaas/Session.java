package nl.sdaas.app.sdaas;

import java.util.ArrayList;

public class Session {
    private ArrayList<Channel> channels;
    private String name;

    public Session(String name) {
        this.name = name;
        this.channels = new ArrayList<>();
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
}
