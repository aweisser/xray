package de.aw.xray.agent.socket;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author armin.weisser
 */
public class SocketBasedXrayServerImpl implements XrayServer {

    private ServerSocket server;
    private Socket client;
    private ObjectOutputStream out;
    private int port;

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
    public void pushEvent(XrayEvent xrayEvent) throws IOException {
        // if out is null, no client is connected yet. So events can not be sent.
        // TODO perhaps it's better to do this threaded?
        if(out != null) {
            out.writeObject(xrayEvent);
            // TODO flush?
        }

    }

    @Override
    public void stop() throws IOException {
        if(out != null) out.close();
        if(client != null) client.close();
        if(server != null) server.close();
    }
}
