package server.listener_interface;

import server.RequestEvent;

import java.util.EventListener;

public interface RequestListener extends EventListener {
    public void onConnectionRequest(RequestEvent e);
    public void onDisconnectionRequest(RequestEvent e);
}
