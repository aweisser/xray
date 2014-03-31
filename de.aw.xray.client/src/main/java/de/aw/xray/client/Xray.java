package de.aw.xray.client;

import java.io.IOException;

/**
 * @author armin.weisser
 */
public interface Xray {

    void register(XrayEventListener... xrayEventListeners);

    void connect() throws IOException;

    void disconnect() throws IOException;

    void verify() throws IOException, InterruptedException;
}
