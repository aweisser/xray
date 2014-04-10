package de.aw.xray.client.impl;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.client.Xray;
import de.aw.xray.client.XrayClient;
import de.aw.xray.client.XrayEventListener;
import de.aw.xray.client.XrayEventListenerBuilder;
import de.aw.xray.client.socket.SocketBasedXrayClientImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author armin.weisser
 */
public class XrayImpl implements Xray, XrayEventListenerBuilder {

    private XrayEventListenerBuilder delegate = new XrayEventListenerBuilderImpl();

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


    @Override
    public XrayEventListenerBuilder lookFor(String eventType) {
        return delegate.lookFor(eventType);
    }

    @Override
    public XrayEventListenerBuilder andExpect(Serializable expectation) {
        return delegate.andExpect(expectation);
    }


    @Override
    public XrayEventListenerBuilder within(long timeout, TimeUnit timeUnit) {
        delegate.within(timeout, timeUnit);
        build();
        return new UnmodifiableXrayEventListenerBuilderImpl();
    }

    @Override
    public XrayEventListener build() {
        XrayEventListener xrayEventListener = delegate.build();
        register(xrayEventListener);
        return xrayEventListener;
    }
}
