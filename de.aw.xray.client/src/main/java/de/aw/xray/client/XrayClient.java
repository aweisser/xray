package de.aw.xray.client;

import de.aw.xray.agent.XrayEvent;

import java.io.IOException;

/**
 * @author armin.weisser
 */
public interface XrayClient {

    void connect() throws IOException;

    XrayEvent[] getEvents() throws IOException;

    void disconnect() throws IOException;
}
