package nl.sdaas.app.sdaas.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import nl.sdaas.app.sdaas.Decoder;
import nl.sdaas.app.sdaas.Logger;
import nl.sdaas.app.sdaas.Session;

/**
 * Created by Devinhillenius on 07/10/16.
 */

public class SdaasService extends Service {
    private final String dummyResponse = "{\n" +
            "    \"success\": true,    \n" +
            "    \"channels\": [\n" +
            "        {\n" +
            "            \"channel_id\": 0,   \n" +
            "            \"color\": -65281,   \n" +
            "            \"url\": \"http://sdaas.nl/stream/0\" \n" +
            "        },\n" +
            "        {\n" +
            "            \"channel_id\": 1,   \n" +
            "            \"color\": -16711681,   \n" +
            "            \"url\": \"http://sdaas.nl/stream/1\"   \n" +
            "        }\n" +
            "    ],\n" +
            "    \"session_name\": \"CoolDisco\" \n" +
            "}";

    private final static String TAG = SdaasService.class.getName();
    /* Binder for binding this service. */
    private final IBinder sdaasBinder = new SdaasBinder();

    private Logger logger;
    private Session session;

    /* Objects needed to receive media button presses. */
    private AudioManager audioManager;
    private ComponentName receiverComponent;

    public SdaasService() {
        createSession(this.dummyResponse);
        setUpLogger();

        // TODO: Get response from Server!
    }

    @Override
    public void onDestroy() {
        /* Unregister the Media Button Receiver (mReceiverComponent). */
        audioManager.unregisterMediaButtonEventReceiver(this.receiverComponent);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getIntExtra("channel", -1) >= 0) {
            this.logger.setCurrentChannel(intent.getIntExtra("channel", -1));
        } else if (intent.getBooleanExtra("nextChannel", false)) {
            this.logger.nextChannel();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpMediaButtonReceiver();
    }

    /**
     * Set up and start the logger.
     */
    private void setUpLogger() {
        this.logger = new Logger(session.getAmountOfChannels());
        /* Set up Logging thread! */
        Thread loggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SdaasService.this.logger.intervalLogger();
            }
        });
        loggingThread.start();
    }

    /**
     * Set up the Media Button receiver that will check whether the headphone button is clicked.
     */
    private void setUpMediaButtonReceiver() {
        /* Set up the Media Button receiver. */
        this.audioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        this.receiverComponent = new ComponentName(this,HeadphoneButtonReceiver.class);
        this.audioManager.registerMediaButtonEventReceiver(this.receiverComponent);
    }

    /**
     * Create a Session if the given JSON response string is of the right format.
     * @param jsonResponse: The JSON response string to load the Session from.
     */
    private void createSession(String jsonResponse) {
        if ((this.session = Decoder.parseInitialSessionResponse(jsonResponse)) == null) {
            Log.d(TAG, "Cannot parse session");
            return;
        }
    }

    public Session getSession() {
        return this.session;
    }

    public static class HeadphoneButtonReceiver extends BroadcastReceiver {

        public HeadphoneButtonReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            KeyEvent keyEvent  = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                Intent channelSwitchIntent = new Intent(context, SdaasService.class);
                channelSwitchIntent.putExtra("nextChannel", true);
                context.startService(channelSwitchIntent);
                System.out.println("BUTTON PRESS: " + intentAction);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.sdaasBinder;
    }

    /* Binder class to handle bounds for this service. */
    public class SdaasBinder extends Binder {
        public SdaasService getService() {
            /* Return this instance of LocalService so clients can call public methods. */
            return SdaasService.this;
        }
    }
}
