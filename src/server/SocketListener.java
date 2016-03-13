package server;

import java.util.EventListener;

/**
 * Created by emilio on 24/01/16.
 */
public interface SocketListener extends EventListener{
    public void onReciveMessage(SocketEvent e);
    public void onRequestDeleteConnection(SocketEvent e);
    public void addClientToGroup(SocketEvent e);
}
