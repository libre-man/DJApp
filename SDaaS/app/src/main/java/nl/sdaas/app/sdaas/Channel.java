package nl.sdaas.app.sdaas;

/**
 * Created by Devinhillenius on 22/09/16.
 */

public class Channel {
    private Color color;
    private int channel_id;
    private String channel_url;

    public static enum Color {MAGENTA, CYAN, YELLOW, GREEN};

    public Channel(Color color, int channel_id, String channel_url) {
        this.color = color;
        this.channel_id = channel_id;
        this.channel_url = channel_url;
    }
}
