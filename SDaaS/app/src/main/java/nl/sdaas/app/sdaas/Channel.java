package nl.sdaas.app.sdaas;

/**
 * Created by Devinhillenius on 22/09/16.
 */

public class Channel {
    private int color;
    private int channelId;
    private String channelUrl;

    public Channel(int color, int channel_id, String channel_url) {
        this.color = color;
        this.channelId = channel_id;
        this.channelUrl = channel_url;
    }

    public int getColor()           { return this.color; }
    public int getChannelId()       { return this.channelId; }
    public String getChannelUrl()   { return this.channelUrl; }
}
