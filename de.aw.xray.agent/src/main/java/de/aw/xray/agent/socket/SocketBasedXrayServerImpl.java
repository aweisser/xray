package de.aw.xray.agent.socket;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author armin.weisser
 */
public class SocketBasedXrayServerImpl implements XrayServer {

    private ServerSocket server;
    private Socket client;
    private ObjectOutputStream out;
    private int port;
    private Set<XrayEvent> xrayEventBuffer = new HashSet<XrayEvent>();

    public SocketBasedXrayServerImpl() {
        this(DEFAULT_PORT);
    }

    public SocketBasedXrayServerImpl(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void start() throws IOException {
        initServerSocket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = server.accept(); // Blocking!!!
                    out = new ObjectOutputStream(client.getOutputStream());
                    out.flush();
                } catch (IOException e) {
                    try {
                        SocketBasedXrayServerImpl.this.stop();
                    } catch (IOException e1) {
                        Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to init socket connection. Reason: " + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void initServerSocket() throws IOException {
        if (server == null || server.isClosed()) {
            server = new ServerSocket(port);
        }
    }

    @Override
    public void pushEvent(XrayEvent xrayEvent) {
        buffer(xrayEvent);
        if (clientIsReadyToReceive()) {
            sendAllBufferedEventsToClient();
        }
    }

    private void buffer(XrayEvent xrayEvent) {
        xrayEventBuffer.add(xrayEvent);
    }

    private boolean clientIsReadyToReceive() {
        return out != null && !client.isOutputShutdown() && !client.isClosed();
    }

    private void sendAllBufferedEventsToClient() {
        try {
            for (XrayEvent bufferedEvent : xrayEventBuffer) {
                out.writeObject(bufferedEvent);
            }
            out.flush();
            xrayEventBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();  //TODO handle exception!!!
        }
    }

    @Override
    public void stop() throws IOException {
        if (out != null) out.close();
        if (client != null) client.close();
        if (server != null) server.close();
    }
}
