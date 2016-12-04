package nl.sdaas.app.sdaas;

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

            InetAddress address = InetAddress.getByName(host);
            info = client.getTime(address);
        } catch (Exception e) {
            Log.d(TAG, "Error getting NTP information " + e.getMessage());
        } finally {
            client.close();
        }

        return info;
    }

}
