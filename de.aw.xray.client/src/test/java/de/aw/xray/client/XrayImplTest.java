package de.aw.xray.client;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;
import de.aw.xray.agent.socket.SocketBasedXrayServerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author armin.weisser
 */
public class XrayImplTest {

    private static final int TIMEOUT = 5000;
    private static final int XRAY_PORT = 4712;

    static Logger LOG = Logger.getAnonymousLogger();

    Xray xray;
    XrayServer server;


    @Before
    public void setUp() throws IOException {
        server = new SocketBasedXrayServerImpl(XRAY_PORT);
        server.start();
        xray = new XrayImpl("localhost", XRAY_PORT);
    }


    @Test(expected = AssertionError.class)
    public void simpleEvent_NoExpectedEvent() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type", "expected result", TIMEOUT);
        xray.register(event1);
        xray.connect();
        // no server.pushEvent(...)
        xray.verify();
    }


    @Test(expected = AssertionError.class)
    public void simpleEvent_UnexpectedResult() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type", "expected result", TIMEOUT);
        xray.register(event1);
        xray.connect();
        server.pushEvent(new XrayEvent("expected type", "unexpected result"));
        xray.verify();
    }

    @Test(expected = AssertionError.class)
    public void simpleEvent_UnexpectedType() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type", "expected result", TIMEOUT);
        xray.register(event1);
        xray.connect();
        server.pushEvent(new XrayEvent("unexpected type", "expected result"));
        xray.verify();
    }


    @Test(expected = AssertionError.class)
    public void simpleEvent_SorryToLate() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type", "expected result", 1000);
        xray.register(event1);
        xray.connect();

        Thread.sleep(2000);

        server.pushEvent(new XrayEvent("expected type", "expected result"));
        xray.verify();
    }

    @Test
    public void simpleEvent_GoodCase() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type", "expected result", TIMEOUT);
        xray.register(event1);
        xray.connect();
        server.pushEvent(new XrayEvent("expected type", "expected result"));
        xray.verify();
    }

    @Test
    public void singleEvent_registerAfterPush_GoodCase() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        xray.connect();
        server.pushEvent(new XrayEvent("expected type 1", "expected result"));
        xray.register(event1);
        xray.verify();
    }

    @Test
    public void multipleEvents_GoodCase() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        XrayEventListener event2 = new TestXrayEventListener("expected type 2", "expected result", TIMEOUT);
        XrayEventListener event3 = new TestXrayEventListener("expected type 3", "expected result", TIMEOUT);
        xray.register(event1, event2, event3);

        xray.connect();

        server.pushEvent(new XrayEvent("expected type 1", "expected result"));
        server.pushEvent(new XrayEvent("expected type 2", "expected result"));
        server.pushEvent(new XrayEvent("expected type 3", "expected result"));

        xray.verify();
    }

    @Test(expected = AssertionError.class)
    public void multipleEvents_MultipleErrors() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        XrayEventListener event2 = new TestXrayEventListener("expected type 2", "expected result", TIMEOUT);
        XrayEventListener event3 = new TestXrayEventListener("expected type 3", "expected result", 0);
        xray.register(event1, event2, event3);

        xray.connect();

        Thread.sleep(1000);

        server.pushEvent(new XrayEvent("unexpected type 1", "expected result"));
        server.pushEvent(new XrayEvent("expected type 2", "unexpected result"));
        server.pushEvent(new XrayEvent("expected type 3", "expected result"));

        xray.verify();
    }


    @Test
    public void multipleEvents_registerBetweenConnectAndPush() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        XrayEventListener event2 = new TestXrayEventListener("expected type 2", "expected result", TIMEOUT);
        XrayEventListener event3 = new TestXrayEventListener("expected type 3", "expected result", TIMEOUT);
        xray.register(event1);

        xray.connect();

        xray.register(event2);
        xray.register(event3);

        server.pushEvent(new XrayEvent("expected type 1", "expected result"));
        server.pushEvent(new XrayEvent("expected type 2", "expected result"));
        server.pushEvent(new XrayEvent("expected type 3", "expected result"));

        xray.verify();
    }

    @Test
    public void singleEvent_registerAfterVerfiy_GoodCase() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        XrayEventListener event2 = new TestXrayEventListener("expected type 2", "expected result", TIMEOUT);
        xray.register(event1);
        xray.connect();
        server.pushEvent(new XrayEvent("expected type 1", "expected result"));
        server.pushEvent(new XrayEvent("expected type 2", "expected result")); // There is an event but no listener
        xray.verify();
        xray.register(event2);
    }

    @Test
    public void multipleEvents_registerBetween_GoodCase() throws IOException, InterruptedException {
        XrayEventListener event1 = new TestXrayEventListener("expected type 1", "expected result", TIMEOUT);
        XrayEventListener event2 = new TestXrayEventListener("expected type 2", "expected result", TIMEOUT);
        XrayEventListener event3 = new TestXrayEventListener("expected type 3", "expected result", TIMEOUT);
        xray.connect();
        xray.register(event2);
        server.pushEvent(new XrayEvent("expected type 3", "expected result"));
        server.pushEvent(new XrayEvent("expected type 1", "expected result"));
        xray.register(event1);
        server.pushEvent(new XrayEvent("expected type 2", "expected result"));
        xray.register(event3);
        xray.verify();
    }


    @After
    public void tearDown() throws IOException {
        xray.disconnect();
        server.stop();
    }


    class TestXrayEventListener implements XrayEventListener {

        private final String eventType;
        private final Object expectedResult;
        private final long timeout;

        private boolean expectedResultFound = false;

        public TestXrayEventListener(String type, Object expectedResult, long timeout) {
            this.eventType = type;
            this.expectedResult = expectedResult;
            this.timeout = timeout;
        }

        @Override
        public String getEventType() {
            return eventType;
        }

        @Override
        public long getTimeout() {
            return timeout;
        }

        @Override
        public void handleEvent(XrayEvent xrayEvent) {
            if(expectedResult.equals(xrayEvent.getResult())) {
                expectedResultFound = true;
            }
        }

        @Override
        public void verify() {
            assertTrue(this.expectedResultFound);
        }
    }



}
