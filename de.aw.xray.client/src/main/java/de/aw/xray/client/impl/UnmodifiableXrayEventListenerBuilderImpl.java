package de.aw.xray.client.impl;

import de.aw.xray.client.XrayEventListener;
import de.aw.xray.client.XrayEventListenerBuilder;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author armin.weisser
 */
public class UnmodifiableXrayEventListenerBuilderImpl implements XrayEventListenerBuilder {

    @Override
    public XrayEventListenerBuilder lookFor(String eventType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XrayEventListenerBuilder within(long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XrayEventListenerBuilder andExpect(Serializable expectation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XrayEventListener build() {
        throw new UnsupportedOperationException();
    }
}
