package nl.sdaas.app.sdaas;


import android.graphics.Color;

public class Channel {
    private String color;
    private int channelId;
    private String channelUrl;
    private String name;

    public Channel(String color, int channel_id, String channel_url, String name) {
        this.color = color;
        this.channelId = channel_id;
        this.channelUrl = channel_url;
        this.name = name;
    }

    public int getColor() {
        return Color.parseColor(this.color);
    }

    public int getChannelId() {
        return this.channelId;
    }

    public String getChannelUrl() {
        return this.channelUrl;
    }

    public String getName() {return this.name; }
}
