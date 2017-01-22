package nl.sdaas.app.sdaas;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by yourivoet on 01/10/16.
 */

public class DecoderTest {

    @Test
    public void parserInitialSessionResponse_CorrectJson_ReturnsSession() {
        String json = "{\n" +
                "    \"success\": true,    \n" +
                "    \"channels\": [\n" +
                "        {\n" +
                "            \"channel_id\": 0,   \n" +
                "            \"color\": \"#ffffff\",   \n" +
                "            \"url\": \"http://sdaas.nl/stream/0\", \n" +
                "            \"name\": \"channel 1\" \n" +
                "        },\n" +
                "        {\n" +
                "            \"channel_id\": 1,   \n" +
                "            \"color\": \"#ffffff\",   \n" +
                "            \"url\": \"http://sdaas.nl/stream/1\",   \n" +
                "            \"name\": \"channel 2\" \n" +
                "        }\n" +
                "    ],\n" +
                "    \"session_name\": \"CoolDisco\" \n" +
                "    \"session_start\": 1000000 \n" +
                "}";

        Session s = Decoder.parseInitialSessionResponse(json, "0");

        assertNotNull(s);
        assertThat(s.getAmountOfChannels(), is(2));
        assertThat(s.getName(), equalTo("CoolDisco"));
    }

    @Test
    public void parserInitialSessionResponse_FalseJson_ReturnsNull() {
        assertNull(Decoder.parseInitialSessionResponse("", "0"));
    }

    @Test
    public void parserInitialSessionResponse_FalseScheme_ReturnsNull() {
        String json = "{\n" +
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
                "            \"missing_url\": \"http://sdaas.nl/stream/1\"   \n" +
                "        }\n" +
                "    ],\n" +
                "    \"session_name\": \"CoolDisco\" \n" +
                "    \"session_start\": 1000000 \n" +
                "}";

        assertNull(Decoder.parseInitialSessionResponse(json, "0"));
    }
}
