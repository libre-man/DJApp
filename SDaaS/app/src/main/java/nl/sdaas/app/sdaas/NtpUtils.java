package nl.sdaas.app.sdaas;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;

public class NtpUtils {
    private final static String TAG = NtpUtils.class.getName();
    private final static int DEFAULT_TIMEOUT = 10000;

    /**
     * Get NTP time info from a host.
     * Based on: https://github.com/Free-Software-for-Android/NTPSync/
     *
     * @param host, host url.
     */
    public static TimeInfo getTimeInfo(String host) {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(DEFAULT_TIMEOUT);

        TimeInfo info = null;

        try {
            client.open();
            Log.d(TAG, "Starting client");
            InetAddress address = InetAddress.getByName(host);
            Log.d(TAG, "Valid address");
            info = client.getTime(address);
            Log.d(TAG, "Getting time");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error getting NTP information " + e.getMessage());
        } finally {
            client.close();
        }

        return info;
    }
}
