package de.aw.xray.client;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.client.socket.SocketBasedXrayClientImpl;

import java.io.IOException;
import java.util.*;

/**
 * @author armin.weisser
 */
public class XrayImpl implements Xray {

    private Set<XrayEventListener> xrayEventListeners = new HashSet<XrayEventListener>();

    private XrayClient xrayClient;
    private long startTime;
    private long maxTimeout = 0;

    public XrayImpl(String host, int port) {
        xrayClient = new SocketBasedXrayClientImpl(host, port);
    }

    @Override
    public void register(XrayEventListener... xrayEventListeners) {
        for(XrayEventListener xrayEventListener: xrayEventListeners) {
            this.xrayEventListeners.add(xrayEventListener);
            Math.max(maxTimeout, xrayEventListener.getTimeout());
        }
    }

    @Override
    public void connect() throws IOException {
        xrayClient.connect();
        startTime = now();
    }

    @Override
    public void disconnect() throws IOException {
        xrayClient.disconnect();
    }

    @Override
    public void verify() throws IOException, InterruptedException {
        Thread.sleep(Math.max(0, now()-startTime+maxTimeout));
        notifyListeners();
        verifyListeners();
    }

    private void notifyListeners() throws IOException {
        XrayEvent[] events = xrayClient.getEvents(); // stop collecting events
        for(XrayEvent event: events) {
            notifyListeners(event);
        }
    }

    private void notifyListeners(XrayEvent event) {
        for(XrayEventListener listener: xrayEventListeners) {
            if(hasSameType(event, listener) && wasInTime(event, listener)) {
                listener.handleEvent(event);
            }
        }
    }

    private boolean wasInTime(XrayEvent event, XrayEventListener listener) {
        return event.getTime() <= startTime + listener.getTimeout();
    }

    private boolean hasSameType(XrayEvent event, XrayEventListener listener) {
        return listener.getEventType().equals(event.getType());
    }

    private void verifyListeners() {
        for(XrayEventListener listener: xrayEventListeners) {
            listener.verify();
        }
    }

    private long now() {
        return System.currentTimeMillis();
    }

}
