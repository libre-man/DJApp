package nl.sdaas.app.sdaas;

import java.util.ArrayList;

public class Session {
    private ArrayList<Channel> channels;
    private String name;
    private long partDuration = 30041;
    private String joinCode;

    public Session(String name, String joinCode) {
        this.name = name;
        this.channels = new ArrayList<>();
        this.joinCode = joinCode;
    }

    /**
     * Add a new channel to the list of channels in this session.
     * @param color: The color of the channel to add, using the constants in android.graphics.Color.
     * @param channelId: The id of the channel to add.
     * @param channelUrl: The URL of the stream of the channel to add.
     * @param name: The name of the session.
     */
    public void addChannel(String color, int channelId, String channelUrl, String name, long start) {
        channels.add(new Channel(color, channelId, channelUrl, name, start));
    }

    public String getName() {
        return this.name;
    }

    public int getAmountOfChannels() {
        return this.channels.size();
    }

    public Channel getChannel(int index) {
        if (index >= this.channels.size())
            return null;
        else
            return this.channels.get(index);
    }

    public long getPartDuration() {
        return this.partDuration;
    }

    public String getJoinCode() { return this.joinCode; }

    public void refreshSession(String name, long start, String joinCode) {

    }

}
