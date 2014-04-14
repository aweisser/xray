package de.aw.xray.client.socket;

import de.aw.xray.client.XrayClient;
import de.aw.xray.agent.XrayEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author armin.weisser
 */
public class SocketBasedXrayClientImpl implements XrayClient, Runnable {

    private String host;
    private int port;

    private Socket agentSocket;
    private ObjectInputStream in;

    private Collection<XrayEvent> events;
    private Thread eventCollectorThread;

    public SocketBasedXrayClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
        eventCollectorThread = new Thread(this);
    }


    @Override
    public void connect() throws IOException {
        agentSocket = new Socket(host, port);
        assert agentSocket.isConnected();
        events = new ArrayList<XrayEvent>();

        // TODO this is blocking!!!
        // If the client socket disconnects but the agentSocket is still there, this statement never returns.
        in = new ObjectInputStream(agentSocket.getInputStream());
        eventCollectorThread.start();
    }

    @Override
    public void run() {
        do{
            try {
                XrayEvent event = (XrayEvent) in.readObject();
                events.add(event);
                Logger.getAnonymousLogger().info("client> received " + event);
            } catch (ClassNotFoundException e) {
                Logger.getAnonymousLogger().info("client> ERROR: data received in unknown format");
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("client> ERROR: " + e.getMessage());
            }
        } while(!Thread.interrupted() && !agentSocket.isClosed());
    }

    @Override
    public XrayEvent[] getEvents() throws IOException {
        interruptEventCollectorThread();
        if(events == null) throw new IllegalStateException("Please call connect() first to collect events.");
        return events.toArray(new XrayEvent[0]);
    }


    @Override
    public void disconnect() throws IOException {
        interruptEventCollectorThread();
        if(in != null) in.close();
        //if(agentSocket != null && agentSocket.getInputStream() != null && !agentSocket.isInputShutdown()) agentSocket.getInputStream().close();
        if(agentSocket != null && !agentSocket.isClosed()) agentSocket.close();
    }


    private void interruptEventCollectorThread() {
        Thread.currentThread().interrupt();
        while(!Thread.interrupted()) {
            Logger.getAnonymousLogger().info("Client> Waiting for event collector to stop ... ");
        }
    }
}

