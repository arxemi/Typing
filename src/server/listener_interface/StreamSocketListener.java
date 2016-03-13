package server.listener_interface;

import server.StreamSocketEvent;

import java.util.EventListener;

/**
 * Created by emilio on 24/01/16.
 */
public interface StreamSocketListener extends EventListener{
    public void onReciveMessage(StreamSocketEvent e);
    public void onRequestDeleteConnection(StreamSocketEvent e);
    public void onJoinGroupRequest(StreamSocketEvent e);
}
