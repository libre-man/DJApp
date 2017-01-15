package nl.sdaas.app.sdaas;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.SocketException;

public class NtpUtils {
    private final static String TAG = NtpUtils.class.getName();
    private final static int DEFAULT_TIMEOUT = 5000;

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
            Log.d(TAG, "Opening client");

            InetAddress address = InetAddress.getByName(host);

            while (info == null) {
                Log.d(TAG, "Trying");
                info = getTime(client, address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Unable to open socket");
        } finally {
            client.close();
            Log.d(TAG, "Closing client");
        }

        return info;
    }

    private static TimeInfo getTime(NTPUDPClient client, InetAddress address) {
        TimeInfo info = null;

        try {
            info = client.getTime(address);
            Log.d(TAG, "Getting time");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error getting NTP information " + e.getMessage());
        }

        return info;
    }
}
