package nl.sdaas.app.sdaas;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;

import static nl.sdaas.app.sdaas.NtpUtils.getTimeInfo;

public class Streamer {
    private final static String TAG = Streamer.class.getName();

    private Channel currentChannel;
    private int currentPart;

    private MediaPlayer currentPlayer;
    private MediaPlayer nextPlayer;

//    private Runnable runnable;

    public Streamer(String ntpHost, Channel channel, final long start, final long partDuration) {
        System.out.println("Streamer setting up!");
        this.currentChannel = channel;

        // TODO: this is a hack, please fix getting time info asynchronously!!
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TimeInfo ntpInfo = getTimeInfo(ntpHost);
        ntpInfo.computeDetails();

        /* Apply offset from NTP server. */
        final long offset = ntpInfo.getOffset();

        final long current = System.currentTimeMillis() + ntpInfo.getOffset();
        this.currentPart = (int)((current - start * 1000) / partDuration);
        Log.d(TAG, Integer.toString(this.currentPart) + " PART!");
        Log.d(TAG, Long.toString(start * 1000) + " START!");
        Log.d(TAG, Long.toString(current) + " CURRENT!");
        final int seekPart = currentPart;

        this.currentPlayer = new MediaPlayer();
        this.currentPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.currentPlayer.setLooping(false);

        this.nextPlayer = new MediaPlayer();
        this.nextPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.nextPlayer.setLooping(false);

        try {
            this.currentPlayer.setDataSource(channel.getChannelUrl() + "/part" + this.currentPart + ".mp3");
            this.currentPlayer.prepareAsync();

            this.currentPart++;

            this.nextPlayer.setDataSource(channel.getChannelUrl() + "/part" + (this.currentPart) + ".mp3");
            this.nextPlayer.prepareAsync();
        } catch (Exception e) {
            Log.d(TAG, "Error setting data source " + e.getMessage());
        }

        setOnCompletionListener();

        this.currentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                int seekto = (int)((System.currentTimeMillis() + offset) - start - (seekPart * player.getDuration()));
                player.seekTo(seekto);
            }
        });

        this.currentPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
            }
        });

        setNextPlayerListener();

//        final Handler handler = new Handler();
//         this.runnable = new Runnable() {
//            int previousPos = -1;
//
//            public void run() {
//                if (currentPlayer.isPlaying()) {
//                    if (currentPlayer.getCurrentPosition() == previousPos) {
//                        // Detected stall -> restart player.
//                    }
//                    previousPos = currentPlayer.getCurrentPosition();
//                }
//
//                handler.postDelayed(this, 2500);
//            }
//        };
//
//        this.runnable.run();
    }

    private void setOnCompletionListener() {
        this.currentPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentPlayer.release();
                currentPlayer = nextPlayer;
                setOnCompletionListener();

                nextPlayer = new MediaPlayer();
                nextPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                currentPart++;

                System.out.println(Integer.toString(currentPart) + "PART!");

                try {
                    nextPlayer.setDataSource(currentChannel.getChannelUrl() + "/part" + currentPart + ".mp3");
                    nextPlayer.prepareAsync();
                } catch (IllegalArgumentException|IOException e) {
                    Log.d(TAG, "Error setting data source " + e.getMessage());
                }

                setNextPlayerListener();
            }
        });
    }

    private void setNextPlayerListener() {
        this.nextPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                currentPlayer.setNextMediaPlayer(player);
            }
        });
    }

    public void release() {
        this.currentPlayer.release();
        this.nextPlayer.release();
    }

}
