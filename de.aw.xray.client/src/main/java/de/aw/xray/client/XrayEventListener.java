package de.aw.xray.client;

import de.aw.xray.agent.XrayEvent;

/**
 * @author armin.weisser
 */
public interface XrayEventListener {

    String getEventType();

    long getTimeout();

    void handleEvent(XrayEvent xrayEvent);

    void verify();
}
