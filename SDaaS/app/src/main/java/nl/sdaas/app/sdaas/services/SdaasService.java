package nl.sdaas.app.sdaas.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.view.KeyEvent;

import nl.sdaas.app.sdaas.Logger;
import nl.sdaas.app.sdaas.Session;

/**
 * Created by Devinhillenius on 07/10/16.
 */

public class SdaasService extends Service {
    private final static String TAG = SdaasService.class.getName();
    private Logger logger;
    private AudioManager mAudioManager;
    private ComponentName mReceiverComponent;
    private Session session;

    @Override
    public void onDestroy() {
        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getIntExtra("channel", -1) >= 0) {
            this.logger.setCurrentChannel(intent.getIntExtra("channel", -1));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.logger = new Logger();
        /* Set up Logging thread! */
        Thread loggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SdaasService.this.logger.intervalLogger();
            }
        });
        loggingThread.start();

        /* Set up the Media Button receiver. */
        this.mAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        this.mReceiverComponent = new ComponentName(this,HeadphoneButtonReceiver.class);
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                System.out.println("BUTTON PRESS: " + intentAction);
            }
        }
    }
}
