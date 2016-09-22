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

    public void addChannel(Channel.Color color, int channel_id, String channel_url) {
        channels.add(new Channel(color, channel_id, channel_url));
    }
}
