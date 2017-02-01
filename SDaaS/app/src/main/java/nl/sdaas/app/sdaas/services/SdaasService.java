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
import nl.sdaas.app.sdaas.SdaasApplication;
import nl.sdaas.app.sdaas.Session;
import nl.sdaas.app.sdaas.Streamer;


public class SdaasService extends Service {
    private final static String TAG = SdaasService.class.getName();
    /* Binder for binding this service. */
    private final IBinder sdaasBinder = new SdaasBinder();

    private Logger logger;
    private Session session;
    private Streamer streamer;

    private MediaSession mediaSession;

    @Override
    public void onDestroy() {
        logger.onDestroy();
        this.mediaSession.release();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("initial_data") && intent.hasExtra("join_code")) {
                String data = intent.getStringExtra("initial_data");
                String joinCode = intent.getStringExtra("join_code");
                createSession(data, joinCode);
                setupLogger();
                setupStreamer();
            }
            if (intent.getIntExtra("channel", -1) >= 0) {
                this.logger.setCurrentChannel(intent.getIntExtra("channel", -1));
                this.streamer = new Streamer("0.nl.pool.ntp.org",
                                             this.session.getChannel(this.logger.getCurrentChannel()),
                                                                     this.session.getStart(), 30041);
            } else if (intent.getBooleanExtra("nextChannel", false)) {
                this.logger.nextChannel();
                this.streamer = new Streamer("0.nl.pool.ntp.org",
                                             this.session.getChannel(this.logger.getCurrentChannel()),
                                                                     this.session.getStart(), 30041);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //setUpMediaButtonReceiver();
    }

    public void setChannel(int channel) {
        this.logger.setCurrentChannel(channel);
    }

    /**
     * Set up and start the logger.
     */
    private void setupLogger() {
        this.logger = new Logger(getApplicationContext(), session.getAmountOfChannels(), ((SdaasApplication)this.getApplication()).getServer());
        /* Set up Logging thread! */
        Thread loggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SdaasService.this.logger.intervalLogger();
            }
        });
        loggingThread.start();
    }

    private void setupStreamer() {
        this.streamer = new Streamer("0.nl.pool.ntp.org", this.session.getChannel(0), this.session.getStart(), 30041);
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
     *
     * @param jsonResponse: The JSON response string to load the Session from.
     */
    public void createSession(String jsonResponse, String joinCode) {
        if ((this.session = Decoder.parseInitialSessionResponse(jsonResponse, joinCode)) == null) {
            Log.d(TAG, "Cannot parse session");
        }


    }

    public Session getSession() {
        return this.session;
    }

    public Logger getLogger() {return this.logger; }

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
