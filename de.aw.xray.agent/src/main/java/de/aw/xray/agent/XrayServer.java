package de.aw.xray.agent;

import java.io.IOException;

/**
 * @author armin.weisser
 */
public interface XrayServer {

    static final int DEFAULT_PORT = 4711;

    void start() throws IOException;

    void pushEvent(XrayEvent xrayEvent) throws IOException;

    void stop() throws IOException;

}
