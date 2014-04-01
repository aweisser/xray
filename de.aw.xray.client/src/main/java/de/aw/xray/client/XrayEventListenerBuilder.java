package de.aw.xray.client;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author armin.weisser
 */
public interface XrayEventListenerBuilder {

    XrayEventListenerBuilder lookFor(String eventType);

    XrayEventListenerBuilder within(long timeout, TimeUnit timeUnit);

    XrayEventListenerBuilder andExpect(Serializable expectation);

    XrayEventListener build();

}
