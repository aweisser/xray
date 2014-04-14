package de.aw.xray.agent.socket;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author armin.weisser
 */
public class SocketBasedXrayServerImplTest {

    private XrayServer xrayServer;

    @Before
    public void setUp() {
        xrayServer = new SocketBasedXrayServerImpl(4713);
    }

    @Test
    public void testPushEventBeforeStart() throws IOException {
        xrayServer.pushEvent(new XrayEvent("type", "result"));
        Assert.assertNotNull("This should not fail at all");
    }


    @Test
    public void testStopWithoutStart() throws IOException {
        xrayServer.stop();
        Assert.assertNotNull("This should not fail at all");
    }

    @Test
    public void testStartAndStopWithoutPushingEvents() throws IOException {
        xrayServer.start();
        xrayServer.stop();
        Assert.assertNotNull("This should not fail at all");
    }


    @Test
    public void testPushEventStartAndStop() throws IOException {
        xrayServer.pushEvent(new XrayEvent("event", "result"));
        xrayServer.start();
        xrayServer.stop();
        Assert.assertNotNull("This should not fail at all");
    }

    @Test
    public void testStartPushEventAndStop() throws IOException {
        xrayServer.start();
        xrayServer.pushEvent(new XrayEvent("event", "result"));
        xrayServer.stop();
        Assert.assertNotNull("This should not fail at all");
    }

}
