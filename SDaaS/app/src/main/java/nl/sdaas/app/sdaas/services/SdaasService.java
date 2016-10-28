package nl.sdaas.app.sdaas.services;

import android.app.Service;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
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

    private MediaSession mediaSession;

    @Override
    public void onDestroy() {
        this.mediaSession.release();
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
        createSession(this.dummyResponse);
        setUpLogger();
        setUpMediaButtonReceiver();
    }

    public void setChannel(int channel) {
        this.logger.setCurrentChannel(channel);
    }

    /**
     * Set up and start the logger.
     */
    private void setUpLogger() {
        this.logger = new Logger(getApplicationContext(), session.getAmountOfChannels());
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
        mediaSession = new MediaSession(getApplicationContext(), "TAG");
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                String intentAction = mediaButtonIntent.getAction();
                System.out.println(intentAction);

                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                    if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
                        SdaasService.this.logger.nextChannel();
                    }
                }

                return true;
            }
        });

        PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY_PAUSE)
                .setState(PlaybackState.STATE_PLAYING, 0, 0, 0)
                .build();

        mediaSession.setPlaybackState(state);
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setActive(true);
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
