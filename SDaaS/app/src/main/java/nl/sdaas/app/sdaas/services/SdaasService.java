package nl.sdaas.app.sdaas.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import nl.sdaas.app.sdaas.Logger;

/**
 * Created by Devinhillenius on 07/10/16.
 */

public class SdaasService extends Service {
    private final static String TAG = SdaasService.class.getName();
    private Logger logger;
    private HeadphoneButtonReceiver receiver;

    @Override
    public void onDestroy() {
        unregisterReceiver(this.receiver);
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
        this.receiver = new HeadphoneButtonReceiver();
        /* Set up Logging thread! */
        Thread loggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SdaasService.this.logger.intervalLogger();
            }
        });
        loggingThread.start();

        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(this.receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static  class HeadphoneButtonReceiver extends BroadcastReceiver {

        public HeadphoneButtonReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            System.out.println("BUTTON PRESS:" + intentAction);
        }
    }
}
