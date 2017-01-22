package nl.sdaas.app.sdaas;

import android.app.Application;

public class SdaasApplication extends Application {

    private Server server = new Server("http://sdaas.nl");

    public Server getServer() {
        return this.server;
    }

}
