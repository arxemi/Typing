package server.listener_interface;

import server.ClientEvent;

import java.util.EventListener;

public interface RequestListener extends EventListener {
    public void onConnectionRequest(ClientEvent e);
    public void onDisconnectionRequest(ClientEvent e);
}
