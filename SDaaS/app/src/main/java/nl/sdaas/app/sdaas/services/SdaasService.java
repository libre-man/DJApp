package nl.sdaas.app.sdaas.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import nl.sdaas.app.sdaas.Logger;

/**
 * Created by Devinhillenius on 07/10/16.
 */

public class SdaasService extends Service {
    private Logger logger;

    @Override
    public void onDestroy() {
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

        Thread loggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SdaasService.this.logger.intervalLogger();
            }
        });

        loggingThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
