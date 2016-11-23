package nl.sdaas.app.sdaas;

import android.app.Application;

/**
 * Created by yourivoet on 23/11/2016.
 */

public class SdaasApplication extends Application {

    private Server server = new Server("http://10.1.10.179:8000");

    public Server getServer() {
        return this.server;
    }

}
