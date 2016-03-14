package server.listener_interface;

import server.StreamSocketEvent;

import java.util.EventListener;

/**
 * Created by emilio on 24/01/16.
 */
public interface StreamSocketListener extends EventListener{
    void onReciveMessage(StreamSocketEvent e);
    void onRequestDeleteConnection(StreamSocketEvent e);
    void onJoinGroupRequest(StreamSocketEvent e);
}
