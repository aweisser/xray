package de.aw.xray.client.socket;

import de.aw.xray.client.XrayClient;
import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;
import de.aw.xray.agent.socket.SocketBasedXrayServerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;


/**
 * @author armin.weisser
 */
public class SocketBasedXrayClientImplTest {

    private static final int XRAY_PORT = 4712;
    XrayServer server;
    XrayClient client;

    @Before
    public void setUp() {
        server = new SocketBasedXrayServerImpl(XRAY_PORT);
        client = new SocketBasedXrayClientImpl("localhost", XRAY_PORT);

    }

    @Test
    public void sendOneEventAndConsumeNothing() throws IOException, InterruptedException {
        server.start();
        client.connect();
        server.pushEvent(new XrayEvent("type1", "hello"));

        // No time to collect events

        XrayEvent[] events = client.getEvents();
        Logger.getAnonymousLogger().info("Test> Got " + events.length + " events");

        assertEquals(0, events.length);
    }

    @Test
    public void sendAndConsumeOneEvent() throws IOException, InterruptedException {
        server.start();
        client.connect();
        server.pushEvent(new XrayEvent("type1", "hello"));

        Thread.sleep(1000); // give the client process a little time to collect the event

        XrayEvent[] events = client.getEvents();

        Logger.getAnonymousLogger().info("Test> Got " + events.length + " events");

        assertEquals(1, events.length);
        assertEquals("type1", events[0].getType());
        assertEquals("hello", events[0].getResult());
    }

    @Test(expected = ConnectException.class)
    public void clientConnectBeforeServerStart() throws IOException, InterruptedException {
        client.connect();
        server.start();
    }

    @Test(expected=IOException.class)
    public void cannotConnectWithoutServer() throws IOException {
        client.connect();
    }

    @Test
    public void disconnectWithoutEffect() throws IOException {
        client.disconnect();
    }

    @Test(expected=IllegalStateException.class)
    public void cannotGetEventsWithoutConnection() throws IOException {
        client.getEvents();
    }


    @After
    public void tearDown() throws IOException {
        client.disconnect();
        server.stop();
    }

}
