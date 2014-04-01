package de.aw.xray.client.impl;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.client.XrayEventListener;
import de.aw.xray.client.XrayEventListenerBuilder;
import org.junit.Assert;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author armin.weisser
 */
public class XrayEventListenerBuilderImpl implements XrayEventListenerBuilder {

    private XrayEventListenerImpl xrayEventListener;

    @Override
    public XrayEventListenerBuilder lookFor(String eventType) {
        xrayEventListener = new XrayEventListenerImpl();
        xrayEventListener.eventType = eventType;
        return this;
    }

    @Override
    public XrayEventListenerBuilder within(long timeout, TimeUnit timeUnit) {
        assertState();
        xrayEventListener.timeout = timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public XrayEventListenerBuilder andExpect(Serializable expectation) {
        assertState();
        xrayEventListener.expectedResult = expectation;
        return this;
    }

    @Override
    public XrayEventListener build() {
        return xrayEventListener;
    }

    private void assertState() {
        if(xrayEventListener == null) {
            throw new IllegalStateException("Please call lookFor first");
        }
    }

    class XrayEventListenerImpl implements XrayEventListener {

        String eventType;
        Serializable expectedResult;
        Long timeout;

        private boolean expectedResultFound;

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
            Assert.assertTrue(expectedResultFound);
        }
    }
}
