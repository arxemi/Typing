package server.listener;

import server.RequestEvent;

import java.util.EventListener;

/**
 * @author emilo acciaro
 */

public interface RequestListener extends EventListener {
    void onConnectionRequest(RequestEvent e);
    void onDisconnectionRequest(RequestEvent e);
}
