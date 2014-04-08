package de.aw.xray.agent.socket;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author armin.weisser
 */
public class SocketBasedXrayServerImpl implements XrayServer {

    private ServerSocket server;
    private Socket client;
    private ObjectOutputStream out;
    private int port;

    private Set<XrayEvent> xrayEventsBuffer;
    private Set<XrayEvent> xrayEventsHistory;

    public SocketBasedXrayServerImpl() {
        this(DEFAULT_PORT);
    }

    public SocketBasedXrayServerImpl(int port) {
        setPort(port);
        xrayEventsBuffer = new HashSet<XrayEvent>();
        xrayEventsHistory = new HashSet<XrayEvent>();
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void start() throws IOException {
        server = new ServerSocket(port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = server.accept(); // Blocking!!!
                    out = new ObjectOutputStream(client.getOutputStream());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();

    }

    @Override
    public void pushEvent(XrayEvent xrayEvent) {
        xrayEventsBuffer.add(xrayEvent);
        if(out != null && client.isConnected()) {
            flushBufferedEvents();
        }
    }

    private void flushBufferedEvents() {
        for(XrayEvent bufferedXrayEvent : xrayEventsBuffer) {
            try {
                out.writeObject(bufferedXrayEvent);
                xrayEventsHistory.add(bufferedXrayEvent);
            } catch (IOException e) {
                Logger.getAnonymousLogger().warning("Event " + bufferedXrayEvent + " could not be written to the socket. But it will be collected, for later transport.");
            }
        }
        xrayEventsBuffer.removeAll(xrayEventsHistory);
    }

    @Override
    public void stop() throws IOException {
        if(client != null) {
            Logger.getAnonymousLogger().info(
                    "isBound()? " + client.isBound() +
                    "\n, isConnected()? " + client.isConnected() +
                    "\n, isClosed()? " + client.isClosed() +
                    "\n, isInputShutdown()? " + client.isInputShutdown() +
                    "\n, isOutputShutdown()? " + client.isOutputShutdown());
        }

        if(out != null) out.close();
//        if(client != null && client.getOutputStream() != null) client.getOutputStream().close();
        if(client != null && !client.isClosed()) client.close();
        if(server != null && !server.isClosed()) server.close();
    }
}
