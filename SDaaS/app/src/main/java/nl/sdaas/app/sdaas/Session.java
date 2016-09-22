package nl.sdaas.app.sdaas;

/**
 * Created by Devinhillenius on 22/09/16.
 */

import java.util.ArrayList;

public class Session {
    private ArrayList<Channel> channels;
    private String name;

    public Session(String name) {
        this.name = name;
        this.channels = new ArrayList<Channel>();
    }

    public void addChannel(int color, int channelId, String channelUrl) {
        channels.add(new Channel(color, channelId, channelUrl));
    }

    public String getName()                 { return this.name; }
    public int numOfChannels()              { return this.channels.size(); }
    public Channel getChannel(int index)    { return this.channels.get(index); }
}
