package nl.sdaas.app.sdaas;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by yourivoet on 01/10/16.
 */

public class ParserTest {

    @Test
    public void parserInitialSessionResponse_CorrectJson_ReturnsSession() {
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
                "            \"url\": \"http://sdaas.nl/stream/1\"   \n" +
                "        }\n" +
                "    ],\n" +
                "    \"session_name\": \"CoolDisco\" \n" +
                "}";

        Session s = Parser.parseInitialSessionResponse(json);

        assertNotNull(s);
        assertThat(s.getAmountOfChannels(), is(2));
        assertThat(s.getName(), equalTo("CoolDisco"));
    }

    @Test
    public void parserInitialSessionResponse_FalseJson_ReturnsNull() {
        assertNull(Parser.parseInitialSessionResponse(""));
    }
}
