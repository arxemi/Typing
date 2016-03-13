package server;

import java.util.EventListener;

public interface ClientListener extends EventListener {
    public void onConnectionRequest(ClientEvent e);
    public void onDisconnectionRequest(ClientEvent e);
}
