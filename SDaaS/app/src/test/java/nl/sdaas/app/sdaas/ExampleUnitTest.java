package nl.sdaas.app.sdaas;

import org.apache.commons.net.ntp.TimeInfo;
import org.junit.Test;

import static nl.sdaas.app.sdaas.NtpUtils.getTimeInfo;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ntpTest() throws Exception {
        TimeInfo info = getTimeInfo("0.nl.pool.ntp.org");
        info.computeDetails();

        System.out.println(info.getOffset());
    }
}